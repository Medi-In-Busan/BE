package com.mediinbusan.app.feature.guide

import com.mediinbusan.app.R
import com.mediinbusan.app.data.guide.GuidePhase

// GuidePhase-상세 콘텐츠 매퍼, ENTRY_PREPARATION만 구현, 나머지는 준비 중 플레이스홀더
fun GuidePhase.toDetailContent(): GuideStepDetailContent = when (this) {
    GuidePhase.ENTRY_PREPARATION -> entryPreparationContent
    else -> comingSoonContent
}

private val entryPreparationContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_guide_entry_preparation_banner,
    checklistTitle = "준비사항 확인",
    checklistItems = listOf(
        GuideDetailItem(
            id = GuideDetailItemId.VISA_ENTRY_CHECK,
            iconResId = R.drawable.ic_visa_entry_requirements,
            title = "비자·입국 조건 확인",
            description = "체류 목적에 맞는 비자 종류와 입국 조건을 미리 확인하세요."
        ),
        GuideDetailItem(
            id = GuideDetailItemId.INSURANCE_DOCUMENT_CHECK,
            iconResId = R.drawable.ic_insurance_document_check,
            title = "보험·서류 준비",
            description = "여행자보험 가입 여부와 진단서 등 필요 서류를 준비하세요."
        ),
        GuideDetailItem(
            id = GuideDetailItemId.HOSPITAL_INQUIRY,
            iconResId = R.drawable.ic_hospital_inquiry,
            title = "병원 문의 전 정보 정리",
            description = "방문 목적과 증상 등 병원에 전달할 정보를 미리 정리해두세요."
        )
    ),
    situationalTitle = "상황별 확인",
    situationalItems = listOf(
        GuideDetailItem(
            id = "long_term_treatment_guardian",
            iconResId = R.drawable.ic_long_term_treatment_guardian,
            title = "91일 이상 치료·요양 가능성",
            description = "91일 이상 장기 체류가 예상된다면 체류 자격과 보호자 동반 여부를 미리 확인하세요."
        )
    ),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "본 안내는 의료 또는 비자 판단이 아닙니다. 병원 또는 공식 기관의 안내를 우선 확인해 주세요."
)

// TODO: STEP 02~06 상세 콘텐츠 작성 필요
private val comingSoonContent = GuideStepDetailContent(
    bannerResId = null,
    checklistTitle = "",
    checklistItems = emptyList(),
    situationalTitle = "",
    situationalItems = emptyList(),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "이 단계의 상세 안내는 준비 중입니다."
)