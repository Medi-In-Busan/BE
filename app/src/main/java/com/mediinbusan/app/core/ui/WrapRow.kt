package com.mediinbusan.app.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * androidx.compose.foundation.layout.FlowRow(실험적 API) 대신 쓰는 안정판 대체 컴포넌트.
 *
 * 병원 상세(HospitalDetailScreen)의 진료과목 칩에 FlowRow를 썼다가 실기기에서
 * `NoSuchMethodError: FlowRow(...)`로 즉시 크래시가 났다 — 이 프로젝트의 의존성 그래프에서
 * androidx.compose.foundation 버전이 뒤섞여(Compose BOM이 지정한 버전과 다른 라이브러리가
 * 끌어오는 더 최신 버전이 충돌), 컴파일 시점엔 FlowRow의 새 오버로드로 링크됐는데 실제 dex엔
 * 그 오버로드가 없는 구버전 클래스가 들어가는 문제였다. Compose 1.0부터 시그니처가 한 번도
 * 안 바뀐 [Layout]만으로 같은 "왼쪽에서 오른쪽으로 채우다 넘치면 다음 줄로" 동작을 직접
 * 구현해, 실험적 API의 버전 스큐 위험 없이 안전하게 쓴다.
 */
@Composable
fun WrapRow(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 0.dp,
    verticalSpacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val hSpacingPx = horizontalSpacing.roundToPx()
        val vSpacingPx = verticalSpacing.roundToPx()
        val maxWidth = constraints.maxWidth

        val placeables = measurables.map { it.measure(Constraints(maxWidth = maxWidth)) }

        val lines = mutableListOf<MutableList<Placeable>>()
        val lineWidths = mutableListOf<Int>()
        val lineHeights = mutableListOf<Int>()

        placeables.forEach { placeable ->
            if (lines.isEmpty()) {
                lines.add(mutableListOf(placeable))
                lineWidths.add(placeable.width)
                lineHeights.add(placeable.height)
                return@forEach
            }
            val lastIndex = lines.lastIndex
            val widthIfAppended = lineWidths[lastIndex] + hSpacingPx + placeable.width
            if (widthIfAppended <= maxWidth) {
                lines[lastIndex].add(placeable)
                lineWidths[lastIndex] = widthIfAppended
                lineHeights[lastIndex] = maxOf(lineHeights[lastIndex], placeable.height)
            } else {
                lines.add(mutableListOf(placeable))
                lineWidths.add(placeable.width)
                lineHeights.add(placeable.height)
            }
        }

        val totalHeight = if (lineHeights.isEmpty()) {
            0
        } else {
            lineHeights.sum() + vSpacingPx * (lineHeights.size - 1)
        }

        layout(width = maxWidth, height = totalHeight) {
            var y = 0
            lines.forEachIndexed { index, line ->
                var x = 0
                line.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + hSpacingPx
                }
                y += lineHeights[index] + vSpacingPx
            }
        }
    }
}
