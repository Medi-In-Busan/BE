package com.mediinbusan.app.feature.guide

import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.GuideBadgeGreenBackground
import com.mediinbusan.app.core.designsystem.GuideBadgePurpleBackground
import com.mediinbusan.app.core.designsystem.GuideCardLavenderBackground
import com.mediinbusan.app.core.designsystem.GuideCardPeachBackground
import com.mediinbusan.app.core.designsystem.GuideStepGreen
import com.mediinbusan.app.core.designsystem.GuideStepPurple
import com.mediinbusan.app.data.guide.GuidePhase

// GuidePhase-상세 콘텐츠 매퍼, ENTRY_PREPARATION·RESERVATION_INQUIRY·HOSPITAL_CHECKIN 구현, 나머지는 준비 중 플레이스홀더
fun GuidePhase.toDetailContent(): GuideStepDetailContent = when (this) {
    GuidePhase.ENTRY_PREPARATION -> entryPreparationContent
    GuidePhase.RESERVATION_INQUIRY -> reservationInquiryContent
    GuidePhase.HOSPITAL_CHECKIN -> hospitalCheckinContent
    else -> comingSoonContent
}

private val entryPreparationContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_guide_entry_preparation_banner,
    bannerStepLabel = "STEP 01",
    bannerTitle = "입국 전 준비",
    bannerSubtitle = "입국 전에는 비자, 보험, 병원 문의 준비만 먼저 확인해도 충분해요.",
    checklistTitle = "준비사항 확인",
    checklistItems = listOf(
        GuideDetailItem(
            id = GuideDetailItemId.VISA_ENTRY_CHECK,
            iconResId = R.drawable.ic_visa_entry_requirements,
            title = "비자·입국 조건 확인",
            description = "체류 목적에 맞는 비자 종류와 입국 조건을 미리 확인하세요.",
            navigable = true
        ),
        GuideDetailItem(
            id = GuideDetailItemId.INSURANCE_DOCUMENT_CHECK,
            iconResId = R.drawable.ic_insurance_document_check,
            title = "보험·서류 준비",
            description = "여행자보험 가입 여부와 진단서 등 필요 서류를 준비하세요.",
            navigable = true
        ),
        GuideDetailItem(
            id = GuideDetailItemId.HOSPITAL_INQUIRY,
            iconResId = R.drawable.ic_hospital_inquiry,
            title = "병원 문의 전 정보 정리",
            description = "방문 목적과 증상 등 병원에 전달할 정보를 미리 정리해두세요.",
            navigable = true
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

private val reservationInquiryContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_reservation_inquiry_banner,
    bannerStepLabel = "STEP 02",
    bannerTitle = "예약 및 문의",
    bannerSubtitle = "병원에 문의하기 전 필요한 정보를 정리하고 언어 지원과 문의 채널을 확인해요.",
    checklistTitle = "준비사항 확인",
    checklistItems = listOf(
        GuideDetailItem(
            id = "official_hospital_inquiry_channel",
            iconResId = R.drawable.ic_official_hospital_inquiry_channel,
            title = "병원 공식 문의 채널 확인",
            description = "메디투어부산에서 부산 의료기관의 공식 문의 채널을 확인하세요.",
            url = "https://www.bsmeditour.go.kr/"
        ),
        GuideDetailItem(
            id = "multilingual_hospital_search",
            iconResId = R.drawable.ic_multilingual_hospital_search,
            title = "상담 가능한 언어로 병원 찾기",
            description = "Medical Korea 등록병원 목록에서 상담 가능한 언어를 확인하세요.",
            url = "https://www.medicalkorea.or.kr/en/registeredhospitals"
        ),
        GuideDetailItem(
            id = GuideDetailItemId.PRE_INQUIRY_INFORMATION,
            iconResId = R.drawable.ic_pre_inquiry_information,
            title = "문의 전 전달할 정보 정리",
            description = "증상, 희망 진료, 방문 시기 등 전달할 정보를 미리 정리하세요.",
            navigable = true
        )
    ),
    situationalTitle = "상황별 확인",
    situationalItems = listOf(
        GuideDetailItem(
            id = "registered_facility_check",
            iconResId = R.drawable.ic_registered_facility_check,
            title = "등록 유치기관 이용 여부 확인",
            description = "등록된 해외환자 유치기관을 통한 진행인지 확인하세요."
        ),
        GuideDetailItem(
            id = "english_documents_availability",
            iconResId = R.drawable.ic_english_documents_availability,
            title = "영문 서류 발급 가능 여부",
            description = "진단서, 소견서 등 영문 서류 발급이 가능한지 확인하세요."
        )
    ),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "본 안내는 특정 병원·기관 이용을 권장하지 않습니다. 실제 진행 여부는 병원에 직접 확인해 주세요."
)

private val hospitalCheckinContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_hospital_visit_checkin_banner,
    bannerStepLabel = "STEP 03",
    bannerTitle = "병원 방문 및 접수",
    bannerSubtitle = "병원 방문 전에는 여권, 예약 정보, 접수 준비만 먼저 확인해도 충분해요.",
    checklistTitle = "이번 단계에서 꼭 확인할 3가지",
    checklistItems = listOf(
        GuideDetailItem(
            id = GuideDetailItemId.PASSPORT_RESERVATION_INFO,
            iconResId = R.drawable.ic_passport_reservation_info,
            title = "여권·예약정보 준비",
            description = "여권 또는 신분 확인 자료와 예약 시간, 진료과를 미리 확인하세요.",
            navigable = true,
            badgeLabel = "신분 확인"
        ),
        GuideDetailItem(
            id = GuideDetailItemId.MEDICAL_RECORDS_TEST_RESULTS,
            iconResId = R.drawable.ic_medical_records_test_results,
            title = "기존 진단서·검사결과 준비",
            description = "기존 진단서, 검사결과, 복용약 정보를 준비하면 접수가 쉬워져요.",
            navigable = true,
            badgeLabel = "기존 자료",
            badgeBackgroundColor = GuideBadgeGreenBackground,
            badgeTextColor = GuideStepGreen
        ),
        GuideDetailItem(
            id = GuideDetailItemId.HOSPITAL_LOCATION_CHECKIN_GUIDE,
            iconResId = R.drawable.ic_hospital_location_checkin_guide,
            title = "병원 위치와 접수 절차 확인",
            description = "병원 위치, 도착 시간, 어디서 접수하는지 먼저 확인하세요.",
            navigable = true,
            badgeLabel = "접수 안내",
            badgeBackgroundColor = GuideBadgePurpleBackground,
            badgeTextColor = GuideStepPurple
        )
    ),
    situationalTitle = "상황별 확인",
    situationalItems = listOf(
        GuideDetailItem(
            id = "interpretation_language_support",
            iconResId = R.drawable.ic_interpretation_language_support,
            title = "통역·지원 언어 확인",
            description = "진료 당일 통역이나 지원 언어가 필요한 경우만 확인하세요.",
            cardBackgroundColor = GuideCardPeachBackground
        ),
        GuideDetailItem(
            id = "payment_method_check",
            iconResId = R.drawable.ic_payment_method_check,
            title = "결제 수단 확인",
            description = "해외 카드, 현금, 송금 가능 여부가 필요한 경우만 확인하세요.",
            cardBackgroundColor = GuideCardLavenderBackground
        )
    ),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "병원마다 접수 절차와 준비 서류가 다를 수 있으니 예약 안내를 다시 확인해 주세요."
)

// TODO: STEP 04~06 상세 콘텐츠 작성 필요
private val comingSoonContent = GuideStepDetailContent(
    bannerResId = null,
    checklistTitle = "",
    checklistItems = emptyList(),
    situationalTitle = "",
    situationalItems = emptyList(),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "이 단계의 상세 안내는 준비 중입니다."
)