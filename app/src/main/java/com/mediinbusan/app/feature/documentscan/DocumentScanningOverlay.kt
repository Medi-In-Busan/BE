package com.mediinbusan.app.feature.documentscan

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.TextSecondary

/*
 * OCR 분석 중 문서 사진 위에 얹는 스캐너 연출. 라인이 위에서 아래로 훑고, 지나간 자리에 인식
 * 박스가 하나씩 팝인한다.
 *
 * 박스 위치는 실제 OCR 탐지 결과가 아니다. 백엔드 DocumentOcrResponse는
 * (text, translatedText, targetLanguage) 세 문자열이 전부고, CLOVA가 준 boundingPoly 좌표는
 * 서버의 DocumentTextLayoutBuilder가 행/열 복원(공백·파이프·개행)에만 쓰고 평문으로 평탄화하면서
 * 버린다. 즉 앱에는 좌표가 애초에 도착하지 않는다 — 여기 박스는 "읽는 중"을 보여주는 장식이고,
 * 사진 속 글자와 맞지 않는 게 정상이다. 진짜 좌표를 그리려면 백엔드 응답 계약부터 바꿔야 한다.
 */

/** 스캔라인이 위에서 아래로 한 번 훑는 데 걸리는 시간. */
private const val ScanCycleMs = 2200

/** 박스가 팝인을 마치기까지 스캔라인이 더 내려가야 하는 진행도(전체 높이 대비). */
private const val BoxPopWindow = 0.12f

/** 팝인 시작 시점의 축소 비율 — 1f까지 커지면서 "잡혔다"는 느낌을 만든다. */
private const val BoxPopMinScale = 0.88f

/**
 * 진단서·처방전 서식을 흉내 낸 고정 배치(이미지 영역 기준 0f~1f 정규화). 매번 난수로 흩뿌리면
 * 리컴포지션마다 자리가 튀어서 오히려 고장 난 것처럼 보인다 — 배치는 상수로 고정한다.
 */
private data class ScanRegion(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

private val ScanRegions = listOf(
    ScanRegion(0.10f, 0.05f, 0.62f, 0.13f), // 문서 제목
    ScanRegion(0.10f, 0.20f, 0.45f, 0.27f), // 라벨/값 왼쪽 칸
    ScanRegion(0.52f, 0.20f, 0.90f, 0.27f), // 라벨/값 오른쪽 칸
    ScanRegion(0.10f, 0.33f, 0.90f, 0.40f), // 가로로 긴 한 줄
    ScanRegion(0.10f, 0.47f, 0.90f, 0.68f), // 처방 약품 표 블록
    ScanRegion(0.10f, 0.75f, 0.58f, 0.82f), // 하단 짧은 줄
    ScanRegion(0.66f, 0.86f, 0.90f, 0.95f) // 우하단 서명·직인 자리
)

/**
 * 스캔라인 + 인식 박스 오버레이. 사진 영역과 정확히 겹치도록 호출부에서 같은 크기·패딩을 준다.
 *
 * 장식이라 접근성 트리에 노출하지 않는다(Canvas는 기본적으로 시맨틱이 없다) — 진행 상태는
 * [DocumentScanningCaption]의 텍스트가 대신 읽어준다.
 */
@Composable
fun DocumentScanningOverlay(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "documentScanning")
    // Reverse가 아니라 Restart다. 되돌아오면 박스가 역순으로 하나씩 사라져 "지우는" 것처럼 보이는데,
    // Restart면 바닥에 닿는 순간 전부 리셋됐다가 다시 잡히는 "재스캔" 사이클이 되어 의도에 맞는다.
    val scanProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = ScanCycleMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanProgress"
    )

    Canvas(modifier = modifier) {
        drawScanRegions(scanProgress)
        drawScanBeam(scanProgress)
    }
}

