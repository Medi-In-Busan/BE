package com.mediinbusan.app.core.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.ui.launchIntentSafely
import com.mediinbusan.app.data.guide.GuidePhase
import com.mediinbusan.app.feature.favorite.FavoriteScreen
import com.mediinbusan.app.feature.guide.AirportDeparturePreparationDetailScreen
import com.mediinbusan.app.feature.guide.EnglishDocumentsResultsDetailScreen
import com.mediinbusan.app.feature.guide.GuideDetailItemId
import com.mediinbusan.app.feature.guide.GuideScreen
import com.mediinbusan.app.feature.guide.GuideStepDetailScreen
import com.mediinbusan.app.feature.guide.HospitalInquiryDetailScreen
import com.mediinbusan.app.feature.guide.HospitalLocationCheckinDetailScreen
import com.mediinbusan.app.feature.guide.InsuranceDocumentsDetailScreen
import com.mediinbusan.app.feature.guide.MedicalRecordsTestResultsDetailScreen
import com.mediinbusan.app.feature.guide.MedicationScheduleDetailScreen
import com.mediinbusan.app.feature.guide.PassportReservationInfoDetailScreen
import com.mediinbusan.app.feature.guide.PaymentMethodCheckDetailScreen
import com.mediinbusan.app.feature.guide.PostTreatmentPrecautionsDetailScreen
import com.mediinbusan.app.feature.guide.PreInquiryInformationDetailScreen
import com.mediinbusan.app.feature.guide.ReceiptInsuranceDocumentsDetailScreen
import com.mediinbusan.app.feature.guide.TotalCostCoverageCheckDetailScreen
import com.mediinbusan.app.feature.guide.TreatmentExaminationDetailScreen
import com.mediinbusan.app.feature.guide.VisaEntryCheckDetailScreen
import com.mediinbusan.app.feature.home.HomeScreen
import com.mediinbusan.app.feature.hospitaldetail.HospitalDetailScreen
import com.mediinbusan.app.feature.hospitalsearchlist.HospitalSearchListScreen
import com.mediinbusan.app.feature.map.MapScreen
import com.mediinbusan.app.feature.nearby.NearbyScreen
import com.mediinbusan.app.feature.nearby.PlaceDetailScreen
import com.mediinbusan.app.feature.languageselect.LanguageSelectScreen
import com.mediinbusan.app.feature.recent.RecentlyViewedScreen
import com.mediinbusan.app.feature.selfdiagnosis.DiagnosisCtaTarget
import com.mediinbusan.app.feature.selfdiagnosis.SelfDiagnosisScreen
import com.mediinbusan.app.feature.settings.NotificationSettingsScreen
import com.mediinbusan.app.feature.settings.SettingsInfoDetailScreen
import com.mediinbusan.app.feature.settings.SettingsScreen
import com.mediinbusan.app.feature.splash.SplashScreen
import com.mediinbusan.app.feature.documentscan.DocumentScanScreen

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
                onNavigateToSelfDiagnosis = {
                    // 언어선택은 이미 끝냈고(재개된 세션) 진단만 남은 경우. Splash가 스스로를
                    // 스택에서 지우므로 뒤로가기는 SelfDiagnosisScreen의 onBack(popBackStack)이
                    // 처리할 대상이 없어 앱을 벗어난다 — 언어선택을 다시 거치게 하고 싶지 않아 의도한 동작이다.
                    navController.navigate(Route.SelfDiagnosis(fromOnboarding = true)) {
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
                    // popUpTo 없이 그대로 push한다 — 진단 화면에서 시스템 뒤로가기를 누르면
                    // 이 언어선택 화면으로 돌아와야 하기 때문(스택에 남겨둬야 함).
                    navController.navigate(Route.SelfDiagnosis(fromOnboarding = true))
                }
            )
        }
        composable<Route.Home> {
            HomeScreen(
                onNavigateToHospitalDetail = { hospitalId -> navController.navigate(Route.HospitalDetail(hospitalId)) },
                onNavigateToFavorite = { navController.navigate(Route.Favorite) },
                onNavigateToSettings = { navController.navigate(Route.Settings) },
                onNavigateToSelfDiagnosis = { navController.navigate(Route.SelfDiagnosis()) },
                // 아래 셋(가이드/지도/검색)은 전부 바텀바가 계속 보이는 목적지라, 하단 탭 클릭과
                // 동일하게 navigateToTab을 써야 한다. 순수 navigate()를 쓰면 저장된 상태가
                // restoreState로 소비되지 않고 쌓여서 이후 바텀바 "홈" 탭이 안 먹는 문제가 있었다.
                onNavigateToGuide = { navController.navigateToTab(Route.Guide) },
                onNavigateToMap = { navController.navigateToTab(Route.MapView(hospitalId = null)) },
                // 병원 검색 API가 실패해도 웰니스 UI/API를 확인할 수 있도록 MVP 확인용 기준 병원(해운대권 regNo=14)으로 바로 진입한다.
                onNavigateToWellness = { navController.navigate(Route.Nearby(hospitalId = "14")) },
                // 의료목적 선택/검색바 진입점이 전부 여기 하나로 모인다. purpose/포커스 요청은 Route
                // 인자가 아니라 HomeViewModel이 PendingHospitalSearchEntry에 미리 심어두고,
                // HospitalSearchListViewModel이 진입 시 그걸 읽는다(PendingHospitalSearchEntry 주석
                // 참고) — 그래서 여기서는 다른 바텀바 탭 화면과 동일하게 항상 args 없는 navigateToTab만
                // 쓰면 된다(navigateToTab 함수 주석 참고 — 예전엔 검색바만 순수 navigate()를 따로 써서
                // 바텀바 "홈" 탭이 안 먹는 문제가 있었다).
                onNavigateToSearch = { navController.navigateToTab(Route.HospitalSearchList) },
                onNavigateToSearchFocused = { navController.navigateToTab(Route.HospitalSearchList) }
            )
        }
        composable<Route.HospitalSearchList> {
            HospitalSearchListScreen(
                onSelectHospital = { hospitalId -> navController.navigate(Route.HospitalDetail(hospitalId)) },
                onNavigateToSettings = { navController.navigate(Route.Settings) }
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
            GuideScreen(
                onMenuClick = { navController.navigate(Route.Settings) },
                onStepClick = { step ->
                    navController.navigate(Route.GuideStepDetail(phase = step.phase, title = step.title))
                }
            )
        }
        composable<Route.GuideStepDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.GuideStepDetail>()
            val phase = route.phase
            if (phase == GuidePhase.TREATMENT_EXAMINATION) {
                // STEP04는 브리핑 카드 섹션이 있어 공용 GuideStepDetailScreen 대신 전용 화면을 쓴다.
                TreatmentExaminationDetailScreen(onBack = navController::popBackStack)
            } else {
                GuideStepDetailScreen(
                    phase = phase,
                    onBack = navController::popBackStack,
                    onItemClick = { item ->
                        when (item.id) {
                            GuideDetailItemId.VISA_ENTRY_CHECK -> navController.navigate(Route.VisaEntryCheckDetail)
                            GuideDetailItemId.INSURANCE_DOCUMENT_CHECK -> navController.navigate(Route.InsuranceDocumentsDetail)
                            GuideDetailItemId.HOSPITAL_INQUIRY -> navController.navigate(Route.HospitalInquiryDetail)
                            GuideDetailItemId.PRE_INQUIRY_INFORMATION -> navController.navigate(Route.PreInquiryInformationDetail)
                            GuideDetailItemId.PASSPORT_RESERVATION_INFO -> navController.navigate(Route.PassportReservationInfoDetail)
                            GuideDetailItemId.MEDICAL_RECORDS_TEST_RESULTS -> navController.navigate(Route.MedicalRecordsTestResultsDetail)
                            GuideDetailItemId.HOSPITAL_LOCATION_CHECKIN_GUIDE -> navController.navigate(Route.HospitalLocationCheckinDetail)
                            GuideDetailItemId.TOTAL_COST_COVERAGE_CHECK -> navController.navigate(Route.TotalCostCoverageCheckDetail)
                            GuideDetailItemId.PAYMENT_METHOD_AVAILABLE_CHECK -> navController.navigate(Route.PaymentMethodCheckDetail)
                            GuideDetailItemId.RECEIPT_INSURANCE_DOCUMENT_CHECK -> navController.navigate(Route.ReceiptInsuranceDocumentsDetail)
                            GuideDetailItemId.MEDICATION_SCHEDULE_CHECK -> navController.navigate(Route.MedicationScheduleDetail)
                            GuideDetailItemId.POST_TREATMENT_PRECAUTIONS_CHECK -> navController.navigate(Route.PostTreatmentPrecautionsDetail)
                            GuideDetailItemId.ENGLISH_DOCUMENTS_RESULTS_CHECK -> navController.navigate(Route.EnglishDocumentsResultsDetail)
                            GuideDetailItemId.AIRPORT_DEPARTURE_PREPARATION_CHECK -> navController.navigate(Route.AirportDeparturePreparationDetail)
                        }
                    }
                )
            }
        }
        composable<Route.VisaEntryCheckDetail> {
            VisaEntryCheckDetailScreen(onBack = navController::popBackStack)
        }
        composable<Route.InsuranceDocumentsDetail> {
            InsuranceDocumentsDetailScreen(onBack = navController::popBackStack)
        }
        composable<Route.HospitalInquiryDetail> {
            HospitalInquiryDetailScreen(onBack = navController::popBackStack)
        }
        composable<Route.PreInquiryInformationDetail> {
            PreInquiryInformationDetailScreen(onBack = navController::popBackStack)
        }
        composable<Route.PassportReservationInfoDetail> {
            PassportReservationInfoDetailScreen(onBack = navController::popBackStack)
        }
        composable<Route.MedicalRecordsTestResultsDetail> {
            MedicalRecordsTestResultsDetailScreen(onBack = navController::popBackStack)
        }
        composable<Route.HospitalLocationCheckinDetail> {
            HospitalLocationCheckinDetailScreen(
                onBack = navController::popBackStack,
                onNavigateToMap = { navController.navigate(Route.MapView(hospitalId = null)) }
            )
        }
        composable<Route.TotalCostCoverageCheckDetail> {
            TotalCostCoverageCheckDetailScreen(onBack = navController::popBackStack)
        }
        composable<Route.PaymentMethodCheckDetail> {
            PaymentMethodCheckDetailScreen(onBack = navController::popBackStack)
        }
        composable<Route.ReceiptInsuranceDocumentsDetail> {
            ReceiptInsuranceDocumentsDetailScreen(onBack = navController::popBackStack)
        }
        composable<Route.MedicationScheduleDetail> {
            MedicationScheduleDetailScreen(onBack = navController::popBackStack)
        }
        composable<Route.PostTreatmentPrecautionsDetail> {
            PostTreatmentPrecautionsDetailScreen(onBack = navController::popBackStack)
        }
        composable<Route.EnglishDocumentsResultsDetail> {
            EnglishDocumentsResultsDetailScreen(onBack = navController::popBackStack)
        }
        composable<Route.AirportDeparturePreparationDetail> {
            AirportDeparturePreparationDetailScreen(onBack = navController::popBackStack)
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
        composable<Route.DocumentScan> {
            DocumentScanScreen(onMenuClick = { navController.navigate(Route.Settings) })
        }
        composable<Route.SelfDiagnosis> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.SelfDiagnosis>()
            val context = LocalContext.current
            val reservationInquiryTitle = LocalAppStrings.current.guide.stepReservationInquiryTitle
            SelfDiagnosisScreen(
                fromOnboarding = route.fromOnboarding,
                onBack = navController::popBackStack,
                onFinishSetup = { navController.navigateToHomeAfterSetup() },
                onNavigateToCtaTarget = { target ->
                    when (target) {
                        DiagnosisCtaTarget.HOSPITAL_INQUIRY_CHECKLIST ->
                            navController.navigate(Route.HospitalInquiryDetail)
                        DiagnosisCtaTarget.HOSPITAL_BROWSE ->
                            // 다른 진입점과 동일하게 navigateToTab으로 통일한다 — HospitalSearchList로
                            // 가는 경로가 하나라도 순수 navigate()를 쓰면 바텀바 "홈" 탭이 못 빠져나오는
                            // 문제가 있다(Route.kt의 navigateToTab 함수 주석 참고).
                            navController.navigateToTab(Route.HospitalSearchList)
                        DiagnosisCtaTarget.INTERPRETATION_SUPPORT ->
                            navController.navigate(
                                Route.GuideStepDetail(phase = GuidePhase.RESERVATION_INQUIRY, title = reservationInquiryTitle)
                            )
                        DiagnosisCtaTarget.REGISTERED_AGENCY_CHECKLIST ->
                            context.launchIntentSafely(
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.medicalkorea.or.kr/en/registeredhospitals"))
                            )
                        DiagnosisCtaTarget.VISA_ENTRY_GUIDE ->
                            navController.navigate(Route.VisaEntryCheckDetail)
                        DiagnosisCtaTarget.TOTAL_COST_COVERAGE_CHECK ->
                            navController.navigate(Route.TotalCostCoverageCheckDetail)
                        DiagnosisCtaTarget.DEPARTURE_CHECKLIST ->
                            navController.navigate(Route.AirportDeparturePreparationDetail)
                        DiagnosisCtaTarget.WELLNESS_PLACES ->
                            // Home의 웰니스 진입점과 동일하게 MVP 기준 병원(해운대권 regNo=14)으로 연결한다.
                            navController.navigate(Route.Nearby(hospitalId = "14"))
                    }
                }
            )
        }
    }
}
