# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

이 문서는 이 저장소에서 작업하는 향후 Claude Code 세션(및 팀원)을 위한 컨텍스트다. "왜 이렇게 만들었는지"에 대한 상세 설명은 `docs/ARCHITECTURE.md`를 참고한다.

**이 저장소는 모노레포다**: 루트가 이 Android 클라이언트(`:app`, 이 문서가 다루는 대상)이고, `backend/`는 별도 Gradle 루트 프로젝트(Spring Boot, 자체 `gradlew`/`settings.gradle`)로 독립적으로 빌드·실행된다. `backend/`에서 작업하거나 API 계약을 바꿀 때는 반드시 `backend/CLAUDE.md`를 같이 읽을 것 — 특히 Android `core/common/MedicalCategory.kt`와 백엔드 `MedicalSpecialty` enum은 값이 **동일하게 유지되어야 한다**(한쪽을 바꾸면 다른 쪽도 확인).

## 빌드/실행 명령

```bash
./gradlew.bat :app:assembleDebug --stacktrace   # 디버그 APK 빌드 (의존성 버전 검증 시 기본 명령)
./gradlew.bat :app:compileDebugKotlin           # 컴파일만 빠르게 확인
./gradlew.bat :app:clean :app:assembleDebug     # dex 캐시 이슈 의심될 때 클린 빌드
```

- 별도 lint/format 태스크는 구성되어 있지 않다 — 위 빌드 명령이 사실상의 정합성 게이트다.
- 단위 테스트(`app/src/test`)·계측 테스트(`app/src/androidTest`)는 현재 스캐폴드 예제(`ExampleUnitTest`, `ExampleInstrumentedTest`)만 있고 실제 테스트는 없다.
- 실기기(USB/같은 Wi-Fi)로 병원·웰니스·문서스캔 데이터를 확인하려면 `backend/`를 로컬에서 먼저 띄워야 한다(`cd backend && ./gradlew.bat bootRun`, 기본 프로필은 Docker 없이 H2로 동작). 아래 §8 참고.

## 1. 제품 요약

**메디인부산(MediIn Busan)**은 부산을 방문하는 외국인 의료관광객이 자신의 의료 목적에 맞는 부산 의료기관을 탐색하고, 의료 이용 절차와 병원 주변 관광·웰니스 정보를 함께 확인할 수 있도록 돕는 **정보 제공형** Android 앱이다.

**MVP 하드 제약 (구조적으로 존재해서는 안 됨):**
- 병원 예약/대행 기능 없음
- 진료비·상품 결제 기능 없음
- 실시간 상담/통역사 매칭 기능 없음
- 사용자 GPS 위치를 서버/API로 전송하는 위치기반 추천 없음 → `AndroidManifest.xml`에 위치 권한이 **절대 추가되어서는 안 된다.**

이 제약을 어기는 기능 요청이 들어오면, 먼저 사용자에게 MVP 범위를 벗어난다는 점을 확인시킬 것. (`backend/`에도 동일한 제약이 적용된다 — 실시간 GPS를 받는 엔드포인트를 만들지 말 것.)

## 2. 기술 스택

| 영역 | 선택 |
| --- | --- |
| UI | Jetpack Compose (Material3), XML 뷰 없음 |
| 아키텍처 | 단일 Gradle 모듈(`:app`), 기능별 패키지(`feature/*`) + 얕은 공유 계층(`core/`, `data/`, `domain/`) |
| DI | Hilt + KSP |
| 비동기 | Kotlin Coroutines + Flow |
| 네트워킹 | Retrofit2 + OkHttp + kotlinx.serialization |
| 로컬 저장 | Room(즐겨찾기·최근 본 항목·검색 기록) + DataStore Preferences(언어·온보딩·의료목적) |
| 이미지 | Coil3 |
| 지도 | Kakao Map SDK (실제 렌더링 동작함 — 아래 §6 참고) |
| 네비게이션 | Navigation Compose 타입세이프 라우트 (`core/navigation/Route.kt`) |

