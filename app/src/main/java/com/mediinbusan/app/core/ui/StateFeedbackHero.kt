package com.mediinbusan.app.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.TextSecondary

/*
 * 빈 상태([EmptyState])·오류 상태([ErrorState])가 함께 쓰는 히어로 일러스트와 등장 연출.
 *
 * 이 둘은 F-019 공용 상태 3종 중 나머지 하나인 [LoadingState](블러 배경 + 코랄 바운싱 도트)와
 * 같은 자리에서 교대로 나타나는데, 여태 Text 하나만 그려서 로딩 → 빈 상태로 넘어가는 순간 화면이
 * 통째로 비어 "고장 났다"처럼 보였다. 13개 화면 23곳이 쓰는 공용 컴포넌트라 여기 한 번만 고치면
 * 앱 전체가 같이 좋아진다.
 *
 * 시각 언어는 문서스캔 인트로(`ScanHeroIllustration`)에서 가져온다 — 코랄 후광 + 기울어진 흰 카드 +
 * 회색 본문 줄. 다만 스캔 전용 요소(브래킷·스캔라인)는 빼고, 그 자리에 상태를 알려주는 코랄 배지를
 * 얹는다. 문서스캔 쪽 히어로를 공용으로 빼지 않고 여기 따로 그리는 이유는 그쪽 카드가 "스캔 중인
 * 문서"라는 뜻을 브래킷·스캔라인으로 만들고 있어서, 하나로 합치면 파라미터만 늘고 두 화면 다
 * 읽기 어려워지기 때문이다.
 */

/** 카드가 기울어진 각도. 반듯하게 두면 그냥 흰 사각형으로 보인다(문서스캔 히어로와 같은 이유). */
private const val HeroCardTiltDegrees = -5f

/** 화면 진입 시 페이드 + 위로 떠오르는 시간. 상태 전환이라 짧고 단호하게. */
private const val HeroEntranceDurationMs = 420

/** 진입할 때 아래에서 올라오는 거리. */
private val HeroEntranceRise = 14.dp

/**
 * 카드가 위아래로 떠다니는 한 주기. 문서스캔 인트로 스캔라인(2600ms)보다 느긋하게 둔다 —
 * 여기는 진행 중인 일이 없는 화면이라, 움직임이 눈에 띄면 오히려 뭔가 로딩 중인 줄 알게 된다.
 */
private const val HeroFloatCycleMs = 3000

/** 떠다니는 진폭. 이보다 크면 "떠 있다"가 아니라 "흔들린다"로 읽힌다. */
private val HeroFloatDistance = 5.dp

/**
 * 히어로 일러스트 + 안내 문구 + (선택) 행동 버튼을 세로로 쌓는 공용 상태 화면 골격.
 *
 * 일러스트는 순수 장식이라 접근성 트리에 노출하지 않는다 — 상태는 [message] 텍스트가 읽어준다.
 *
 * @param linePattern 카드 안 회색 줄의 가로 비율. 빈 상태는 줄이 고르게, 오류 상태는 줄이 끊긴
 *   느낌이 나도록 호출부에서 다르게 넘긴다.
 */
@Composable
internal fun StateFeedbackContent(
    badgeIcon: ImageVector,
    message: String,
    linePattern: List<Float>,
    modifier: Modifier = Modifier,
    action: @Composable ColumnScope.() -> Unit = {}
) {
    val entrance = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        entrance.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = HeroEntranceDurationMs, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            // 진입 연출은 바깥 Column에 한 번만 건다. 안쪽 요소마다 따로 걸면 서로 다른 프레임에
            // 그려져 계단처럼 들어온다.
            .graphicsLayer {
                alpha = entrance.value
                translationY = (1f - entrance.value) * HeroEntranceRise.toPx()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StateHeroIllustration(badgeIcon = badgeIcon, linePattern = linePattern)

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        action()
    }
}

/** 코랄 후광 위에 뜬 흰 카드 한 장 + 상태 배지. 카드만 천천히 떠다닌다. */
@Composable
private fun StateHeroIllustration(badgeIcon: ImageVector, linePattern: List<Float>) {
    val transition = rememberInfiniteTransition(label = "stateHero")
    // Reverse. 여기는 무엇을 처리하는 중이 아니라 멈춰 있는 상태라, 한쪽으로 계속 흐르는
    // Restart보다 제자리로 돌아오는 왕복이 맞는다.
    val float by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = HeroFloatCycleMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stateHeroFloat"
    )

    Box(modifier = Modifier.size(136.dp), contentAlignment = Alignment.Center) {
        // 후광은 흰 배경/연분홍 배경 어디에 놓여도 자연스럽게 풀리도록 Transparent로 끝낸다
        // (문서스캔 히어로는 배경색인 HomeBackgroundPink로 끝내지만, 여긴 배경을 고를 수 없다).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // 카드가 떠오를 때 후광도 같이 숨 쉬어야 두 겹이 한 덩어리로 읽힌다.
                    val haloScale = 0.95f + 0.05f * float
                    scaleX = haloScale
                    scaleY = haloScale
                }
                .background(
                    brush = Brush.radialGradient(listOf(CoralPrimaryContainer, Color.Transparent)),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = HeroCardTiltDegrees
                    translationY = -float * HeroFloatDistance.toPx()
                }
                .size(width = 84.dp, height = 104.dp)
                .shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = Color.Black.copy(alpha = 0.12f),
                    spotColor = Color.Black.copy(alpha = 0.16f)
                )
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            linePattern.forEach { widthFraction ->
                PlaceholderLine(widthFraction = widthFraction)
            }
        }

        // 배지는 카드 바깥으로 걸치게 둔다 — 카드 안에 넣으면 회색 줄과 같은 층으로 읽혀서
        // "지금 무슨 상태인지" 알려주는 역할을 못 한다. 카드와 달리 떠다니지 않아 시선이 여기 멈춘다.
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 8.dp)
                .size(40.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = CoralPrimary,
                    spotColor = CoralPrimary
                )
                .clip(CircleShape)
                .background(CoralPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = badgeIcon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(21.dp)
            )
        }
    }
}

/**
 * 빈 상태·오류 상태가 함께 쓰는 단 하나의 행동 버튼.
 *
 * 두 상태 모두 화면에 이 버튼 말고는 누를 게 없으므로(막다른 화면을 여는 유일한 문이다) 보조가
 * 아니라 주 버튼으로 그린다 — 문서스캔 인트로의 주 버튼과 같은 톤(코랄 채움 + 코랄 그림자).
 * 두 상태가 서로 다른 버튼을 쓰면 한 시스템으로 안 읽혀서 여기 한 곳에 모아 둔다.
 */
@Composable
internal fun StateActionButton(label: String, onClick: () -> Unit) {
    Spacer(modifier = Modifier.height(18.dp))
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun PlaceholderLine(widthFraction: Float, height: Dp = 6.dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(percent = 50))
            .background(DividerColor)
    )
}
