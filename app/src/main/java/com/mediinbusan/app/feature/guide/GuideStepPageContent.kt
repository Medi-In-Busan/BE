package com.mediinbusan.app.feature.guide

import androidx.annotation.DrawableRes

// STEP 상세 리디자인(합본 페이지) 전용 콘텐츠 모델. 기존 GuideStepDetailContent(및 그걸 쓰는
// GuideDetailTemplateScreen)는 leaf 상세 화면들이 계속 그대로 쓰므로 건드리지 않는다 — 이 모델은
// GuideStepPageContentMapper가 "STEP 개요 + leaf 3~4개"의 실제 데이터(GuideDetailItem)를 재배치해
// 한 페이지로 합치는 용도로만 쓰인다.

// 카드 렌더링 방식. LINK는 공식 사이트 카드(배지+바로가기, 가로 스크롤), MEMO는 메모지 카드,
// INFO는 아이콘+제목+설명+화살표의 전체 폭 안내 카드 한 장(rowItems의 첫 항목만 렌더링).
enum class GuideStepCardStyle { LINK, MEMO, INFO }

// title이 비어 있으면 헤더를 그리지 않는다(기존 STEP02 checklistTitle 미설정 동작과 동일하게 유지).
data class GuideStepPageSection(
    val title: String,
    val cardStyle: GuideStepCardStyle,
    val rowItems: List<GuideDetailItem>,
    val questionsTitle: String = "",
    val questions: List<String> = emptyList(),
    val orderStepsTitle: String = "",
    val orderSteps: List<GuideOrderStep> = emptyList()
)

data class GuideStepPageContent(
    @param:DrawableRes val heroResId: Int,
    val stepNumberLabel: String,
    val stepTitle: String,
    val stepSubtitle: String,
    val sections: List<GuideStepPageSection>,
    val tipLead: String,
    val tipHighlight: String
)
