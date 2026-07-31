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

private data class CostChecklistItem(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String
)

private val COST_CHECKLIST = listOf(
    CostChecklistItem(
        iconResId = R.drawable.ic_examination_name_clipboard,
        title = "진료·검사·시술비",
        description = "상담료, 검사비, 시술비 등 기본 비용 항목을 확인하세요."
    ),
    CostChecklistItem(
        iconResId = R.drawable.ic_current_medication_info,
        title = "약제비·재료비 포함 여부",
        description = "약값, 소모품, 재료비가 총 비용에 포함되는지 확인하세요."
    ),
    CostChecklistItem(
        iconResId = R.drawable.ic_caution_warning,
        title = "추가 비용 발생 가능 항목",
        description = "추가 검사, 마취, 입원, 병실 이용 시 비용이 달라질 수 있어요."
    ),
    CostChecklistItem(
        iconResId = R.drawable.ic_medical_documents_folder,
        title = "보증금·선납금 필요 여부",
        description = "입원이나 예약 조건에 따라 선납금이 필요한 경우만 확인하세요."
    )
)

private val QUESTIONS = listOf(
    "총 비용에 포함되지 않는 항목이 있나요?",
    "추가 검사나 입원이 생기면 비용이 얼마나 달라지나요?",
    "보증금 또는 선납금이 필요한가요?"
)

// S-06 하위 STEP05 상세의 "총 비용과 포함 항목 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TotalCostCoverageCheckDetailScreen(onBack: () -> Unit) {
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
                        text = "05-01 총 비용과 포함 항목 확인",
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
                backgroundResId = R.drawable.img_payment_billing_banner,
                aspectRatio = 1448f / 1086f,
                title = "총 비용과 포함 항목 확인",
                subtitle = "결제 전, 상담료·검사비·시술비·약제비 등 총 비용과 포함·불포함 항목을 꼼꼼히 확인하세요.",
                stepLabel = "STEP 05",
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "먼저 확인하세요")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    COST_CHECKLIST.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "이렇게 물어보세요")
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
                text = "최종 비용은 진료 내용에 따라 달라질 수 있으니 병원 안내를 기준으로 확인해 주세요.",
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
