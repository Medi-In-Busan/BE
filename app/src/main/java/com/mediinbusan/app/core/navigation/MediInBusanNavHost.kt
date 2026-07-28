package com.mediinbusan.app.core.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mediinbusan.app.feature.favorite.FavoriteScreen
import com.mediinbusan.app.feature.guide.GuideScreen
import com.mediinbusan.app.feature.home.HomeScreen
import com.mediinbusan.app.feature.hospitaldetail.HospitalDetailScreen
import com.mediinbusan.app.feature.hospitalsearchlist.HospitalSearchListScreen
import com.mediinbusan.app.feature.map.MapScreen
import com.mediinbusan.app.feature.nearby.NearbyScreen
import com.mediinbusan.app.feature.nearby.PlaceDetailScreen
import com.mediinbusan.app.feature.languageselect.LanguageSelectScreen
import com.mediinbusan.app.feature.recent.RecentlyViewedScreen
import com.mediinbusan.app.feature.selfdiagnosis.SelfDiagnosisScreen
import com.mediinbusan.app.feature.settings.NotificationSettingsScreen
import com.mediinbusan.app.feature.settings.SettingsInfoDetailScreen
import com.mediinbusan.app.feature.settings.SettingsScreen
import com.mediinbusan.app.feature.splash.SplashScreen

/** 10개 화면(S-01~S-10)을 잇는 단일 NavHost. feature 패키지는 서로를 직접 참조하지 않고 이 파일을 통해서만 연결된다. */
@Composable
fun MediInBusanNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    // Navigation Compose 2.8+는 지정 안 해도 자체 기본 fade 전환이 들어가는데, Splash(엣지투엣지
    // 풀스크린 이미지)와 Home(TopAppBar 인셋 적용)처럼 레이아웃 구조가 크게 다른 화면 사이에
    // 그 전환이 걸리면 겹치는 동안 이미지가 위로 튀는 것처럼 보인다. 명시적으로 애니메이션을 끈다.
    NavHost(
        navController = navController,
        startDestination = Route.Splash,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable<Route.Splash> {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Route.Onboarding) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.Onboarding> {
            // 스켈레톤 단계의 통합 샘플(구 OnboardingScreen, 언어+의료목적 통합)은 삭제됐고
            // feature/languageselect의 언어선택 화면만 배선한다. 의료 목적 선택(F-003)은 별도
            // 진단 플로우로 분리될 예정이라 이 화면에서 다루지 않는다.
            LanguageSelectScreen(
                onNext = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Onboarding) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.Home> {
            HomeScreen(
                onNavigateToHospitalDetail = { hospitalId -> navController.navigate(Route.HospitalDetail(hospitalId)) },
                onNavigateToFavorite = { navController.navigate(Route.Favorite) },
                onNavigateToSettings = { navController.navigate(Route.Settings) },
                onNavigateToSelfDiagnosis = { navController.navigate(Route.SelfDiagnosis) },
                // 아래 셋(가이드/지도/검색)은 전부 바텀바가 계속 보이는 목적지라, 하단 탭 클릭과
                // 동일하게 navigateToTab을 써야 한다. 순수 navigate()를 쓰면 저장된 상태가
                // restoreState로 소비되지 않고 쌓여서 이후 바텀바 "홈" 탭이 안 먹는 문제가 있었다.
                onNavigateToGuide = { navController.navigateToTab(Route.Guide) },
                onNavigateToMap = { navController.navigateToTab(Route.MapView(hospitalId = null)) },
                // 의료목적 선택/의료기관 찾기/웰니스/검색바 4개 진입점이 전부 여기 하나로 모인다.
                // purpose가 있으면 HospitalSearchListScreen 진입 시 해당 필터로 자동 검색된다.
                onNavigateToSearch = { purpose -> navController.navigateToTab(Route.HospitalSearchList(purpose)) }
            )
        }
        composable<Route.HospitalSearchList> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.HospitalSearchList>()
            HospitalSearchListScreen(
                medicalPurpose = route.medicalPurpose,
                onSelectHospital = { hospitalId -> navController.navigate(Route.HospitalDetail(hospitalId)) },
                onBack = navController::popBackStack
            )
        }
        composable<Route.HospitalDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.HospitalDetail>()
            HospitalDetailScreen(
                hospitalId = route.hospitalId,
                onNavigateToGuide = { navController.navigate(Route.Guide) },
                onNavigateToNearby = { navController.navigate(Route.Nearby(route.hospitalId)) },
                onNavigateToMap = { navController.navigate(Route.MapView(route.hospitalId)) },
                onBack = navController::popBackStack
            )
        }
        composable<Route.Guide> {
            GuideScreen(onBack = navController::popBackStack)
        }
        composable<Route.Nearby> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Nearby>()
            NearbyScreen(
                hospitalId = route.hospitalId,
                onSelectPlace = { placeId -> navController.navigate(Route.PlaceDetail(placeId)) },
                onNavigateToMap = { navController.navigate(Route.MapView(route.hospitalId)) },
                onBack = navController::popBackStack
            )
        }
        composable<Route.PlaceDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.PlaceDetail>()
            PlaceDetailScreen(
                placeId = route.placeId,
                onBack = navController::popBackStack
            )
        }
        composable<Route.MapView> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.MapView>()
            MapScreen(
                hospitalId = route.hospitalId,
                onSelectHospital = { hospitalId -> navController.navigate(Route.HospitalDetail(hospitalId)) },
                onBack = navController::popBackStack
            )
        }
        composable<Route.Favorite> {
            FavoriteScreen(
                onSelectHospital = { hospitalId -> navController.navigate(Route.HospitalDetail(hospitalId)) },
                onSelectPlace = { placeId -> navController.navigate(Route.PlaceDetail(placeId)) },
                onBack = navController::popBackStack
            )
        }
        composable<Route.Settings> {
            SettingsScreen(
                onBack = navController::popBackStack,
                onNavigateToInfoDetail = { infoId -> navController.navigate(Route.SettingsInfoDetail(infoId)) },
                onNavigateToNotificationSettings = { navController.navigate(Route.NotificationSettings) },
                onNavigateToFavoriteManage = { navController.navigate(Route.Favorite) },
                onNavigateToRecentlyViewed = { navController.navigate(Route.RecentlyViewed) }
            )
        }
        composable<Route.SettingsInfoDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.SettingsInfoDetail>()
            SettingsInfoDetailScreen(infoId = route.infoId, onBack = navController::popBackStack)
        }
        composable<Route.NotificationSettings> {
            NotificationSettingsScreen(onBack = navController::popBackStack)
        }
        composable<Route.RecentlyViewed> {
            RecentlyViewedScreen(
                onSelectHospital = { hospitalId -> navController.navigate(Route.HospitalDetail(hospitalId)) },
                onSelectPlace = { placeId -> navController.navigate(Route.PlaceDetail(placeId)) },
                onBack = navController::popBackStack
            )
        }
        composable<Route.SelfDiagnosis> {
            SelfDiagnosisScreen(onBack = navController::popBackStack)
        }
    }
}
