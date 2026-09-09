package com.mediinbusan.app.core.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

/**
 * 아래로 스크롤하면 하단 탭바가 축소되고, 위로 스크롤하거나 맨 위 근처로 돌아오면 원래 크기로
 * 되돌아오는 애니메이션 훅. 실제 스크롤 오프셋은 소비하지 않고 델타만 관찰한다(onPreScroll에서
 * 항상 Offset.Zero 반환) — 스크롤 컨테이너(Modifier.verticalScroll(scrollState)) 바로 위에
 * Modifier.nestedScroll(connection)만 얹으면 기존 스크롤 동작에 영향을 주지 않는다.
 *
 * 반환된 scale은 호출부가 바텀바 쪽 Modifier.graphicsLayer { scaleX = scale; scaleY = scale }로
 * 직접 적용한다 — 이 파일은 바텀바의 디자인/블러/기존 애니메이션은 전혀 건드리지 않는다.
 */
@Composable
fun rememberScrollShrinkAnimation(
    scrollState: ScrollState,
    shrinkScale: Float = 0.80f,
    threshold: Float = 6f,
    durationMillis: Int = 320
): Pair<NestedScrollConnection, Float> = rememberScrollShrinkAnimation(
    atTop = { scrollState.value < 20 },
    shrinkScale = shrinkScale,
    threshold = threshold,
    durationMillis = durationMillis
)

/** LazyColumn(LazyListState) 버전 — "맨 위 근처" 판정만 firstVisibleItemIndex/Offset 기준으로 다르다. */
@Composable
fun rememberScrollShrinkAnimation(
    listState: LazyListState,
    shrinkScale: Float = 0.80f,
    threshold: Float = 6f,
    durationMillis: Int = 320
): Pair<NestedScrollConnection, Float> = rememberScrollShrinkAnimation(
    atTop = { listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 20 },
    shrinkScale = shrinkScale,
    threshold = threshold,
    durationMillis = durationMillis
)

@Composable
private fun rememberScrollShrinkAnimation(
    atTop: () -> Boolean,
    shrinkScale: Float,
    threshold: Float,
    durationMillis: Int
): Pair<NestedScrollConnection, Float> {
    var isShrunk by remember { mutableStateOf(false) }

    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                isShrunk = when {
                    atTop() -> false // 최상단 근처면 무조건 원래 크기
                    delta < -threshold -> true // 아래로 스크롤
                    delta > threshold -> false // 위로 스크롤
                    else -> isShrunk // 미세 스크롤은 무시
                }
                return Offset.Zero
            }
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isShrunk) shrinkScale else 1f,
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        label = "bottomBarShrinkScale"
    )

    return connection to scale
}
