package com.mediinbusan.app.feature.hospitaldetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.hospital.HospitalRepository
import com.mediinbusan.app.data.recent.RecentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** F-006 상세 정보, F-007 다국어, F-009 지원 언어 표시, F-015 즐겨찾기, F-016 최근 본 항목. */
@HiltViewModel
class HospitalDetailViewModel @Inject constructor(
    private val hospitalRepository: HospitalRepository,
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HospitalDetailUiState())
    val uiState: StateFlow<HospitalDetailUiState> = _uiState

    fun load(hospitalId: String) {
        viewModelScope.launch {
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            hospitalRepository.getHospitalDetail(hospitalId, languageCode).collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Result.Loading -> state.copy(isLoading = true, isError = false, errorMessage = null)
                        is Result.Success -> {
                            recordView(result.data)
                            state.copy(isLoading = false, isError = false, hospital = result.data, errorMessage = null)
                        }
                        // 폴백 문구는 여기서 언어를 고정하지 않고 화면이 LocalAppStrings로 매번 새로 읽는다.
                        is Result.Error -> state.copy(isLoading = false, isError = true, errorMessage = result.message)
                    }
                }
            }
        }
        viewModelScope.launch {
            favoriteRepository.observeIsFavorite(hospitalId).collect { isFavorite ->
                _uiState.update { it.copy(isFavorite = isFavorite) }
            }
        }
    }

    private fun recordView(hospital: Hospital) {
        viewModelScope.launch {
            recentRepository.recordView(
                itemId = hospital.id,
                itemName = hospital.name,
                itemType = FavoriteItemType.HOSPITAL,
                imageUrl = hospital.imageUrl,
                subtitle = hospital.specialties.joinToString(", "),
                address = hospital.address,
                latitude = hospital.latitude,
                longitude = hospital.longitude
            )
        }
    }

    fun onToggleFavorite() {
        val hospital = _uiState.value.hospital ?: return
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(
                Favorite(
                    itemId = hospital.id,
                    itemType = FavoriteItemType.HOSPITAL,
                    name = hospital.name,
                    imageUrl = hospital.imageUrl,
                    savedAt = System.currentTimeMillis(),
                    subtitle = hospital.specialties.joinToString(", "),
                    address = hospital.address,
                    latitude = hospital.latitude,
                    longitude = hospital.longitude
                )
            )
        }
    }
}
