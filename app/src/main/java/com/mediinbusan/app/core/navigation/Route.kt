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

    @Serializable
    data class HospitalSearchList(val medicalPurpose: String? = null) : Route // S-04, 구 HospitalList + Search 통합. medicalPurpose가 있으면 진입 시 해당 필터로 자동 검색

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

    @Serializable
    data object SelfDiagnosis : Route // TODO: 화면 미구현, 진입점만 예약 (자가진단 플로우는 별도 이슈)
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
