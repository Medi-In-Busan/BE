# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

이 문서는 이 저장소에서 작업하는 향후 Claude Code 세션(및 팀원)을 위한 컨텍스트다. "왜 이렇게 만들었는지"에 대한 상세 설명은 `docs/ARCHITECTURE.md`를 참고한다.

**이 저장소는 모노레포다**: 루트가 이 Android 클라이언트(`:app`, 이 문서가 다루는 대상)이고, `backend/`는 별도 Gradle 루트 프로젝트(Spring Boot, 자체 `gradlew`/`settings.gradle`)로 독립적으로 빌드·실행된다. `backend/`에서 작업하거나 API 계약을 바꿀 때는 반드시 `backend/CLAUDE.md`를 같이 읽을 것 — 특히 Android `core/common/MedicalCategory.kt`와 백엔드 `MedicalSpecialty` enum은 값이 **동일하게 유지되어야 한다**(한쪽을 바꾸면 다른 쪽도 확인).

루트에 `AGENTS.md`(Codex용)도 있지만 **스캐폴드 시점 내용에서 갱신이 멈춰 있다** — 내용이 이 문서와 충돌하면 이 문서가 맞다. AGENTS.md를 근거로 판단하지 말 것.

## 빌드/실행 명령

```bash
./gradlew.bat :app:assembleDebug --stacktrace   # 디버그 APK 빌드 (의존성 버전 검증 시 기본 명령)
./gradlew.bat :app:compileDebugKotlin           # 컴파일만 빠르게 확인
./gradlew.bat :app:clean :app:assembleDebug     # dex 캐시 이슈 의심될 때 클린 빌드
```

- 별도 lint/format 태스크는 구성되어 있지 않다 — 위 빌드 명령이 사실상의 정합성 게이트다.
- 단위 테스트(`app/src/test`)·계측 테스트(`app/src/androidTest`)는 현재 스캐폴드 예제(`ExampleUnitTest`, `ExampleInstrumentedTest`)만 있고 실제 테스트는 없다. 즉 `:app`에는 회귀를 잡아줄 자동 테스트가 없다 — 동작 변경은 실기기 확인이 유일한 검증 수단이다.
- `assembleRelease`/`bundleRelease`는 gitignore된 `keystore.properties`(`storeFile`/`storePassword`/`keyAlias`/`keyPassword`)가 없으면 `app/build.gradle.kts`의 `tasks.configureEach { check(...) }`가 **의도적으로 빌드를 실패시킨다**. 디버그 빌드는 이 파일 없이도 된다.
- **x86_64 에뮬레이터에는 설치 자체가 안 된다.** `defaultConfig.ndk.abiFilters += "arm64-v8a"`로 ABI를 하나로 묶어놨다 — Kakao Map SDK의 `libK3fAndroid.so`가 arm64-v8a/armeabi-v7a로만 배포돼서, x86_64가 1순위인 기기에 붙이면 PackageManager가 x86_64 폴더만 골라 설치해 카카오 네이티브 라이브러리가 통째로 빠지기 때문이다. 실기기 또는 ARM 이미지 에뮬레이터를 쓸 것.
- 실기기(USB/같은 Wi-Fi)로 병원·웰니스·관광·문서스캔·AI챗 데이터를 확인하려면 `backend/`를 로컬에서 먼저 띄워야 한다(`cd backend && ./gradlew.bat bootRun`, 기본 프로필은 Docker 없이 H2로 동작). 아래 §9 참고.

## 1. 제품 요약

**메디인부산(MediIn Busan)**은 부산을 방문하는 외국인 의료관광객이 자신의 의료 목적에 맞는 부산 의료기관을 탐색하고, 의료 이용 절차와 병원 주변 관광·웰니스 정보를 함께 확인할 수 있도록 돕는 **정보 제공형** Android 앱이다.

**MVP 하드 제약 (구조적으로 존재해서는 안 됨):**
- 병원 예약/대행 기능 없음
- 진료비·상품 결제 기능 없음
- 실시간 상담/통역사 매칭 기능 없음
- 사용자 GPS 위치를 서버/API로 전송하는 위치기반 추천 없음 → `AndroidManifest.xml`에 위치 권한이 **절대 추가되어서는 안 된다.**

이 제약을 어기는 기능 요청이 들어오면, 먼저 사용자에게 MVP 범위를 벗어난다는 점을 확인시킬 것. (`backend/`에도 동일한 제약이 적용된다 — 실시간 GPS를 받는 엔드포인트를 만들지 말 것.)

