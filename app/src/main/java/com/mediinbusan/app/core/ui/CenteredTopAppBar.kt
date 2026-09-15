package com.mediinbusan.app.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.i18n.LocalAppStrings

/**
 * wellness_tourism_recommendation_list.png 기준 — 뒤로가기 + 완전히 가운데 정렬된 굵은 검정
 * 제목만 있는 가벼운 헤더(동백꽃 장식 없음). 원래 관광 카탈로그 리스트업(TourismCatalogScreen)
 * 전용이었는데, 관광 카탈로그 상세(TourismCatalogItemDetailScreen)와 웰니스 장소 상세
 * (PlaceDetailScreen)도 같은 탑바를 쓰게 되면서 여기로 옮겼다 — feature 패키지끼리는 서로 import할
 * 수 없다(CLAUDE.md §4). 여러 화면을 오가도 탑바 생김새가 절대 바뀌지 않는다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredTopAppBar(title: String, onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = LocalAppStrings.current.common.backContentDescription,
                    tint = TextPrimary
                )
            }
        },
        title = {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        }
    )
}
