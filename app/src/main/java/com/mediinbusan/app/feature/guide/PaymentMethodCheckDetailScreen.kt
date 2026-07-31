package com.mediinbusan.app.feature.guide

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.PageBackground

private data class PaymentCheckItem(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String
)

private val BEFORE_PAYMENT_CHECKLIST = listOf(
    PaymentCheckItem(
        iconResId = R.drawable.ic_estimated_cost_inquiry,
        title = "원화 기준 결제 여부",
        description = "많은 병원이 원화 기준으로 결제하니 실제 청구 통화를 확인하세요."
    ),
    PaymentCheckItem(
        iconResId = R.drawable.ic_payment_method_check,
        title = "해외 카드 사용 가능 여부",
        description = "Visa, Mastercard, Amex 등 사용 가능한 해외 카드 브랜드를 확인하세요."
    ),
    PaymentCheckItem(
        iconResId = R.drawable.ic_medical_documents_folder,
        title = "현금·계좌이체·송금 가능 여부",
        description = "카드 외에 현금, 계좌이체, 해외송금 결제가 가능한지 확인하세요."
    ),
    PaymentCheckItem(
        iconResId = R.drawable.ic_medical_receipt,
        title = "보증금·선납금 결제 방식",
        description = "선납금이 필요한 경우 어떤 수단으로 결제해야 하는지 확인하세요."
    )
)

private val TOGETHER_CHECKLIST = listOf(
    PaymentCheckItem(
        iconResId = R.drawable.ic_insurance_document_check,
        title = "카드 한도·해외결제 차단",
        description = "결제 전에 카드 한도와 해외 사용 차단 여부를 미리 확인하세요."
    ),
    PaymentCheckItem(
        iconResId = R.drawable.ic_hospital_location_map,
        title = "현장 결제 위치",
        description = "응급실, 외래, 입원 등 어디에서 수납하는지 확인하면 더 편리해요."
    )
)

// S-06 하위 STEP05 상세의 "결제 가능 수단 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodCheckDetailScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = PageBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                title = {
                    Text(
                        text = "05-02 결제 가능 수단 확인",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            GuideDetailBanner(
                backgroundResId = R.drawable.img_available_payment_methods_banner,
                aspectRatio = 1448f / 1086f,
                title = "결제 가능 수단 확인",
                subtitle = "치료 전이나 퇴원 전에 실제로 사용 가능한 결제 수단을 미리 확인해 보세요.",
                stepLabel = "STEP 05",
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "결제 전에 체크하세요")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BEFORE_PAYMENT_CHECKLIST.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "함께 확인하면 좋아요")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TOGETHER_CHECKLIST.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            GuideDetailNoticeBanner(
                iconResId = R.drawable.ic_guide_information,
                text = "병원마다 가능한 결제 수단이 다르니 실제 수납 전 다시 확인해 주세요.",
                modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
            )
        }
    }
}
