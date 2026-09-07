package com.mediinbusan.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer

/**
 * 사진(imageUrl)이 없는 관광/핫플레이스 썸네일에 쓰는 공용 대체 이미지 — 코랄핑크 그라데이션
 * 배경 위에 지도 마커 아이콘을 얹는다. 핫플레이스 Top5(NearbyScreen)와 부산 핫플레이스
 * 전체보기 목록(TourismCatalogScreen의 CrowdingRankCard)이 같은 패턴을 공유한다.
 */
@Composable
fun MapMarkerFallbackThumbnail(modifier: Modifier = Modifier, iconSize: Dp = 28.dp) {
    Box(
        modifier = modifier.background(Brush.linearGradient(listOf(CoralPrimaryContainer, Color.White))),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = CoralPrimary.copy(alpha = 0.55f),
            modifier = Modifier.size(iconSize)
        )
    }
}
