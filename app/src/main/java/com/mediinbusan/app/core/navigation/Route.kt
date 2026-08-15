package com.mediinbusan.app.core.navigation

import com.mediinbusan.app.data.guide.GuidePhase
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

/**
 * Navigation Compose 2.8+ 타입세이프 라우트. 화면 ID(S-01~S-10)와 1:1로 대응한다.
 */
@Serializable
sealed interface Route {
    @Serializable
    data object Splash : Route // S-01

    @Serializable
    data object Onboarding : Route // S-02

    @Serializable
    data object Home : Route // S-03

    // S-04, 구 HospitalList + Search 통합. initialSearchFocus=true면 Home 검색바를 누른 경우처럼
    // 결과 목록 대신 검색 입력창에 바로 포커스를 줘서 최근 검색어/자동완성 패널(SearchAssistPanel)을
    // 먼저 보여준다. medicalPurpose 필터는 여기 args로 안 싣는다 — core/common/
    // PendingHospitalSearchFilter 참고(Nav 백스택 저장/복원 과정에서 무시될 수 있는 문제 때문).
    @Serializable
    data class HospitalSearchList(val initialSearchFocus: Boolean = false) : Route

    @Serializable
    data class HospitalDetail(val hospitalId: String) : Route // S-05

    @Serializable
    data object Guide : Route // S-06

    @Serializable
    data class GuideStepDetail(val phase: GuidePhase, val title: String) : Route // S-06 하위 STEP 상세

    @Serializable
    data object VisaEntryCheckDetail : Route // S-06 STEP 01 하위 "비자·입국 조건 확인" 상세

    @Serializable
    data object InsuranceDocumentsDetail : Route // S-06 STEP 01 하위 "보험·서류 준비" 상세

    @Serializable
    data object HospitalInquiryDetail : Route // S-06 STEP 01 하위 "병원 문의 전 정보 정리" 상세

    @Serializable
    data object PreInquiryInformationDetail : Route // S-06 STEP 02 하위 "문의 전 전달할 정보 정리" 상세

    @Serializable
    data object PassportReservationInfoDetail : Route // S-06 STEP 03 하위 "여권·예약정보 준비" 상세

    @Serializable
    data object MedicalRecordsTestResultsDetail : Route // S-06 STEP 03 하위 "기존 진단서·검사결과 준비" 상세

    @Serializable
    data object HospitalLocationCheckinDetail : Route // S-06 STEP 03 하위 "병원 위치와 접수 절차 확인" 상세

    @Serializable
    data object TotalCostCoverageCheckDetail : Route // S-06 STEP 05 하위 "총 비용과 포함 항목 확인" 상세

    @Serializable
    data object PaymentMethodCheckDetail : Route // S-06 STEP 05 하위 "결제 가능 수단 확인" 상세

    @Serializable
    data object ReceiptInsuranceDocumentsDetail : Route // S-06 STEP 05 하위 "영수증·보험 청구 서류 확인" 상세

    @Serializable
    data object MedicationScheduleDetail : Route // S-06 STEP 06 하위 "약 복용 방법 확인" 상세

    @Serializable
    data object PostTreatmentPrecautionsDetail : Route // S-06 STEP 06 하위 "진료 후 주의사항 확인" 상세

    @Serializable
    data object EnglishDocumentsResultsDetail : Route // S-06 STEP 06 하위 "영문 서류·검사결과 수령 확인" 상세

    @Serializable
    data object AirportDeparturePreparationDetail : Route // S-06 STEP 06 하위 "귀국 전 반입·공항 준비" 상세

    @Serializable
    data class Nearby(val hospitalId: String) : Route // S-07

    @Serializable
    data class PlaceDetail(val placeId: String) : Route // S-07 상세

    @Serializable
    data class MapView(val hospitalId: String? = null) : Route // S-08, hospitalId=null이면 전체 병원 지도 모드

    @Serializable
    data object Favorite : Route // S-09

    @Serializable
    data object Settings : Route // S-10

    @Serializable
    data class SettingsInfoDetail(val infoId: String) : Route // S-10 하위 정적 정보 페이지 (이용안내/개인정보처리방침/이용약관)

    @Serializable
    data object NotificationSettings : Route // S-10 하위 알림 설정 (로컬 토글, 실제 푸시 인프라 없음)

