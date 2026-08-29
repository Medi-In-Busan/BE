package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.PendingTourismCatalogItem
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.recent.RecentRepository
import com.mediinbusan.app.data.tourism.TourismCatalogRepository
import com.mediinbusan.app.data.tourism.TourismInteractionRepository
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.RecommendTourismCatalogUseCase
import com.mediinbusan.app.domain.tourism.TourismCatalog
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import com.mediinbusan.app.domain.tourism.TourismRecommendationContext
import com.mediinbusan.app.domain.tourism.TourismReferenceLocation
import com.mediinbusan.app.domain.tourism.inferTourismRecoveryStage
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

@HiltViewModel
class TourismCatalogViewModel @Inject constructor(
    private val repository: TourismCatalogRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val interactionRepository: TourismInteractionRepository,
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository,
    private val recommendTourismCatalog: RecommendTourismCatalogUseCase,
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
            val district = when {
                category == TourismCatalogCategory.CROWDING -> null
                category.isPersonalizedPlaceCategory() -> null
                category.supportsDistrict -> _uiState.value.selectedDistrict ?: BusanDistrict.HAEUNDAE
                else -> null
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
        viewModelScope.launch {
            interactionRepository.recordItemSelection(category, item)
        }
    }

    private fun loadCatalog(category: TourismCatalogCategory, district: BusanDistrict?) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            interactionRepository.recordCategoryView(category)
            repository.getCatalog(category, district).collect { result ->
                when (result) {
                    Result.Loading -> _uiState.update { state ->
                        state.copy(category = category, selectedDistrict = district, isLoading = true, errorMessage = null)
                    }
                    is Result.Success -> {
                        val personalizedCatalog = personalizeCatalog(category, result.data)
                        _uiState.update { state ->
                            state.copy(
                                category = category,
                                selectedDistrict = district,
                                catalog = personalizedCatalog,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
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

    private suspend fun personalizeCatalog(
        category: TourismCatalogCategory,
        catalog: TourismCatalog
    ): TourismCatalog {
        if (!category.isPersonalizedPlaceCategory()) return catalog

        val preferences = userPreferencesRepository.userPreferences.first()
        val profile = interactionRepository.profile.first()
        val favorites = favoriteRepository.observeFavorites().first()
        val recent = recentRepository.observeRecentlyViewed().first()
        val recentHospital = recent
            .filter {
                it.itemType == FavoriteItemType.HOSPITAL &&
                    it.latitude != null && it.longitude != null
            }
            .maxByOrNull { it.viewedAt }
        val now = System.currentTimeMillis()
        return recommendTourismCatalog(
            catalog = catalog,
            profile = profile,
            favoritePlaceNames = favorites
                .filter { it.itemType == FavoriteItemType.PLACE }
                .map { it.name },
            recentPlaceNames = recent
                .filter { it.itemType == FavoriteItemType.PLACE }
                .map { it.itemName },
            context = TourismRecommendationContext(
                medicalPurpose = preferences.medicalPurpose,
                referenceLocation = recentHospital?.let {
                    TourismReferenceLocation(
                        latitude = requireNotNull(it.latitude),
                        longitude = requireNotNull(it.longitude)
                    )
                },
                recoveryStage = inferTourismRecoveryStage(
                    medicalPurpose = preferences.medicalPurpose,
                    lastHospitalViewedAt = recentHospital?.viewedAt,
                    nowEpochMillis = now
                ),
                nowEpochMillis = now
            )
        ).catalog
    }

    private fun TourismCatalogCategory.isPersonalizedPlaceCategory(): Boolean =
        isLanguageVariant || this == TourismCatalogCategory.ACCESSIBLE
}
