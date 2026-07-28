package com.mediinbusan.app.feature.guide

import androidx.annotation.DrawableRes

// STEP 상세 화면 콘텐츠 모델 (Mapper에서 조립, Screen은 렌더링만 담당)
// url이 있으면 외부 링크, 없고 navigable=true면 내부 하위 페이지, 둘 다 아니면 정보 전용 카드
data class GuideDetailItem(
    val id: String,
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String,
    val navigable: Boolean = false,
    val url: String? = null
)

// GuideDetailItem.id 상수 (하위 상세 화면 라우팅 분기용)
object GuideDetailItemId {
    const val VISA_ENTRY_CHECK = "visa_entry_check"
    const val INSURANCE_DOCUMENT_CHECK = "insurance_document_check"
    const val HOSPITAL_INQUIRY = "hospital_inquiry"
    const val PRE_INQUIRY_INFORMATION = "pre_inquiry_information"
}

data class GuideStepDetailContent(
    @param:DrawableRes val bannerResId: Int?,
    val checklistTitle: String,
    val checklistItems: List<GuideDetailItem>,
    val situationalTitle: String,
    val situationalItems: List<GuideDetailItem>,
    @param:DrawableRes val noticeIconResId: Int,
    val noticeText: String
)