현재 매니페스트에 있는 권한은 `INTERNET`, `ACCESS_NETWORK_STATE`, `CAMERA`(문서스캔 촬영 전용) 셋뿐이다. 카메라는 §11의 사전 고지 절차와 한 묶음이다.

## 2. 기술 스택

| 영역 | 선택 |
| --- | --- |
| UI | Jetpack Compose (Material3), XML 뷰 없음 |
| 아키텍처 | 단일 Gradle 모듈(`:app`), 기능별 패키지(`feature/*`) + 얕은 공유 계층(`core/`, `data/`, `domain/`) |
| DI | Hilt + KSP |
| 비동기 | Kotlin Coroutines + Flow |
| 네트워킹 | Retrofit2 + OkHttp + kotlinx.serialization |
| 로컬 저장 | Room(즐겨찾기·최근 본 항목·검색 기록) + DataStore Preferences(언어·의료목적·권한 고지 확인 여부 등) |
| 인메모리 캐시 | `core/common/TtlCache.kt` — `@Singleton` 리포지토리 필드로 두는 프로세스 수명 TTL 캐시(Room에 넣을 만큼 오래 보존하지 않을 목록의 재조회 절감용) |
| 이미지 | Coil3 (+ `androidx.exifinterface`로 OCR 업로드 전 회전 보정) |
| 카메라 | CameraX(`camera-view` PreviewView + `AndroidView`) — `camera-compose`는 아직 alpha라 쓰지 않는다(KakaoMapView와 같은 패턴) |
| 지도 | Kakao Map SDK (실제 렌더링 동작함 — 아래 §9 참고) |
| UI 이펙트 | Haze 1.7.2 — 하단바/Home FAB의 실시간 backdrop blur(glassmorphism). 2.x는 alpha라 stable에 고정 |
| 네비게이션 | Navigation Compose 타입세이프 라우트 (`core/navigation/Route.kt`) |

각 선택의 이유는 `docs/ARCHITECTURE.md` 참고.

## 3. 화면/기능 ↔ 패키지 매핑

| 화면 | 기능 ID | 패키지 |
| --- | --- | --- |
| S-01 스플래시 | F-001 | `feature/splash` |
| S-03 홈 | - | `feature/home` (추천 병원 섹션은 `domain/recommendation` 참고) |
| S-04 의료기관 목록/검색 | F-004, F-005 | `feature/hospitalsearchlist` (Home의 의료목적 선택/의료기관 찾기/검색바 진입점이 모두 이 화면 하나로 모인다) |
| S-05 의료기관 상세 | F-006, F-007, F-009 | `feature/hospitaldetail` |
| S-06 의료 이용 가이드 | F-008 | `feature/guide` (STEP 01~06 + 항목별 리프 화면 + STEP04 전용 `TreatmentExaminationDetailScreen`. 하위 라우트는 `core/navigation/Route.kt` 참고) |
| S-07 주변 관광·웰니스 | F-011, F-012, F-014 | `feature/nearby` (+ `domain/nearby`, `domain/course`) |
| S-07 하위 부산 관광 공공데이터 | 원 기능명세에 없던 추가 기능 | `feature/tourism` (+ `domain/tourism`, `data/tourism`) — 허브/카테고리 리스트업/개인화 추천 코스/항목 상세 4개 화면 |
| S-08 지도 | F-010, F-013 | `feature/map` |
| S-09 즐겨찾기 | F-015 | `feature/favorite` |
| S-10 설정/출처 | F-018 | `feature/settings` (하위 라우트: `NotificationSettings`, `SettingsInfoDetail`, `RecentlyViewed`) |
| 최근 본 항목 | F-016 | `feature/recent` + `data/recent` — 전용 화면은 이미 있고 설정(S-10) 하위에서 진입한다. 원래 계획이던 "홈/즐겨찾기 화면에 직접 노출"은 아직 없다. |
| 공통 | F-019 오류/빈 상태 처리 | `core/ui` (`LoadingState`, `ErrorState`, `EmptyState`, `StateFeedbackHero`, `AsyncImageBox`) |
| 외부 지도 연결 | F-017 | `core/ui/MapIntents.kt`의 `launchExternalDirections` — 구현됨(`geo:` 인텐트로 기기 기본 지도 앱 실행) |
| 문서 스캔(OCR·번역) | 원 기능명세에 없던 추가 기능 | `feature/documentscan` — Home FAB에서 진입. `backend/document`(CLOVA OCR + Papago) 호출 |
| 자가진단(AI 챗) | 원 기능명세에 없던 추가 기능 | `feature/selfdiagnosis` + `data/diagnosischat` — Home의 AI FAB에서만 진입. `backend/diagnosischat`(Gemini 프록시) 호출 |
| 앱 접근권한 고지 | 법정 고지(원스토어 검증 대응) | `feature/permission` — 최초 실행 시 스플래시 다음에 1회, 이후에는 설정(S-10) > 정보 > 앱 접근권한에서 상시 열람. 아래 §11 참고 |