    @Serializable
    data object RecentlyViewed : Route // S-10 하위 최근 본 항목 (F-016)

    // 준비 유형 진단(TYPE A~E). Home 퀵링크·설정에서 재진입 시 fromOnboarding=false,
    // 최초 실행 흐름(Splash -> 언어선택 -> 진단 -> Home) 중에는 true.
    @Serializable
    data class SelfDiagnosis(val fromOnboarding: Boolean = false) : Route

    // 진단서·처방전 OCR 번역(문서 스캔). 바텀바 5번째 탭. OCR/번역 백엔드 연동 전, 이미지
    // 촬영·선택까지만 우선 배선한다 — 관련 이슈 참고.
    @Serializable
    data object DocumentScan : Route
}

/**
 * 탭 전환 표준 패턴: Route.Home까지 스택을 정리하되 각 탭의 상태는 보존한다.
 * graph.findStartDestination()은 Splash를 가리키는데, Splash는 앱 시작 시
 * popUpTo(Route.Splash){inclusive=true}로 이미 백스택에서 빠져 있어 popUpTo 대상이 될 수
 * 없다. 탭 내비게이션의 실질적인 루트인 Route.Home을 직접 지정한다.
 *
 * 바텀바가 항상 보이는 화면(Home/HospitalSearchList/Guide/MapView) 사이의 이동은 하단 탭 클릭이든
 * Home 카드 진입(의료목적 선택/의료기관 찾기/웰니스/가이드/지도)이든 전부 이 함수를 통해야 한다.
 * 한쪽만 이 옵션(popUpTo+saveState+launchSingleTop+restoreState)을 쓰고 다른 쪽은 순수 navigate()를
 * 쓰면, 같은 route에 대해 저장된 상태가 restoreState로 소비되지 않고 계속 쌓여 바텀바의 "홈" 탭이
 * Home으로 돌아가지 못하는 문제가 있었다.
 */
internal fun NavHostController.navigateToTab(route: Route) {
    navigate(route) {
        popUpTo(Route.Home) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Home 검색바 → HospitalSearchList 검색 입력 모드(최근 검색어/자동완성 패널) 전용 진입점.
 * navigateToTab()의 popUpTo+saveState+restoreState 조합을 일부러 쓰지 않는다 — HospitalSearchList를
 * 이전에 다른 args(예: initialSearchFocus=false)로 방문한 적이 있으면, popUpTo{saveState=true}로
 * 저장된 백스택 청크가 destination 단위(인자 무관)로 남아있어서 restoreState=true는 물론
 * launchSingleTop 조합만으로도 그 이전 저장 상태(ViewModel + Compose remember 포함)가 되살아나
 * 새로 넘긴 initialSearchFocus=true가 무시될 수 있다. 순수 navigate()로 항상 완전히 새 백스택
 * 엔트리(=새 ViewModel)를 쌓아서 이 문제를 근본적으로 피한다 — 뒤로가기를 누르면 정상적으로 Home으로
 * 돌아간다.
 *
 * 의료목적 필터 진입(Home 카테고리 칩)에는 이 함수를 쓰지 않는다 — 여기서 순수 navigate()를 쓰면
 * 바텀바 "홈" 탭과 뒤섞여 restoreState가 소비되지 않고 쌓이는 문제가 있었다(navigateToTab 함수 주석
 * 참고). 대신 navigateToTab을 항상 args 없는 HospitalSearchList()로 쓰고, 실제 필터 값은 Route 인자가
 * 아니라 core/common/PendingHospitalSearchFilter를 거쳐 전달한다.
 */
internal fun NavHostController.navigateToSearchFocused(route: Route.HospitalSearchList) {
    navigate(route)
}

/**
 * 최초 실행 설정 흐름(언어선택 -> 진단) 완료 시 Home으로 이동하며 그 전까지 쌓인 스택을 비운다.
 * 앱을 종료했다가 재개한 세션에서는 Splash가 언어선택을 건너뛰고 진단으로 바로 보낼 수도 있어
 * (SplashViewModel 참고) 스택에 Onboarding이 있을 수도, 없을 수도 있다 — 특정 destination을
 * popUpTo 기준으로 잡을 수 없으므로 그래프 전체를 기준으로 비운다.
 */
internal fun NavHostController.navigateToHomeAfterSetup() {
    navigate(Route.Home) {
        popUpTo(graph.id) { inclusive = true }
    }
}
