package com.mediinbusan.app.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mediinbusan.app.core.ui.BottomNavBar
import com.mediinbusan.app.core.ui.BottomNavTabUiModel

/** MainActivity가 호출하는 앱 최상위 컴포저블. 공용 하단 내비게이션 바 노출 여부/활성 탭을 결정한다. */
@Composable
fun MediInBusanApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        // 기본값(systemBars)을 그대로 두면 상태바/제스처 인셋만큼 여백이 자동으로 생겨
        // Splash의 풀스크린 이미지, Home 자체 TopAppBar 등 모든 화면이 밀려 보인다.
        // 각 화면이 자기 insets는 알아서 처리하므로 여기서는 추가 여백을 만들지 않는다.
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            // expandVertically/shrinkVertically로 높이까지 애니메이션하면 탭바 높이가 매 프레임
            // 바뀌면서 innerPadding.bottom도 같이 흔들려, 그 안에서 세로 중앙 정렬된 LoadingState
            // 스피너가 위로 밀려 보이는 부작용이 있었다. 높이는 즉시 확정하고 알파만 애니메이션한다.
            // delayMillis로 콘텐츠가 먼저 자리 잡은 다음 바가 나타나게 해서, Splash(풀스크린) →
            // Home(상하단 바 있음) 전환이 한 프레임에 훅 줄어드는 느낌을 완화한다.
            AnimatedVisibility(
                visible = shouldShowBottomBar(backStackEntry),
                enter = fadeIn(tween(durationMillis = 300, delayMillis = 150)),
                exit = fadeOut(tween(150))
            ) {
                BottomNavBar(tabs = bottomNavTabs(navController, currentDestination))
            }
        }
    ) {
        // Scaffold의 innerPadding을 NavHost 전체에 매다지 않는다. back stack이 navigate() 호출과
        // 동시에 즉시 갱신되는 반면 실제로 화면에 그려지는 컴포저블은 한두 프레임 늦게 바뀔 수
        // 있는데, innerPadding을 여기서 걸면 그 타이밍차 동안 아직 화면에 남아있는 이전 화면
        // (예: Splash)까지 하단 바 공간만큼 눌려서 "위로 밀리는" 것처럼 보인다. 대신 하단 바가
        // 보이는 화면(Home/HospitalList/Guide/MapView) 각자가 core/ui의 BottomNavBarHeight만큼
        // 직접 여백을 둔다.
        MediInBusanNavHost(navController = navController)
    }
}

private fun shouldShowBottomBar(backStackEntry: NavBackStackEntry?): Boolean {
    val destination = backStackEntry?.destination ?: return false
    return when {
        destination.hasRoute(Route.Home::class) -> true
        destination.hasRoute(Route.HospitalList::class) -> true
        destination.hasRoute(Route.Guide::class) -> true
        destination.hasRoute(Route.MapView::class) -> {
            // TODO: MapView 하나가 "전역 지도"/"병원 상세 지도" 두 의미를 겸하고 있어
            // route 타입만으로는 노출 여부를 못 정하고 argument까지 봐야 한다.
            // 향후 MapOverview / MapDetail(hospitalId)로 route 자체를 분리하는 걸 고려한다.
            backStackEntry.toRoute<Route.MapView>().hospitalId == null
        }
        else -> false
    }
}

// TODO: 4개 탭 아이콘 전부 디자인팀 전용 PNG 리소스 확정되면 교체하고 material-icons-extended 의존성 재검토
@Composable
private fun bottomNavTabs(
    navController: NavHostController,
    currentDestination: NavDestination?
): List<BottomNavTabUiModel> = listOf(
    BottomNavTabUiModel(
        label = "홈",
        icon = Icons.Default.Home,
        selected = currentDestination.isRouteSelected<Route.Home>(),
        onClick = { navController.navigateToTab(Route.Home) }
    ),
    BottomNavTabUiModel(
        label = "의료기관",
        icon = Icons.Default.LocalHospital,
        selected = currentDestination.isRouteSelected<Route.HospitalList>(),
        onClick = { navController.navigateToTab(Route.HospitalList()) }
    ),
    BottomNavTabUiModel(
        label = "가이드",
        icon = Icons.AutoMirrored.Filled.MenuBook,
        selected = currentDestination.isRouteSelected<Route.Guide>(),
        onClick = { navController.navigateToTab(Route.Guide) }
    ),
    BottomNavTabUiModel(
        label = "지도",
        icon = Icons.Default.Map,
        selected = currentDestination.isRouteSelected<Route.MapView>(),
        onClick = { navController.navigateToTab(Route.MapView()) }
    )
)

private inline fun <reified T : Route> NavDestination?.isRouteSelected(): Boolean =
    this?.hierarchy?.any { it.hasRoute(T::class) } == true

/**
 * 탭 전환 표준 패턴: Route.Home까지 스택을 정리하되 각 탭의 상태는 보존한다.
 * graph.findStartDestination()은 Splash를 가리키는데, Splash는 앱 시작 시
 * popUpTo(Route.Splash){inclusive=true}로 이미 백스택에서 빠져 있어 popUpTo 대상이 될 수
 * 없다. 탭 내비게이션의 실질적인 루트인 Route.Home을 직접 지정한다.
 */
private fun NavHostController.navigateToTab(route: Route) {
    navigate(route) {
        popUpTo(Route.Home) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
