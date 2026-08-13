package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.mediinbusan.app.core.i18n.LocalAppStrings

// S-06 하위 STEP05의 "총 비용과 포함 항목 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TotalCostCoverageCheckDetailScreen(onBack: () -> Unit) {
    val strings = LocalAppStrings.current.guide
    GuideDetailTemplateScreen(
        topBarTitle = "05-01 ${strings.totalCostCoverageCheck.bannerTitle}",
        content = totalCostCoverageCheckContent(strings),
        onBack = onBack
    )
}
