package com.mediinbusan.app.core.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * 지도(S-08)에서 마커를 고르면 뜨는 선택 카드를 **위로 끌어올려 상세화면으로 넘어가고**, 그
 * 상세화면을 다시 **아래로 끌어내려 지도로 돌아오는** 한 쌍의 제스처가 공유하는 수치와 모션.
 *
 * 두 방향이 한 동작으로 읽히려면 임계값·이동거리·감속 곡선이 양쪽에서 같아야 한다 — 한쪽만
 * 고치면 "끌어올린 만큼 올라오고 내린 만큼 내려간다"는 느낌이 바로 깨져서 한 파일에 모아둔다.
 * 화면 전환 자체의 슬라이드([detailPullEnter]/[detailPullPopExit])도 손가락이 움직인 방향을
 * 그대로 이어받게 맞춰 뒀다.
 *
 * 쓰는 곳:
 * - 끌어올리기: `feature/map/MapScreen.kt`의 선택 카드 시트 드래그
 * - 끌어내리기: [DetailPullDismissBox] — `HospitalDetailScreen`, `PlaceDetailScreen`
 * - 화면 전환: `core/navigation/MediInBusanNavHost.kt`의 HospitalDetail/PlaceDetail 라우트
 */

/** 이만큼 끌면 화면 전환이 확정된다. 실수로 스쳐서 넘어가지 않을 만큼은 커야 한다. */
val DetailPullThreshold = 64.dp

/** 손가락을 따라 움직일 수 있는 최대 거리. 이 이상 끌어도 더는 밀리지 않는다. */
val DetailPullTravel = 180.dp

/**
 * 손가락 이동량 대비 실제로 따라 움직이는 비율(고무줄 저항). 1보다 작게 둬서 화면이 손가락보다
 * 조금 무겁게 따라오고, 그만큼 "아직 놓으면 되돌아간다"는 여지가 손에 남는다.
 */
const val DetailPullResistance = 0.75f

/** 끌수록 옅어지는 최대 정도. 완전히 투명해지면 뒤의 빈 배경이 드러나 어색해서 살짝만 준다. */
const val DetailPullMaxFade = 0.3f

/**
 * 임계값을 넘긴 채 손을 뗐을 때 남은 거리를 마저 움직이는 스프링.
 *
 * 예전엔 130ms짜리 tween이었는데, tween은 손가락이 내던 속도를 이어받지 못한다(시작 속도가 항상
 * 0이다) — 빠르게 튕겨도 손을 떼는 순간 화면이 한 번 멈췄다가 다시 가속해서, 이 지점이 "뚝"
 * 끊기는 가장 큰 원인이었다. 스프링은 [androidx.compose.animation.core.animate]에 넘긴
 * initialVelocity를 그대로 이어받아 움직이던 속도로 계속 나간다.
 *
 * dampingRatio는 튕김 없는 임계감쇠(1f)다. 여기서 되돌아 튀면 화면을 떠나는 방향과 반대로 한 번
 * 밀리는 셈이라 전환이 더 어색해진다.
 */
val DetailPullCommitSpec: SpringSpec<Float> =
    spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)

/** 임계값을 못 넘기고 손을 뗐을 때 제자리로 돌아오는 스프링 — 살짝만 튕기고 곧 멈춘다. */
val DetailPullSettleSpec: SpringSpec<Float> =
    spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow)

/*
 * 화면 전환 길이·감속 곡선.
 *
 * 예전엔 둘 다 FastOutSlowIn이었다 — 시작에서 가속하고 끝에서 감속하는 곡선이라, 이미 손가락이
 * 밀어 올려 **움직이고 있던** 화면을 이어받는 자리에는 맞지 않는다(정지 상태에서 새로 출발하는 것처럼
 * 보인다). 머티리얼 표준대로 들어올 때는 감속 곡선(LinearOutSlowIn: 처음부터 빠르게 들어와 부드럽게
 * 멈춤), 나갈 때는 가속 곡선(FastOutLinearIn: 부드럽게 떠나 점점 빨라짐)을 쓴다.
 *
 * 길이도 조금 늘렸다(260/220 → 320/280). 짧고 딱 끊기는 것보다, 감속 곡선에 여유를 줘야 마지막
 * 몇 프레임이 부드럽게 잦아든다.
 */
