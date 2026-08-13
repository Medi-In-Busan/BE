package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.mediinbusan.app.core.i18n.LocalAppStrings

// S-06 하위 STEP02의 "문의 전 전달할 정보 정리" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreInquiryInformationDetailScreen(onBack: () -> Unit) {
    val strings = LocalAppStrings.current.guide
    GuideDetailTemplateScreen(
        topBarTitle = "02-03 ${strings.preInquiryInformation.bannerTitle}",
        content = preInquiryInformationContent(strings),
        onBack = onBack
    )
}
