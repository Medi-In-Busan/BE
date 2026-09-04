package com.mediinbusan.app.core.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
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

/** 임계값을 넘긴 채 손을 뗐을 때 남은 거리를 마저 움직이는 시간. 뒤이어 화면 전환이 받는다. */
const val DetailPullCommitDurationMs = 130

/** 임계값을 못 넘기고 손을 뗐을 때 제자리로 돌아오는 스프링 — 살짝만 튕기고 곧 멈춘다. */
val DetailPullSettleSpec: SpringSpec<Float> =
    spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow)

private const val DetailPullEnterDurationMs = 260
private const val DetailPullExitDurationMs = 220

/**
 * 화면 높이의 몇 분의 1 지점에서 슬라이드를 시작할지. 화면 밖(1/1)에서부터 올라오면 손가락이
 * 멈춘 자리와 너무 멀어 두 동작이 끊겨 보인다 — 카드가 있던 높이 근처에서 이어붙인다.
 */
private const val DetailPullSlideFraction = 3

/** 지도 카드를 끌어올린 방향을 이어받는 상세화면 등장 — 아래에서 위로 밀려 올라온다. */
fun detailPullEnter(): EnterTransition =
    slideInVertically(tween(DetailPullEnterDurationMs, easing = FastOutSlowInEasing)) { height ->
        height / DetailPullSlideFraction
    } + fadeIn(tween(DetailPullEnterDurationMs))

/** 되돌아가기(뒤로가기 버튼·끌어내리기 모두) — 올라온 방향 그대로 아래로 내려가며 사라진다. */
fun detailPullPopExit(): ExitTransition =
    slideOutVertically(tween(DetailPullExitDurationMs, easing = FastOutSlowInEasing)) { height ->
        height / DetailPullSlideFraction
    } + fadeOut(tween(DetailPullExitDurationMs))

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

    val connection = remember(thresholdPx, travelPx) {
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
                    animate(pullPx, travelPx, animationSpec = tween(DetailPullCommitDurationMs)) { value, _ ->
                        pullPx = value
                    }
                    onDismiss()
                } else {
                    animate(pullPx, 0f, animationSpec = DetailPullSettleSpec) { value, _ -> pullPx = value }
                }
                return Velocity(0f, available.y)
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
