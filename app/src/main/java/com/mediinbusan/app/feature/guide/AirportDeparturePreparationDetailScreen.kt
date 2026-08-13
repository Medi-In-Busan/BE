package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.mediinbusan.app.core.i18n.LocalAppStrings

// S-06 하위 STEP06의 "귀국 전 반입·공항 준비" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirportDeparturePreparationDetailScreen(onBack: () -> Unit) {
    val strings = LocalAppStrings.current.guide
    GuideDetailTemplateScreen(
        topBarTitle = "06-04 ${strings.airportDeparturePreparation.bannerTitle}",
        content = airportDeparturePreparationContent(strings),
        onBack = onBack
    )
}
