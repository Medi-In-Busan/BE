package com.mediinbusan.app.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
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
 * 사용자 현재 위치는 조회하거나 외부로 전송하지 않는다(구조적 제약).
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val hospitalRepository: HospitalRepository,
    private val placeRepository: PlaceRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    fun load(hospitalId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode

            val hospitalResult = hospitalRepository.getHospitalDetail(hospitalId, languageCode)
                .first { it !is Result.Loading }
            val placesResult = placeRepository.getNearbyPlaces(hospitalId)
                .first { it !is Result.Loading }

            val hospital = when (hospitalResult) {
                is Result.Success -> hospitalResult.data
                else -> null
            }
            val places = when (placesResult) {
                is Result.Success -> placesResult.data
                else -> emptyList()
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    hospital = hospital,
                    nearbyPlaces = places,
                    errorMessage = if (hospital == null) "병원 위치 정보를 불러올 수 없습니다." else null
                )
            }
        }
    }
}
