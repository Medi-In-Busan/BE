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

private data class HospitalInquiryChecklistItem(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String
)

private val HOSPITAL_INQUIRY_CHECKLIST = listOf(
    HospitalInquiryChecklistItem(
        iconResId = R.drawable.ic_passport_validity_check,
        title = "여권 유효기간 확인",
        description = "입국일 기준 여권 유효기간이 충분한지 확인하세요."
    ),
    HospitalInquiryChecklistItem(
        iconResId = R.drawable.ic_stay_duration_visit_purpose,
        title = "체류기간 및 방문 목적 정리",
        description = "예상 체류기간과 방문 목적을 정리해두면 문의가 수월해요."
    ),
    HospitalInquiryChecklistItem(
        iconResId = R.drawable.ic_contact_accommodation_info,
        title = "연락처 및 숙소 정보 준비",
        description = "현지 연락처와 숙소 정보를 미리 준비해두세요."
    ),
    HospitalInquiryChecklistItem(
        iconResId = R.drawable.ic_hospital_info_schedule,
        title = "병원 정보 및 일정 정리",
        description = "문의할 병원명과 희망 일정을 정리해두세요."
    )
)

// S-06 하위 STEP 상세의 "병원 문의 전 정보 정리" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalInquiryDetailScreen(onBack: () -> Unit) {
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
                        text = "01-03 병원 문의 전 정보 정리",
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
                backgroundResId = R.drawable.img_entry_preparation_banner,
                aspectRatio = 1672f / 941f,
                title = "입국 전 필요한 준비를 미리 챙겨두세요.",
                subtitle = "여권, 체류기간, 방문 목적, 연락처 등 기본 정보를 미리 정리하면 입국과 병원 방문이 편리해요.",
                stepLabel = "STEP 01",
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "정보 정리 체크리스트")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HOSPITAL_INQUIRY_CHECKLIST.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            GuideDetailNoticeBanner(
                iconResId = R.drawable.ic_entry_preparation_tip,
                text = "문의 전 정보를 미리 정리해두면 병원과의 소통이 더 원활해져요.",
                modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
            )
        }
    }
}