**S-02 온보딩(언어 선택) 화면은 더 이상 없다.** `feature/languageselect`와 `Route.Onboarding`은 제거됐고, 언어 선택은 모든 화면 공통 탑바(`core/ui/BrandTopAppBar.kt`)의 언어 드롭다운으로 상시 가능하다. 지원 언어의 단일 출처는 `core/datastore/SupportedLanguage.kt`. 최초 실행 흐름은 이제 스플래시 → (미확인 시) 앱 접근권한 고지 → 홈 뿐이고, 자가진단도 여기에 강제로 끼지 않는다(`SplashViewModel` 참고).

**하단 탭바는 5개: 홈 / 의료기관 / 가이드 / 지도 / 추천 웰니스** (`core/navigation/MediInBusanApp.kt`의 `bottomNavTabs`). 문서스캔은 한때 5번째 탭이었지만 추천 웰니스로 교체됐고, 지금은 Home의 FAB 두 개(문서스캔 + AI 진단)에서 진입한다. 설정·즐겨찾기·최근 본 항목·자가진단은 탭이 아니라 push 라우트다.

탭 활성 표시와 하단바 노출은 route 타입만으로 결정되지 않는다 — 웰니스 탭은 `Nearby`/`TourismCatalog`/`RecommendedTourismCourse`를 모두 자기 하위로 보고, `MapView`는 인자(`hospitalId`)와 마커 선택 상태까지 봐야 한다. 새 하위 화면을 추가하면 `shouldShowBottomBar`와 `bottomNavTabs`의 `selected`를 같이 갱신할 것(빼먹으면 활성 탭이 하나도 없게 되고, 캡슐 인디케이터가 첫 탭으로 미끄러져 "홈에 있는 것처럼" 보인다).

## 4. 디렉토리 규칙

```
app/src/main/java/com/mediinbusan/app/
├── MediInBusanApp.kt, MainActivity.kt   # 앱 진입점
├── core/       # 여러 feature가 공유하는 인프라 (network, database, datastore, navigation, designsystem, ui, common, i18n)
├── data/       # 도메인별 리포지토리 구현 + DTO + 매퍼
│              #   hospital, place(=웰니스/주변 장소), tourism(관광 카탈로그), route(자동차 경로),
│              #   guide, document(OCR), diagnosischat(AI 챗), favorite, recent, searchhistory, di
├── domain/     # 실제 비즈니스 로직이 있는 UseCase만 (nearby 거리정렬, course 코스 조립,
│              #   recommendation 홈 추천 점수화, tourism 카탈로그 추천·핫플레이스 랭킹·추천 코스 생성)
└── feature/    # 화면 단위 패키지 (Screen + ViewModel + UiState 3종 세트)
```

- **feature 패키지는 서로를 직접 import하지 않는다.** 화면 간 이동은 반드시 `core/navigation/MediInBusanNavHost.kt`를 통해서만 연결한다. (나중에 멀티모듈로 쪼갤 때 마찰을 줄이기 위한 규칙.)
- 단순 조회/저장 화면은 ViewModel → Repository 직접 호출. `domain/`은 실제 계산/조합 로직이 있는 곳에만 존재한다.
- 리포지토리 인터페이스 → 구현 바인딩은 `data/di/RepositoryModule.kt` 한 곳에 모은다.
- Hospital/Place/GuideStep은 Room 엔티티가 **아니다**(매 세션 API/정적 데이터에서 조회). Room에는 Favorite/RecentlyViewed/SearchHistory만 있다(`core/database/AppDatabase.kt`, 현재 version 5).
- Room 스키마를 바꿀 때는 `core/database/Migrations.kt`에 실제 `Migration`을 작성한다 — 지금까지의 변경은 전부 컬럼 추가라 destructive fallback 없이 로컬 즐겨찾기·최근 본 항목을 보존해 왔다. 이 관례를 깨지 말 것.

## 5. 네비게이션 관례 (여기 어긋나면 백스택이 조용히 깨진다)

`core/navigation/Route.kt`의 함수 주석에 실제로 겪은 버그가 기록돼 있다. 요약:

