package com.mediinbusan.app.feature.guide

import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.GuideBadgeGreenBackground
import com.mediinbusan.app.core.designsystem.GuideBadgeOrangeBackground
import com.mediinbusan.app.core.designsystem.GuideBadgePurpleBackground
import com.mediinbusan.app.core.designsystem.GuideStepGreen
import com.mediinbusan.app.core.designsystem.GuideStepOrange
import com.mediinbusan.app.core.designsystem.GuideStepPurple
import com.mediinbusan.app.core.i18n.GuideStrings
import com.mediinbusan.app.data.guide.GuidePhase

// GuidePhase-상세 콘텐츠 매퍼, 전체 6단계 구현 완료. strings는 항상 LocalAppStrings.current.guide를 호출부(Composable)에서
// 넘겨받는다 — 언어가 바뀌면 이 함수가 재호출되며 즉시 재구성(recomposition)되도록 하기 위함.
fun GuidePhase.toDetailContent(strings: GuideStrings): GuideStepDetailContent = when (this) {
    GuidePhase.ENTRY_PREPARATION -> entryPreparationContent(strings)
    GuidePhase.RESERVATION_INQUIRY -> reservationInquiryContent(strings)
    GuidePhase.HOSPITAL_CHECKIN -> hospitalCheckinContent(strings)
    GuidePhase.TREATMENT_EXAMINATION -> comingSoonContent(strings) // STEP04는 전용 화면(TreatmentExaminationDetailScreen) 사용
    GuidePhase.PAYMENT_RECEIPT -> paymentReceiptContent(strings)
    GuidePhase.AFTERCARE_RETURN_CHECK -> aftercareReturnCheckContent(strings)
}

private fun entryPreparationContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.entryPreparation
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_guide_entry_preparation_banner,
        bannerAspectRatio = 1672f / 941f,
        bannerStepLabel = "STEP 01",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = GuideDetailItemId.VISA_ENTRY_CHECK,
                iconResId = R.drawable.ic_visa_entry_requirements,
                title = s.item1Title,
                description = s.item1Description,
                navigable = true
            ),
            GuideDetailItem(
                id = GuideDetailItemId.INSURANCE_DOCUMENT_CHECK,
                iconResId = R.drawable.ic_insurance_document_check,
                title = s.item2Title,
                description = s.item2Description,
                navigable = true
            ),
            GuideDetailItem(
                id = GuideDetailItemId.HOSPITAL_INQUIRY,
                iconResId = R.drawable.ic_hospital_inquiry,
                title = s.item3Title,
                description = s.item3Description,
                navigable = true
            )
        ),
        situationalTitle = s.situationalTitle,
        situationalItems = listOf(
            GuideDetailItem(
                id = "long_term_treatment_guardian",
                iconResId = R.drawable.ic_long_term_treatment_guardian,
                title = s.situational1Title,
                description = s.situational1Description
            )
        ),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

private fun reservationInquiryContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.reservationInquiry
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_reservation_inquiry_banner,
        bannerAspectRatio = 1672f / 941f,
        bannerStepLabel = "STEP 02",
        bannerTitle = strings.stepReservationInquiryTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "official_hospital_inquiry_channel",
                iconResId = R.drawable.ic_official_hospital_inquiry_channel,
                title = s.item1Title,
                description = s.item1Description,
                url = "https://www.bsmeditour.go.kr/"
            ),
            GuideDetailItem(
                id = "multilingual_hospital_search",
                iconResId = R.drawable.ic_multilingual_hospital_search,
                title = s.item2Title,
                description = s.item2Description,
                url = "https://www.medicalkorea.or.kr/en/registeredhospitals"
            ),
            GuideDetailItem(
                id = GuideDetailItemId.PRE_INQUIRY_INFORMATION,
                iconResId = R.drawable.ic_pre_inquiry_information,
                title = s.item3Title,
                description = s.item3Description,
                navigable = true
            )
        ),
        situationalTitle = s.situationalTitle,
        situationalItems = listOf(
            GuideDetailItem(
                id = "registered_facility_check",
                iconResId = R.drawable.ic_registered_facility_check,
                title = s.situational1Title,
                description = s.situational1Description
            ),
            GuideDetailItem(
                id = "english_documents_availability",
                iconResId = R.drawable.ic_english_documents_availability,
                title = s.situational2Title,
                description = s.situational2Description
            )
        ),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

private fun hospitalCheckinContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.hospitalCheckin
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_hospital_visit_checkin_banner,
        bannerAspectRatio = 1536f / 1024f,
        bannerStepLabel = "STEP 03",
        bannerTitle = strings.stepHospitalCheckinTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = GuideDetailItemId.PASSPORT_RESERVATION_INFO,
                iconResId = R.drawable.ic_passport_reservation_info,
                title = s.item1Title,
                description = s.item1Description,
                navigable = true,
                badgeLabel = s.item1BadgeLabel
            ),
            GuideDetailItem(
                id = GuideDetailItemId.MEDICAL_RECORDS_TEST_RESULTS,
                iconResId = R.drawable.ic_medical_records_test_results,
                title = s.item2Title,
                description = s.item2Description,
                navigable = true,
                badgeLabel = s.item2BadgeLabel,
                badgeBackgroundColor = GuideBadgeGreenBackground,
                badgeTextColor = GuideStepGreen
            ),
            GuideDetailItem(
                id = GuideDetailItemId.HOSPITAL_LOCATION_CHECKIN_GUIDE,
                iconResId = R.drawable.ic_hospital_location_checkin_guide,
                title = s.item3Title,
                description = s.item3Description,
                navigable = true,
                badgeLabel = s.item3BadgeLabel,
                badgeBackgroundColor = GuideBadgePurpleBackground,
                badgeTextColor = GuideStepPurple
            )
        ),
        situationalTitle = s.situationalTitle,
        situationalItems = listOf(
            GuideDetailItem(
                id = "interpretation_language_support",
                iconResId = R.drawable.ic_interpretation_language_support,
                title = s.situational1Title,
                description = s.situational1Description
            ),
            GuideDetailItem(
                id = "payment_method_check",
                iconResId = R.drawable.ic_payment_method_check,
                title = s.situational2Title,
                description = s.situational2Description
            )
        ),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

