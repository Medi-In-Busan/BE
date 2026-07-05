package com.mediinbusan.app.core.navigation

import androidx.compose.runtime.Composable
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
import com.mediinbusan.app.feature.onboarding.OnboardingScreen
import com.mediinbusan.app.feature.settings.SettingsScreen
import com.mediinbusan.app.feature.splash.SplashScreen

/** 10개 화면(S-01~S-10)을 잇는 단일 NavHost. feature 패키지는 서로를 직접 참조하지 않고 이 파일을 통해서만 연결된다. */
@Composable
fun MediInBusanNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Route.Splash) {
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
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Onboarding) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.Home> {
            HomeScreen(
                onNavigateToHospitalList = { purpose -> navController.navigate(Route.HospitalList(purpose)) },
                onNavigateToGuide = { navController.navigate(Route.Guide) },
                onNavigateToFavorite = { navController.navigate(Route.Favorite) },
                onNavigateToSettings = { navController.navigate(Route.Settings) }
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
            SettingsScreen(onBack = navController::popBackStack)
        }
    }
}
