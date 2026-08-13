package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.mediinbusan.app.core.i18n.LocalAppStrings

// S-06 하위 STEP01의 "병원 문의 전 정보 정리" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalInquiryDetailScreen(onBack: () -> Unit) {
    val strings = LocalAppStrings.current.guide
    GuideDetailTemplateScreen(
        topBarTitle = "01-03 ${strings.hospitalInquiry.bannerTitle}",
        content = hospitalInquiryContent(strings),
        onBack = onBack
    )
}
