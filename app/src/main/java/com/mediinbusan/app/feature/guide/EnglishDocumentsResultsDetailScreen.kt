package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

// S-06 하위 STEP06의 "영문 서류·검사결과 수령 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnglishDocumentsResultsDetailScreen(onBack: () -> Unit) {
    GuideDetailTemplateScreen(
        topBarTitle = "06-03 영문 서류·검사결과 수령 확인",
        content = englishDocumentsResultsContent,
        onBack = onBack
    )
}
