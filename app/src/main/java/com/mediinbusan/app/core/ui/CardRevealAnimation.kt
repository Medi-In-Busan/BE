package com.mediinbusan.app.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.DividerColor
import kotlinx.coroutines.delay

// HospitalSearchListScreen에서 처음 만든 "시그널 리빌" 카드 등장 연출 — 즐겨찾기/최근 본 항목
// 리스트도 같은 톤을 쓰기 위해 공용으로 뺐다. 무한스크롤(또는 긴 리스트)에서 나중에 덧붙는/뒤쪽
// 항목까지 매번 재생하면 과하니, 첫 로딩 시 한 화면에 걸치는 정도(맨 앞 N개)까지만 적용한다.
const val InitialCardRevealCount = 6
private const val CardRevealBaseDelayMs = 500L
private const val CardRevealStaggerMs = 220L
private const val CardRevealDurationMs = 500

/** itemsKey(보통 리스트 그 자체)가 바뀔 때마다 0부터 다시 순차 공개되는 인덱스 카운트. */
@Composable
fun rememberRevealedCount(itemsKey: Any, itemCount: Int): Int {
    var revealedCount by remember(itemsKey) { mutableStateOf(0) }
    LaunchedEffect(itemsKey) {
        if (itemCount == 0) return@LaunchedEffect
        val revealTarget = minOf(itemCount, InitialCardRevealCount)
        delay(CardRevealBaseDelayMs)
        repeat(revealTarget) { index ->
            revealedCount = index + 1
            delay(CardRevealStaggerMs)
        }
    }
    return revealedCount
}

/** 0(등장 전, 스켈레톤만 보임) ~ 1(완전히 보임) 진행도. 애니메이션 대상이 아닌 카드는 항상 1. */
@Composable
fun rememberCardRevealProgress(isRevealAnimated: Boolean, isRevealed: Boolean): Float {
    val progress by animateFloatAsState(
        targetValue = if (!isRevealAnimated || isRevealed) 1f else 0f,
        animationSpec = tween(durationMillis = CardRevealDurationMs, easing = FastOutSlowInEasing),
        label = "cardReveal"
    )
    return progress
}

/** 카드가 등장하기 전(alpha에 반비례) 그 위에 덮이는 반짝이는 스켈레톤 placeholder. */
@Composable
fun ShimmerSkeleton(alpha: Float, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -400f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1400, easing = LinearEasing)),
        label = "shimmerOffset"
    )
    Box(
        modifier = modifier
            .graphicsLayer { this.alpha = alpha }
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(DividerColor, Color.White, DividerColor),
                    start = Offset(shimmerOffset, 0f),
                    end = Offset(shimmerOffset + 300f, 300f)
                )
            )
    )
}

/** 0에서 target까지 카운트업하는 애니메이션 값. target이 바뀔 때마다(새 데이터 도착) 0부터 재생된다. */
@Composable
fun rememberCountUpValue(target: Int): Int {
    val animated = remember { Animatable(0f) }
    LaunchedEffect(target) {
        animated.snapTo(0f)
        animated.animateTo(target.toFloat(), animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing))
    }
    return animated.value.toInt()
}
