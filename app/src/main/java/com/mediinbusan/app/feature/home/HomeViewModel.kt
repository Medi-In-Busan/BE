package com.mediinbusan.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.PendingHospitalSearchFilter
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.hospital.HospitalRepository
import com.mediinbusan.app.data.recent.RecentRepository
import com.mediinbusan.app.domain.recommendation.GetRecommendedHospitalsUseCase
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
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository,
    private val pendingHospitalSearchFilter: PendingHospitalSearchFilter,
    private val getRecommendedHospitalsUseCase: GetRecommendedHospitalsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { preferences ->
                _uiState.update { it.copy(languageCode = preferences.languageCode) }
            }
        }
        loadRecommendedHospitals()
    }

    // "이런 의료기관은 어떠세요?" 섹션 — GetRecommendedHospitalsUseCase 참고. 즐겨찾기·최근 본
    // 항목·전체 병원 목록 세 Flow를 combine해서, 즐겨찾기 토글이나 최근 본 항목 갱신이 생기면
    // 재조회 없이 바로 다시 점수를 매겨 갱신된다.
    private fun loadRecommendedHospitals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false, error = null) }
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            combine(
                favoriteRepository.observeFavorites(),
                hospitalRepository.getAllHospitals(languageCode),
                recentRepository.observeRecentlyViewed()
            ) { favorites, result, recentlyViewed -> Triple(favorites, result, recentlyViewed) }
                .collect { (favorites, result, recentlyViewed) ->
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
                                recommendedHospitals = getRecommendedHospitalsUseCase(
                                    allHospitals = result.data,
                                    favorites = favorites,
                                    recentlyViewed = recentlyViewed
                                ),
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

    // 카테고리 칩 탭 직후(같은 클릭 핸들러에서) 검색 화면으로 이동하기 직전에 호출된다.
    // PendingHospitalSearchFilter 주석 참고 — DataStore가 아니라 인메모리 싱글턴에 심어서,
    // Home으로 돌아와도 아무 흔적이 남지 않고 검색 화면 진입 시 정확히 한 번만 적용된다.
    fun onCategorySelected(purpose: MedicalCategory) {
        pendingHospitalSearchFilter.set(purpose)
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
