package com.mediinbusan.app.feature.guide

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

// S-06 하위 STEP05의 "결제 가능 수단 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodCheckDetailScreen(onBack: () -> Unit) {
    GuideDetailTemplateScreen(
        topBarTitle = "05-02 결제 가능 수단 확인",
        content = paymentMethodCheckContent,
        onBack = onBack
    )
}
