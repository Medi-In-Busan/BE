# 아키텍처 결정 기록 (Architecture Decision Record)

이 문서는 메디인부산 Android 앱의 디렉토리 구조와 기술 스택을 **왜** 이렇게 선택했는지 설명한다. "무엇을" 했는지는 코드와 `CLAUDE.md`를 보면 알 수 있으므로, 여기서는 결정의 배경과 트레이드오프에 집중한다.

## 1. 왜 Jetpack Compose (XML View 대신)

프로젝트 시작 시점에 XML 레이아웃이나 Fragment가 전혀 없는 완전히 빈 스켈레톤이었다. 신규 프로젝트이므로 레거시 View 시스템과의 상호운용을 고민할 필요가 없고, Compose는 현재 구글이 신규 앱에 권장하는 기본값이다. 특히 이 앱은 10개 화면(S-01~S-10)을 오가는 네비게이션이 많은 구조인데, Navigation Compose의 타입세이프 라우트(`core/navigation/Route.kt`)와 Compose의 선언형 상태 관리가 이런 구조에 잘 맞는다.

## 2. 왜 단일 모듈 + 기능별 패키지 (멀티모듈 대신)

Now in Android 같은 대형 레퍼런스는 `core-*`, `feature-*`를 별도 Gradle 모듈로 분리한다. 하지만 이는 빌드 캐싱/병렬 빌드 이득이 큰 대신 초기 설정 비용(모듈 간 API 경계 설계, 빌드 스크립트 중복 관리)도 크다. MVP 단계에서 화면 10개, 팀 규모가 작은 상황에서는 이 비용이 이득보다 크다고 판단했다.

대신 **패키지 경계 규칙**으로 멀티모듈의 이점 중 상당 부분을 미리 확보해 둔다:
- `feature/*` 패키지는 서로를 직접 import하지 않는다 — 반드시 `core/navigation`을 통해서만 연결.
- 모든 리포지토리 인터페이스는 `data/*`에 있고, feature는 인터페이스만 알고 구현은 Hilt가 주입한다.

이 규칙을 지키면 나중에 `feature/hospitallist`를 별도 Gradle 모듈로 떼어낼 때 코드 이동만 하면 되고, 순환 의존성을 풀어내는 큰 리팩터링이 필요 없다.

## 3. 왜 부분적 domain 계층 (풀 Clean Architecture 대신)

전형적인 Clean Architecture는 모든 화면 액션마다 UseCase 클래스를 하나씩 만든다. 하지만 이 앱의 화면 대부분(병원 목록 조회, 상세 조회, 즐겑찾기 토글 등)은 리포지토리 호출을 그대로 화면에 전달하는 수준이라, UseCase 계층을 강제하면 보일러플레이트만 늘어난다.

그래서 이 프로젝트는 **실제로 조합/계산 로직이 있는 두 곳에만** `domain/` UseCase를 둔다:
- `domain/nearby/GetNearbyPlacesSortedByDistanceUseCase` — F-011, 병원 좌표 기준 거리 정렬이라는 순수 계산 로직
- `domain/course/AssembleWellnessCourseUseCase` — F-014, 여러 장소를 하나의 코스로 조합하는 로직

나머지는 ViewModel이 Repository를 직접 호출한다. 이는 "화면에 로직이 없으면 계층도 만들지 않는다"는 실용적 기준이다.

## 4. 왜 Room은 즐겨찾기·최근 본 항목에만 쓰고 병원/장소 데이터엔 안 쓰는지

Favorite(F-015)와 RecentlyViewed(F-016)는 **사용자가 직접 만들어내는 로컬 상태**다 — 서버에 원본이 없고, 앱 재시작 후에도 유지되어야 하며, 조건 검색(즐겨찾기 목록 조회 등)이 필요하다. 이건 관계형 로컬 저장소가 맞는 사용처다.

반면 Hospital/Place/GuideStep은 원본이 항상 외부(OpenAPI 또는 정적 콘텐츠)에 있다. MVP 단계에서는 오프라인 캐싱 요구사항이 명세에 없으므로, 이들을 Room 엔티티로 만들면 스키마 마이그레이션 관리 비용만 생기고 얻는 이득이 없다. 캐싱이 실제로 필요해지면(예: 오프라인 지원 요구가 생기면) 그때 `data/hospital`, `data/place`에 Room 캐시 레이어를 추가하면 된다 — 지금 미리 만들지 않는다.

