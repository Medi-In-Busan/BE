package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.mediinbusan.app.core.i18n.LocalAppStrings

// S-06 하위 STEP06의 "진료 후 주의사항 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTreatmentPrecautionsDetailScreen(onBack: () -> Unit) {
    val strings = LocalAppStrings.current.guide
    GuideDetailTemplateScreen(
        topBarTitle = "06-02 ${strings.postTreatmentPrecautions.bannerTitle}",
        content = postTreatmentPrecautionsContent(strings),
        onBack = onBack
    )
}
