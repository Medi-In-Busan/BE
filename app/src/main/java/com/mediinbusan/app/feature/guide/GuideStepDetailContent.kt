package com.mediinbusan.app.feature.guide

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.mediinbusan.app.core.designsystem.InfoBackgroundBlue
import com.mediinbusan.app.core.designsystem.SkyBlue

// STEP 상세 화면 콘텐츠 모델 (Mapper에서 조립, Screen은 렌더링만 담당)
// url이 있으면 외부 링크, 없고 navigable=true면 내부 하위 페이지, 둘 다 아니면 정보 전용 카드
data class GuideDetailItem(
    val id: String,
    // LINK 카드(GuideOfficialLinkCard)·INFO 카드(GuideDetailItemCard)에서만 실제로 그려진다.
    // MEMO 카드(GuideMemoRow)는 memoIllustrationResId를 쓰므로 이 값을 안 본다 — null이면 그런 항목이란 뜻.
    @param:DrawableRes val iconResId: Int? = null,
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
    val accentColor: Color? = null,
    // description의 선행 부분을 강조색으로 렌더링하고 싶을 때 그 부분만 넣는다(반드시 description의 접두사여야
    // 한다). null이면 description 전체를 단일 색으로 그린다 — GuideDetailItemCard의 INFO 카드 등에서 사용.
    val descriptionHighlightPrefix: String? = null
)

// 번호 매긴 확인 순서 카드 한 줄 (제목 + 설명). GuideOrderStepCard가 렌더링한다.
data class GuideOrderStep(
    val title: String,
    val description: String
)

// GuideDetailItem.id 상수 (하위 상세 화면 라우팅 분기용). STEP03 합본 페이지의 "병원 정보 확인하기"
// 카드 하나만 여전히 지도로 내비게이션하므로 이 상수만 남는다.
object GuideDetailItemId {
    const val HOSPITAL_LOCATION_CHECKIN_GUIDE = "hospital_location_checkin_guide"
}

// STEP 상세(합본 페이지) 섹션 콘텐츠 모델 (Mapper에서 조립, GuideStepDetailScreen은 렌더링만 담당)
data class GuideStepDetailContent(
    val bannerTitle: String = "",
    val checklistItems: List<GuideDetailItem>,
    // 번호 매긴 질문 카드 섹션 ("이렇게 물어보세요" 등). 문의 스크립트 예시가 필요한 페이지에서만 사용.
    val questionsTitle: String = "",
    val questions: List<String> = emptyList(),
    // 번호 매긴 확인 순서 섹션 ("확인 순서" 등). 체크리스트 바로 아래에 렌더링된다.
    val orderStepsTitle: String = "",
    val orderSteps: List<GuideOrderStep> = emptyList()
)
