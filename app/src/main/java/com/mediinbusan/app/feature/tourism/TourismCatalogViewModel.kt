package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.PendingTourismCatalogItem
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.tourism.TourismCatalogRepository
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import com.mediinbusan.app.domain.tourism.isLanguageVariant
import com.mediinbusan.app.domain.tourism.tourismCategoryForLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * "맞춤 추천" 재정렬(RecommendTourismCatalogUseCase)과 방문 기록 저장(TourismInteractionRepository)은
 * feature/tourism-recommendation/84의 몫이라 이 ViewModel엔 없다 — 카테고리 조회·구·군 필터만 다룬다.
 */
@HiltViewModel
class TourismCatalogViewModel @Inject constructor(
    private val repository: TourismCatalogRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val pendingTourismCatalogItem: PendingTourismCatalogItem
) : ViewModel() {
    private val _uiState = MutableStateFlow(TourismCatalogUiState())
    val uiState: StateFlow<TourismCatalogUiState> = _uiState
    private var loadJob: Job? = null

    fun load(categoryName: String) {
        viewModelScope.launch {
            val requestedCategory = runCatching { TourismCatalogCategory.valueOf(categoryName) }.getOrNull()
            if (requestedCategory == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "지원하지 않는 관광 데이터입니다.") }
                return@launch
            }
            val preferences = userPreferencesRepository.userPreferences.first()
            val category = if (requestedCategory.isLanguageVariant) {
                tourismCategoryForLanguage(preferences.languageCode)
            } else {
                requestedCategory
            }
            val district = if (category.supportsDistrict) {
                _uiState.value.selectedDistrict ?: BusanDistrict.HAEUNDAE
            } else {
                null
            }
            loadCatalog(category, district)
        }
    }

    fun selectDistrict(district: BusanDistrict) {
        val category = _uiState.value.category ?: return
        loadCatalog(category, district)
    }

    fun retry() {
        val category = _uiState.value.category ?: return
        loadCatalog(category, _uiState.value.selectedDistrict)
    }

    fun selectItem(item: TourismCatalogItem) {
        val category = _uiState.value.category ?: return
        pendingTourismCatalogItem.set(category, item)
    }

    private fun loadCatalog(category: TourismCatalogCategory, district: BusanDistrict?) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            repository.getCatalog(category, district).collect { result ->
                when (result) {
                    Result.Loading -> _uiState.update { state ->
                        state.copy(category = category, selectedDistrict = district, isLoading = true, errorMessage = null)
                    }
                    is Result.Success -> _uiState.update { state ->
                        state.copy(
                            category = category,
                            selectedDistrict = district,
                            catalog = result.data,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    is Result.Error -> _uiState.update { state ->
                        state.copy(
                            category = category,
                            selectedDistrict = district,
                            isLoading = false,
                            errorMessage = result.message ?: "관광 데이터를 불러오지 못했습니다."
                        )
                    }
                }
            }
        }
    }
}