private const val DetailPullEnterDurationMs = 320
private const val DetailPullExitDurationMs = 280

/**
 * 페이드는 슬라이드보다 먼저 끝낸다. 슬라이드 내내 반투명하면 움직이는 동안 뒤가 비쳐 잔상처럼
 * 보이는데, 그게 "덜컹거린다"는 인상을 키운다 — 내용은 일찍 또렷해지고 위치만 마저 정리되게 한다.
 */
private const val DetailPullFadeRatio = 0.6f

/**
 * 화면 높이의 몇 분의 1 지점에서 슬라이드를 시작할지. 화면 밖(1/1)에서부터 올라오면 손가락이
 * 멈춘 자리와 너무 멀어 두 동작이 끊겨 보인다 — 카드가 있던 높이 근처에서 이어붙인다.
 */
private const val DetailPullSlideFraction = 3

/** 지도 카드를 끌어올린 방향을 이어받는 상세화면 등장 — 아래에서 위로 밀려 올라온다. */
fun detailPullEnter(): EnterTransition =
    slideInVertically(tween(DetailPullEnterDurationMs, easing = LinearOutSlowInEasing)) { height ->
        height / DetailPullSlideFraction
    } + fadeIn(tween((DetailPullEnterDurationMs * DetailPullFadeRatio).toInt()))

/** 되돌아가기(뒤로가기 버튼·끌어내리기 모두) — 올라온 방향 그대로 아래로 내려가며 사라진다. */
fun detailPullPopExit(): ExitTransition =
    slideOutVertically(tween(DetailPullExitDurationMs, easing = FastOutLinearInEasing)) { height ->
        height / DetailPullSlideFraction
    } + fadeOut(tween((DetailPullExitDurationMs * DetailPullFadeRatio).toInt()))

/**
 * 상세화면이 [detailPullEnter]로 밀려 올라오는 동안, **떠나는 화면**(지도·홈·목록 등)을 그동안
 * 화면에 그대로 붙잡아 두는 퇴장 전환.
 *
 * NavHost 기본값인 `ExitTransition.None`은 "애니메이션 없음"이라 떠나는 화면이 첫 프레임에
 * 바로 사라진다 — 그러면 아래에서 올라오는 상세화면이 아직 덮지 못한 위쪽에 앱 배경색만 남아
 * 한 번 번쩍인다. 눈에 띄지 않는 알파(1 → 0.99)를 상세화면 등장과 같은 길이로 돌려서, 실제로
 * 색이 변하지는 않으면서 그 시간 동안 화면이 유지되게만 만든다.
 */
fun detailPullUnderlayHold(): ExitTransition =
    fadeOut(tween(DetailPullEnterDurationMs), targetAlpha = 0.99f)

/**
 * 본문을 맨 위에서 더 아래로 끌면 화면이 손가락을 따라 내려가고, [DetailPullThreshold]를 넘긴
 * 채 손을 떼면 [onDismiss]로 되돌아가는 컨테이너.
 *
 * 제스처를 pointerInput이 아니라 nestedScroll로 받는 이유: 상세화면 본문은 전체가 세로 스크롤
 * 영역이라 부모에 드래그 감지를 걸면 스크롤과 서로 제스처를 뺏는다. 대신 스크롤이 맨 위에 닿아
 * 더 소비하지 못하고 남긴 양(onPostScroll의 available.y)만 받아서, "더 내려갈 데가 없을 때부터"
 * 화면이 밀리게 한다.
 */
