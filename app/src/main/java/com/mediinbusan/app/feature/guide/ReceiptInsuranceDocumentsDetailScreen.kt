package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.mediinbusan.app.core.i18n.LocalAppStrings

// S-06 하위 STEP05의 "영수증·보험 청구 서류 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptInsuranceDocumentsDetailScreen(onBack: () -> Unit) {
    val strings = LocalAppStrings.current.guide
    GuideDetailTemplateScreen(
        topBarTitle = "05-03 ${strings.receiptInsuranceDocuments.bannerTitle}",
        content = receiptInsuranceDocumentsContent(strings),
        onBack = onBack
    )
}
