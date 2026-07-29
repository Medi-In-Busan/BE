package com.mediinbusan.app.feature.guide

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.GuideCardLavenderBackground
import com.mediinbusan.app.core.designsystem.GuideCardPeachBackground
import com.mediinbusan.app.core.designsystem.PageBackground
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary

private data class MedicalRecordsChecklistItem(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String
)

private val MEDICAL_RECORDS_CHECKLIST = listOf(
    MedicalRecordsChecklistItem(
        iconResId = R.drawable.ic_medical_report_or_opinion,
        title = "기존 진단서 또는 소견서",
        description = "받았던 진단 내용이나 소견서를 준비하세요."
    ),
    MedicalRecordsChecklistItem(
        iconResId = R.drawable.ic_xray_test_result,
        title = "검사결과 또는 영상자료",
        description = "혈액검사, 영상검사 결과가 있다면 함께 준비하세요."
    ),
    MedicalRecordsChecklistItem(
        iconResId = R.drawable.ic_current_medication_info,
        title = "복용 중인 약 정보",
        description = "현재 복용 중인 약 이름이나 처방 내용을 정리하세요."
    ),
    MedicalRecordsChecklistItem(
        iconResId = R.drawable.ic_allergy_or_underlying_condition,
        title = "알레르기·기저질환 정보",
        description = "알레르기나 중요한 건강정보가 있다면 함께 전달하세요."
    )
)

private data class SituationalPrepItem(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String,
    val backgroundColor: Color
)

private val SITUATIONAL_PREP_ITEMS = listOf(
    SituationalPrepItem(
        iconResId = R.drawable.ic_english_or_translation_material,
        title = "영문 또는 번역 자료",
        description = "있는 경우 함께 가져가면 설명이 더 쉬워져요.",
        backgroundColor = GuideCardPeachBackground
    ),
    SituationalPrepItem(
        iconResId = R.drawable.ic_digital_file_preparation,
        title = "디지털 파일 준비",
        description = "사진이나 PDF 파일도 함께 준비해 두세요.",
        backgroundColor = GuideCardLavenderBackground
    )
)

// S-06 하위 STEP03 상세의 "기존 진단서·검사결과 준비" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalRecordsTestResultsDetailScreen(onBack: () -> Unit) {
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
                        text = "03-02 기존 진단서·검사결과 준비",
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
                backgroundResId = R.drawable.img_medical_records_test_results_banner,
                aspectRatio = 1536f / 1024f,
                title = "기존 진단서·검사결과 준비",
                subtitle = "기존 자료를 미리 준비하면 의료진이 상태를 더 빠르게 이해할 수 있어요.",
                stepLabel = "STEP 03",
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "준비하면 좋은 자료")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MEDICAL_RECORDS_CHECKLIST.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "상황에 따라 준비하세요")
                Row(
                    modifier = Modifier.padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SITUATIONAL_PREP_ITEMS.forEach { item ->
                        SituationalPrepCard(item = item, modifier = Modifier.weight(1f))
                    }
                }
            }

            GuideDetailNoticeBanner(
                iconResId = R.drawable.ic_guide_information,
                text = "모든 자료가 꼭 필요한 것은 아니며 병원이 요청한 자료를 우선 준비해 주세요.",
                modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun SituationalPrepCard(item: SituationalPrepItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = item.backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(painter = painterResource(id = item.iconResId), contentDescription = null, modifier = Modifier.size(36.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