각 선택의 이유는 `docs/ARCHITECTURE.md` 참고.

## 3. 화면/기능 ↔ 패키지 매핑

| 화면 | 기능 ID | 패키지 |
| --- | --- | --- |
| S-01 스플래시 | F-001 | `feature/splash` |
| S-02 온보딩(언어 선택) | F-002, F-003 | `feature/languageselect` (Route 이름은 여전히 `Onboarding`) |
| S-03 홈 | - | `feature/home` (추천 병원 섹션은 `domain/recommendation` 참고) |
| S-04 의료기관 목록/검색 | F-004, F-005 | `feature/hospitalsearchlist` (Home의 의료목적 선택/의료기관 찾기/웰니스/검색바 4개 진입점이 모두 이 화면 하나로 모인다) |
| S-05 의료기관 상세 | F-006, F-007, F-009 | `feature/hospitaldetail` |
| S-06 의료 이용 가이드 | F-008 | `feature/guide` (STEP별 세부 하위 라우트가 다수 있다 — `core/navigation/Route.kt` 참고) |
| S-07 주변 관광·웰니스 | F-011, F-012, F-014 | `feature/nearby` (+ `domain/nearby`, `domain/course`) |
| S-08 지도 | F-010, F-013 | `feature/map` |
| S-09 즐겨찾기 | F-015 | `feature/favorite` |
| S-10 설정/출처 | F-018 | `feature/settings` (하위 라우트: `NotificationSettings`, `SettingsInfoDetail`, `RecentlyViewed`) |
| 최근 본 항목 | F-016 | `feature/recent` + `data/recent` — 전용 화면은 이미 있고 설정(S-10) 하위에서 진입한다. 원래 계획이던 "홈/즐겨찾기 화면에 직접 노출"은 아직 없다. |
| 공통 | F-019 오류/빈 상태 처리 | `core/ui` (`LoadingState`, `ErrorState`, `EmptyState`, `AsyncImageBox`) |
| 외부 지도 연결 | F-017 | `core/ui/MapIntents.kt`의 `launchExternalDirections` — 구현됨(`geo:` 인텐트로 기기 기본 지도 앱 실행) |
| 문서 스캔(OCR·번역) | 원 기능명세에 없던 추가 기능 | `feature/documentscan` — 하단 탭 5번째. `backend/document`(CLOVA OCR + Papago) 호출 |
| 자가진단 | 원 기능명세에 없던 추가 기능 | `feature/selfdiagnosis` — 온보딩 직후 또는 독립 진입, `Route.SelfDiagnosis(fromOnboarding)` |

하단 탭바는 5개: 홈 / 의료기관 / 가이드 / 지도 / 문서스캔 (`core/navigation/MediInBusanApp.kt`의 `bottomNavTabs`). 설정·즐겨찾기·자가진단 등은 탭이 아니라 다른 화면에서 진입하는 push 라우트다.

## 4. 디렉토리 규칙

```
app/src/main/java/com/mediinbusan/app/
├── MediInBusanApp.kt, MainActivity.kt   # 앱 진입점
├── core/       # 여러 feature가 공유하는 인프라 (network, database, datastore, navigation, designsystem, ui, common, i18n)
├── data/       # 도메인별 리포지토리 구현 + DTO + 매퍼 (hospital, place, guide, document, favorite, recent, searchhistory)
├── domain/     # 실제 비즈니스 로직이 있는 UseCase만 (nearby 거리정렬, course 큐레이션, recommendation 홈 추천 점수화)
└── feature/    # 화면 단위 패키지 (Screen + ViewModel + UiState 3종 세트)
```

