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
    val cardBackgroundColor: Color = Color.White
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
}

// 배너는 텍스트 없는 배경 이미지 + Compose Text 오버레이 조합 (언어 전환 대응)
data class GuideStepDetailContent(
    @param:DrawableRes val bannerResId: Int?,
    val bannerStepLabel: String = "",
    val bannerTitle: String = "",
    val bannerSubtitle: String = "",
    val checklistTitle: String,
    val checklistItems: List<GuideDetailItem>,
    val situationalTitle: String,
    val situationalItems: List<GuideDetailItem>,
    @param:DrawableRes val noticeIconResId: Int,
    val noticeText: String
)