package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

// S-06 하위 STEP06의 "약 복용 방법 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScheduleDetailScreen(onBack: () -> Unit) {
    GuideDetailTemplateScreen(
        topBarTitle = "06-01 약 복용 방법 확인",
        content = medicationScheduleContent,
        onBack = onBack
    )
}
