package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.data.guide.GuidePhase

// S-06 하위 STEP 상세. GuideDetailTemplateScreen + phase.toDetailContent()로 화면을 구성한다.
// title은 nav 인자로 받지 않고 phase에서 직접 도출한다 — 그래야 이 화면에 머무는 동안 언어를
// 바꿔도 상단 타이틀이 즉시 재구성(recomposition)된다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideStepDetailScreen(
    phase: GuidePhase,
    onBack: () -> Unit,
    onItemClick: (GuideDetailItem) -> Unit = {}
) {
    val strings = LocalAppStrings.current.guide
    GuideDetailTemplateScreen(
        topBarTitle = "${phase.toStepNumberLabel()} ${phase.toStepTitle(strings)}",
        content = phase.toDetailContent(strings),
        onBack = onBack,
        onItemClick = onItemClick
    )
}
