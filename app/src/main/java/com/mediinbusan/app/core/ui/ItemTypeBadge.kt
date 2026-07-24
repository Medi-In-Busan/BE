package com.mediinbusan.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.InfoBackgroundBlue
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.data.favorite.FavoriteItemType

/** 즐겨찾기·최근 본 항목 목록에서 병원/장소를 구분하는 색상 배지. */
@Composable
fun ItemTypeBadge(itemType: FavoriteItemType, modifier: Modifier = Modifier) {
    val (background, tint, label) = when (itemType) {
        FavoriteItemType.HOSPITAL -> Triple(InfoBackgroundBlue, SkyBlue, "병원")
        FavoriteItemType.PLACE -> Triple(CoralPrimaryContainer, CoralPrimary, "장소")
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = tint)
    }
}
