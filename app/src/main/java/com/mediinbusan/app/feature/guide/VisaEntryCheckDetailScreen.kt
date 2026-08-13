package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.mediinbusan.app.core.i18n.LocalAppStrings

// S-06 하위 STEP01의 "비자·입국 조건 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisaEntryCheckDetailScreen(onBack: () -> Unit) {
    val strings = LocalAppStrings.current.guide
    GuideDetailTemplateScreen(
        topBarTitle = strings.visaEntryCheck.bannerTitle,
        content = visaEntryCheckContent(strings),
        onBack = onBack
    )
}
