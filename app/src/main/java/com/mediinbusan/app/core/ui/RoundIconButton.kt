package com.mediinbusan.app.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.TextPrimary

/** 원형 배경 위 아이콘 버튼. 뒤로가기·공유·길찾기 등 화면 상단/지도 오버레이 컨트롤에서 공용으로 쓴다. */
@Composable
fun RoundIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    background: Color = Color.White,
    tint: Color = TextPrimary,
    shape: Shape = CircleShape
) {
    RoundIconButtonContainer(onClick, modifier, size, background, shape) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = tint)
    }
}

/**
 * hospital_detail_share.png 등 Figma 공통 컴포넌트로 내려받은 코랄 라인 아이콘(래스터 PNG)을 쓰는
 * 화면(PlaceDetailScreen 등)용 오버로드. Icon(ImageVector)과 달리 Image는 자체 색을 갖고 있어
 * 그대로 두면 배경과 무관하게 항상 원본 코랄 색으로 보인다 — [tint]를 주면 ColorFilter로 덮어써서
 * (예: 코랄 배경 위에서는 흰색으로) 다른 배경에서도 재사용할 수 있게 한다.
 */
@Composable
fun RoundIconButton(
    painter: Painter,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    background: Color = Color.White,
    tint: Color? = null,
    shape: Shape = CircleShape,
    iconSize: Dp = size * 0.4f
) {
    RoundIconButtonContainer(onClick, modifier, size, background, shape) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
            colorFilter = tint?.let { ColorFilter.tint(it) }
        )
    }
}

// 지도/병원 상세의 뒤로가기·공유 버튼처럼 시각적으로 36dp로 줄여 쓰는 곳이 있어, 실제 터치
// 영역은 안드로이드 최소 권장치(48dp) 밑으로 안 내려가게 minimumInteractiveComponentSize()로
// 보이지 않는 여백을 더한다 — 원 자체가 커지진 않는다. 두 오버로드(ImageVector/Painter)가
// 공유하는 원형 배경 셸.
@Composable
private fun RoundIconButtonContainer(
    onClick: () -> Unit,
    modifier: Modifier,
    size: Dp,
    background: Color,
    shape: Shape,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .size(size)
            .clip(shape)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
