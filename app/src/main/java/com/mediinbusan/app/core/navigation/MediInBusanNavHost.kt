package com.mediinbusan.app.core.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mediinbusan.app.feature.favorite.FavoriteScreen
import com.mediinbusan.app.feature.guide.GuideScreen
import com.mediinbusan.app.feature.home.HomeScreen
import com.mediinbusan.app.feature.hospitaldetail.HospitalDetailScreen
import com.mediinbusan.app.feature.hospitallist.HospitalListScreen
import com.mediinbusan.app.feature.map.MapScreen
import com.mediinbusan.app.feature.nearby.NearbyScreen
import com.mediinbusan.app.feature.nearby.PlaceDetailScreen
import com.mediinbusan.app.feature.onboarding.LanguageSelectScreen
import com.mediinbusan.app.feature.recent.RecentlyViewedScreen
import com.mediinbusan.app.feature.search.SearchScreen
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
            // 팀원이 스켈레톤 단계에서 만든 OnboardingScreen(언어+의료목적 통합 샘플)은 라우팅에서
            // 제거하고 파일만 보존한다. 언어선택 UI 개선 작업으로 독립 화면을 새로 배선한다.
            // 의료 목적 선택(F-003)은 별도 진단 플로우로 분리될 예정이라 이 화면에서 다루지 않는다.
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
                onNavigateToHospitalList = { purpose -> navController.navigate(Route.HospitalList(purpose)) },
                onNavigateToHospitalDetail = { hospitalId -> navController.navigate(Route.HospitalDetail(hospitalId)) },
                onNavigateToGuide = { navController.navigate(Route.Guide) },
                onNavigateToFavorite = { navController.navigate(Route.Favorite) },
                onNavigateToSettings = { navController.navigate(Route.Settings) },
                // TODO: '추천 웰니스' 전용 진입 route가 없음. Route.Nearby(hospitalId)는 병원 맥락이
                // 필수라 Home에서 못 쓰기 때문에, 임시로 '웰니스' 카테고리의 HospitalList로 연결한다.
                // 전용 route가 생기면 교체할 것.
                onNavigateToWellness = { navController.navigate(Route.HospitalList(medicalPurpose = "웰니스")) },
                onNavigateToMap = { navController.navigate(Route.MapView(hospitalId = null)) },
                onNavigateToSelfDiagnosis = { navController.navigate(Route.SelfDiagnosis) },
                onNavigateToSearch = { navController.navigate(Route.Search) }
            )
        }
        composable<Route.Search> {
            SearchScreen(
                onSelectHospital = { hospitalId -> navController.navigate(Route.HospitalDetail(hospitalId)) },
                onBack = navController::popBackStack
            )
        }
        composable<Route.HospitalList> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.HospitalList>()
            HospitalListScreen(
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
            SelfDiagnosisPlaceholderScreen()
        }
    }
}

// TODO: 자가진단 플로우(회원 정보 로컬 저장 포함) 실제 구현은 별도 이슈. 지금은 크래시 없이
// 진입만 가능하도록 하는 최소 스텁 화면이다.
@Composable
private fun SelfDiagnosisPlaceholderScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "진단하기 기능은 준비 중입니다.", style = MaterialTheme.typography.titleMedium)
    }
}
