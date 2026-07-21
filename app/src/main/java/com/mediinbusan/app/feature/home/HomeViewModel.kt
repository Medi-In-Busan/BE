package com.mediinbusan.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.hospital.HospitalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val hospitalRepository: HospitalRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { preferences ->
                _uiState.update {
                    it.copy(selectedPurpose = preferences.medicalPurpose, languageCode = preferences.languageCode)
                }
            }
        }
        viewModelScope.launch {
            favoriteRepository.observeFavorites().collect { favorites ->
                val hospitalIds = favorites
                    .filter { it.itemType == FavoriteItemType.HOSPITAL }
                    .map { it.itemId }
                    .toSet()
                _uiState.update { it.copy(favoriteHospitalIds = hospitalIds) }
            }
        }
        loadRecommendedHospitals()
    }

    private fun loadRecommendedHospitals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            // 추천 의료기관 섹션은 선택된 의료 목적과 무관하게 전체 추천 목록을 보여준다.
            when (val result = hospitalRepository.getHospitals(null, languageCode).first { it !is Result.Loading }) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, recommendedHospitals = result.data, error = null)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message ?: "추천 의료기관을 불러오지 못했습니다.")
                }
                Result.Loading -> Unit
            }
        }
    }

    fun onMedicalPurposeSelected(purpose: String) {
        viewModelScope.launch {
            userPreferencesRepository.setMedicalPurpose(purpose)
        }
    }

    fun onFavoriteToggleClicked(hospitalId: String) {
        val hospital = _uiState.value.recommendedHospitals.firstOrNull { it.id == hospitalId } ?: return
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

    fun onRetryClicked() {
        loadRecommendedHospitals()
    }

    fun onLanguageSelected(languageCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguageCode(languageCode)
        }
    }
}
