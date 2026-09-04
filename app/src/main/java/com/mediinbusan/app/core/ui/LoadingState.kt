package com.mediinbusan.app.core.ui

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CoralMuted
import com.mediinbusan.app.core.designsystem.CoralPrimary

/**
 * F-019: 로딩 상태를 모든 화면에서 동일하게 표시하기 위한 공용 컴포넌트.
 * 반투명 + 블러 배경 위에 코랄 핑크 바운싱 도트를 띄운다.
 */
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        // 배경 레이어: 블러는 이 레이어에만 적용해 도트가 함께 흐려지지 않게 한다.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 6.dp)
                .background(Color.White.copy(alpha = 0.55f))
        )
        // 도트 레이어: 블러 없이 선명하게 그린다.
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            BouncingDots()
        }
    }
}

@Composable
private fun BouncingDots() {
    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        // 웹 버전의 animation-delay(0s, 0.15s, 0.3s)에 대응
        val delays = listOf(0, 150, 300)
        val colors = listOf(CoralPrimary, CoralMuted, CoralPrimary)

        delays.forEachIndexed { index, delayMillis ->
            Dot(color = colors[index], delayMillis = delayMillis)
        }
    }
}

@Composable
private fun Dot(color: Color, delayMillis: Int, size: Dp = 13.dp) {
    val transition = rememberInfiniteTransition(label = "dotTransition")

    // translateY(0) -> translateY(-12px) -> translateY(0) 를 흉내
    val offsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1100,
                delayMillis = delayMillis,
                easing = LinearOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotOffsetY"
    )

    val alpha by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1100,
                delayMillis = delayMillis,
                easing = LinearOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Box(
        modifier = Modifier
            .size(size)
            .offset(y = (-12 * offsetY).dp)
            .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = color, spotColor = color)
            .background(color = color.copy(alpha = alpha), shape = CircleShape)
    )
}
