package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.recent.RecentRepository
import com.mediinbusan.app.data.tourism.TourismCatalogRepository
import com.mediinbusan.app.data.tourism.TourismInteractionRepository
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.BuildRecommendedTourismCourseUseCase
import com.mediinbusan.app.domain.tourism.RecommendTourismCatalogUseCase
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
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
class RecommendedCourseViewModel @Inject constructor(
    private val catalogRepository: TourismCatalogRepository,
    private val interactionRepository: TourismInteractionRepository,
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val recommendTourismCatalog: RecommendTourismCatalogUseCase,
    private val buildCourse: BuildRecommendedTourismCourseUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecommendedCourseUiState())
    val uiState: StateFlow<RecommendedCourseUiState> = _uiState
    private var loadJob: Job? = null

    fun load(categoryName: String, districtName: String?) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val requestedCategory = runCatching { TourismCatalogCategory.valueOf(categoryName) }.getOrNull()
            if (requestedCategory == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "지원하지 않는 관광 데이터입니다.") }
                return@launch
            }

            val preferences = userPreferencesRepository.userPreferences.first()
            val language = SupportedLanguage.entries.find { it.code == preferences.languageCode }
                ?: SupportedLanguage.DEFAULT
            val category = if (requestedCategory.isLanguageVariant) {
                tourismCategoryForLanguage(language.code)
            } else {
                requestedCategory
            }
            val profile = interactionRepository.profile.first()
            val district = if (category.supportsDistrict) {
                districtName?.let { runCatching { BusanDistrict.valueOf(it) }.getOrNull() }
                    ?: profile.preferredDistrict
                    ?: BusanDistrict.HAEUNDAE
            } else {
                null
            }
            val result = catalogRepository.getCatalog(category, district).first { it !is Result.Loading }
            if (result !is Result.Success) {
                _uiState.update {
                    it.copy(
                        language = language,
                        category = category,
                        district = district,
                        isLoading = false,
                        errorMessage = (result as? Result.Error)?.message ?: "관광 데이터를 불러오지 못했습니다."
                    )
                }
                return@launch
            }

            val favorites = favoriteRepository.observeFavorites().first()
                .filter { it.itemType == FavoriteItemType.PLACE }
                .map { it.name }
            val recent = recentRepository.observeRecentlyViewed().first()
            val recentPlaceNames = recent
                .filter { it.itemType == FavoriteItemType.PLACE }
                .map { it.itemName }
            val recentHospital = recent.firstOrNull {
                it.itemType == FavoriteItemType.HOSPITAL && it.latitude != null && it.longitude != null
            }
            val reference = recentHospital?.let {
                TourismReferenceLocation(requireNotNull(it.latitude), requireNotNull(it.longitude))
            }
            val now = System.currentTimeMillis()
            val recommendation = recommendTourismCatalog(
                catalog = result.data,
                profile = profile,
                favoritePlaceNames = favorites,
                recentPlaceNames = recentPlaceNames,
                context = TourismRecommendationContext(
                    medicalPurpose = preferences.medicalPurpose,
                    referenceLocation = reference,
                    recoveryStage = inferTourismRecoveryStage(
                        preferences.medicalPurpose,
                        recentHospital?.viewedAt,
                        now
                    ),
                    nowEpochMillis = now
                )
            )
            val course = buildCourse(recommendation.catalog.items, reference)
            _uiState.value = RecommendedCourseUiState(
                language = language,
                category = category,
                district = district,
                course = course,
                selectedStopId = course?.stops?.firstOrNull()?.item?.id,
                isLoading = false
            )
        }
    }

    fun selectStop(itemId: String) {
        _uiState.update { it.copy(selectedStopId = itemId) }
    }
}
