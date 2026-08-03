package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

// S-06 하위 STEP01의 "보험·서류 준비" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceDocumentsDetailScreen(onBack: () -> Unit) {
    GuideDetailTemplateScreen(
        topBarTitle = "01-02 보험·서류 준비",
        content = insuranceDocumentsContent,
        onBack = onBack
    )
}
