package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

// S-06 하위 STEP01의 "비자·입국 조건 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisaEntryCheckDetailScreen(onBack: () -> Unit) {
    GuideDetailTemplateScreen(
        topBarTitle = "비자·입국 조건 확인",
        content = visaEntryCheckContent,
        onBack = onBack
    )
}
