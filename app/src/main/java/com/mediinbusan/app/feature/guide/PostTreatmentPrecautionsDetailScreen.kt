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

private data class PrecautionChecklistItem(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String
)

private val PRECAUTION_CHECKLIST = listOf(
    PrecautionChecklistItem(
        iconResId = R.drawable.ic_meal_shower_exercise_time,
        title = "식사 가능 시점 확인",
        description = "금식 해제 시점과 먹어도 되는 음식을 확인하세요."
    ),
    PrecautionChecklistItem(
        iconResId = R.drawable.ic_restricted_activities,
        title = "샤워·운동 제한 확인",
        description = "샤워, 사우나, 운동이 가능한 시점을 확인하세요."
    ),
    PrecautionChecklistItem(
        iconResId = R.drawable.ic_warning_symptoms,
        title = "이상 증상 기준 확인",
        description = "발열, 출혈 등 병원에 연락해야 하는 증상 기준을 확인하세요."
    ),
    PrecautionChecklistItem(
        iconResId = R.drawable.ic_followup_visit_needed,
        title = "재방문 필요 여부 확인",
        description = "추가 진료나 재방문이 필요한지 확인하세요."
    )
)

// S-06 하위 STEP06 상세의 "진료 후 주의사항 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTreatmentPrecautionsDetailScreen(onBack: () -> Unit) {
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
                        text = "06-02 진료 후 주의사항 확인",
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
                backgroundResId = R.drawable.img_post_treatment_precautions,
                aspectRatio = 1672f / 941f,
                title = "진료 후 주의사항 확인",
                subtitle = "식사, 샤워, 운동 가능 시점과 이상 증상 기준을 미리 확인하세요.",
                stepLabel = "STEP 06",
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "회복 중 체크리스트")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PRECAUTION_CHECKLIST.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            GuideDetailNoticeBanner(
                iconResId = R.drawable.ic_guide_information,
                text = "회복 속도는 개인차가 있으니 의료진의 안내를 우선해 주세요.",
                modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
            )
        }
    }
}
