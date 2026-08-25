package com.mediinbusan.app.feature.nearby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.domain.course.GetRecommendedHospitalWellnessRouteUseCase
import com.mediinbusan.app.domain.nearby.GetNearbyPlacesSortedByDistanceUseCase
import com.mediinbusan.app.data.place.WellnessTourismRepository
import com.mediinbusan.app.data.tourism.TourismCatalogRepository
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.RankTourismHotPlacesUseCase
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

/** F-011 병원 주변 관광·웰니스 추천. */
@HiltViewModel
class NearbyViewModel @Inject constructor(
    private val getNearbyPlacesSortedByDistance: GetNearbyPlacesSortedByDistanceUseCase,
    private val getRecommendedRoute: GetRecommendedHospitalWellnessRouteUseCase,
    private val wellnessTourismRepository: WellnessTourismRepository,
    private val tourismCatalogRepository: TourismCatalogRepository,
    private val rankHotPlaces: RankTourismHotPlacesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NearbyUiState())
    val uiState: StateFlow<NearbyUiState> = _uiState

    fun load(hospitalId: String) {
        loadHotPlaces()
        viewModelScope.launch {
            getNearbyPlacesSortedByDistance(hospitalId).collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Result.Loading -> state.copy(isLoading = true, errorMessage = null)
                        is Result.Success -> state.copy(isLoading = false, places = result.data, errorMessage = null)
                        is Result.Error -> state.copy(isLoading = false, errorMessage = result.message ?: "오류가 발생했습니다.")
                    }
                }
            }
        }
        viewModelScope.launch {
            when (val result = getRecommendedRoute.getRoutes(hospitalId)) {
                is Result.Success -> _uiState.update { it.copy(recommendedRoutes = result.data) }
                is Result.Error, Result.Loading -> Unit
            }
        }
        viewModelScope.launch {
            wellnessTourismRepository.getWalkingCourses().collect { result ->
                if (result is Result.Success) {
                    _uiState.update { it.copy(walkingCourses = result.data) }
                }
            }
        }
    }

    private fun loadHotPlaces() {
        viewModelScope.launch {
            _uiState.update { it.copy(isHotPlacesLoading = true, hotPlacesError = null) }
            val catalogs = supervisorScope {
                BusanDistrict.entries.map { district ->
                    async {
                        when (
                            val result = tourismCatalogRepository
                                .getCatalog(TourismCatalogCategory.CROWDING, district)
                                .first { it !is Result.Loading }
                        ) {
                            is Result.Success -> district to result.data
                            is Result.Error, Result.Loading -> null
                        }
                    }
                }.awaitAll().filterNotNull()
            }
            val hotPlaces = rankHotPlaces(catalogs, HOT_PLACE_LIMIT)
            _uiState.update {
                it.copy(
                    hotPlaces = hotPlaces,
                    isHotPlacesLoading = false,
                    hotPlacesError = if (hotPlaces.isEmpty()) "예상 혼잡 정보를 불러오지 못했습니다." else null
                )
            }
        }
    }

    private companion object {
        const val HOT_PLACE_LIMIT = 5
    }
}