- **탭 전환은 반드시 `navigateToTab()`으로.** 하단 탭 클릭이든 Home 카드 진입이든 예외 없이 이 함수를 쓴다(`popUpTo(Route.Home){saveState}` + `launchSingleTop` + `restoreState`). 한쪽만 순수 `navigate()`를 쓰면 저장된 상태가 `restoreState`로 소비되지 않고 계속 쌓여 "홈" 탭이 홈으로 돌아가지 못한다. `popUpTo` 대상이 `graph.findStartDestination()`(=Splash)이 아니라 `Route.Home`인 이유도 그 주석에 있다.
- **push 라우트(즐겨찾기·최근 본 항목·자가진단)에서 탭으로 나갈 때는 `navigateToTabLeavingCurrent()`.** `navigateToTab`을 그대로 쓰면 그 push 화면이 홈 탭의 스택으로 저장되어, 나중에 "홈"을 눌렀을 때 즐겨찾기가 되살아난다(실제로 겪은 버그).
- **`Route`에 인자를 늘리기 전에 Pending 핸드오프를 먼저 검토한다.** `HospitalSearchList`는 필터·검색 포커스 요청을 `core/common/PendingHospitalSearchEntry`로, `TourismCatalogItemDetail`은 선택 항목을 `core/common/PendingTourismCatalogItem`으로 넘긴다. 백스택 저장/복원 과정에서 Route 인자가 무시되는 문제와, 모든 진입 경로를 `navigateToTab` 하나로 통일하기 위한 구조다.
- `DocumentCapture` → `DocumentScan`처럼 결과를 되돌려줄 때는 이전 백스택 엔트리의 `SavedStateHandle`에 넣고 pop한다(키: `core/navigation/Route.kt`의 `CapturedImageUriKey`).
- Scaffold의 `innerPadding`을 NavHost 전체에 매달지 않는다 — 하단바가 보이는 화면이 각자 `core/ui`의 `BottomNavBarHeight`만큼 직접 여백을 둔다(이유는 `MediInBusanApp.kt` 주석). NavHost 전환 애니메이션도 의도적으로 꺼져 있다.

## 6. 다국어(i18n) 문자열 관리

화면에 보이는 정적 UI 문구는 하드코딩하지 않고 `core/i18n/`의 구조를 따른다:

- `core/i18n/AppStrings.kt` — 화면별 `XxxStrings` 데이터 클래스를 전부 묶는 루트. Composable에서는 `LocalAppStrings`(CompositionLocal)로 읽고, 언어 코드만 있는 비 Composable 컨텍스트에서는 `appStringsFor(languageCode)`를 쓴다.
- 화면(또는 화면 그룹)마다 `core/i18n/XxxStrings.kt` 하나 — `data class XxxStrings(...)`에 `companion object { val Ko = ...; val En = ...; val Zh = ...; val Ja = ... }` 4개 언어를 전부 채운다(지원 언어는 `core/datastore/SupportedLanguage.kt`의 `KO/EN/ZH/JA` 4종 — 문서 곳곳의 "5개 언어" 표현은 오기다).
- 새 화면에 문자열을 추가할 때: ① 해당 화면에 아직 `XxxStrings.kt`가 없으면 새로 만들고 `AppStrings`에 필드 추가 + 4개 언어 값 채움, ② 이미 있으면 필드만 추가, ③ 다른 화면과 뜻이 겹치는 문구(뒤로가기, 검색 등)는 새로 만들지 말고 `CommonStrings`나 해당 화면의 기존 필드를 재사용한다.
- **가이드(F-008) 본문 콘텐츠도 여기에 있다.** `GuideStrings.kt`는 2000줄이 넘는 STEP 01~06 실제 콘텐츠 × 4개 언어다 — 데이터가 아니라 i18n 문자열로 관리하는 의도적 선택이고, `data/guide/GuideRepositoryImpl.kt`는 이 값을 읽어 조립할 뿐이다. 가이드 문구 수정은 백엔드가 아니라 이 파일(+ `feature/guide`의 `*ContentMapper`)에서 한다.
- `MedicalCategory.label`(한국어 원문)은 화면 표시용이 아니라 필터 선택 상태/서버 파라미터의 **식별자**로 계속 쓰인다 — 화면에 그릴 때만 `MedicalCategory.translatedLabel(language)`(`core/i18n/MedicalCategoryStrings.kt`)로 변환한다.
- 병원/장소 이름·주소 같은 API 응답 데이터 자체는 이 시스템의 대상이 아니다(원문 그대로 표시).

