package com.mediinbusan.app.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.TextPrimary
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * BottomNavBar의 실측 높이(바 자체 64dp + 위아래 여백 4dp씩).
 * 시스템 제스처/내비게이션 바 인셋은 여기 포함되지 않는다 — BottomNavBar 자신이
 * navigationBarsPadding()으로 직접 소비해서 뜬(floating) 카드가 제스처 영역 위에 걸치지 않게
 * 한다(그래서 화면들이 별도로 navigationBarsPadding()을 또 호출하면 인셋이 중복 적용된다 —
 * MapScreen.kt는 이 변경에 맞춰 중복 호출을 제거했다).
 * 이 바가 보이는 화면들은 이 값만큼 자기 콘텐츠에 직접 하단 여백을 둬야 한다.
 */
val BottomNavBarHeight = 72.dp

private val BarHeight = 64.dp
private val BarShape = RoundedCornerShape(50)
private val IndicatorInset = 6.dp

// A(출발)→B(도착) 이동 시 두 edge를 동시에 같은 spring으로 움직이면 그냥 폭이 고정된 박스가
// 밀려가는 것처럼 보인다. 이동 방향의 리딩 edge를 먼저 출발시키고 트레일링 edge를 이 시간만큼
// 늦게 출발시켜서, 그 사이 캡슐이 가로로 늘어나는(Stretch) 구간을 만든다. 40ms는 두 edge의
// 출발 간격이 눈에 띄게 "끊겨" 보였다 — 18ms로 좁혀서 늘어남이 딜레이 없이 이어지게 한다.
private const val EdgeStaggerDelayMs = 18L

