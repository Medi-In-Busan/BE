package com.mediinbusan.app.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.hospital.HospitalRepository
import com.mediinbusan.app.data.place.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * F-010,F-013: 병원 및 주변 장소의 공개 좌표만 지도에 표시한다.
 * 사용자 현재 위치는 조회하거나 외부로 전송하지 않는다(구조적 제약) — "내 위치로 이동" 류의
 * 버튼이 있더라도 고정된 부산 기본 좌표로만 이동한다(core/ui/KakaoMapView.kt의 BusanDefaultCenter 참고).
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val hospitalRepository: HospitalRepository,
    private val placeRepository: PlaceRepository,
    private val favoriteRepository: FavoriteRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    init {
        viewModelScope.launch {
            favoriteRepository.observeFavorites().collect { favorites ->
                val hospitalIds = favorites
                    .filter { it.itemType == FavoriteItemType.HOSPITAL }
                    .map { it.itemId }
                    .toSet()
                _uiState.update { it.copy(favoriteHospitalIds = hospitalIds) }
            }
        }
    }

    fun load(hospitalId: String?) {
        if (hospitalId != null) loadHospitalFocused(hospitalId) else loadAllHospitals()
    }

    private fun loadHospitalFocused(hospitalId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode

            val hospitalResult = hospitalRepository.getHospitalDetail(hospitalId, languageCode)
                .first { it !is Result.Loading }
            val placesResult = placeRepository.getNearbyPlaces(hospitalId)
                .first { it !is Result.Loading }

            val hospital = (hospitalResult as? Result.Success)?.data
            val places = (placesResult as? Result.Success)?.data.orEmpty()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    focusedHospital = hospital,
                    nearbyPlaces = places,
                    selectedMarkerId = hospital?.id,
                    errorMessage = if (hospital == null) "병원 위치 정보를 불러올 수 없습니다." else null
                )
            }
        }
    }

    private fun loadAllHospitals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode

            val hospitalsResult = hospitalRepository.getHospitals(null, languageCode)
                .first { it !is Result.Loading }
            val hospitals = (hospitalsResult as? Result.Success)?.data.orEmpty()

            // PlaceRepositoryImpl은 지금 hospitalId와 무관하게 같은 샘플 목록을 반환하는 mock이라,
            // 전체 브라우징 모드에서는 대표로 한 번만 조회해 관광/음식 카테고리를 채운다.
            val placesResult = hospitals.firstOrNull()?.let { hospital ->
                placeRepository.getNearbyPlaces(hospital.id).first { it !is Result.Loading }
            }
            val places = (placesResult as? Result.Success)?.data.orEmpty()

            _uiState.update {
                it.copy(isLoading = false, allHospitals = hospitals, allPlaces = places, errorMessage = null)
            }
        }
    }

    fun onCategorySelected(category: MapCategory) {
        _uiState.update { it.copy(selectedCategory = category, selectedMarkerId = null) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onMarkerSelected(hospitalId: String?) {
        _uiState.update { it.copy(selectedMarkerId = hospitalId) }
    }

    fun onToggleFavorite(hospitalId: String) {
        val hospital: Hospital = _uiState.value.allHospitals.firstOrNull { it.id == hospitalId }
            ?: _uiState.value.focusedHospital?.takeIf { it.id == hospitalId }
            ?: return
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(
                Favorite(
                    itemId = hospital.id,
                    itemType = FavoriteItemType.HOSPITAL,
                    name = hospital.name,
                    imageUrl = hospital.imageUrl,
                    savedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
