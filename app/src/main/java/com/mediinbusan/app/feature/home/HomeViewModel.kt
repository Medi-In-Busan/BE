package com.mediinbusan.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.hospital.HospitalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
        loadFavoriteHospitals()
    }

    // "추천 의료기관" 섹션은 (일차적으로는) 서버 추천이 아니라 사용자가 즐겨찾기 체크한
    // 병원만 보여준다. favoriteRepository는 hospitalId/이름/이미지 스냅샷(Favorite)만 갖고
    // 있고 주소·전문분야 등 Hospital 전체 필드는 없어서, 전체 병원 목록과 조합해 그 중
    // 즐겨찾기된 것만 걸러낸다. combine이라 즐겨찾기 토글 시 재조회 없이 바로 갱신된다.
    private fun loadFavoriteHospitals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false, error = null) }
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            favoriteRepository.observeFavorites()
                .combine(hospitalRepository.getAllHospitals(languageCode)) { favorites, result -> favorites to result }
                .collect { (favorites, result) ->
                    val favoriteHospitalIds = favorites
                        .filter { it.itemType == FavoriteItemType.HOSPITAL }
                        .map { it.itemId }
                        .toSet()
                    when (result) {
                        is Result.Success -> _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = false,
                                favoriteHospitalIds = favoriteHospitalIds,
                                recommendedHospitals = result.data.filter { hospital -> hospital.id in favoriteHospitalIds },
                                error = null
                            )
                        }
                        is Result.Error -> _uiState.update {
                            // 서버 메시지가 없을 때 보여줄 폴백 문구는 여기서 언어를 고정해 넣지 않고,
                            // 화면(HomeScreen)이 LocalAppStrings로 매 리컴포지션마다 새로 읽게 한다.
                            it.copy(isLoading = false, isError = true, error = result.message, favoriteHospitalIds = favoriteHospitalIds)
                        }
                        Result.Loading -> Unit
                    }
                }
        }
    }

    fun onMedicalPurposeSelected(purpose: MedicalCategory) {
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
        loadFavoriteHospitals()
    }

    fun onLanguageSelected(languageCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguageCode(languageCode)
        }
    }
}