## 5. 왜 DataStore Preferences (언어/온보딩/의료목적)

F-001(최초 실행 여부), F-002(언어 선택), F-003(의료 목적 선택)는 앱 실행마다 딱 한 번씩만 읽는 스칼라 값 3개다. 이런 값에 Room 테이블을 쓰면 스키마 정의, DAO, 마이그레이션까지 관리해야 하는데 값 3개를 위해서는 과하다. DataStore Preferences는 key-value 저장에 최적화되어 있고 Flow 기반이라 Compose와도 자연스럽게 연결된다.

## 6. 왜 kotlinx.serialization (Moshi/Gson 대신)

Navigation Compose의 타입세이프 라우트(`Route.kt`가 `@Serializable sealed interface`인 이유)가 kotlinx.serialization을 요구한다. 이미 네비게이션 계층에서 이 라이브러리를 쓰고 있으므로, OpenAPI 응답 DTO 파싱에도 같은 라이브러리를 재사용해 리플렉션/코드젠 시스템을 두 개 유지하지 않는다.

## 7. 왜 Hilt + KSP (kapt 대신)

kapt는 성능 면에서 사실상 레거시 취급을 받고, KSP가 현재 Room/Hilt가 최적화 대상으로 삼는 어노테이션 프로세싱 백엔드다. 다만 이 프로젝트가 쓰는 AGP 9.0.1의 "내장 Kotlin(built-in Kotlin)" 지원과 KSP 2.1.20-1.0.31 사이에 소스셋 등록 방식 충돌이 있어 `gradle.properties`에 임시 우회 플래그(`android.disallowKotlinSourceSets=false`)를 넣어둔 상태다. 이 부분은 KSP가 새 DSL을 네이티브 지원하는 버전을 내면 정리해야 한다 (`CLAUDE.md` §5 참고).

## 8. 왜 Kakao Map

메디인부산의 대상은 부산을 방문하는 외국인이지만, 실제로 지도에 표시되는 대상(병원, 관광지, 음식점)은 전부 한국 국내 주소·POI다. Kakao Map은 국내 주소 체계와 로컬 POI 데이터 정확도가 가장 높은 지도 SDK 중 하나이며, 이는 외국인 사용자가 실제로 그 장소를 찾아갈 수 있어야 한다는 요구(F-010, F-013)와 직결된다. Google Maps는 글로벌 사용자에게 더 친숙하다는 장점이 있지만, 이번 MVP는 정확한 국내 위치 표시를 우선순위로 판단해 Kakao Map을 선택했다.

## 9. 시크릿 관리 방식

`local.properties`(이미 gitignore되어 있던 파일)에 API 키를 넣고, `app/build.gradle.kts`가 이를 읽어 `BuildConfig` 필드와 매니페스트 placeholder로 주입한다. 별도의 원격 설정 서비스나 암호화 저장소를 도입하지 않은 이유는, MVP 단계에서 이미 존재하는 gitignore된 파일 하나로 "커밋되지 않음"이라는 요구를 충분히 만족시키기 때문이다 — 이보다 무거운 구성은 지금 단계에서 과한 엔지니어링이다.

## 10. 이번 스캐폴드 범위에서 의도적으로 제외한 것

다음은 "잊어버린 것"이 아니라 **의도적으로 다음 단계로 미룬 것**이다:
- 한국관광공사 OpenAPI 실제 연동 (정확한 오퍼레이션명이 미확정이므로 잘못된 값을 하드코딩하는 것보다 TODO로 남기는 편이 안전하다)
- Kakao Map 실제 렌더링 및 앱 키 연동
- F-011 거리 계산, F-014 코스 큐레이션의 실제 알고리즘

이렇게 나눈 이유는 "스캐폴딩"과 "기능 구현"을 분리해, 매 단계마다 빌드가 그린 상태를 유지하면서 리뷰 가능한 단위로 진행하기 위함이다. 실제 API 문서 없이 엔드포인트/필드명을 추측해서 구현하면, 나중에 "그럴듯하게 작동하는 것처럼 보이지만 실제로는 틀린 코드"를 걷어내야 하는 비용이 더 크다.