/** 스캔라인이 이미 지나간 영역만, 지난 정도에 비례해 팝인시킨다. */
private fun DrawScope.drawScanRegions(scanProgress: Float) {
    val cornerRadius = CornerRadius(6.dp.toPx())
    val strokeWidth = 1.5.dp.toPx()
    val tickLength = 7.dp.toPx()

    ScanRegions.forEach { region ->
        // 팝인 진행도는 스캔라인 위치에서 바로 계산한다 — 박스마다 상태를 들고 있지 않아도
        // 사이클이 돌 때 자동으로 0부터 다시 시작된다.
        val pop = ((scanProgress - region.top) / BoxPopWindow).coerceIn(0f, 1f)
        if (pop <= 0f) {
            return@forEach
        }

        val fullWidth = (region.right - region.left) * size.width
        val fullHeight = (region.bottom - region.top) * size.height
        val scale = BoxPopMinScale + (1f - BoxPopMinScale) * pop
        val width = fullWidth * scale
        val height = fullHeight * scale
        // 중심을 고정한 채 축소/확대해야 자리에서 "부풀어 오르는" 것처럼 보인다.
        val left = region.left * size.width + (fullWidth - width) / 2f
        val top = region.top * size.height + (fullHeight - height) / 2f
        val boxSize = Size(width, height)
        val topLeft = Offset(left, top)

        drawRoundRect(
            color = CoralPrimary.copy(alpha = 0.10f * pop),
            topLeft = topLeft,
            size = boxSize,
            cornerRadius = cornerRadius
        )
        drawRoundRect(
            color = CoralPrimary.copy(alpha = 0.85f * pop),
            topLeft = topLeft,
            size = boxSize,
            cornerRadius = cornerRadius,
            style = Stroke(width = strokeWidth)
        )
        drawRegionCornerTicks(topLeft = topLeft, boxSize = boxSize, length = tickLength, alpha = pop)
    }
}

/** 박스 네 모서리에 짧은 굵은 틱을 얹는다 — ScanFrameCorners와 같은 톤의 "조준" 신호. */
private fun DrawScope.drawRegionCornerTicks(
    topLeft: Offset,
    boxSize: Size,
    length: Float,
    alpha: Float
) {
    val color = CoralPrimary.copy(alpha = alpha)
    val strokeWidth = 2.5.dp.toPx()
    val l = topLeft.x
    val t = topLeft.y
    val r = l + boxSize.width
    val b = t + boxSize.height
    // 모서리가 붙을 만큼 작은 박스에서 틱이 서로 겹쳐 테두리가 통째로 굵어 보이는 걸 막는다.
    val tick = minOf(length, boxSize.width / 3f, boxSize.height / 3f)

    val segments = listOf(
        Offset(l, t) to Offset(l + tick, t),
        Offset(l, t) to Offset(l, t + tick),
        Offset(r, t) to Offset(r - tick, t),
        Offset(r, t) to Offset(r, t + tick),
        Offset(l, b) to Offset(l + tick, b),
        Offset(l, b) to Offset(l, b - tick),
        Offset(r, b) to Offset(r - tick, b),
        Offset(r, b) to Offset(r, b - tick)
    )
    segments.forEach { (start, end) ->
        drawLine(color = color, start = start, end = end, strokeWidth = strokeWidth, cap = StrokeCap.Round)
    }
}

/** 스캔라인 본체 + 그 위를 따라오는 글로우 밴드. */
private fun DrawScope.drawScanBeam(scanProgress: Float) {
    val lineY = scanProgress * size.height
    val glowHeight = 56.dp.toPx()
    val glowTop = (lineY - glowHeight).coerceAtLeast(0f)

    if (lineY > glowTop) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, CoralPrimary.copy(alpha = 0.28f)),
                startY = glowTop,
                endY = lineY
            ),
            topLeft = Offset(0f, glowTop),
            size = Size(size.width, lineY - glowTop)
        )
    }

    drawLine(
        color = CoralPrimary,
        start = Offset(0f, lineY),
        end = Offset(size.width, lineY),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round
    )
    // 코랄 라인 위에 흰 심지를 한 겹 더 얹어야 사진이 어두울 때도 선이 묻히지 않는다.
    drawLine(
        color = Color.White.copy(alpha = 0.85f),
        start = Offset(0f, lineY),
        end = Offset(size.width, lineY),
        strokeWidth = 1.dp.toPx(),
        cap = StrokeCap.Round
    )
}

/**
 * 사진 아래 진행 캡션. 실제 진행도를 알 수 없어(백엔드가 완료 시점에 한 번에 응답한다) 퍼센트
 * 대신 indeterminate 바로 "진행 중"만 표시한다 — 가짜 숫자를 띄우지 않기 위한 선택이다.
 */
@Composable
fun DocumentScanningCaption(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(MaterialTheme.shapes.extraLarge),
            color = CoralPrimary,
            trackColor = CoralPrimaryContainer
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(name = "DocumentScan - 스캔 오버레이", showBackground = true)
@Composable
private fun DocumentScanningOverlayPreview() {
    MediInBusanTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(width = 280.dp, height = 320.dp)
                    .background(DividerColor)
            ) {
                DocumentScanningOverlay(modifier = Modifier.fillMaxSize())
            }
            DocumentScanningCaption(message = "이미지를 분석하고 있어요...")
        }
    }
}