@Composable
fun DetailPullDismissBox(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val thresholdPx = with(density) { DetailPullThreshold.toPx() }
    val travelPx = with(density) { DetailPullTravel.toPx() }
    // 애니메이션 중에도 매 프레임 읽고 쓰는 값이라 Animatable 대신 단순 상태로 둔다 —
    // Animatable.snapTo는 코루틴이라 같은 프레임 안에서 방금 쓴 값을 되읽을 때 한 박자 늦는다.
    var pullPx by remember { mutableFloatStateOf(0f) }
    // 전환이 확정된 뒤 들어오는 스크롤은 무시한다(popBackStack이 두 번 불리는 것 방지).
    var isDismissing by remember { mutableStateOf(false) }
    // 확정 애니메이션을 onPreFling 바깥에서 계속 돌리기 위한 스코프(아래 주석 참고).
    val scope = rememberCoroutineScope()

    val connection = remember(thresholdPx, travelPx, scope) {
        object : NestedScrollConnection {
            // 내려가 있는 상태에서 손가락을 다시 위로 올리면(available.y < 0) 본문을 스크롤하기
            // 전에 내려간 만큼을 먼저 되감는다 — 안 그러면 화면이 내려간 채로 본문만 스크롤된다.
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isDismissing || source != NestedScrollSource.UserInput) return Offset.Zero
                if (available.y >= 0f || pullPx <= 0f) return Offset.Zero
                val consumed = -minOf(pullPx, -available.y)
                pullPx += consumed
                return Offset(0f, consumed)
            }

            // 본문이 맨 위에 닿아 더 소비하지 못한 아래 방향 이동량만 화면을 끌어내리는 데 쓴다.
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (isDismissing || source != NestedScrollSource.UserInput) return Offset.Zero
                if (available.y <= 0f) return Offset.Zero
                pullPx = (pullPx + available.y * DetailPullResistance).coerceAtMost(travelPx)
                return Offset(0f, available.y)
            }

            // 손을 뗀 시점 — 여기서 확정(되돌아가기)과 취소(제자리 복귀)를 가른다. suspend라
            // 애니메이션이 끝날 때까지 관성 스크롤이 시작되지 않는다.
            override suspend fun onPreFling(available: Velocity): Velocity {
                if (isDismissing || pullPx <= 0f) return Velocity.Zero
                if (pullPx >= thresholdPx) {
                    isDismissing = true
                    // 애니메이션이 끝날 때까지 기다렸다 넘어가면 그 사이 화면이 한 번 멈춰 선다 —
                    // 남은 거리를 계속 내려가는 동안 화면 전환도 같이 시작해서 두 움직임을 겹친다.
                    // popBackStack 이후에도 퇴장 전환이 도는 동안 이 컴포저블은 살아 있어 애니메이션이
                    // 그대로 이어진다.
                    scope.launch {
                        animate(
                            initialValue = pullPx,
                            targetValue = travelPx,
                            initialVelocity = available.y,
                            animationSpec = DetailPullCommitSpec
                        ) { value, _ -> pullPx = value }
                    }
                    onDismiss()
                } else {
                    // 되돌아갈 때도 손가락 속도를 이어받는다 — 위로 튕겨 취소하면 그 기세 그대로 올라간다.
                    animate(
                        initialValue = pullPx,
                        targetValue = 0f,
                        initialVelocity = available.y,
                        animationSpec = DetailPullSettleSpec
                    ) { value, _ -> pullPx = value }
                }
                // 아래 방향 속도만 삼킨다. 예전엔 available.y를 그대로 돌려줘서 위로 튕기는 플링까지
                // 전부 소비했고, 화면을 조금 끌어내린 상태에서 위로 튕기면 제자리로 복귀만 하고
                // 본문은 스크롤되지 않았다.
                return if (available.y > 0f) Velocity(0f, available.y) else Velocity.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = pullPx
                alpha = 1f - (pullPx / travelPx).coerceIn(0f, 1f) * DetailPullMaxFade
            }
            .nestedScroll(connection),
        content = content
    )
}