private fun paymentReceiptContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.paymentReceipt
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_payment_billing_banner,
        // 원본 캔버스(1448x1086)는 위아래에 흰 여백이 커서 그대로 쓰면 그림이 작아 보인다 —
        // 실제 카드 그림 비율(1416x796)로 좁혀 Crop이 여백을 잘라내고 그림이 프레임을 채우게 한다.
        bannerAspectRatio = 1416f / 796f,
        bannerStepLabel = "STEP 05",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = GuideDetailItemId.TOTAL_COST_COVERAGE_CHECK,
                iconResId = R.drawable.ic_estimated_cost_inquiry,
                title = s.item1Title,
                description = s.item1Description,
                navigable = true,
                badgeLabel = s.item1BadgeLabel
            ),
            GuideDetailItem(
                id = GuideDetailItemId.PAYMENT_METHOD_AVAILABLE_CHECK,
                iconResId = R.drawable.ic_payment_method_check,
                title = s.item2Title,
                description = s.item2Description,
                navigable = true,
                badgeLabel = s.item2BadgeLabel,
                badgeBackgroundColor = GuideBadgeGreenBackground,
                badgeTextColor = GuideStepGreen
            ),
            GuideDetailItem(
                id = GuideDetailItemId.RECEIPT_INSURANCE_DOCUMENT_CHECK,
                iconResId = R.drawable.ic_medical_receipt,
                title = s.item3Title,
                description = s.item3Description,
                navigable = true,
                badgeLabel = s.item3BadgeLabel,
                badgeBackgroundColor = GuideBadgeOrangeBackground,
                badgeTextColor = GuideStepOrange
            )
        ),
        situationalTitle = s.situationalTitle,
        situationalItems = listOf(
            GuideDetailItem(
                id = "deposit_prepayment_check",
                iconResId = R.drawable.ic_medical_documents_folder,
                title = s.situational1Title,
                description = s.situational1Description
            ),
            GuideDetailItem(
                id = "cosmetic_tax_refund_check",
                iconResId = R.drawable.ic_beauty_treatment_tax_refund_eligibility,
                title = s.situational2Title,
                description = s.situational2Description
            )
        ),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

private fun aftercareReturnCheckContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.aftercareReturnCheck
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_post_treatment_travel_preparation_banner,
        bannerAspectRatio = 1672f / 941f,
        bannerStepLabel = "STEP 06",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = GuideDetailItemId.MEDICATION_SCHEDULE_CHECK,
                iconResId = R.drawable.ic_medication_schedule,
                title = s.item1Title,
                description = s.item1Description,
                navigable = true,
                badgeLabel = s.item1BadgeLabel,
                badgeBackgroundColor = GuideBadgeGreenBackground,
                badgeTextColor = GuideStepGreen
            ),
            GuideDetailItem(
                id = GuideDetailItemId.POST_TREATMENT_PRECAUTIONS_CHECK,
                iconResId = R.drawable.ic_post_treatment_precautions,
                title = s.item2Title,
                description = s.item2Description,
                navigable = true,
                badgeLabel = s.item2BadgeLabel,
                badgeBackgroundColor = GuideBadgeOrangeBackground,
                badgeTextColor = GuideStepOrange
            ),
            GuideDetailItem(
                id = GuideDetailItemId.ENGLISH_DOCUMENTS_RESULTS_CHECK,
                iconResId = R.drawable.ic_medical_documents_folder,
                title = s.item3Title,
                description = s.item3Description,
                navigable = true,
                badgeLabel = s.item3BadgeLabel,
                badgeBackgroundColor = GuideBadgePurpleBackground,
                badgeTextColor = GuideStepPurple
            ),
            GuideDetailItem(
                id = GuideDetailItemId.AIRPORT_DEPARTURE_PREPARATION_CHECK,
                iconResId = R.drawable.ic_airport_departure_preparation,
                title = s.item4Title,
                description = s.item4Description,
                navigable = true,
                badgeLabel = s.item4BadgeLabel
            )
        ),
        situationalTitle = s.situationalTitle,
        situationalItems = listOf(
            GuideDetailItem(
                id = "restricted_medicine_check",
                iconResId = R.drawable.ic_restricted_medicine_check,
                title = s.situational1Title,
                description = s.situational1Description
            ),
            GuideDetailItem(
                id = "long_flight_caution",
                iconResId = R.drawable.ic_long_flight_seat,
                title = s.situational2Title,
                description = s.situational2Description
            )
        ),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

private fun comingSoonContent(strings: GuideStrings): GuideStepDetailContent = GuideStepDetailContent(
    bannerResId = null,
    checklistTitle = "",
    checklistItems = emptyList(),
    situationalTitle = "",
    situationalItems = emptyList(),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = strings.comingSoonNoticeText
)