package com.mediinbusan.app.feature.guide

import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.i18n.GuideStrings

// STEP02 개요 콘텐츠. GuideStepPageContentMapper(합본 페이지)가 필요한 조각만 가져다 쓴다
// (공식 링크 필터링). STEP01/03/04/05/06 개요 함수는 합본 페이지 리디자인 이후 더 이상 쓰이지
// 않아 제거했다 — leaf 단독 진입 라우트도 전부 클릭 경로가 사라져 함께 삭제했다.
internal fun reservationInquiryContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.reservationInquiry
    return GuideStepDetailContent(
        bannerTitle = strings.stepReservationInquiryTitle,
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
            )
        )
    )
}

// STEP03 합본 페이지 "방문·접수 준비" 메모 카드 4개. 리프 단독 화면이 없어 다른 STEP02 개요
// 함수와 달리 GuideStepDetailContent 전체를 감싸지 않고 항목 리스트만 반환한다. 전부 메모지
// 카드(GuideMemoRow)로만 렌더링되므로 iconResId는 안 그려진다(memoIllustrationResId만 쓰임).
internal fun visitReceptionPreparationItems(strings: GuideStrings): List<GuideDetailItem> {
    val s = strings.visitReceptionPreparation
    return listOf(
        GuideDetailItem(
            id = "visit_passport_id",
            title = s.item1Title,
            description = s.item1Description,
            memoIllustrationResId = R.drawable.guide_passport_id,
            memoBackgroundResId = R.drawable.guide_memo5
        ),
        GuideDetailItem(
            id = "visit_reservation_info",
            title = s.item2Title,
            description = s.item2Description,
            memoIllustrationResId = R.drawable.guide_appointment_info,
            memoBackgroundResId = R.drawable.guide_memo3
        ),
        GuideDetailItem(
            id = "visit_reception_location",
            title = s.item3Title,
            description = s.item3Description,
            memoIllustrationResId = R.drawable.guide_reception_location,
            memoBackgroundResId = R.drawable.guide_memo1
        ),
        GuideDetailItem(
            id = "visit_arrival_time",
            title = s.item4Title,
            description = s.item4Description,
            memoIllustrationResId = R.drawable.guide_arrival_time,
            memoBackgroundResId = R.drawable.guide_memo4
        )
    )
}
