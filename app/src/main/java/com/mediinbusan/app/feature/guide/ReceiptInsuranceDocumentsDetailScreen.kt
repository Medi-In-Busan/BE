package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

// S-06 하위 STEP05의 "영수증·보험 청구 서류 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptInsuranceDocumentsDetailScreen(onBack: () -> Unit) {
    GuideDetailTemplateScreen(
        topBarTitle = "05-03 영수증·보험 청구 서류 확인",
        content = receiptInsuranceDocumentsContent,
        onBack = onBack
    )
}
