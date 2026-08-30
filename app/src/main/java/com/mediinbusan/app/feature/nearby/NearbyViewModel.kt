package com.mediinbusan.app.feature.nearby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.common.PendingTourismCatalogItem
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.core.i18n.appStringsFor
import com.mediinbusan.app.domain.course.GetRecommendedHospitalWellnessRouteUseCase
import com.mediinbusan.app.domain.nearby.GetNearbyPlacesSortedByDistanceUseCase
import com.mediinbusan.app.data.place.WellnessTourismRepository
import com.mediinbusan.app.data.tourism.TourismCatalogRepository
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.RankTourismHotPlacesUseCase
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import com.mediinbusan.app.domain.tourism.TourismHotPlace
import com.mediinbusan.app.domain.tourism.tourismCategoryForLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

/** F-011 병원 주변 관광·웰니스 추천. */
@HiltViewModel
class NearbyViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getNearbyPlacesSortedByDistance: GetNearbyPlacesSortedByDistanceUseCase,
    private val getRecommendedRoute: GetRecommendedHospitalWellnessRouteUseCase,
    private val wellnessTourismRepository: WellnessTourismRepository,
    private val tourismCatalogRepository: TourismCatalogRepository,
    private val rankHotPlaces: RankTourismHotPlacesUseCase,
    private val pendingTourismCatalogItem: PendingTourismCatalogItem
) : ViewModel() {

    private val _uiState = MutableStateFlow(NearbyUiState())
    val uiState: StateFlow<NearbyUiState> = _uiState
    private var placesJob: Job? = null
    private var routeJob: Job? = null
    private var walkingJob: Job? = null
    private var hotPlacesJob: Job? = null
    private var previewsJob: Job? = null

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { preferences ->
                _uiState.update { it.copy(selectedLanguage = preferences.languageCode) }
            }
        }
    }

    fun onLanguageSelected(languageCode: String) {
        viewModelScope.launch { userPreferencesRepository.setLanguageCode(languageCode) }
    }

    fun selectHotPlace(hotPlace: TourismHotPlace) {
        pendingTourismCatalogItem.setHotPlace(hotPlace)
    }

    fun selectTourismItem(category: TourismCatalogCategory, item: TourismCatalogItem) {
        pendingTourismCatalogItem.set(category, item)
    }

    fun load(hospitalId: String, languageCode: String? = null) {
        loadHotPlaces()
        val resolvedLanguage = languageCode ?: _uiState.value.selectedLanguage
        loadCatalogPreviews(resolvedLanguage)
        placesJob?.cancel()
        placesJob = viewModelScope.launch {
            getNearbyPlacesSortedByDistance(hospitalId, resolvedLanguage).collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Result.Loading -> state.copy(isLoading = true, errorMessage = null)
                        is Result.Success -> state.copy(isLoading = false, places = result.data, errorMessage = null)
                        is Result.Error -> state.copy(
                            isLoading = false,
                            errorMessage = result.message ?: appStringsFor(resolvedLanguage).nearby.genericErrorMessage
                        )
                    }
                }
            }
        }
        routeJob?.cancel()
        routeJob = viewModelScope.launch {
            when (val result = getRecommendedRoute.getRoutes(hospitalId)) {
                is Result.Success -> _uiState.update { it.copy(recommendedRoutes = result.data) }
                is Result.Error, Result.Loading -> Unit
            }
        }
        walkingJob?.cancel()
        walkingJob = viewModelScope.launch {
            wellnessTourismRepository.getWalkingCourses().collect { result ->
                if (result is Result.Success) {
                    _uiState.update { it.copy(walkingCourses = result.data) }
                }
            }
        }
    }

    private fun loadHotPlaces() {
        hotPlacesJob?.cancel()
        hotPlacesJob = viewModelScope.launch {
            _uiState.update { it.copy(isHotPlacesLoading = true, hotPlacesError = null) }
            val catalogResult = tourismCatalogRepository
                .getCatalog(TourismCatalogCategory.CROWDING, null)
                .first { it !is Result.Loading }
            val catalogs = (catalogResult as? Result.Success)
                ?.data
                ?.let(::splitCrowdingCatalogByDistrict)
                .orEmpty()
            val hotPlaces = rankHotPlaces(catalogs, HOT_PLACE_LIMIT)
            _uiState.update {
                it.copy(
                    hotPlaces = hotPlaces,
                    isHotPlacesLoading = false,
                    hotPlacesError = if (hotPlaces.isEmpty()) {
                        appStringsFor(_uiState.value.selectedLanguage).nearby.crowdingLoadError
                    } else null
                )
            }
        }
    }

    private fun splitCrowdingCatalogByDistrict(catalog: com.mediinbusan.app.domain.tourism.TourismCatalog) =
        catalog.items
            .groupBy(::districtForItem)
            .map { (district, items) -> district to catalog.copy(items = items) }

    private fun districtForItem(item: TourismCatalogItem): BusanDistrict {
        val districtText = listOfNotNull(
            item.details["signguNm"],
            item.details["signguName"],
            item.address
        ).joinToString(" ")
        return BusanDistrict.entries.firstOrNull { districtText.contains(it.label) }
            ?: BusanDistrict.HAEUNDAE
    }

    private fun loadCatalogPreviews(languageCode: String) {
        previewsJob?.cancel()
        previewsJob = viewModelScope.launch {
            val tourismCategory = tourismCategoryForLanguage(languageCode)
            val previews = supervisorScope {
                listOf(tourismCategory, TourismCatalogCategory.ACCESSIBLE).map { category ->
                    async {
                        val result = tourismCatalogRepository
                            .getCatalog(category, BusanDistrict.HAEUNDAE)
                            .first { it !is Result.Loading }
                        category to (result as? Result.Success)?.data?.items.orEmpty()
                    }
                }.awaitAll().toMap()
            }
            _uiState.update {
                it.copy(
                    tourismPreviews = previews[tourismCategory].toPreviewItems(),
                    accessiblePreviews = previews[TourismCatalogCategory.ACCESSIBLE].toPreviewItems()
                )
            }
        }
    }

    private fun List<TourismCatalogItem>?.toPreviewItems() =
        orEmpty()
            .distinctBy { it.title }
            .sortedByDescending { it.imageUrl != null }
            .take(PREVIEW_LIMIT)

    private companion object {
        const val HOT_PLACE_LIMIT = 5
        const val PREVIEW_LIMIT = 6
    }
}
