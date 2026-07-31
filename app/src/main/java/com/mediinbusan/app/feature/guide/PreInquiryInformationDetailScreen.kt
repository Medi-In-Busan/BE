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

private data class PreInquiryChecklistItem(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String
)

private val PRE_INQUIRY_CHECKLIST = listOf(
    PreInquiryChecklistItem(
        iconResId = R.drawable.ic_desired_treatment_checklist,
        title = "희망 진료·검사 내용",
        description = "받고 싶은 진료나 검사 항목을 구체적으로 정리하세요."
    ),
    PreInquiryChecklistItem(
        iconResId = R.drawable.ic_symptoms_medical_records,
        title = "현재 증상과 기존 자료",
        description = "현재 증상과 기존 진단서·검사 결과가 있다면 함께 정리하세요."
    ),
    PreInquiryChecklistItem(
        iconResId = R.drawable.ic_preferred_visit_schedule,
        title = "방문 희망 시기",
        description = "방문을 원하는 대략적인 날짜나 기간을 정리하세요."
    ),
    PreInquiryChecklistItem(
        iconResId = R.drawable.ic_basic_personal_information,
        title = "기본 인적 정보",
        description = "이름, 생년월일, 국적 등 기본 정보를 준비하세요."
    ),
    PreInquiryChecklistItem(
        iconResId = R.drawable.ic_estimated_cost_inquiry,
        title = "예상 비용 문의 여부",
        description = "예상 비용을 함께 문의할지 미리 정리해두세요."
    ),
    PreInquiryChecklistItem(
        iconResId = R.drawable.ic_english_document_requirement,
        title = "영문 서류 필요 여부",
        description = "귀국 후 필요한 영문 서류가 있는지 미리 확인하세요."
    )
)

// S-06 하위 STEP02 상세의 "문의 전 전달할 정보 정리" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreInquiryInformationDetailScreen(onBack: () -> Unit) {
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
                        text = "02-03 문의 전 전달할 정보 정리",
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
                backgroundResId = R.drawable.img_pre_inquiry_information_banner,
                aspectRatio = 1672f / 941f,
                title = "문의 전 전달할 정보 정리",
                subtitle = "희망 진료와 증상, 방문 시기 등 핵심 정보만 정리해 두면 병원이 더 정확하게 안내할 수 있어요.",
                stepLabel = "STEP 02",
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "전달 정보 체크리스트")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PRE_INQUIRY_CHECKLIST.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            GuideDetailNoticeBanner(
                iconResId = R.drawable.ic_guide_information,
                text = "정리한 정보는 병원 문의 시 그대로 전달하면 상담이 더 빠르고 정확해져요.",
                modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
            )
        }
    }
}