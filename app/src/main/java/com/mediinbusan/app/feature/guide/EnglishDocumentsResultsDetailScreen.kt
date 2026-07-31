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

private data class DocumentResultChecklistItem(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String
)

private val DOCUMENT_RESULT_CHECKLIST = listOf(
    DocumentResultChecklistItem(
        iconResId = R.drawable.ic_english_medical_certificate,
        title = "영문 진단서·소견서 수령",
        description = "영문으로 발급되는 진단서나 소견서를 받았는지 확인하세요."
    ),
    DocumentResultChecklistItem(
        iconResId = R.drawable.ic_test_results_imaging_files,
        title = "검사결과·영상자료 수령",
        description = "검사결과와 영상자료를 파일이나 CD로 받을 수 있는지 확인하세요."
    ),
    DocumentResultChecklistItem(
        iconResId = R.drawable.ic_issuance_time_fee,
        title = "발급 소요시간·비용 확인",
        description = "서류 발급에 걸리는 시간과 추가 비용이 있는지 확인하세요."
    ),
    DocumentResultChecklistItem(
        iconResId = R.drawable.ic_original_copy_document_check,
        title = "원본·사본 서류 확인",
        description = "원본과 사본 중 어떤 서류가 필요한지 확인하세요."
    ),
    DocumentResultChecklistItem(
        iconResId = R.drawable.ic_airport_departure_pickup_complete,
        title = "공항 출국 전 수령 완료 확인",
        description = "출국 전에 모든 서류를 수령했는지 다시 한번 확인하세요."
    ),
    DocumentResultChecklistItem(
        iconResId = R.drawable.ic_email_file_receipt_available,
        title = "이메일·파일 수령 가능 여부",
        description = "귀국 후에도 이메일이나 파일로 받을 수 있는지 확인하세요."
    )
)

// S-06 하위 STEP06 상세의 "영문 서류·검사결과 수령 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnglishDocumentsResultsDetailScreen(onBack: () -> Unit) {
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
                        text = "06-03 영문 서류·검사결과 수령 확인",
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
                backgroundResId = R.drawable.img_english_documents_test_results_banner,
                aspectRatio = 1491f / 1055f,
                title = "영문 서류·검사결과 수령 확인",
                subtitle = "귀국 후 제출이 필요한 서류와 검사결과 수령 여부를 미리 확인하세요.",
                stepLabel = "STEP 06",
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "수령 전 체크리스트")
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DOCUMENT_RESULT_CHECKLIST.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            GuideDetailNoticeBanner(
                iconResId = R.drawable.ic_guide_information,
                text = "필요한 서류 종류는 본국 제출 기관 기준에 따라 다를 수 있어요.",
                modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
            )
        }
    }
}
