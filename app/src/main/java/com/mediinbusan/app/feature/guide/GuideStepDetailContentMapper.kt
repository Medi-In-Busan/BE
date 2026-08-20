package com.mediinbusan.app.feature.guide

import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.i18n.GuideStrings

// STEP02/STEP03 개요 콘텐츠. GuideStepPageContentMapper(합본 페이지)가 필요한 조각만 가져다 쓴다
// (STEP02의 공식 링크 필터링, STEP03의 "방문·접수 준비" 메모 카드 항목). STEP01/04/05/06 개요
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

// STEP03 합본 페이지 "방문·접수 준비" 메모 카드 4개. 리프 단독 화면이 없어 다른 STEP02/03 개요
// 함수와 달리 GuideStepDetailContent 전체를 감싸지 않고 항목 리스트만 반환한다.
internal fun visitReceptionPreparationItems(strings: GuideStrings): List<GuideDetailItem> {
    val s = strings.visitReceptionPreparation
    return listOf(
        GuideDetailItem(
            id = "visit_passport_id",
            iconResId = R.drawable.ic_passport_identity_verification,
            title = s.item1Title,
            description = s.item1Description,
            memoIllustrationResId = R.drawable.guide_passport_id,
            memoBackgroundResId = R.drawable.guide_memo5
        ),
        GuideDetailItem(
            id = "visit_reservation_info",
            iconResId = R.drawable.ic_appointment_confirmation,
            title = s.item2Title,
            description = s.item2Description,
            memoIllustrationResId = R.drawable.guide_appointment_info,
            memoBackgroundResId = R.drawable.guide_memo3
        ),
        GuideDetailItem(
            id = "visit_reception_location",
            iconResId = R.drawable.ic_reception_direction_sign,
            title = s.item3Title,
            description = s.item3Description,
            memoIllustrationResId = R.drawable.guide_reception_location,
            memoBackgroundResId = R.drawable.guide_memo1
        ),
        GuideDetailItem(
            id = "visit_arrival_time",
            iconResId = R.drawable.ic_expected_arrival_time,
            title = s.item4Title,
            description = s.item4Description,
            memoIllustrationResId = R.drawable.guide_arrival_time,
            memoBackgroundResId = R.drawable.guide_memo4
        )
    )
}
