package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

// S-06 하위 STEP03의 "여권·예약정보 준비" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassportReservationInfoDetailScreen(onBack: () -> Unit) {
    GuideDetailTemplateScreen(
        topBarTitle = "03-01 여권·예약정보 준비",
        content = passportReservationInfoContent,
        onBack = onBack
    )
}
