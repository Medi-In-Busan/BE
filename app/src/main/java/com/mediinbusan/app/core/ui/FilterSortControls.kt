package com.mediinbusan.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
    // 필터는 화면이 통째로 다시 그려져서 무엇이 바뀌었는지 눈으로 좇기 어렵다 — 누른 순간의
    // 가벼운 틱이 "내 입력이 먹었다"를 즉시 알려준다. 공용 조각이라 필터를 쓰는 화면 전부에 걸린다.
    val haptics = rememberHaptics()
    Box(
        modifier = modifier
            .clip(RoundedCornerShapePercent50)
            .background(if (selected) CoralPrimaryContainer else Color.White)
            .border(
                width = 1.dp,
                color = if (selected) CoralPrimaryContainer else SettingsBorder,
                shape = RoundedCornerShapePercent50
            )
            .clickable { haptics.tick(); onClick() }
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
// maxHeight를 주면 그 높이로 제한하고 세로 스크롤 가능하게 만든다 — 항목이 많아 전체 펼치면
// 화면을 거의 다 덮는 팝업처럼 보이는 드롭다운(예: 지역 선택)에 사용.
// 주의: 이 안에 LazyColumn을 넣으면 안 된다 — Material3 DropdownMenuContent가 바깥을
// Column(modifier.width(IntrinsicSize.Max)...)으로 감싸는데, LazyColumn은 intrinsic 측정을
// 지원하지 않아 열자마자 UnsupportedOperationException으로 즉시 크래시한다. 일반 Column +
// verticalScroll만 사용할 것.
@Composable
fun BrandDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    maxHeight: Dp? = null,
    modifier: Modifier = Modifier,
    // 앵커(선택 칸) 바로 아래에서 하나로 이어지는 느낌을 내고 싶을 때, 위쪽 모서리만 각지게(0dp)
    // 깎은 shape를 넘긴다 — 지역 드롭다운(TourismCatalogScreen.PlacesDistrictDropdownPill)이 사용.
    shape: CornerBasedShape = RoundedCornerShape(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        shape = shape,
        containerColor = Color.White,
        shadowElevation = 6.dp
    ) {
        if (maxHeight != null) {
            Column(modifier = Modifier.heightIn(max = maxHeight).verticalScroll(rememberScrollState())) {
                content()
            }
        } else {
            content()
        }
    }
}

// Material3 DropdownMenuItem 대신 직접 짠 Row — 기본 DropdownMenuItem은 trailingIcon 슬롯을
// 위해 항상 일정 너비를 예약해서, 앵커 너비에 맞춰 좁힌 드롭다운(지역 선택 등)에서 "부산진구"
// 같은 4글자 라벨도 줄바꿈되는 문제가 있었다. 체크 아이콘은 selected일 때만 자리를 차지한다.
@Composable
fun BrandDropdownMenuItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    accentColor: Color = CoralPrimary
) {
    val haptics = rememberHaptics()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) accentColor.copy(alpha = 0.12f) else Color.Transparent)
            .clickable { haptics.tick(); onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = SettingsItemTitleStyle.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal),
            color = if (selected) accentColor else SettingsPrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
        }
    }
}

private val RoundedCornerShapePercent50 = RoundedCornerShape(percent = 50)
