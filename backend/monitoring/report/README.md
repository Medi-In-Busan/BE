# 부하 테스트 결과 캡처

k6 실행 뒤 Prometheus/Grafana가 떠 있는 상태에서 실행한다. 결과 이미지는 측정 기록이라 저장소에 커밋하지 않고 로컬에 둔다
(예: `docs/performance/` — 각자 `.git/info/exclude`에 넣어 추적에서 뺀다).

```bash
cd backend/monitoring/report
npm i puppeteer-core@24          # 설치된 Chrome을 쓴다(브라우저 다운로드 없음)

# Grafana 대시보드 전체 + 패널별 PNG
FROM=2026-09-25T16:27:30+09:00 TO=2026-09-25T16:32:00+09:00 \
  node grafana_screenshots.js "C:\path\to\images"
#   CHROME=<chrome.exe 경로>, GRAFANA_AUTH=user:password 로 바꿀 수 있다.

# 요약 그림(matplotlib, Malgun Gothic) — ④ 핵심 수치 표는 스크립트 안에서 그 실행의 값으로 고친다
python summary_figure.py 2026-09-25T16:28:00 2026-09-25T16:31:15 "C:\path\to\images\summary.png"
# 전후 비교 그림 — 개선 전 시작, 개선 후 시작, 길이(초), 출력
python compare_figure.py 2026-09-25T16:28:00 2026-09-25T17:45:05 180 "C:\path\to\images\compare.png"
```
