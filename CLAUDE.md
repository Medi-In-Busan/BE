# CLAUDE.md

이 문서는 이 저장소에서 작업하는 향후 Claude Code 세션(및 팀원)을 위한 컨텍스트다. "왜 이렇게 만들었는지"에 대한 상세 설명은 `docs/ARCHITECTURE.md`를 참고한다.

## 1. 제품 요약

**메디인부산(MediIn Busan)**은 부산을 방문하는 외국인 의료관광객이 자신의 의료 목적에 맞는 부산 의료기관을 탐색하고, 의료 이용 절차와 병원 주변 관광·웰니스 정보를 함께 확인할 수 있도록 돕는 **정보 제공형** Android 앱이다.

**MVP 하드 제약 (구조적으로 존재해서는 안 됨):**
- 병원 예약/대행 기능 없음
- 진료비·상품 결제 기능 없음
- 실시간 상담/통역사 매칭 기능 없음
- 사용자 GPS 위치를 서버/API로 전송하는 위치기반 추천 없음 → `AndroidManifest.xml`에 위치 권한이 **절대 추가되어서는 안 된다.**

이 제약을 어기는 기능 요청이 들어오면, 먼저 사용자에게 MVP 범위를 벗어난다는 점을 확인시킬 것.

## 2. 기술 스택

| 영역 | 선택 |
| --- | --- |
| UI | Jetpack Compose (Material3), XML 뷰 없음 |
| 아키텍처 | 단일 Gradle 모듈(`:app`), 기능별 패키지(`feature/*`) + 얕은 공유 계층(`core/`, `data/`, `domain/`) |
| DI | Hilt + KSP |
| 비동기 | Kotlin Coroutines + Flow |
| 네트워킹 | Retrofit2 + OkHttp + kotlinx.serialization |
| 로컬 저장 | Room(즐겨찾기·최근 본 항목만) + DataStore Preferences(언어·온보딩·의료목적) |
| 이미지 | Coil3 |
| 지도 | Kakao Map SDK |
| 네비게이션 | Navigation Compose 타입세이프 라우트 (`core/navigation/Route.kt`) |

각 선택의 이유는 `docs/ARCHITECTURE.md` 참고.

## 3. 화면/기능 ↔ 패키지 매핑

| 화면 | 기능 ID | 패키지 |
| --- | --- | --- |
| S-01 스플래시 | F-001 | `feature/splash` |
| S-02 온보딩 | F-002, F-003 | `feature/onboarding` |
| S-03 홈 | - | `feature/home` |
| S-04 의료기관 목록 | F-004, F-005 | `feature/hospitallist` |
| S-05 의료기관 상세 | F-006, F-007, F-009 | `feature/hospitaldetail` |
| S-06 의료 이용 가이드 | F-008 | `feature/guide` |
| S-07 주변 관광·웰니스 | F-011, F-012, F-014 | `feature/nearby` (+ `domain/nearby`, `domain/course`) |
| S-08 지도 | F-010, F-013 | `feature/map` |
| S-09 즐겨찾기 | F-015, F-016(최근 본 항목은 현재 데이터 계층만 존재, 화면 미배선) | `feature/favorite`, `data/recent` |
| S-10 설정/출처 | F-018 | `feature/settings` |
| 공통 | F-019 오류/빈 상태 처리 | `core/ui` (`LoadingState`, `ErrorState`, `EmptyState`, `AsyncImageBox`) |
| 외부 지도 연결 | F-017 | 미구현 (TODO) |

## 4. 디렉토리 규칙

```
app/src/main/java/com/mediinbusan/app/
├── MediInBusanApp.kt, MainActivity.kt   # 앱 진입점
├── core/       # 여러 feature가 공유하는 인프라 (network, database, datastore, navigation, designsystem, ui, common)
├── data/       # 도메인별 리포지토리 구현 + DTO + 매퍼 (hospital, place, guide, favorite, recent)
├── domain/     # 실제 비즈니스 로직이 있는 극히 일부 UseCase만 (nearby 거리정렬, course 큐레이션)
└── feature/    # 화면 단위 패키지 (Screen + ViewModel + UiState 3종 세트)
```

- **feature 패키지는 서로를 직접 import하지 않는다.** 화면 간 이동은 반드시 `core/navigation/MediInBusanNavHost.kt`를 통해서만 연결한다. (나중에 멀티모듈로 쪼갤 때 마찰을 줄이기 위한 규칙.)
- 단순 조회/저장 화면은 ViewModel → Repository 직접 호출. `domain/`은 F-011(거리 정렬), F-014(코스 큐레이션)처럼 실제 로직이 있는 곳에만 존재한다.
- Hospital/Place/GuideStep은 Room 엔티티가 **아니다** (매 세션 OpenAPI/정적 데이터에서 조회). Room에는 Favorite/RecentlyViewed만 있다.

