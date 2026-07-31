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

private data class DepartureChecklistItem(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String
)

private val DEPARTURE_CHECKLIST = listOf(
    DepartureChecklistItem(
        iconResId = R.drawable.ic_carry_on_medical_documents,
        title = "기내 반입 의료서류 확인",
        description = "여권, 진단서 등 기내에 직접 소지할 의료서류를 확인하세요."
    ),
    DepartureChecklistItem(
        iconResId = R.drawable.ic_liquid_medicine_medical_supplies,
        title = "액체 의약품·의료용품 확인",
        description = "물약, 안약 등 액체류 의약품의 반입 용량 기준을 확인하세요."
    ),
    DepartureChecklistItem(
        iconResId = R.drawable.ic_airport_security_screening_preparation,
        title = "공항 보안검색 준비",
        description = "보안검색 시 의약품과 의료용품을 미리 꺼내둘 수 있도록 준비하세요."
    ),
    DepartureChecklistItem(
        iconResId = R.drawable.ic_restricted_medicine_check,
        title = "의약품 반입 제한 확인",
        description = "처방약, 한약 등 반입 제한 품목인지 확인하세요."
    ),
    DepartureChecklistItem(
        iconResId = R.drawable.ic_passport_validity_check,
        title = "여권 유효기간 재확인",
        description = "귀국편 탑승에 필요한 여권 유효기간을 다시 확인하세요."
    ),
    DepartureChecklistItem(
        iconResId = R.drawable.ic_long_flight_seat,
        title = "항공권·수하물 확인",
        description = "항공권 일정과 수하물 규정을 확인하세요."
    )
)

// S-06 하위 STEP06 상세의 "귀국 전 반입·공항 준비" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirportDeparturePreparationDetailScreen(onBack: () -> Unit) {
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
                        text = "06-04 귀국 전 반입·공항 준비",
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
                title = "귀국 전 반입·공항 준비",
                subtitle = "약 반입 제한과 귀국 전 필요한 준비 항목을 미리 확인하세요.",
                stepLabel = "STEP 06",
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "출국 전 체크리스트")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DEPARTURE_CHECKLIST.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            GuideDetailNoticeBanner(
                iconResId = R.drawable.ic_guide_information,
                text = "반입 제한 품목과 세관 규정은 국가마다 다르니 항공사·세관 안내를 확인해 주세요.",
                modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
            )
        }
    }
}
