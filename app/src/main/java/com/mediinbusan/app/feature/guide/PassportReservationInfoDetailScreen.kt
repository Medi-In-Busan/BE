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

private data class PassportReservationChecklistItem(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String
)

private val PASSPORT_RESERVATION_CHECKLIST = listOf(
    PassportReservationChecklistItem(
        iconResId = R.drawable.ic_passport_identity_verification,
        title = "여권 신원 확인",
        description = "여권 또는 외국인등록증으로 신원을 확인할 수 있도록 준비하세요."
    ),
    PassportReservationChecklistItem(
        iconResId = R.drawable.ic_appointment_confirmation,
        title = "예약 확인",
        description = "예약 일시와 진료과를 다시 한번 확인하세요."
    ),
    PassportReservationChecklistItem(
        iconResId = R.drawable.ic_patient_name_verification,
        title = "환자 성명 확인",
        description = "여권상 영문 성명과 예약자 정보가 일치하는지 확인하세요."
    ),
    PassportReservationChecklistItem(
        iconResId = R.drawable.ic_contact_or_messenger,
        title = "연락처 확인",
        description = "병원에서 연락 가능한 전화번호나 메신저를 준비하세요."
    ),
    PassportReservationChecklistItem(
        iconResId = R.drawable.ic_companion_check,
        title = "동반자 확인",
        description = "동반자가 있다면 인원과 관계를 미리 정리하세요."
    ),
    PassportReservationChecklistItem(
        iconResId = R.drawable.ic_expected_arrival_time,
        title = "도착 예정 시간",
        description = "병원 도착 예정 시간을 미리 확인해두면 접수가 수월해요."
    )
)

// S-06 하위 STEP03 상세의 "여권·예약정보 준비" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassportReservationInfoDetailScreen(onBack: () -> Unit) {
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
                        text = "03-01 여권·예약정보 준비",
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
                backgroundResId = R.drawable.img_passport_reservation_preparation_banner,
                aspectRatio = 1536f / 1024f,
                title = "여권·예약정보 준비",
                subtitle = "여권과 예약 정보를 미리 준비하면 접수가 더 빨라져요.",
                stepLabel = "STEP 03",
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "준비물 체크리스트")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PASSPORT_RESERVATION_CHECKLIST.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            GuideDetailNoticeBanner(
                iconResId = R.drawable.ic_guide_information,
                text = "정리한 정보는 접수 시 안내 직원에게 전달하면 더 빠르게 도와드려요.",
                modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
            )
        }
    }
}