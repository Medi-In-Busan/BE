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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// HospitalSearchListScreen에서 처음 만든 "시그널 리빌" 카드 등장 연출 — 즐겨찾기/최근 본 항목
// 리스트도 같은 톤을 쓰기 위해 공용으로 뺐다. 무한스크롤(또는 긴 리스트)에서 나중에 덧붙는/뒤쪽
// 항목까지 매번 재생하면 과하니, 첫 로딩 시 한 화면에 걸치는 정도(맨 앞 N개)까지만 적용한다.
const val InitialCardRevealCount = 6

/**
 * 연출의 속도·범위. 한 화면에 몇 장이 들어오는 레이아웃이냐에 따라 다르게 잡아야 한다.
 *
 * [LIST]는 세로 리스트(병원 목록·즐겨찾기·최근 본 항목·혼잡도)용 원래 값이다 — 카드 한 장이
 * 화면을 넓게 차지해서 한 번에 두세 장만 보이므로, 느긋한 스태거가 그대로 "차례로 등장하는" 연출로 읽힌다.
 *
 * [GRID]는 2열 포토 그리드(부산 관광지·무장애 관광)용이다. 거기서 [LIST] 값을 쓰니
 * **맨 위 6장만 1.8초 동안 스켈레톤인데 그 아래 카드들은 이미 다 떠 있어서**, 연출이 아니라
 * "위쪽만 아직 로딩 중"으로 보였다 — 화면에 들어오는 장수만큼(12장) 넓게 잡고, 대기 없이
 * 촘촘한 간격으로 훑고 지나가게 해서 한 번의 물결로 읽히게 한다.
 */
enum class CardRevealPace(
    val count: Int,
    internal val baseDelayMs: Long,
    internal val staggerMs: Long,
    internal val durationMs: Int
) {
    LIST(InitialCardRevealCount, baseDelayMs = 500L, staggerMs = 220L, durationMs = 500),
    GRID(count = 12, baseDelayMs = 0L, staggerMs = 45L, durationMs = 280)
}

/**
 * itemsKey(보통 리스트 그 자체)가 바뀔 때마다 0부터 다시 순차 공개되는 인덱스 카운트.
 *
 * `rememberSaveable`을 쓴다 — 이 섹션을 담은 LazyColumn 아이템이 화면 밖으로 멀리 스크롤됐다가
 * 돌아오면 Compose가 해당 서브트리를 통째로 폐기했다 다시 만드는데, 그냥 `remember`였다면
 * revealedCount가 0으로 리셋되면서 이미 다 본 카드들이 스크롤해서 다시 볼 때마다 매번 처음처럼
 * 재생됐다. 저장 가능한 상태로 바꿔서 "처음 진입할 때 한 번"만 재생되게 한다.
 *
 * `revealedCount` 값 자체는 복원돼도, 아래 `LaunchedEffect`는 재구성될 때마다 무조건 다시
 * 실행된다(같은 itemsKey라도 컴포저블이 폐기됐다 새로 생기면 "처음 실행"이라 재생을 건너뛸
 * 근거가 없다) — 그래서 이미 다 공개된 상태(revealTarget에 도달)면 즉시 반환해 스태거 시퀀스를
 * 처음부터 다시 돌리지 않게 막는다. 이 가드가 없으면 rememberSaveable로 값은 복원돼도, 그 값이
 * 이 가드 없는 시퀀스에 곧바로 덮어써져 애니메이션만 다시 재생되는 문제가 있었다.
 *
 * [revealOnceKey]를 주면 "앱 실행 중 이 화면의 첫 진입 한 번"으로 더 좁힌다 — 화면을 나갔다 다시
 * 들어오거나(백스택 엔트리와 ViewModel이 새로 생겨 위 rememberSaveable로는 알 수 없다) 같은 화면
 * 안에서 목록을 다시 조회해도(지역 변경·재시도) 연출 없이 곧바로 다 보인다. 같은 화면을 가리키는
 * 안정적인 문자열이면 되고(예: "tourism-catalog-ACCESSIBLE"), 안 주면 기존 동작 그대로다.
 */
