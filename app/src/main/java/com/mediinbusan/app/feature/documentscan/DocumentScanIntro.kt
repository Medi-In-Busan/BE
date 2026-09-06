package com.mediinbusan.app.feature.documentscan

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CoralInk
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.DocumentScanStrings
import com.mediinbusan.app.core.i18n.LocalAppStrings

/**
 * 문서 스캔 첫 화면(이미지 선택 전).
 *
 * 예전에는 아이콘·문구·버튼을 흰 카드 하나에 담고 32dp짜리 검은 그림자를 줬는데, 배경이 거의 흰색
 * (HomeBackgroundPink)이라 카드가 떠 보이기만 하고 화면 아래 절반은 그대로 비었다. 게다가 채움
 * 버튼과 외곽선 버튼이 같은 폭·같은 높이로 나란히 있어 어느 쪽이 주 행동인지 흐렸다.
 *
 * 그래서 카드를 걷어내고 ① 스캔 중인 문서 히어로 → ② 제목/설명 → ③ 촬영·인식·번역 3단계 →
 * ④ 하나뿐인 주 버튼 순으로 세로 흐름을 만든다. 히어로의 브래킷·스캔라인은 촬영 화면
 * ([DocumentCaptureScreen])·분석 중 오버레이([DocumentScanningOverlay])와 같은 시각 언어라,
 * 세 화면이 하나의 기능으로 읽히게 하는 역할도 겸한다.
 */

/** 히어로 문서가 기울어진 각도. 반듯하게 두면 그냥 흰 사각형으로 보인다. */
private const val HeroCardTiltDegrees = -4f

/** 히어로 스캔라인이 한 번 훑는 시간. 분석 중 오버레이(2200ms)보다 느긋하게 둬서 대기 화면답게. */
private const val HeroScanCycleMs = 2600

@Composable
internal fun DocumentScanIntro(
    strings: DocumentScanStrings,
    onCaptureClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScanHeroIllustration(modifier = Modifier.fillMaxWidth().height(160.dp))

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = strings.introTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = strings.introSubtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))
        IntroStepsRow(strings = strings)

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onCaptureClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                // 검은 그림자 대신 버튼 색과 같은 코랄 그림자를 깔아 "빛나는" 주 버튼으로 만든다.
                .shadow(
                    elevation = 12.dp,
                    shape = MaterialTheme.shapes.extraLarge,
                    ambientColor = CoralPrimary,
                    spotColor = CoralPrimary
                ),
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
        ) {
            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = strings.captureButton,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        // 보조 행동은 외곽선 버튼(주 버튼과 같은 덩치)이 아니라 텍스트 버튼으로 낮춘다 —
        // 어느 쪽을 눌러야 할지 한눈에 갈리게 하는 게 이 화면의 핵심이다.
        TextButton(onClick = onGalleryClick, modifier = Modifier.height(44.dp)) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = strings.galleryButton,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        PrivacyNotePill(text = strings.privacyNote)
    }
}

/** 스캔 중인 진단서 한 장. 코랄 브래킷과 스캔라인은 촬영·분석 화면과 같은 톤이다. */
@Composable
private fun ScanHeroIllustration(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "introHero")
    // 여기서는 Reverse가 맞다. 잡아낼 항목이 없는 대기 화면이라 왕복이 더 잔잔하다
    // (분석 중 오버레이는 박스를 하나씩 잡는 사이클이라 Restart를 쓴다).
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = HeroScanCycleMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "introHeroSweep"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // 문서 뒤 옅은 코랄 후광. 배경(HomeBackgroundPink)으로 자연스럽게 풀린다.
        Box(
            modifier = Modifier
                .size(164.dp)
                .background(
                    brush = Brush.radialGradient(listOf(CoralPrimaryContainer, HomeBackgroundPink)),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .graphicsLayer { rotationZ = HeroCardTiltDegrees }
                .size(width = 112.dp, height = 150.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(14.dp),
                        ambientColor = Color.Black.copy(alpha = 0.12f),
                        spotColor = Color.Black.copy(alpha = 0.16f)
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .padding(horizontal = 13.dp, vertical = 15.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 문서 제목 줄만 코랄로 강조하고 나머지는 회색 본문 줄로 흉내 낸다.
                DocumentLine(widthFraction = 0.52f, height = 8.dp, color = CoralPrimary.copy(alpha = 0.55f))
                Spacer(modifier = Modifier.height(3.dp))
                DocumentLine(widthFraction = 1f)
                DocumentLine(widthFraction = 0.86f)
                DocumentLine(widthFraction = 0.94f)
                Spacer(modifier = Modifier.height(3.dp))
                DocumentLine(widthFraction = 0.7f)
                DocumentLine(widthFraction = 0.9f)
                DocumentLine(widthFraction = 0.44f)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawHeroBrackets()
                drawHeroScanLine(sweep)
            }
        }
    }
}

@Composable
private fun DocumentLine(
    widthFraction: Float,
    height: androidx.compose.ui.unit.Dp = 6.dp,
    color: Color = DividerColor
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(percent = 50))
            .background(color)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHeroBrackets() {
    val cornerLength = 18.dp.toPx()
    val strokeWidth = 3.dp.toPx()
    val w = size.width
    val h = size.height
    val corners = listOf(
        Offset(0f, cornerLength) to Offset(0f, 0f),
        Offset(0f, 0f) to Offset(cornerLength, 0f),
        Offset(w - cornerLength, 0f) to Offset(w, 0f),
        Offset(w, 0f) to Offset(w, cornerLength),
        Offset(0f, h - cornerLength) to Offset(0f, h),
        Offset(0f, h) to Offset(cornerLength, h),
        Offset(w - cornerLength, h) to Offset(w, h),
        Offset(w, h) to Offset(w, h - cornerLength)
    )
    corners.forEach { (start, end) ->
        drawLine(color = CoralPrimary, start = start, end = end, strokeWidth = strokeWidth, cap = StrokeCap.Round)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHeroScanLine(sweep: Float) {
    val lineY = sweep * size.height
    drawLine(
        color = CoralPrimary.copy(alpha = 0.9f),
        start = Offset(0f, lineY),
        end = Offset(size.width, lineY),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round
    )
}

/** 촬영 → 텍스트 인식 → 번역. 처음 쓰는 사람에게 이 기능이 뭘 해주는지 한 줄로 보여준다. */
@Composable
private fun IntroStepsRow(strings: DocumentScanStrings) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IntroStep(icon = Icons.Default.CameraAlt, label = strings.introStepCaptureLabel, modifier = Modifier.weight(1f))
        IntroStep(
            icon = Icons.AutoMirrored.Filled.Subject,
            label = strings.introStepRecognizeLabel,
            modifier = Modifier.weight(1f)
        )
        IntroStep(icon = Icons.Default.Translate, label = strings.introStepTranslateLabel, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun IntroStep(icon: ImageVector, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(CoralPrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = CoralInk, modifier = Modifier.size(21.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PrivacyNotePill(text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(Color.White)
            .border(width = 1.dp, color = DividerColor, shape = RoundedCornerShape(percent = 50))
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(name = "DocumentScan - 인트로", showBackground = true, backgroundColor = 0xFFFFFAFA)
@Composable
private fun DocumentScanIntroPreview() {
    MediInBusanTheme {
        Column(modifier = Modifier.background(HomeBackgroundPink).padding(horizontal = 20.dp, vertical = 24.dp)) {
            DocumentScanIntro(
                strings = LocalAppStrings.current.documentScan,
                onCaptureClick = {},
                onGalleryClick = {}
            )
        }
    }
}
