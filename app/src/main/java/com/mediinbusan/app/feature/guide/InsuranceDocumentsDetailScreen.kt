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

private data class DocumentChecklistItem(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String
)

private val DOCUMENT_CHECKLIST = listOf(
    DocumentChecklistItem(
        iconResId = R.drawable.ic_passport_copy,
        title = "여권 사본",
        description = "출입국 심사와 병원 접수 시 필요할 수 있어요."
    ),
    DocumentChecklistItem(
        iconResId = R.drawable.ic_appointment_confirmation,
        title = "진료 예약 확인서",
        description = "예약한 병원의 예약 확인서나 문자를 준비해 두세요."
    ),
    DocumentChecklistItem(
        iconResId = R.drawable.ic_medical_report_document,
        title = "진단서 또는 소견서",
        description = "기존에 받은 진단서나 소견서가 있다면 함께 준비하세요."
    ),
    DocumentChecklistItem(
        iconResId = R.drawable.ic_medical_receipt,
        title = "진료비 영수증",
        description = "보험 청구나 세금 환급에 필요할 수 있어 보관해 두세요."
    )
)

// S-06 하위 STEP 상세의 "보험·서류 준비" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceDocumentsDetailScreen(onBack: () -> Unit) {
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
                        text = "01-02 보험·서류 준비",
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
                bannerResId = R.drawable.img_insurance_documents_banner,
                aspectRatio = 1536f / 1024f,
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "준비 서류 체크리스트")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DOCUMENT_CHECKLIST.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            GuideDetailNoticeBanner(
                iconResId = R.drawable.ic_guide_information,
                text = "본 안내는 보험 보장 범위를 보장하지 않습니다. 정확한 보장 내용은 가입한 보험사에 확인해 주세요.",
                modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
            )
        }
    }
}