- **feature 패키지는 서로를 직접 import하지 않는다.** 화면 간 이동은 반드시 `core/navigation/MediInBusanNavHost.kt`를 통해서만 연결한다. (나중에 멀티모듈로 쪼갤 때 마찰을 줄이기 위한 규칙.)
- 단순 조회/저장 화면은 ViewModel → Repository 직접 호출. `domain/`은 실제 계산/조합 로직이 있는 곳(F-011 거리 정렬, F-014 코스 큐레이션, 홈 추천 점수화)에만 존재한다.
- Hospital/Place/GuideStep은 Room 엔티티가 **아니다**(매 세션 API/정적 데이터에서 조회). Room에는 Favorite/RecentlyViewed/SearchHistory만 있다.

## 5. 다국어(i18n) 문자열 관리

화면에 보이는 정적 UI 문구는 하드코딩하지 않고 `core/i18n/`의 구조를 따른다:

- `core/i18n/AppStrings.kt` — 화면별 `XxxStrings` 데이터 클래스를 전부 묶는 루트. `LocalAppStrings`(CompositionLocal)로 어디서든 읽는다.
- 화면(또는 화면 그룹)마다 `core/i18n/XxxStrings.kt` 하나 — `data class XxxStrings(...)`에 `companion object { val Ko = ...; val En = ...; val Zh = ...; val Ja = ... }` 4개 언어를 전부 채운다(지원 언어는 `core/datastore/SupportedLanguage.kt`의 `KO/EN/ZH/JA` 4종 — 문서 곳곳의 "5개 언어" 표현은 오기다).
- 새 화면에 문자열을 추가할 때: ① 해당 화면에 아직 `XxxStrings.kt`가 없으면 새로 만들고 `AppStrings`에 필드 추가 + 4개 언어 값 채움, ② 이미 있으면 필드만 추가, ③ 다른 화면과 뜻이 겹치는 문구(뒤로가기, 검색 등)는 새로 만들지 말고 `CommonStrings`나 해당 화면의 기존 필드를 재사용한다.
- `MedicalCategory.label`(한국어 원문)은 화면 표시용이 아니라 필터 선택 상태/서버 파라미터의 **식별자**로 계속 쓰인다 — 화면에 그릴 때만 `MedicalCategory.translatedLabel(language)`(`core/i18n/MedicalCategoryStrings.kt`)로 변환한다.
- 병원/장소 이름·주소 같은 API 응답 데이터 자체는 이 시스템의 대상이 아니다(원문 그대로 표시).

## 6. 빌드 환경 living note (중요 — 최신 상태 유지할 것)

이 프로젝트는 **AGP 9.0.1 / Gradle 9.2.1**이라는 매우 최신 조합을 사용한다 (2026년 1월 AGP 9.0.1 릴리스 기준). 스캐폴드 단계와 이후 개발 과정에서 실제로 겪은 호환성 이슈와 해결책:

