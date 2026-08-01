package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.mediinbusan.app.data.guide.GuidePhase

// S-06 하위 STEP 상세. GuideDetailTemplateScreen + phase.toDetailContent()로 화면을 구성한다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideStepDetailScreen(
    phase: GuidePhase,
    title: String,
    onBack: () -> Unit,
    onItemClick: (GuideDetailItem) -> Unit = {}
) {
    GuideDetailTemplateScreen(
        topBarTitle = "${phase.toStepNumberLabel()} $title",
        content = phase.toDetailContent(),
        onBack = onBack,
        onItemClick = onItemClick
    )
}
