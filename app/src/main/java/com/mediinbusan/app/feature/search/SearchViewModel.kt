package com.mediinbusan.app.feature.search

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

/**
 * 검색 화면 UI 스켈레톤. 병원+장소 통합 검색, 필터 칩 실제 적용, 정렬 적용, 서버 페이지네이션은
 * 전부 다음 이슈로 넘긴 상태다. 지금은 병원 샘플 데이터에 대해 이름만 클라이언트 필터링한다.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val hospitalRepository: HospitalRepository,
    private val favoriteRepository: FavoriteRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { preferences ->
                _uiState.update { it.copy(selectedLanguage = preferences.languageCode) }
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
        loadResults()
    }

    private fun loadResults() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            when (val result = hospitalRepository.getHospitals(null, languageCode).first { it !is Result.Loading }) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, results = result.data, errorMessage = null)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message ?: "검색 결과를 불러오지 못했습니다.")
                }
                Result.Loading -> Unit
            }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    // TODO: 필터 칩 실제 적용은 카테고리 확정 후 다음 이슈에서 연결한다. 지금은 선택 상태만 토글.
    fun onFilterToggled(label: String) {
        _uiState.update { state ->
            state.copy(
                filters = state.filters.map { chip ->
                    if (chip.label == label) chip.copy(selected = !chip.selected) else chip
                }
            )
        }
    }

    // TODO: 정렬 기준 확정 후 실제 정렬 로직을 연결한다. 지금은 선택 상태만 보관.
    fun onSortSelected(sort: SearchSortOption) {
        _uiState.update { it.copy(selectedSort = sort) }
    }

    fun onResetSearchConditions() {
        _uiState.update {
            it.copy(query = "", filters = SearchFilterChip.DEFAULTS)
        }
    }

    // TODO: 페이지네이션 백엔드 연동 전까지는 항상 마지막 페이지로 취급하는 스텁이다.
    fun onLoadMore() {
        _uiState.update { it.copy(hasReachedEnd = true) }
    }

    fun onToggleFavorite(hospitalId: String) {
        val hospital = _uiState.value.results.firstOrNull { it.id == hospitalId } ?: return
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

    fun onLanguageSelected(languageCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguageCode(languageCode)
        }
    }

    fun onRetry() {
        loadResults()
    }
}
