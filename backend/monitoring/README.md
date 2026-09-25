# 백엔드 성능 모니터링 (로컬)

Prometheus + Grafana로 백엔드 동시성 병목(전역 락, 트랜잭션 안의 외부 호출, 외부 클라이언트 타임아웃)을 눈으로 확인하기 위한 로컬 스택이다.
운영(EC2)에는 아직 올리지 않았다 — 앱 쪽 지표 엔드포인트(관리 포트 8081)만 같이 배포된다.

## 구성

| 파일 | 역할 |
| --- | --- |
| `docker-compose.monitoring.yml` | Prometheus(:9090) + Grafana(:3000). 둘 다 `127.0.0.1`에만 바인딩 |
| `prometheus/prometheus.local.yml` | `host.docker.internal:8081/actuator/prometheus`를 5초마다 수집 |
| `grafana/provisioning/**` | 데이터소스·대시보드 자동 등록 |
| `grafana/dashboards/mediinbusan-backend.json` | "MediIn Busan — 백엔드 병목" 대시보드 |
| `k6/bottlenecks.js` | 병목 재현 부하 스크립트 |
| `k6/slow_upstream.py` | 느린/무응답 외부 API를 흉내 내는 가짜 서버 |

## 실행

```bash
# 1) 앱 (관리 포트 8081에 /actuator/prometheus가 열린다. 8080에는 없다)
cd backend && ./gradlew.bat bootRun

# 2) 모니터링 스택 (Docker Desktop 실행 필요)
cd backend/monitoring && docker compose -f docker-compose.monitoring.yml up -d
#    Grafana: http://localhost:3000 (admin / admin, GRAFANA_ADMIN_PASSWORD로 변경 가능)
#    Prometheus 타깃 확인: http://localhost:9090/targets

# 3) 부하 — backend/ 에서 실행 (마운트 경로가 monitoring/k6 여야 한다)
cd backend
docker run --rm -i -v "${PWD}/monitoring/k6:/scripts" grafana/k6 run /scripts/bottlenecks.js
#    옵션: -e DURATION=3m -e DETAIL_VUS=30 -e CROWDING_VUS=5 -e SAMPLE=50 -e BASE_URL=http://host.docker.internal:8080
```

## 실제 API 한도를 쓰지 않고 병목 재현하기

실제 Papago 키로 부하를 걸면 캐시 미스마다 **번역 한도를 소모**한다(최대 `SAMPLE × 3개 언어`건).
대신 외부 API를 가짜 느린 서버로 돌려서 재현할 수 있다.

```powershell
# 가짜 외부 API: 모든 요청을 30초 붙잡고 503 (두 번째 인자를 600으로 주면 사실상 무응답)
python backend/monitoring/k6/slow_upstream.py 9999 30

# 앱을 가짜 서버로 향하게 해서 실행 (PowerShell)
$env:PAPAGO_TRANSLATION_API_URL='http://localhost:9999/papago'
$env:PAPAGO_TRANSLATION_CLIENT_ID='dummy'; $env:PAPAGO_TRANSLATION_CLIENT_SECRET='dummy'
$env:TOURISM_API_SERVICE_KEY='dummy'
$env:WELLNESS_INGESTION_CROWDINGBASEURL='http://localhost:9999/crowding'
./gradlew.bat bootRun
```

## 대시보드에서 볼 것

| 병목 | 패널 | 문제 신호 |
| --- | --- | --- |
| #1 번역 전역 락 | (해결됨 — 락 제거) | 이제 `wellness-translation` 락 지표는 나오지 않는다 |
| #2 커넥션 2개 점유 | 커넥션 active / pending, 점유 시간 max | active = max(10), pending > 0, 무관한 `/api/hospitals` p95·5xx 동반 상승 |
| #3 타임아웃 부재 | 진행 중인 호출 수, 가장 오래 진행 중인 호출 | `papago`/`clova-ocr`의 경과 시간이 끝없이 증가(Timer엔 안 찍힘) |
| #4 혼잡도 전역 락 | 락 대기열(`crowding-catalog`), 스냅샷 생성 시간 | 대기열 > 0, 생성 시간 ≈ TourAPI 타임아웃(40s) |

> ReentrantLock 대기는 JVM 스레드 상태로 BLOCKED가 아니라 WAITING이다 — 락 경합은 `mediinbusan_lock_queue`로 본다.

## 지표 목록 (앱이 내보내는 커스텀 지표)

| 이름 (Prometheus) | 종류 | 태그 |
| --- | --- | --- |
| `mediinbusan_external_api_seconds_*` | Timer(히스토그램) | client, operation, outcome, exception |
| `mediinbusan_external_api_active_seconds_gcount` / `_max` | LongTaskTimer | client, operation — 진행 중 호출 수 / 최장 경과 |
| `mediinbusan_lock_wait_seconds_*`, `mediinbusan_lock_held_seconds_*` | Timer(히스토그램) | lock |
| `mediinbusan_lock_queue` | Gauge | lock |
| `mediinbusan_translation_cache_total` | Counter | domain, path, result |
| `mediinbusan_papago_quota_blocked` | Gauge(0/1) | - |
| `mediinbusan_crowding_snapshot_build_seconds_*` | Timer(히스토그램) | outcome |
| `cache_gets_total{cache="tourismPlaces"}` | Caffeine | result |

외부 API 태그에는 URL·쿼리스트링·본문을 싣지 않는다(Gemini 키가 쿼리스트링에 있고, CLOVA 호출 URL은 시크릿이다 —
`monitoring/ExternalApiMetrics` 주석 참고). 지표 태그에 좌표·사용자 입력·IP를 추가하지 말 것.