1. **`org.jetbrains.kotlin.android` 플러그인을 적용하면 안 된다.** AGP 9.0부터 Kotlin 지원이 내장되어 있고, 별도 플러그인을 적용하면 `Cannot add extension with name 'kotlin'` 에러가 난다. `org.jetbrains.kotlin.plugin.compose`, `org.jetbrains.kotlin.plugin.serialization`은 여전히 별도로 적용해야 한다.
   - 참고: [Migrate to built-in Kotlin (공식)](https://developer.android.com/build/migrate-to-built-in-kotlin), [AGP 9.0.1 release notes](https://developer.android.com/build/releases/agp-9-0-0-release-notes)
2. **Hilt는 2.59 이상**을 써야 한다. 2.58 이하는 AGP 9.x의 새 DSL(`BaseExtension` 제거)과 호환되지 않아 `Android BaseExtension not found` 에러가 난다.
   - 참고: [Hilt Gradle Plugin 2.58 is incompatible with AGP 9 (google/dagger#5083)](https://github.com/google/dagger/issues/5083), [Hilt Gradle Plugin does not work with AGP 9.0.0-alpha04 (google/dagger#4944)](https://github.com/google/dagger/issues/4944)
3. **KSP 2.1.20-1.0.31**이 생성 소스를 등록할 때 구식 `kotlin.sourceSets` DSL을 사용해서 AGP의 built-in Kotlin이 이를 거부한다. 현재 `gradle.properties`에 `android.disallowKotlinSourceSets=false`로 임시 우회 중 — 이 프로젝트의 Kotlin 버전에 맞으면서 `android.sourceSets` DSL을 네이티브로 쓰는 KSP 릴리스가 나오면 이 플래그를 제거하고 마이그레이션할 것.
   - 참고: [KSP uses kotlin.sourceSets DSL when using AGP Built-In Kotlin (google/ksp#2729)](https://github.com/google/ksp/issues/2729)
4. **`androidx.core:core-ktx`는 1.18.0에 고정.** 1.19.0은 compileSdk 37 + AGP 9.1.0을 요구해 현재 compileSdk 36 / AGP 9.0.1 조합과 맞지 않는다 (`CheckAarMetadataWorkAction` 에러). 버전 확인은 `https://dl.google.com/dl/android/maven2/androidx/core/core-ktx/maven-metadata.xml`.
5. **Kakao Map SDK는 devrepo.kakao.com 기준 2.13.5가 최신** (2.14.7 같은 더 높은 버전은 존재하지 않음 — 항상 `https://devrepo.kakao.com/nexus/content/groups/public/com/kakao/maps/open/android/maven-metadata.xml`로 실제 최신 버전 확인할 것).
   - 참고: [Kakao Android SDK - Getting started](https://developers.kakao.com/docs/latest/en/android/getting-started), [Kakao Map SDK - Getting started](https://developers.kakao.com/docs/latest/en/kakaomap/common)
6. **`androidx.compose.foundation.layout.FlowRow`(실험적 API)를 쓰지 말 것.** 이 프로젝트의 의존성 그래프에서 `androidx.compose.foundation` 버전이 뒤섞여 있어(Compose BOM이 지정한 버전과 다른 라이브러리가 끌어오는 더 최신 버전이 충돌), 컴파일은 통과하지만 실기기에서 `NoSuchMethodError: FlowRow(...)`로 **즉시 크래시**한다(컴파일 시점엔 새 오버로드로 링크되는데 실제 dex엔 없는 구버전 클래스가 들어감). 칩/배지 줄바꿈이 필요하면 `core/ui/WrapRow.kt`(순수 `Layout` API로 직접 구현한 안정판 대체 컴포넌트)를 대신 쓴다.
7. 의존성 버전을 올릴 때는 항상 `./gradlew.bat :app:assembleDebug --stacktrace`로 한 단계씩 검증할 것. 이 조합 자체가 불안정하므로 여러 라이브러리를 동시에 올리면 원인 파악이 어렵다. 실험적(`@ExperimentalXxxApi`) Compose API를 새로 도입할 때는 컴파일 통과만으로 안전하다고 판단하지 말고, 실기기/에뮬레이터에서 실제로 진입시켜 확인할 것 — 위 6번이 그 예다.

## 7. 백엔드 연동 (`backend/`)

Android 앱은 한국관광공사 OpenAPI나 data.go.kr을 **직접** 호출하지 않는다 — 전부 자체 Spring Boot 백엔드(`backend/`, 모노레포 형제 프로젝트)를 통해서만 데이터를 받는다. `core/network/NetworkModule.kt`에서 `HospitalApi`/`TourismApi`/`DocumentOcrApi` 셋 다 `BuildConfig.MEDIINBUSAN_API_BASE_URL`로 Retrofit을 빌드한다(`TOUR_API_BASE_URL = "https://apis.data.go.kr/"`는 현재 미사용 상수로 남아있다).

백엔드 쪽 실제 구현 상태(자세한 건 `backend/CLAUDE.md` 참고):
- **병원(`hospital/`)** — 구현됨. `data/hospital/HospitalRepositoryImpl.kt`가 실제로 이 백엔드를 호출한다(122개 큐레이션된 병원, Flyway 시드).
- **웰니스/주변 장소(`wellness/`)** — 구현됨. `TourismApi`/`data/place/PlaceRepositoryImpl.kt`가 호출하는 대상이 사실 이 `wellness/` 엔드포인트다(백엔드 쪽 패키지명이 `place`가 아니라 `wellness`인 점 주의). 서버가 Haversine으로 거리 계산·반경 필터링까지 끝내서 내려준다.
- **문서 OCR/번역(`document/`)** — 구현됨. `data/document/DocumentOcrRepositoryImpl.kt`가 호출, CLOVA OCR + Papago 번역 프록시.
- **가이드(`guide/`)** — 백엔드에 `package-info.java` 플레이스홀더만 있고 미구현. Android `data/guide/GuideRepositoryImpl.kt`도 여전히 하드코딩된 샘플 콘텐츠를 반환한다(아래 §9).

`core/common/MedicalCategory.kt`(Android) ↔ `MedicalSpecialty`(백엔드) enum 값은 **동일하게 유지되어야 한다** — 한쪽만 바꾸면 필터가 조용히 깨진다.

Kakao Map은 실제로 렌더링된다(`core/ui/KakaoMapView.kt`) — `KAKAO_NATIVE_APP_KEY`가 유효하면 실제 타일·마커가 보이고, 없으면 `MapUnavailableFallback` 폴백 화면만 뜬다. x86_64 에뮬레이터는 `libK3fAndroid.so`가 arm64-v8a/armeabi-v7a로만 배포돼 `KakaoMapSdk.init()`이 실패한다(실기기·ARM 에뮬레이터에선 정상).

## 8. 시크릿·로컬 설정 관리

`local.properties`(gitignore됨)에 다음을 넣는다:
- `TOURISM_API_SERVICE_KEY`, `KAKAO_NATIVE_APP_KEY` — `app/build.gradle.kts`가 읽어 `BuildConfig` 필드와 매니페스트 `${KAKAO_NATIVE_APP_KEY}` 플레이스홀더로 주입. 값이 없으면 빈 문자열로 폴백해 클론 직후에도 빌드는 된다.
- `MEDIINBUSAN_API_BASE_URL` — 자체 백엔드 주소. debug 기본값은 `http://10.0.2.2:8080/`(Android 에뮬레이터에서 호스트 PC의 localhost를 가리키는 별칭), release 기본값은 `https://ownrefrigerator.site/`이다. 실기기에서 로컬 서버를 테스트하거나 다른 서버를 사용할 때만 `local.properties` 또는 환경변수로 덮어쓴다. 끝의 `/`는 빌드 설정에서 자동 보정한다.

**절대 키를 코드/매니페스트에 하드코딩하지 말 것.**

## 9. 아직 구현되지 않은 것 (TODO로 명시되어 있음)

- F-008 가이드 콘텐츠 실제 텍스트 (`data/guide/GuideRepositoryImpl.kt` — Android/백엔드 양쪽 다 미구현, §7 참고)
- F-011 병원 좌표 기준 실제 거리 계산 (`domain/nearby/GetNearbyPlacesSortedByDistanceUseCase.kt`는 여전히 정렬 없이 그대로 통과시킨다 — `core/common/GeoDistance.kt`의 `haversineDistanceMeters`가 이미 있으니 이걸 연결하면 된다)
- F-014 웰니스 코스 실제 큐레이션 로직 (`domain/course/AssembleWellnessCourseUseCase.kt`는 데모용 임시 로직)
- F-016 최근 본 항목을 홈/즐겨찾기 화면에 직접 노출하는 카드 (전용 화면·데이터 계층은 이미 있음 — §3 참고)
- 백엔드 `guide/`, `place/` 패키지 구현 (현재 `package-info.java`만 존재 — `place/` 관련 요청은 이미 `wellness/`가 커버 중일 수 있으니 새로 만들기 전에 확인)