/** 4개 탭을 가진 공용 하단 내비게이션 바. Route를 알지 못하는 순수 프레젠테이션 컴포넌트다. */
data class BottomNavTabUiModel(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val selected: Boolean,
    val onClick: () -> Unit
)

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BottomNavBar(
    tabs: List<BottomNavTabUiModel>,
    hazeState: HazeState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .shadow(
                    elevation = 8.dp,
                    shape = BarShape,
                    ambientColor = Color.Black.copy(alpha = 0.12f),
                    spotColor = Color.Black.copy(alpha = 0.16f)
                )
                .fillMaxWidth()
                .height(BarHeight)
                .clip(BarShape)
                .hazeEffect(state = hazeState, style = HazeMaterials.thin(Color.White))
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.9f), Color.White.copy(alpha = 0.15f))
                    ),
                    shape = BarShape
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BarHeight / 2)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.28f), Color.Transparent)
                        )
                    )
            )

            GlassSlidingIndicator(
                tabCount = tabs.size,
                selectedIndex = tabs.indexOfFirst { it.selected }.coerceAtLeast(0),
                hazeState = hazeState
            )

            Row(modifier = Modifier.fillMaxSize()) {
                tabs.forEach { tab -> GlassTabItem(tab) }
            }
        }
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
private fun BoxWithConstraintsScope.GlassSlidingIndicator(
    tabCount: Int,
    selectedIndex: Int,
    hazeState: HazeState
) {
    val slotWidth = maxWidth / tabCount
    val baseWidth = slotWidth - IndicatorInset * 2
    fun restLeft(index: Int) = (slotWidth * index + IndicatorInset).value
    fun restRight(index: Int) = restLeft(index) + baseWidth.value
    val targetRestLeft = restLeft(selectedIndex)
    val targetRestRight = restRight(selectedIndex)

    // 캡슐을 하나의 (left, width) 쌍이 아니라 두 edge를 독립된 Animatable로 관리한다 — 두
    // edge가 서로 다른 타이밍/스프링으로 움직여야 늘어났다 튕기며 자리잡는 모션이 나온다.
    val leftEdge = remember { Animatable(targetRestLeft) }
    val rightEdge = remember { Animatable(targetRestRight) }
    // 최초 진입 시엔 "A에서 B로 이동"이 아니라 그냥 배치라 스프링 모션 없이 바로 정착시킨다.
    var isFirstMount by remember { mutableStateOf(true) }

    LaunchedEffect(selectedIndex) {
        if (isFirstMount) {
            isFirstMount = false
            leftEdge.snapTo(targetRestLeft)
            rightEdge.snapTo(targetRestRight)
            return@LaunchedEffect
        }

        val direction = if (targetRestLeft + targetRestRight >= leftEdge.value + rightEdge.value) 1 else -1
        // DampingRatioLowBouncy(0.2f 근방)는 통통 튀는 가벼운 느낌이라, 배민/iOS 특유의 "늘어났다
        // 점성 있게 착 붙는" 젤리 질감엔 안 맞는다. 0.78f로 훨씬 덜 튀되 여전히 살짝 오버슈트가
        // 남는 값을 직접 지정해 Viscous Spring에 가깝게 만든다.
        val elasticSpec = spring<Float>(
            dampingRatio = 0.78f,
            stiffness = Spring.StiffnessMediumLow
        )

        coroutineScope {
            if (direction > 0) {
                // 오른쪽으로 이동 — rightEdge(리딩)가 즉시 출발하고 leftEdge(트레일링)는 살짝
                // 늦게 따라간다. 그 사이 캡슐 폭이 늘어나 보이다가, 각 edge가 도착 지점에서
                // 점성 있게 착 붙듯 정착한다.
                launch { rightEdge.animateTo(targetRestRight, elasticSpec) }
                launch {
                    delay(EdgeStaggerDelayMs)
                    leftEdge.animateTo(targetRestLeft, elasticSpec)
                }
            } else {
                launch { leftEdge.animateTo(targetRestLeft, elasticSpec) }
                launch {
                    delay(EdgeStaggerDelayMs)
                    rightEdge.animateTo(targetRestRight, elasticSpec)
                }
            }
        }
    }

    val left = leftEdge.value.dp
    // 두 edge가 각자 독립된 bouncy spring이라 순간적으로 leftEdge가 rightEdge를 앞지를 수
    // 있다 — 그 프레임에 음수 폭을 Box에 넘기면 크래시라 0 이상으로 방어한다.
    val width = (rightEdge.value - leftEdge.value).coerceAtLeast(0f).dp
    val indicatorHeight = BarHeight - 16.dp

    Box(
        modifier = Modifier
            .offset(x = left, y = 8.dp)
            .width(width)
            .height(indicatorHeight)
            .shadow(
                elevation = 8.dp,
                shape = BarShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .clip(BarShape)
            // 1. 실시간 배경 블러(Frosted Glass) — HazeMaterials.thin(color)는 겉보기와 달리
            // color의 alpha를 그대로 쓰지 않는다: 내부에서 color의 luminance만 보고 자체 고정
            // alpha(밝은 색이면 0.6f, 어두운 색이면 0.65f, "thin" 기준)로 갈아끼운다 — 그래서
            // 여기 alpha를 0.20f→0.30f→0.8f로 계속 바꿔봐도 실제 렌더링은 항상 그 고정값으로만
            // 나왔던 것(우리가 준 alpha는 완전히 무시됨). HazeMaterials 프리셋을 버리고 HazeStyle/
            // HazeTint를 직접 구성해서 alpha가 실제로 반영되게 한다.
            // HazeLogger 로그로 blurEffect=RenderEffectBlurEffect(실제 블러, fallback 아님)/area
            // 정상/tint alpha=0.30 그대로 반영까지 다 확인됐는데도 화면에서 안 비쳤음 — 남은 원인은
            // blurRadius=20dp가 165x132px밖에 안 되는 좁은 인디케이터 영역엔 과해서 뒤 배경을
            // 거의 뭉갠 단색으로 만들어버리는 것. 10.dp로 낮춘다.
            .hazeEffect(
                state = hazeState,
                style = HazeStyle(
                    tint = HazeTint(CoralPrimaryContainer.copy(alpha = 0.30f)),
                    blurRadius = 10.dp
                )
            )
            // ==================== 진단용 임시 제거 — 확인 끝나면 원복 ====================
            // hazeEffect(블러+tint)는 HazeLogger 로그로 RenderEffectBlurEffect 사용/area 정상/
            // tint·blurRadius 값 정상 반영까지 다 확인됐는데도 화면에서는 여전히 단색으로 보임.
            // 가설: hazeEffect 위에 겹겹이 쌓인 아래 3개 반투명 레이어(베이스 그라데이션 + gloss +
            // border)의 누적 alpha가 블러 결과 자체를 가리고 있다. 이 가설을 육안으로 검증하기
            // 위해 아래 3개를 전부 주석 처리해서 .clip(BarShape).hazeEffect(...)까지만 남긴다.
            // 원래 코드는 그대로 주석으로 남겨뒀으니 확인 끝나면 주석만 풀어서 원복하면 된다.

            // // 2. 투명 유리 베이스 그라데이션 — 92%/62%처럼 알파가 높으면 블러를 완전히 가려서
            // // 그냥 단색 패널처럼 보인다. 28%/12%까지 낮춰야 뒤쪽 블러 배경이 비쳐 보인다.
            // .background(
            //     brush = Brush.verticalGradient(
            //         colors = listOf(
            //             CoralPrimaryContainer.copy(alpha = 0.28f),
            //             CoralPrimaryContainer.copy(alpha = 0.12f)
            //         )
            //     )
            // )
            // // 3. 유리 상단 표면 빛 반사(Specular Gloss) — 캡슐 오른쪽 위 모서리에 무지개색
            // // 얼룩이 보였던 원인으로 이 레이어를 의심했다: BarShape는 완전 pill(코너 반경이
            // // indicatorHeight/2 ≈ 48px 이상)인데 endY=40f는 그보다 작아서, 그라데이션의 하드
            // // Transparent 경계가 곡률이 시작되는 지점 "안쪽"에서 끝나버려 anti-aliased 클립
            // // 경계와 겹쳐 색 번짐이 생겼을 가능성이 크다. endY를 코너 곡률 전체를 덮고도 남을
            // // 만큼 크게 늘려서 경계가 곡선 구간 밖(직선 구간)에서 부드럽게 사라지게 한다.
            // .background(
            //     brush = Brush.verticalGradient(
            //         colors = listOf(
            //             Color.White.copy(alpha = 0.35f),
            //             Color.White.copy(alpha = 0.05f),
            //             Color.Transparent
            //         ),
            //         startY = 0f,
            //         endY = 96f
            //     )
            // )
            // // 4. 유리 테두리(Specular Rim) — 상단은 빛을 받아 또렷하게, 하단은 은은하게. 색상
            // // stop을 2개에서 3개로 늘려 급격한 alpha 전환 구간을 없앴다(원인이 이 레이어였을
            // // 경우를 대비한 방어적 수정 — 급격한 전환이 곡선 테두리에서 색 번짐을 유발할 수 있음).
            // .border(
            //     width = 1.dp,
            //     brush = Brush.verticalGradient(
            //         colors = listOf(
            //             Color.White.copy(alpha = 0.65f),
            //             Color.White.copy(alpha = 0.35f),
            //             Color.White.copy(alpha = 0.15f)
            //         )
            //     ),
            //     shape = BarShape
            // )
            // ==================== 진단용 임시 제거 끝 ====================
    )
}

@Composable
private fun RowScope.GlassTabItem(tab: BottomNavTabUiModel) {
    val contentColor by animateColorAsState(
        targetValue = if (tab.selected) CoralPrimary else TextPrimary,
        label = "bottomNavContentColor"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (tab.selected) 1.03f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "bottomNavIconScale"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = tab.onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (tab.selected) tab.selectedIcon else tab.icon,
            contentDescription = tab.label,
            tint = contentColor,
            modifier = Modifier.size(20.dp).scale(iconScale)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = tab.label,
            color = contentColor,
            fontWeight = if (tab.selected) FontWeight.Bold else FontWeight.Medium,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.85f
            )
        )
    }
}
