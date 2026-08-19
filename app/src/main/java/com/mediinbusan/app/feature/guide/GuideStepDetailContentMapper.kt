package com.mediinbusan.app.feature.guide

import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.GuideBadgeGreenBackground
import com.mediinbusan.app.core.designsystem.GuideBadgePurpleBackground
import com.mediinbusan.app.core.designsystem.GuideStepGreen
import com.mediinbusan.app.core.designsystem.GuideStepPurple
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.i18n.GuideStrings

// STEP02/STEP03 개요 콘텐츠. GuideStepPageContentMapper(합본 페이지)가 필요한 조각만 가져다 쓴다
// (STEP02의 공식 링크 필터링, STEP03의 "병원 위치와 접수 절차 확인" 항목). STEP01/04/05/06 개요
// 함수는 합본 페이지 리디자인 이후 더 이상 쓰이지 않아 제거했다 — leaf 단독 화면들은 각자
// GuideItemDetailContentMapper의 함수를 직접 쓰므로 영향 없다.
internal fun reservationInquiryContent(strings: GuideStrings): GuideStepDetailContent {
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
                iconResId = R.drawable.guide_hospital_contact_channel,
                title = s.item1Title,
                description = s.item1Description,
                url = "https://www.bsmeditour.go.kr/",
                accentColor = SkyBlue
            ),
            GuideDetailItem(
                id = "multilingual_hospital_search",
                iconResId = R.drawable.guide_hospital_language_support,
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

internal fun hospitalCheckinContent(strings: GuideStrings): GuideStepDetailContent {
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