@Composable
fun rememberRevealedCount(
    itemsKey: Any,
    itemCount: Int,
    revealOnceKey: String? = null,
    pace: CardRevealPace = CardRevealPace.LIST
): Int {
    // 이 화면에서 이미 한 번 재생했는지. 판정은 진입 시점에 한 번만 하고(remember) 그 뒤로는
    // 바뀌지 않는다 — 재생을 시작하면서 곧바로 기록하기 때문에, 다시 읽으면 "재생 중인데 이미
    // 재생됨"이 되어 진행 중인 연출이 중간에 끊긴다.
    val alreadyPlayed = remember(revealOnceKey) {
        revealOnceKey != null && revealOnceKey in playedRevealKeys
    }
    var revealedCount by rememberSaveable(itemsKey) { mutableStateOf(0) }
    // itemCount도 키에 넣는다. 목록 화면은 "데이터는 도착했지만 화면에 그릴 목록은 아직 비어 있는"
    // 순간을 거칠 수 있는데(예: TourismCatalogViewModel은 catalog를 먼저 넣고 필터링 결과인
    // visibleItems를 코루틴으로 조금 뒤에 채운다), 그때 이 이펙트가 itemCount == 0으로 한 번 돌고
    // 끝나버렸다. 키가 itemsKey뿐이면 뒤늦게 항목이 채워져도 다시 돌지 않아서, 연출 대상 카드들이
    // revealedCount = 0인 채 스켈레톤으로 영영 굳었다("위쪽 카드만 계속 로딩 중"의 정체다).
    // 항목이 늘어난 뒤 다시 돌더라도 바로 아래 revealTarget 가드가 이미 다 공개된 경우를 걸러내므로,
    // 무한 스크롤로 페이지가 덧붙을 때 연출이 다시 재생되지는 않는다.
    LaunchedEffect(itemsKey, itemCount, alreadyPlayed) {
        if (itemCount == 0 || alreadyPlayed) return@LaunchedEffect
        val revealTarget = minOf(itemCount, pace.count)
        if (revealedCount >= revealTarget) return@LaunchedEffect
        // 끝까지 본 게 아니라 "시작했다"를 기록한다 — 연출 도중에 나가버린 경우에도 다시 들어와서
        // 처음부터 재생되지 않아야 한다.
        revealOnceKey?.let { playedRevealKeys += it }
        if (pace.baseDelayMs > 0) delay(pace.baseDelayMs)
        repeat(revealTarget) { index ->
            revealedCount = index + 1
            delay(pace.staggerMs)
        }
    }
    // 이미 본 화면은 스켈레톤 한 프레임도 없이 처음부터 다 보이게 한다.
    return if (alreadyPlayed) itemCount else revealedCount
}

/**
 * [rememberRevealedCount]에 revealOnceKey를 넘긴 화면 중 연출을 이미 재생한 것들.
 *
 * 화면을 나갔다 다시 들어오면 ViewModel과 함께 리스트도 새로 조회되므로(nav 백스택 엔트리가
 * 새로 생긴다) 화면 안의 어떤 상태로도 "전에 본 적 있음"을 알 수 없다 — 그래서 프로세스 수명으로
 * 들고 있는다. 앱을 완전히 껐다 켜면 다시 한 번 재생된다.
 *
 * Compose 컴포지션(메인 스레드)에서만 읽고 쓴다.
 */
private val playedRevealKeys = mutableSetOf<String>()

/** 0(등장 전, 스켈레톤만 보임) ~ 1(완전히 보임) 진행도. 애니메이션 대상이 아닌 카드는 항상 1. */
@Composable
fun rememberCardRevealProgress(
    isRevealAnimated: Boolean,
    isRevealed: Boolean,
    pace: CardRevealPace = CardRevealPace.LIST
): Float {
    val progress by animateFloatAsState(
        targetValue = if (!isRevealAnimated || isRevealed) 1f else 0f,
        animationSpec = tween(durationMillis = pace.durationMs, easing = FastOutSlowInEasing),
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
                // 이전엔 DividerColor(#EEEEEE)~흰색 사이를 오갔는데, 흰 배경 화면(부산 관광지/
                // 무장애 관광 그리드 등) 위에서는 스켈레톤 전체가 거의 안 보여서 "로딩 중인데
                // 흰 화면처럼 보인다"는 문제로 이어졌다 — 배경색과 무관하게 항상 또렷한 회색이
                // 보이도록 두 색 다 흰색보다 뚜렷이 어둡게 내린다.
                Brush.linearGradient(
                    colors = listOf(ShimmerBase, ShimmerHighlight, ShimmerBase),
                    start = Offset(shimmerOffset, 0f),
                    end = Offset(shimmerOffset + 300f, 300f)
                )
            )
    )
}

private val ShimmerBase = Color(0xFFD9D9D9)
private val ShimmerHighlight = Color(0xFFEFEFEF)

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
