package com.mediinbusan.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.SettingsBorder
import com.mediinbusan.app.core.designsystem.SettingsItemTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsPrimaryText
import com.mediinbusan.app.core.designsystem.SettingsSecondaryText

/**
 * 검색/즐겨찾기 등에서 공용으로 쓰는 필터·정렬 UI 조각. HospitalSearchListScreen의 필터 칩 +
 * 정렬 드롭다운 디자인을 그대로 뽑아낸 것으로, 다른 화면에서도 같은 룩을 쓰고 싶을 때 재사용한다.
 */
@Composable
fun FilterChipPill(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShapePercent50)
            .background(if (selected) CoralPrimaryContainer else Color.White)
            .border(
                width = 1.dp,
                color = if (selected) CoralPrimaryContainer else SettingsBorder,
                shape = RoundedCornerShapePercent50
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) CoralPrimary else SettingsSecondaryText
        )
    }
}

// 정렬/필터 드롭다운 공용 스타일: Material3 기본 대신 카드 톤(라운드 16dp+흰 배경)으로, 선택된
// 항목은 코랄 배경 틴트+굵은 코랄 텍스트+체크 아이콘으로 강조한다.
@Composable
fun BrandDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
        shadowElevation = 6.dp,
        content = content
    )
}

@Composable
fun BrandDropdownMenuItem(label: String, selected: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = {
            Text(
                text = label,
                style = SettingsItemTitleStyle.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal),
                color = if (selected) CoralPrimary else SettingsPrimaryText
            )
        },
        trailingIcon = {
            if (selected) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(18.dp))
            }
        },
        onClick = onClick,
        modifier = Modifier.background(if (selected) CoralPrimaryContainer else Color.Transparent)
    )
}

private val RoundedCornerShapePercent50 = RoundedCornerShape(percent = 50)
