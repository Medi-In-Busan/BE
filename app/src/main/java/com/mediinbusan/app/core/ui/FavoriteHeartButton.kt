package com.mediinbusan.app.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import kotlinx.coroutines.launch

/** 하트를 눌러 들어갈 때의 축소 비율. 여기서 튀어 올라야 "눌렀다"가 손끝에 남는다. */
private const val FavoriteAddPressScale = 0.72f

/** 해제는 추가보다 얕게 눌린다 — 되돌리는 동작이라 같은 세기로 튀면 과하다. */
private const val FavoriteRemovePressScale = 0.86f

/** 눌리는 데 걸리는 시간. 손가락이 닿은 순간 이미 반쯤 눌려 있어야 해서 아주 짧다. */
private const val FavoritePressDurationMs = 90

/**
 * 즐겨찾기 하트를 눌렀을 때의 스케일 팝.
 *
 * `isFavorite` 값이 바뀌는 걸 보고 재생하지 않고 **클릭 시점에** 재생한다. 값 기준으로 만들면
 * LazyColumn/LazyGrid가 슬롯을 재활용할 때(키 없이 index로 재사용되는 목록이 있다) 스크롤만 해도
 * 이전 항목과 값이 달라 하트가 혼자 튀어 오른다. 클릭 기준이면 그런 오작동이 없고, 저장 결과가
 * Room Flow를 타고 돌아오기 전에 바로 반응한다는 이점도 있다.
 *
 * 되돌아올 때는 낮은 damping 스프링이라 1f를 지나쳐 한 번 부풀었다가 잡힌다 — 3단계 애니메이션을
 * 직접 쓰지 않고 스프링에 맡기는 이유다.
 */
@Composable
fun rememberFavoriteTogglePop(isFavorite: Boolean, onToggle: () -> Unit): FavoriteTogglePop {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val haptics = rememberHaptics()
    val currentIsFavorite by rememberUpdatedState(isFavorite)
    val currentOnToggle by rememberUpdatedState(onToggle)

    return remember(scale, scope, haptics) {
        FavoriteTogglePop(
            scaleModifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
            onClick = {
                // 누른 시점의 값을 여기서 붙잡아 둔다. 코루틴 안에서 읽으면 그 사이 저장 결과가
                // Room Flow를 타고 돌아와 값이 이미 뒤집혀 있을 수 있어, 추가인데 해제 팝이 나간다.
                val isAdding = !currentIsFavorite
                // 스케일 팝과 짝이 되는 촉각 피드백. 만드는 동작(추가)은 확정 신호, 되돌리는
                // 동작(해제)은 가벼운 틱이라 화면을 안 봐도 어느 쪽이었는지 손끝에서 갈린다.
                if (isAdding) haptics.confirm() else haptics.tick()
                currentOnToggle()
                scope.launch {
                    // 연타하면 이전 팝이 아직 돌고 있다. 멈추지 않으면 두 애니메이션이 같은
                    // Animatable을 두고 다퉈 하트가 어중간한 크기에서 멈춘다.
                    scale.stop()
                    scale.animateTo(
                        targetValue = if (isAdding) FavoriteAddPressScale else FavoriteRemovePressScale,
                        animationSpec = tween(
                            durationMillis = FavoritePressDurationMs,
                            easing = FastOutLinearInEasing
                        )
                    )
                    // 추가는 1f를 크게 지나쳤다 잡히고(HighBouncy), 해제는 얌전히 제자리로 온다.
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = if (isAdding) {
                                Spring.DampingRatioHighBouncy
                            } else {
                                Spring.DampingRatioMediumBouncy
                            },
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
            }
        )
    }
}

/**
 * [rememberFavoriteTogglePop]의 결과. [scaleModifier]는 하트 아이콘에만 걸어야 한다 —
 * 흰 원 배경이나 터치 영역까지 같이 커지면 옆 요소를 밀어내는 것처럼 보인다.
 */
@Immutable
class FavoriteTogglePop internal constructor(
    val scaleModifier: Modifier,
    val onClick: () -> Unit
)

/** 흰 원형 배경 위 하트 아이콘 즐겨찾기 토글. Home의 RecommendedHospitalCard 패턴을 공용화한다. */
@Composable
fun FavoriteHeartButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    val pop = rememberFavoriteTogglePop(isFavorite = isFavorite, onToggle = onClick)
    val strings = LocalAppStrings.current.common
    // 기본값 36dp는 최소 권장 터치 영역(48dp)보다 작다 — RoundIconButton과 같은 이유로
    // minimumInteractiveComponentSize()를 붙여 보이는 원은 그대로 두고 터치 영역만 넓힌다.
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .size(size)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = pop.onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorite) {
                strings.favoriteRemoveContentDescription
            } else {
                strings.favoriteAddContentDescription
            },
            tint = CoralPrimary,
            modifier = pop.scaleModifier
        )
    }
}
