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

private data class MedicationChecklistItem(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String
)

private val MEDICATION_CHECKLIST = listOf(
    MedicationChecklistItem(
        iconResId = R.drawable.ic_current_medication_info,
        title = "약 이름 확인",
        description = "처방받은 약 이름과 성분을 확인해두세요."
    ),
    MedicationChecklistItem(
        iconResId = R.drawable.ic_expected_arrival_time,
        title = "복용 시간·식전후 여부 확인",
        description = "정해진 복용 시간과 식전·식후 여부를 놓치지 않도록 확인하세요."
    ),
    MedicationChecklistItem(
        iconResId = R.drawable.ic_return_date_calendar,
        title = "복용 기간 확인",
        description = "며칠간 복용해야 하는지, 언제까지 복용하는지 확인하세요."
    ),
    MedicationChecklistItem(
        iconResId = R.drawable.ic_caution_warning,
        title = "보관 및 주의사항 확인",
        description = "보관 방법과 함께 복용하면 안 되는 약이 있는지 확인하세요."
    )
)

// S-06 하위 STEP06 상세의 "약 복용 방법 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScheduleDetailScreen(onBack: () -> Unit) {
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
                        text = "06-01 약 복용 방법 확인",
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
                backgroundResId = R.drawable.img_post_treatment_travel_preparation_banner,
                aspectRatio = 1672f / 941f,
                title = "약 복용 방법 확인",
                subtitle = "약 이름과 복용 시간, 복용 기간을 미리 확인하면 안전하게 복용할 수 있어요.",
                stepLabel = "STEP 06",
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "복용 전 체크리스트")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MEDICATION_CHECKLIST.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            GuideDetailNoticeBanner(
                iconResId = R.drawable.ic_guide_information,
                text = "정확한 복용법은 처방전과 약사 안내를 기준으로 다시 확인해 주세요.",
                modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
            )
        }
    }
}
