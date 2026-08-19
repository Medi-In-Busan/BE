package com.mediinbusan.app.feature.guide

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.mediinbusan.app.core.designsystem.InfoBackgroundBlue
import com.mediinbusan.app.core.designsystem.SkyBlue

// STEP 상세 화면 콘텐츠 모델 (Mapper에서 조립, Screen은 렌더링만 담당)
// url이 있으면 외부 링크, 없고 navigable=true면 내부 하위 페이지, 둘 다 아니면 정보 전용 카드
data class GuideDetailItem(
    val id: String,
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String,
    val navigable: Boolean = false,
    val url: String? = null,
    val badgeLabel: String? = null,
    val badgeBackgroundColor: Color = InfoBackgroundBlue,
    val badgeTextColor: Color = SkyBlue,
    val cardBackgroundColor: Color = Color.White,
    // GuideMemoRow(메모지 카드) 전용 삽화·배경 오버라이드. null이면 GuideMemoRow가 위치 기반 기본값을 쓴다 —
    // 메모지 카드가 아닌 LIST/GRID 카드(iconResId 사용)에는 영향 없다.
    @param:DrawableRes val memoIllustrationResId: Int? = null,
    @param:DrawableRes val memoBackgroundResId: Int? = null,
    // GuideOfficialLinkRow(공식 사이트 카드) 전용 강조색 오버라이드. null이면 위치 기반 기본값(첫 카드만 코랄, 나머지는 스카이블루)을 쓴다.
    val accentColor: Color? = null
)

// 번호 매긴 확인 순서 카드 한 줄 (제목 + 설명). GuideOrderStepCard가 렌더링한다.
data class GuideOrderStep(
    val title: String,
    val description: String
)

// GuideDetailItem.id 상수 (하위 상세 화면 라우팅 분기용)
object GuideDetailItemId {
    const val VISA_ENTRY_CHECK = "visa_entry_check"
    const val INSURANCE_DOCUMENT_CHECK = "insurance_document_check"
    const val HOSPITAL_INQUIRY = "hospital_inquiry"
    const val PRE_INQUIRY_INFORMATION = "pre_inquiry_information"
    const val PASSPORT_RESERVATION_INFO = "passport_reservation_info"
    const val MEDICAL_RECORDS_TEST_RESULTS = "medical_records_test_results"
    const val HOSPITAL_LOCATION_CHECKIN_GUIDE = "hospital_location_checkin_guide"
    const val TOTAL_COST_COVERAGE_CHECK = "total_cost_coverage_check"
    const val PAYMENT_METHOD_AVAILABLE_CHECK = "payment_method_available_check"
    const val RECEIPT_INSURANCE_DOCUMENT_CHECK = "receipt_insurance_document_check"
    const val MEDICATION_SCHEDULE_CHECK = "medication_schedule_check"
    const val POST_TREATMENT_PRECAUTIONS_CHECK = "post_treatment_precautions_check"
    const val ENGLISH_DOCUMENTS_RESULTS_CHECK = "english_documents_results_check"
    const val AIRPORT_DEPARTURE_PREPARATION_CHECK = "airport_departure_preparation_check"
}

// 상황별 섹션 카드 배치 방식. GRID는 MedicalRecordsTestResultsDetailScreen처럼 2열 컴팩트 카드가 필요할 때 쓴다.
enum class GuideSituationalLayout { LIST, GRID }

// 배너는 텍스트 없는 배경 이미지 + Compose Text 오버레이 조합 (언어 전환 대응)
// bannerAspectRatio는 실제 배너 파일 원본 비율과 반드시 일치해야 한다 — 다르면 ContentScale.Crop이
// 좌우를 잘라내며 삽화가 예상보다 안쪽(텍스트 영역)으로 밀려 들어와 텍스트와 겹칠 수 있다.
data class GuideStepDetailContent(
    @param:DrawableRes val bannerResId: Int?,
    val bannerAspectRatio: Float = 1536f / 1024f,
    val bannerStepLabel: String = "",
    val bannerTitle: String = "",
    val bannerSubtitle: String = "",
    val checklistTitle: String = "",
    val checklistItems: List<GuideDetailItem>,
    val situationalTitle: String = "",
    val situationalItems: List<GuideDetailItem> = emptyList(),
    val situationalLayout: GuideSituationalLayout = GuideSituationalLayout.LIST,
    // 번호 매긴 질문 카드 섹션 ("이렇게 물어보세요" 등). 문의 스크립트 예시가 필요한 페이지에서만 사용.
    val questionsTitle: String = "",
    val questions: List<String> = emptyList(),
    // 번호 매긴 확인 순서 섹션 ("확인 순서" 등). 체크리스트 바로 아래, 안내 배너 위에 렌더링된다.
    val orderStepsTitle: String = "",
    val orderSteps: List<GuideOrderStep> = emptyList(),
    @param:DrawableRes val noticeIconResId: Int,
    val noticeText: String
)