## 7. 민감정보 취급 관례 (의료 데이터 — 임의로 완화하지 말 것)

이 앱은 진단서·처방전 원문과 건강 상태 서술을 다룬다. 두 곳에 방어가 들어가 있다:

- **로깅**: `core/network/SensitivePathLoggingInterceptor.kt` — `/documents/ocr`, `/diagnosis-chat` 경로는 디버그 빌드에서도 BODY 로깅에서 완전히 제외한다. 본문 로깅은 **네트워크** 인터셉터여야 한다(애플리케이션 인터셉터는 최초 요청 경로로 한 번만 민감도를 판정해서, 비민감 경로가 민감 경로로 리디렉션되면 최종 본문이 그대로 찍힌다). 반대로 호출 단위 BASIC 로그는 애플리케이션 인터셉터여야 한다(연결 자체가 실패한 요청도 남기려면). 민감 엔드포인트를 추가하면 `NetworkModule.SENSITIVE_LOG_PATHS`에 같이 넣을 것.
- **화면/클립보드**: `feature/documentscan/SensitiveTextMasking.kt` — 주민(외국인)등록번호와 `010` 휴대전화는 기본 마스킹이고 사용자가 눈 아이콘으로 해제한다. 병원 대표번호(`051-`, `1588-` 등)는 환자가 실제로 걸어야 하므로 가리지 않는다(`010` 접두사가 그 둘을 가르는 기준). 서버에 원문이 저장되지 않는다는 전제 위에서, 남은 노출 경로가 화면과 클립보드 둘뿐이라 이 파일이 그 둘을 모두 막는다.

## 8. 빌드 환경 living note (중요 — 최신 상태 유지할 것)

이 프로젝트는 **AGP 9.0.1 / Gradle 9.2.1 / Kotlin 2.1.20 / compileSdk 36(minorApiLevel 1) / minSdk 24 / JVM 17**이라는 매우 최신 조합을 사용한다 (2026년 1월 AGP 9.0.1 릴리스 기준). 스캐폴드 단계와 이후 개발 과정에서 실제로 겪은 호환성 이슈와 해결책:

1. **`org.jetbrains.kotlin.android` 플러그인을 적용하면 안 된다.** AGP 9.0부터 Kotlin 지원이 내장되어 있고, 별도 플러그인을 적용하면 `Cannot add extension with name 'kotlin'` 에러가 난다. `org.jetbrains.kotlin.plugin.compose`, `org.jetbrains.kotlin.plugin.serialization`은 여전히 별도로 적용해야 한다.
   - 참고: [Migrate to built-in Kotlin (공식)](https://developer.android.com/build/migrate-to-built-in-kotlin), [AGP 9.0.1 release notes](https://developer.android.com/build/releases/agp-9-0-0-release-notes)
2. **Hilt는 2.59 이상**을 써야 한다. 2.58 이하는 AGP 9.x의 새 DSL(`BaseExtension` 제거)과 호환되지 않아 `Android BaseExtension not found` 에러가 난다.
   - 참고: [Hilt Gradle Plugin 2.58 is incompatible with AGP 9 (google/dagger#5083)](https://github.com/google/dagger/issues/5083), [Hilt Gradle Plugin does not work with AGP 9.0.0-alpha04 (google/dagger#4944)](https://github.com/google/dagger/issues/4944)
3. **KSP 2.1.20-1.0.31**이 생성 소스를 등록할 때 구식 `kotlin.sourceSets` DSL을 사용해서 AGP의 built-in Kotlin이 이를 거부한다. 현재 `gradle.properties`에 `android.disallowKotlinSourceSets=false`로 임시 우회 중 — 이 프로젝트의 Kotlin 버전에 맞으면서 `android.sourceSets` DSL을 네이티브로 쓰는 KSP 릴리스가 나오면 이 플래그를 제거하고 마이그레이션할 것.
   - 참고: [KSP uses kotlin.sourceSets DSL when using AGP Built-In Kotlin (google/ksp#2729)](https://github.com/google/ksp/issues/2729)
4. **`androidx.core:core-ktx`는 1.18.0에 고정.** 1.19.0은 compileSdk 37 + AGP 9.1.0을 요구해 현재 compileSdk 36 / AGP 9.0.1 조합과 맞지 않는다 (`CheckAarMetadataWorkAction` 에러). 버전 확인은 `https://dl.google.com/dl/android/maven2/androidx/core/core-ktx/maven-metadata.xml`.
5. **Kakao Map SDK는 2.13.5에 고정 — 최신이 아니라 이 프로젝트에서 동작이 확인된 버전이다.** 2026-09-15 기준 devrepo.kakao.com의 최신은 **2.15.2**이고(2.13.5 이후 2.14.0/2.14.1/2.14.2/2.15.0/2.15.1/2.15.2가 나왔다), 올릴 때는 아래 7번 규칙대로 한 단계씩 검증할 것 — 특히 `abiFilters`가 arm64-v8a 하나로 묶여 있어(위 빌드 명령 섹션) 네이티브 라이브러리 구성이 바뀌면 설치 단계에서 먼저 드러난다. 버전은 반드시 `https://devrepo.kakao.com/nexus/content/groups/public/com/kakao/maps/open/android/maven-metadata.xml`의 `<latest>`로 확인하고, 문서나 기억에 있는 번호를 믿지 말 것 — 실제로 존재하지 않는 2.14.7을 최신으로 적어둔 적이 있다(2.14 계열은 2.14.2에서 끝난다). 저장소 선언은 `settings.gradle.kts`에 있다.
   - 참고: [Kakao Android SDK - Getting started](https://developers.kakao.com/docs/latest/en/android/getting-started), [Kakao Map SDK - Getting started](https://developers.kakao.com/docs/latest/en/kakaomap/common)
6. **`androidx.compose.foundation.layout.FlowRow`(실험적 API)를 쓰지 말 것.** 이 프로젝트의 의존성 그래프에서 `androidx.compose.foundation` 버전이 뒤섞여 있어(Compose BOM이 지정한 버전과 다른 라이브러리가 끌어오는 더 최신 버전이 충돌), 컴파일은 통과하지만 실기기에서 `NoSuchMethodError: FlowRow(...)`로 **즉시 크래시**한다(컴파일 시점엔 새 오버로드로 링크되는데 실제 dex엔 없는 구버전 클래스가 들어감). 칩/배지 줄바꿈이 필요하면 `core/ui/WrapRow.kt`(순수 `Layout` API로 직접 구현한 안정판 대체 컴포넌트)를 대신 쓴다.
7. 의존성 버전을 올릴 때는 항상 `./gradlew.bat :app:assembleDebug --stacktrace`로 한 단계씩 검증할 것. 이 조합 자체가 불안정하므로 여러 라이브러리를 동시에 올리면 원인 파악이 어렵다. 실험적(`@ExperimentalXxxApi`) Compose API를 새로 도입할 때는 컴파일 통과만으로 안전하다고 판단하지 말고, 실기기/에뮬레이터에서 실제로 진입시켜 확인할 것 — 위 6번이 그 예다. `:app`에 자동 테스트가 없다는 점이 이 규칙을 더 중요하게 만든다.

## 9. 백엔드 연동 (`backend/`)

Android 앱은 한국관광공사 OpenAPI나 data.go.kr을 **직접** 호출하지 않는다 — 전부 자체 Spring Boot 백엔드(`backend/`, 모노레포 형제 프로젝트)를 통해서만 데이터를 받는다. `core/network/NetworkModule.kt`에서 6개 API 인터페이스(`HospitalApi`, `TourismApi`, `TourismCatalogApi`, `DrivingRouteApi`, `DocumentOcrApi`, `DiagnosisChatApi`)를 전부 `BuildConfig.MEDIINBUSAN_API_BASE_URL`로 각각 따로 빌드한다. 같은 파일의 `TOUR_API_BASE_URL = "https://apis.data.go.kr/"`와 그것을 baseUrl로 쓰는 공용 `provideRetrofit`은 **현재 아무도 주입받지 않는 잔재**다.

백엔드 쪽 실제 구현 상태(자세한 건 `backend/CLAUDE.md` 참고):
- **병원(`hospital/`)** — 구현됨. `data/hospital/HospitalRepositoryImpl.kt`가 호출한다(122개 큐레이션된 병원, Flyway 시드).
- **웰니스/주변 장소(`wellness/`)** — 구현됨. `TourismApi`/`data/place/PlaceRepositoryImpl.kt`가 호출하는 대상이 사실 이 `wellness/` 엔드포인트다(백엔드 쪽 패키지명이 `place`가 아니라 `wellness`인 점 주의). 서버가 Haversine으로 거리 계산·반경 필터링까지 끝내서 내려준다.
- **관광 카탈로그·자동차 경로** — 이것도 백엔드 `wellness/` 아래에 붙어 있다: `GET api/wellness/tourism/catalog/{category}`, `GET api/wellness/tourism/matched-place`(`data/tourism`), `POST api/wellness/routes`(`data/route`). 관광 기능을 찾을 때 백엔드의 `tourism` 패키지를 뒤지지 말 것 — 없다.
- **문서 OCR/번역(`document/`)** — 구현됨. `data/document/DocumentOcrRepositoryImpl.kt`가 호출, CLOVA OCR + Papago 번역 프록시.
- **AI 진단 챗(`diagnosischat/`)** — 구현됨. `data/diagnosischat/DiagnosisChatRepositoryImpl.kt`가 호출, Gemini 프록시(구조화 출력 + 레이트리밋 인터셉터). 결과 타입(TYPE A~E)은 Android `feature/selfdiagnosis/DiagnosisResultType.kt` ↔ 백엔드 `diagnosischat/domain/DiagnosisResultType.java` 양쪽에 있다.
- **가이드(`guide/`), `place/`** — 백엔드에 `package-info.java` 플레이스홀더만 있고 미구현. 가이드 콘텐츠는 앱 내부 정적 콘텐츠(`core/i18n/GuideStrings.kt`, §6)로 이미 채워져 있어서 백엔드가 급하지 않다. `place/` 관련 요청은 이미 `wellness/`가 커버 중일 수 있으니 새로 만들기 전에 확인.

**쿼리 파라미터 이름은 양쪽이 글자까지 같아야 한다 — 틀리면 오류 없이 기능만 죽는다.** Spring은 모르는 파라미터를 조용히 무시하고 `defaultValue`를 쓰므로, 이름이 어긋나면 200 OK에 기본값 응답이 와서 추적이 어렵다. 실제로 `GET /api/wellness/places`만 `lang`이었고 Android는 `language`를 보내서, 지도 전체 브라우징의 장소 이름·주소가 다른 언어에서도 한국어로 나오고 "번역된 장소만" 필터가 아무것도 걸러내지 못했다(`WellnessControllerLanguageParamTest`가 이 이름을 못박는다). 현재 규약은 **언어 파라미터가 기능마다 다르다** — 병원(`/api/hospitals/{regNo}`)은 `lang`, 웰니스 3종(`/api/wellness/places`, `/places/{contentId}`, `/hospitals/{regNo}/places`)은 `language`다. 새 엔드포인트를 추가할 때 그 기능의 기존 이름을 따를 것. `/api/wellness/places`는 이름을 바로잡기 전의 `lang`도 별칭으로 계속 받는다(둘 다 오면 `language`가 이김) — 같은 방식으로 조용히 깨지는 길을 남기지 않기 위한 것이고, 새 호출부는 `language`를 쓴다.

**양쪽을 같이 고쳐야 하는 enum 쌍:**
- `core/common/MedicalCategory.kt`(Android) ↔ `MedicalSpecialty`(백엔드) — 한쪽만 바꾸면 필터가 조용히 깨진다.
- `data/place/PlaceCategory.kt`(Android) ↔ `wellness/domain/WellnessPlaceCategory.java`(백엔드) — 장소 세부 분류(백화점/전통시장/면세점 등, TourAPI cat3 기반). 한쪽만 바꾸면 앱이 모르는 이름을 받아 조용히 `OTHER`로 떨어진다(목록이 깨지진 않고 세분화만 사라진다). 분류의 근거인 cat3 원본 코드는 백엔드 `wellness_place.category_code`에 그대로 저장되고, 코드→분류 매핑은 수집 시점이 아니라 응답 생성 시점(`WellnessDtoMapper.categoryOf`)에 일어난다 — 매핑이 틀려도 재수집 없이 코드 배포만으로 고칠 수 있게 한 구조다.

Kakao Map은 실제로 렌더링된다(`core/ui/KakaoMapView.kt`) — `KAKAO_NATIVE_APP_KEY`가 유효하면 실제 타일·마커가 보이고, 없으면 `MapUnavailableFallback` 폴백 화면만 뜬다.

## 10. 시크릿·로컬 설정 관리

`local.properties`(gitignore됨)에 다음을 넣는다. `app/build.gradle.kts`의 `secret()`은 `local.properties` → 환경변수 → 빈 문자열 순으로 폴백하므로 클론 직후에도 디버그 빌드는 된다.
- `TOURISM_API_SERVICE_KEY`, `KAKAO_NATIVE_APP_KEY` — `BuildConfig` 필드와 매니페스트 `${KAKAO_NATIVE_APP_KEY}` 플레이스홀더로 주입.
- `MEDIINBUSAN_API_BASE_URL` — 자체 백엔드 주소. debug 기본값은 `http://10.0.2.2:8080/`(Android 에뮬레이터에서 호스트 PC의 localhost를 가리키는 별칭), release 기본값은 `https://ownrefrigerator.site/`이다. 실기기에서 로컬 서버를 테스트하거나 다른 서버를 사용할 때만 `local.properties` 또는 환경변수로 덮어쓴다. 끝의 `/`는 빌드 설정에서 자동 보정한다.

릴리스 서명 정보는 별도로 `keystore.properties`(역시 gitignore됨)에 둔다 — 위 빌드 명령 섹션 참고.

**절대 키를 코드/매니페스트에 하드코딩하지 말 것.**

## 11. 앱 접근권한 고지·동의 (법정 요구사항 — 임의로 걷어내지 말 것)

원스토어 검증 의견(2026-09-02, OA01008717)에서 "카메라 접근권한을 요구하면서 사전 고지·동의 절차가 없다"는 지적을 받아 추가한 구조다. 근거는 정보통신망법 제22조의2와 방송미디어통신위원회 「앱 접근권한 동의 가이드라인」이고, 요구사항은 ① 필수/선택 접근권한 구분, ② 권한별 필요 기능과 목적, ③ 선택 권한은 동의하지 않아도 서비스 이용이 가능하다는 사실, ④ 철회 방법 안내 네 가지다.

구현 위치:
- `core/i18n/PermissionNoticeStrings.kt` — 고지 문구 전부(4개 언어). 설정 리스트 행 문구까지 여기 있다(다른 행은 `SettingsStrings`에 있지만, 법정 고지 문구는 화면·다이얼로그와 한 파일에서 관리한다).
- `feature/permission/AppPermissionNoticeScreen.kt` — 고지 화면. `Route.AppPermissionNotice(fromSplash = true)`(최초 실행)면 뒤로가기 없이 "확인했습니다"로만 진행하고, 그때 `UserPreferencesKeys.PERMISSION_NOTICE_ACKNOWLEDGED`를 저장해 다음 실행부터는 뜨지 않는다. `SplashViewModel`이 이 값을 읽어 Home/고지 화면 중 어디로 보낼지 정한다.
- `feature/documentscan/CameraPermissionDialogs.kt` — 시스템 권한 팝업 **직전** 사전 고지 다이얼로그와, 영구 거부 상태에서 뜨는 설정 안내 다이얼로그. `shouldShowRequestPermissionRationale`은 "아직 안 물어봄"과 "다시 묻지 않음"을 둘 다 false로 주므로 `UserPreferencesKeys.CAMERA_PERMISSION_REQUESTED`(요청 이력)와 함께 봐야 두 상태가 갈린다.
- `core/ui/AppSettingsIntents.kt` — 철회 경로(시스템 앱 상세 설정) 열기.

새 런타임 권한을 추가한다면 위 네 곳의 문구·분기를 반드시 같이 갱신해야 한다(고지 없는 권한 요구는 재검증에서 다시 걸린다).

## 12. 아직 구현되지 않은 것 (TODO로 명시되어 있음)

- F-011 병원 좌표 기준 실제 거리 계산 (`domain/nearby/GetNearbyPlacesSortedByDistanceUseCase.kt`는 여전히 정렬 없이 그대로 통과시킨다 — `core/common/GeoDistance.kt`의 `haversineDistanceMeters`가 이미 있다. 다만 백엔드 `wellness/`가 이미 거리 정렬해서 내려주므로 실질적으로 중복일 수 있다 — 구현 전에 이 UseCase 자체를 지우는 게 맞는지 먼저 확인할 것)
- F-014 웰니스 코스 실제 큐레이션 로직 (`domain/course/AssembleWellnessCourseUseCase.kt`는 데모용 임시 로직)
- F-016 최근 본 항목을 홈/즐겨찾기 화면에 직접 노출하는 카드 (전용 화면·데이터 계층은 이미 있음 — §3 참고)
- 백엔드 `guide/`, `place/` 패키지 구현 (현재 `package-info.java`만 존재 — §9 참고)
- 하단 탭 아이콘이 아직 `material-icons-extended`다. 디자인팀 PNG 확정 시 교체하고 그 의존성 재검토 (`MediInBusanApp.kt` TODO)
- `Route.MapView` 하나가 "전역 지도"/"병원 상세 지도"/"코스 동선" 세 의미를 겸해서 하단바 노출 판정에 인자까지 봐야 한다 — `MapOverview`/`MapDetail`로 route를 쪼개는 것을 검토 (`MediInBusanApp.kt` TODO)
