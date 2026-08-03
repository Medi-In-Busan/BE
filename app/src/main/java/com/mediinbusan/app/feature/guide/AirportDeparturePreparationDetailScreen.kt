package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

// S-06 하위 STEP06의 "귀국 전 반입·공항 준비" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirportDeparturePreparationDetailScreen(onBack: () -> Unit) {
    GuideDetailTemplateScreen(
        topBarTitle = "06-04 귀국 전 반입·공항 준비",
        content = airportDeparturePreparationContent,
        onBack = onBack
    )
}
