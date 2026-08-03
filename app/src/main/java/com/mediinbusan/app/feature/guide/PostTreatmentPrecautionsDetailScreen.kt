package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

// S-06 하위 STEP06의 "진료 후 주의사항 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTreatmentPrecautionsDetailScreen(onBack: () -> Unit) {
    GuideDetailTemplateScreen(
        topBarTitle = "06-02 진료 후 주의사항 확인",
        content = postTreatmentPrecautionsContent,
        onBack = onBack
    )
}
