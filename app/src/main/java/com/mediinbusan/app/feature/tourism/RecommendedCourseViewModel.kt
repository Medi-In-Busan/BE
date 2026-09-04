package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.core.i18n.appStringsFor
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.recent.RecentItemType
import com.mediinbusan.app.data.recent.RecentRepository
import com.mediinbusan.app.data.route.DrivingRoute
import com.mediinbusan.app.data.route.DrivingRoutePoint
import com.mediinbusan.app.data.route.DrivingRouteRepository
import com.mediinbusan.app.data.route.TravelMode
import com.mediinbusan.app.data.tourism.TourismCatalogRepository
import com.mediinbusan.app.data.tourism.TourismInteractionRepository
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.BuildRecommendedTourismCourseUseCase
import com.mediinbusan.app.domain.tourism.RecommendTourismCatalogUseCase
import com.mediinbusan.app.domain.tourism.RecommendedTourismCourse
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismRecommendationContext
import com.mediinbusan.app.domain.tourism.TourismReferenceLocation
import com.mediinbusan.app.domain.tourism.inferTourismRecoveryStage
import com.mediinbusan.app.domain.tourism.isLanguageVariant
import com.mediinbusan.app.domain.tourism.tourismCategoryForLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendedCourseViewModel @Inject constructor(
    private val catalogRepository: TourismCatalogRepository,
    private val interactionRepository: TourismInteractionRepository,
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val recommendTourismCatalog: RecommendTourismCatalogUseCase,
    private val buildCourse: BuildRecommendedTourismCourseUseCase,
    private val drivingRouteRepository: DrivingRouteRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecommendedCourseUiState())
    val uiState: StateFlow<RecommendedCourseUiState> = _uiState
    private var loadJob: Job? = null
    private var recommendedCourse: RecommendedTourismCourse? = null

    fun load(categoryName: String, districtName: String?) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val preferences = userPreferencesRepository.userPreferences.first()
            val requestedCategory = runCatching { TourismCatalogCategory.valueOf(categoryName) }.getOrNull()
            if (requestedCategory == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = appStringsFor(preferences.languageCode).tourism.unsupportedDataError
                    )
                }
                return@launch
            }

            val language = SupportedLanguage.entries.find { it.code == preferences.languageCode }
                ?: SupportedLanguage.DEFAULT
            val category = if (requestedCategory.isLanguageVariant) {
                tourismCategoryForLanguage(language.code)
            } else {
                requestedCategory
            }
            val profile = interactionRepository.profile.first()
            val district = if (category.supportsDistrict) {
                districtName?.let { runCatching { BusanDistrict.valueOf(it) }.getOrNull() }
                    ?: profile.preferredDistrict
                    ?: BusanDistrict.HAEUNDAE
            } else {
                null
            }
            val result = catalogRepository.getCatalog(category, district).first { it !is Result.Loading }
            if (result !is Result.Success) {
                _uiState.update {
                    it.copy(
                        language = language,
                        category = category,
                        district = district,
                        isLoading = false,
                        errorMessage = (result as? Result.Error)?.message
                            ?: appStringsFor(language).tourism.catalogLoadError
                    )
                }
                return@launch
            }

            val favorites = favoriteRepository.observeFavorites().first()
                .filter { it.itemType == FavoriteItemType.PLACE }
                .map { it.name }
            val recent = recentRepository.observeRecentlyViewed().first()
            val recentPlaceNames = recent
                .filter { it.itemType == RecentItemType.PLACE }
                .map { it.itemName }
            val recentHospital = recent.firstOrNull {
                it.itemType == RecentItemType.HOSPITAL && it.latitude != null && it.longitude != null
            }
            val reference = recentHospital?.let {
                TourismReferenceLocation(requireNotNull(it.latitude), requireNotNull(it.longitude))
            }
            val now = System.currentTimeMillis()
            val recommendation = recommendTourismCatalog(
                catalog = result.data,
                profile = profile,
                favoritePlaceNames = favorites,
                recentPlaceNames = recentPlaceNames,
                context = TourismRecommendationContext(
                    medicalPurpose = preferences.medicalPurpose,
                    referenceLocation = reference,
                    recoveryStage = inferTourismRecoveryStage(
                        preferences.medicalPurpose,
                        recentHospital?.viewedAt,
                        now
                    ),
                    nowEpochMillis = now
                )
            )
            val course = buildCourse(recommendation.catalog.items, reference)
            if (course == null) {
                recommendedCourse = null
                _uiState.value = RecommendedCourseUiState(
                    language = language,
                    category = category,
                    district = district,
                    isLoading = false
                )
                return@launch
            }
            recommendedCourse = course
            val routeResult = getRoute(course, TravelMode.DRIVING)
            val route = (routeResult as? Result.Success)?.data?.takeIf { course.isValidRoute(it) }
            _uiState.value = RecommendedCourseUiState(
                language = language,
                category = category,
                district = district,
                course = course,
                route = route,
                selectedStopId = course.stops.first().item.id,
                travelMode = TravelMode.DRIVING,
                isLoading = false,
                errorMessage = if (route == null) {
                    (routeResult as? Result.Error)?.message ?: appStringsFor(language).nearby.routeLoadError
                } else {
                    null
                }
            )
        }
    }

    fun selectStop(itemId: String) {
        _uiState.update { it.copy(selectedStopId = itemId) }
        val stop = recommendedCourse?.stops?.firstOrNull { it.item.id == itemId }?.item ?: return
        val state = _uiState.value
        viewModelScope.launch {
            recentRepository.recordView(
                itemId = stop.id,
                itemName = stop.title,
                itemType = RecentItemType.TOURISM_ITEM,
                imageUrl = stop.imageUrl,
                subtitle = stop.subtitle ?: "",
                address = stop.address ?: "",
                latitude = stop.latitude,
                longitude = stop.longitude,
                tourismCategory = state.category?.name,
                tourismDistrict = state.district?.name
            )
        }
    }

    fun selectTravelMode(mode: TravelMode) {
        val course = recommendedCourse ?: return
        if (_uiState.value.travelMode == mode || _uiState.value.isRouteRefreshing) return
        viewModelScope.launch {
            val strings = appStringsFor(_uiState.value.language).nearby
            _uiState.update { it.copy(isRouteRefreshing = true, routeErrorMessage = null) }
            val result = getRoute(course, mode)
            val route = (result as? Result.Success)?.data?.takeIf { course.isValidRoute(it) }
            if (route == null) {
                _uiState.update {
                    it.copy(
                        isRouteRefreshing = false,
                        routeErrorMessage = (result as? Result.Error)?.message ?: strings.routeChangeError
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        route = route,
                        travelMode = mode,
                        isRouteRefreshing = false,
                        routeErrorMessage = null
                    )
                }
            }
        }
    }

    private suspend fun getRoute(
        course: RecommendedTourismCourse,
        mode: TravelMode
    ): Result<DrivingRoute> {
        val points = course.stops.map { stop ->
            DrivingRoutePoint(
                name = stop.item.title,
                latitude = requireNotNull(stop.item.latitude),
                longitude = requireNotNull(stop.item.longitude)
            )
        }
        return drivingRouteRepository.getRoute(
            origin = points.first(),
            stops = points.drop(1),
            mode = mode
        )
    }

    private fun RecommendedTourismCourse.isValidRoute(
        route: DrivingRoute
    ): Boolean = route.path.size >= 2 && route.sections.size == stops.size - 1
}
