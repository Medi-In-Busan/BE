package com.mediinbusan.app.core.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mediinbusan.app.core.i18n.AppLanguageViewModel
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.appStringsFor
import com.mediinbusan.app.core.ui.BottomNavBar
import com.mediinbusan.app.core.ui.BottomNavTabUiModel
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

/** MainActivity가 호출하는 앱 최상위 컴포저블. 공용 하단 내비게이션 바 노출 여부/활성 탭을 결정한다. */
@Composable
fun MediInBusanApp(languageViewModel: AppLanguageViewModel = hiltViewModel()) {
    val language by languageViewModel.language.collectAsState()

    CompositionLocalProvider(LocalAppStrings provides appStringsFor(language)) {
        MediInBusanAppContent()
    }
}

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun MediInBusanAppContent() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    // BottomNavBar가 실시간으로 흐리게 비춰 보여줄 실제 화면 콘텐츠를 여기서 hazeSource로 표시하고,
    // 같은 상태를 BottomNavBar에 넘겨 그 위에서 hazeEffect로 블러를 그린다.
    val hazeState = rememberHazeState()
    // 지도(S-08)에서 마커/카드를 선택하면 하단 탭바 대신 선택 카드가 그 자리를 차지한다 —
    // feature/map은 이 core/navigation 패키지를 모르므로 BottomBarVisibilityController(순수 Kotlin
    // object 싱글턴)를 통해서만 신호를 받는다.
    val mapSelectionActive by BottomBarVisibilityController.mapSelectionActive.collectAsState()

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
                visible = shouldShowBottomBar(backStackEntry, mapSelectionActive),
                enter = fadeIn(tween(durationMillis = 300, delayMillis = 150)),
                exit = fadeOut(tween(150))
            ) {
                BottomNavBar(
                    tabs = bottomNavTabs(navController, currentDestination),
                    hazeState = hazeState
                )
            }
        }
    ) {
        // Scaffold의 innerPadding을 NavHost 전체에 매다지 않는다. back stack이 navigate() 호출과
        // 동시에 즉시 갱신되는 반면 실제로 화면에 그려지는 컴포저블은 한두 프레임 늦게 바뀔 수
        // 있는데, innerPadding을 여기서 걸면 그 타이밍차 동안 아직 화면에 남아있는 이전 화면
        // (예: Splash)까지 하단 바 공간만큼 눌려서 "위로 밀리는" 것처럼 보인다. 대신 하단 바가
        // 보이는 화면(Home/HospitalSearchList/Guide/MapView) 각자가 core/ui의 BottomNavBarHeight만큼
        // 직접 여백을 둔다.
        MediInBusanNavHost(
            navController = navController,
            modifier = Modifier.hazeSource(state = hazeState)
        )
    }
}

private fun shouldShowBottomBar(backStackEntry: NavBackStackEntry?, mapSelectionActive: Boolean): Boolean {
    val destination = backStackEntry?.destination ?: return false
    return when {
        destination.hasRoute(Route.Home::class) -> true
        destination.hasRoute(Route.HospitalSearchList::class) -> true
        destination.hasRoute(Route.Guide::class) -> true
        destination.hasRoute(Route.MapView::class) -> {
            // TODO: MapView 하나가 "전역 지도"/"병원 상세 지도" 두 의미를 겸하고 있어
            // route 타입만으로는 노출 여부를 못 정하고 argument까지 봐야 한다.
            // 향후 MapOverview / MapDetail(hospitalId)로 route 자체를 분리하는 걸 고려한다.
            // 전역 지도 모드라도 마커/카드가 선택돼 있으면(mapSelectionActive) 하단 탭바 자리를
            // 선택 카드가 대신 차지하므로 숨긴다 — feature/map/MapScreen.kt의 BrowseMap 참고.
            backStackEntry.toRoute<Route.MapView>().hospitalId == null && !mapSelectionActive
        }
        destination.hasRoute(Route.DocumentScan::class) -> true
        // Settings/알림설정/즐겨찾기/최근본항목/정보 상세는 전부 공용 탑바 없이 자체 뒤로가기
        // 버튼이 있는 일반 push 화면이라 하단 탭바를 안 보여준다(SettingsScreen.kt 참고).
        else -> false
    }
}

// TODO: 5개 탭 아이콘 전부 디자인팀 전용 PNG 리소스 확정되면 교체하고 material-icons-extended 의존성 재검토
// 활성 표시는 크기 확대가 아니라 Outline(비활성)→Filled(활성) 아이콘 전환이 핵심이라(BottomNavBar.kt
// 리뷰 피드백 참고) 탭마다 두 버전을 다 넘긴다.
@Composable
private fun bottomNavTabs(
    navController: NavHostController,
    currentDestination: NavDestination?
): List<BottomNavTabUiModel> {
    val strings = LocalAppStrings.current.common
    return listOf(
        BottomNavTabUiModel(
            label = strings.bottomNavHomeLabel,
            icon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home,
            selected = currentDestination.isRouteSelected<Route.Home>(),
            onClick = { navController.navigateToTab(Route.Home) }
        ),
        BottomNavTabUiModel(
            label = strings.bottomNavHospitalLabel,
            icon = Icons.Outlined.LocalHospital,
            selectedIcon = Icons.Filled.LocalHospital,
            selected = currentDestination.isRouteSelected<Route.HospitalSearchList>(),
            onClick = { navController.navigateToTab(Route.HospitalSearchList) }
        ),
        BottomNavTabUiModel(
            label = strings.bottomNavGuideLabel,
            icon = Icons.AutoMirrored.Outlined.MenuBook,
            selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
            selected = currentDestination.isRouteSelected<Route.Guide>(),
            onClick = { navController.navigateToTab(Route.Guide) }
        ),
        BottomNavTabUiModel(
            label = strings.bottomNavMapLabel,
            icon = Icons.Outlined.Map,
            selectedIcon = Icons.Filled.Map,
            selected = currentDestination.isRouteSelected<Route.MapView>(),
            onClick = { navController.navigateToTab(Route.MapView()) }
        ),
        BottomNavTabUiModel(
            label = strings.bottomNavDocumentScanLabel,
            icon = Icons.Outlined.CameraAlt,
            selectedIcon = Icons.Filled.CameraAlt,
            selected = currentDestination.isRouteSelected<Route.DocumentScan>(),
            onClick = { navController.navigateToTab(Route.DocumentScan) }
        )
    )
}

private inline fun <reified T : Route> NavDestination?.isRouteSelected(): Boolean =
    this?.hierarchy?.any { it.hasRoute(T::class) } == true
