package com.mediinbusan.app.feature.guide

import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.PageBackground
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.ui.launchIntentSafely

private data class OfficialSiteLink(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String,
    val url: String
)

// 공식 사이트 URL, 각 기관 공식 도메인
private val OFFICIAL_SITE_LINKS = listOf(
    OfficialSiteLink(
        iconResId = R.drawable.ic_k_eta_official_site,
        title = "K-ETA 공식 사이트",
        description = "무비자 입국 대상국이라면 K-ETA 사전 승인이 필요해요.",
        url = "https://www.k-eta.go.kr"
    ),
    OfficialSiteLink(
        iconResId = R.drawable.ic_korea_visa_portal,
        title = "대한민국 비자포털",
        description = "체류 목적별 비자 종류와 신청 절차를 확인할 수 있어요.",
        url = "https://www.visa.go.kr"
    ),
    OfficialSiteLink(
        iconResId = R.drawable.ic_hikorea_residence_guide,
        title = "HiKorea 체류 안내",
        description = "입국 후 체류 자격, 외국인등록 등 안내를 확인할 수 있어요.",
        url = "https://www.hikorea.go.kr"
    )
)

// S-06 하위 STEP 상세의 "비자·입국 조건 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisaEntryCheckDetailScreen(onBack: () -> Unit) {
    val context = LocalContext.current

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
                        text = "비자·입국 조건 확인",
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
                backgroundResId = R.drawable.img_visa_entry_check_banner,
                aspectRatio = 1536f / 1024f,
                title = "비자와 입국 조건을 먼저 확인하세요.",
                subtitle = "국적과 체류기간, 방문 목적에 따라 비자 또는 K-ETA가 필요할 수 있어요.",
                stepLabel = "STEP 01",
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(
                modifier = Modifier.padding(top = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OFFICIAL_SITE_LINKS.forEach { link ->
                    GuideDetailItemCard(
                        iconResId = link.iconResId,
                        title = link.title,
                        description = link.description,
                        trailingIcon = Icons.AutoMirrored.Filled.OpenInNew,
                        trailingIconTint = SkyBlue,
                        onClick = { context.launchIntentSafely(Intent(Intent.ACTION_VIEW, Uri.parse(link.url))) }
                    )
                }
            }

            GuideDetailNoticeBanner(
                iconResId = R.drawable.ic_visa_guide_information,
                text = "본 안내는 비자 발급을 보장하지 않습니다. 정확한 자격 요건은 각 공식 사이트에서 반드시 확인해 주세요.",
                modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
            )
        }
    }
}