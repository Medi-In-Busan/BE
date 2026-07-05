package com.mediinbusan.app.feature.hospitaldetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
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
                        is Result.Loading -> state.copy(isLoading = true, errorMessage = null)
                        is Result.Success -> {
                            recordView(result.data.id, result.data.name)
                            state.copy(isLoading = false, hospital = result.data, errorMessage = null)
                        }
                        is Result.Error -> state.copy(isLoading = false, errorMessage = result.message ?: "오류가 발생했습니다.")
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

    private fun recordView(id: String, name: String) {
        viewModelScope.launch { recentRepository.recordView(id, name) }
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
                    savedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
