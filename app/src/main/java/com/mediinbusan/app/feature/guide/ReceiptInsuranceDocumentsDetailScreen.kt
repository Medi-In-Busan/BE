package com.mediinbusan.app.feature.guide

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.BorderColor
import com.mediinbusan.app.core.designsystem.PageBackground
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary

private data class DocumentChecklistEntry(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String
)

private val REQUIRED_DOCUMENTS = listOf(
    DocumentChecklistEntry(
        iconResId = R.drawable.ic_medical_receipt,
        title = "영수증",
        description = "결제 금액을 확인할 수 있는 기본 영수증을 받아두세요."
    ),
    DocumentChecklistEntry(
        iconResId = R.drawable.ic_results_document,
        title = "진료비 세부내역서",
        description = "비용 항목이 자세히 적힌 서류가 필요한지 확인하세요."
    ),
    DocumentChecklistEntry(
        iconResId = R.drawable.ic_examination_name_clipboard,
        title = "보험 청구용 서류",
        description = "보험사에서 요구하는 진단서, 소견서, 확인서 여부를 확인하세요."
    ),
    DocumentChecklistEntry(
        iconResId = R.drawable.ic_english_documents_availability,
        title = "영문 서류 발급 가능 여부",
        description = "본국 제출이 필요한 경우 영문 발급 가능 여부를 함께 확인하세요."
    )
)

private val SITUATIONAL_CHECKLIST = listOf(
    DocumentChecklistEntry(
        iconResId = R.drawable.ic_insurance_document_check,
        title = "보험사 요구 양식 여부",
        description = "보험사 전용 양식이나 추가 증빙 서류가 필요한 경우만 확인하세요."
    ),
    DocumentChecklistEntry(
        iconResId = R.drawable.ic_beauty_treatment_tax_refund_eligibility,
        title = "미용시술 세금 환급 가능 여부",
        description = "피부·미용 시술을 받은 경우에만 세금 환급 가능 여부를 확인하세요."
    )
)

private val QUESTIONS = listOf(
    "영수증과 진료비 세부내역서를 받을 수 있을까요?",
    "보험 청구에 필요한 서류를 함께 발급받을 수 있나요?",
    "영문 서류 발급이 가능하면 언제 받을 수 있나요?"
)

// S-06 하위 STEP05 상세의 "영수증·보험 청구 서류 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptInsuranceDocumentsDetailScreen(onBack: () -> Unit) {
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
                        text = "05-03 영수증·보험 청구 서류 확인",
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
                backgroundResId = R.drawable.img_receipt_insurance_documents_banner,
                aspectRatio = 1448f / 1086f,
                title = "영수증·보험 청구 서류 확인",
                subtitle = "결제 후 필요한 서류를 미리 확인하고 발급 가능 여부를 체크하면 보험 청구와 환급 절차가 더 쉬워집니다.",
                stepLabel = "STEP 05",
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "받아야 할 서류")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    REQUIRED_DOCUMENTS.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "상황별 확인")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SITUATIONAL_CHECKLIST.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "이렇게 요청해 보세요")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QUESTIONS.forEachIndexed { index, question ->
                        QuestionCard(number = index + 1, question = question)
                    }
                }
            }

            GuideDetailNoticeBanner(
                iconResId = R.drawable.ic_guide_information,
                text = "필요 서류는 보험사와 병원 기준이 다를 수 있으니 둘 다 확인해 주세요.",
                modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun QuestionCard(number: Int, question: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(SkyBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Text(
                text = question,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(start = 14.dp)
            )
        }
    }
}