## 5. 빌드 환경 living note (중요 — 최신 상태 유지할 것)

이 프로젝트는 **AGP 9.0.1 / Gradle 9.2.1**이라는 매우 최신 조합을 사용한다 (2026년 1월 AGP 9.0.1 릴리스 기준). 스캐폴드 단계에서 실제로 겪은 호환성 이슈와 해결책:

1. **`org.jetbrains.kotlin.android` 플러그인을 적용하면 안 된다.** AGP 9.0부터 Kotlin 지원이 내장되어 있고, 별도 플러그인을 적용하면 `Cannot add extension with name 'kotlin'` 에러가 난다. `org.jetbrains.kotlin.plugin.compose`, `org.jetbrains.kotlin.plugin.serialization`은 여전히 별도로 적용해야 한다.
2. **Hilt는 2.59 이상**을 써야 한다. 2.58 이하는 AGP 9.x의 새 DSL(`BaseExtension` 제거)과 호환되지 않아 `Android BaseExtension not found` 에러가 난다.
3. **KSP 2.1.20-1.0.31**이 생성 소스를 등록할 때 구식 `kotlin.sourceSets` DSL을 사용해서 AGP의 built-in Kotlin이 이를 거부한다. 현재 `gradle.properties`에 `android.disallowKotlinSourceSets=false`로 임시 우회 중 — 이 프로젝트의 Kotlin 버전에 맞으면서 `android.sourceSets` DSL을 네이티브로 쓰는 KSP 릴리스가 나오면 이 플래그를 제거하고 마이그레이션할 것.
4. **`androidx.core:core-ktx`는 1.18.0에 고정.** 1.19.0은 compileSdk 37 + AGP 9.1.0을 요구해 현재 compileSdk 36 / AGP 9.0.1 조합과 맞지 않는다 (`CheckAarMetadataWorkAction` 에러).
5. **Kakao Map SDK는 devrepo.kakao.com 기준 2.13.5가 최신** (2.14.7 같은 더 높은 버전은 존재하지 않음 — 항상 `maven-metadata.xml`로 실제 최신 버전 확인할 것).
6. 의존성 버전을 올릴 때는 항상 `./gradlew.bat :app:assembleDebug --stacktrace`로 한 단계씩 검증할 것. 이 조합 자체가 불안정하므로 여러 라이브러리를 동시에 올리면 원인 파악이 어렵다.

## 6. 외부 API (한국관광공사 OpenAPI)

- **의료관광정보 서비스** — 병원 목록/상세/다국어 정보 (`data/hospital/HospitalApi.kt`)
- **관광정보서비스** — 주변 관광지/음식점/쇼핑/웰니스 장소, 사진 (`data/place/TourismApi.kt`)

⚠️ **두 API 모두 정확한 오퍼레이션명·파라미터·응답 구조가 미확정이다.** 현재 `HospitalApi.kt`, `TourismApi.kt`, `HospitalDto.kt`, `PlaceDto.kt`의 경로/필드명은 전부 **플레이스홀더**다. 실제 연동 작업 시 반드시 공식 API 문서를 먼저 확인하고 교체할 것 — 기존 필드명을 그대로 믿지 말 것.

## 7. 시크릿 관리

`local.properties`(gitignore됨)에 `TOURISM_API_SERVICE_KEY`, `KAKAO_NATIVE_APP_KEY`를 넣는다. `app/build.gradle.kts`가 이를 읽어 `BuildConfig.TOURISM_API_SERVICE_KEY`와 매니페스트 `${KAKAO_NATIVE_APP_KEY}` 플레이스홀더로 각각 주입한다. 값이 없으면 빈 문자열로 폴백해 클론 직후에도 빌드는 된다. **절대 키를 코드/매니페스트에 하드코딩하지 말 것.**

## 8. 아직 구현되지 않은 것 (TODO로 명시되어 있음)

- 실제 한국관광공사 OpenAPI 연동 (현재 모든 Repository는 샘플 데이터 반환)
- 실제 Kakao Map 렌더링 (`feature/map/MapScreen.kt`는 현재 좌표를 텍스트 목록으로만 표시)
- F-011 병원 좌표 기준 실제 거리 계산 (`domain/nearby/GetNearbyPlacesSortedByDistanceUseCase.kt`)
- F-014 웰니스 코스 실제 큐레이션 로직 (`domain/course/AssembleWellnessCourseUseCase.kt`)
- F-008 가이드 콘텐츠 5단계 × 5개 언어 실제 텍스트 (`data/guide/GuideRepositoryImpl.kt`)
- F-016 최근 본 항목을 홈/즐겨찾기 화면에 노출하는 UI (데이터 계층(`data/recent`)과 기록 로직은 이미 연결되어 있음)
- F-017 외부 지도 앱 연결 인텐트
- 다국어 문자열 리소스 테이블 (현재 한국어 하드코딩 UI 문구가 많음)
