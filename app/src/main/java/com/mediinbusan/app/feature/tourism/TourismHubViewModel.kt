package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.recent.RecentRepository
import com.mediinbusan.app.data.tourism.TourismInteractionRepository
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismInteractionProfile
import com.mediinbusan.app.domain.tourism.isLanguageVariant
import com.mediinbusan.app.domain.tourism.tourismCategoryForLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TourismHubViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    interactionRepository: TourismInteractionRepository,
    favoriteRepository: FavoriteRepository,
    recentRepository: RecentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TourismHubUiState())
    val uiState: StateFlow<TourismHubUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                userPreferencesRepository.userPreferences,
                interactionRepository.profile,
                favoriteRepository.observeFavorites(),
                recentRepository.observeRecentlyViewed()
            ) { preferences, profile, favorites, recent ->
                val language = SupportedLanguage.entries.find { it.code == preferences.languageCode }
                    ?: SupportedLanguage.DEFAULT
                val languageCategory = tourismCategoryForLanguage(language.code)
                val categories = TourismCatalogCategory.entries.filter { category ->
                    !category.isLanguageVariant || category == languageCategory
                }
                val hasPlaceHistory = favorites.any { it.itemType == FavoriteItemType.PLACE } ||
                    recent.any { it.itemType == FavoriteItemType.PLACE }
                TourismHubUiState(
                    language = language,
                    categories = categories,
                    recommendedCategories = categories
                        .sortedByDescending { recommendationScore(it, profile, languageCategory, hasPlaceHistory) }
                        .take(3)
                )
            }.collect { _uiState.value = it }
        }
    }

    private fun recommendationScore(
        category: TourismCatalogCategory,
        profile: TourismInteractionProfile,
        languageCategory: TourismCatalogCategory,
        hasPlaceHistory: Boolean
    ): Int {
        var score = (profile.categoryViews[category] ?: 0) * 10
        if (category == languageCategory) score += 8
        if (category == TourismCatalogCategory.ACCESSIBLE) score += 5
        if (category == TourismCatalogCategory.WALKING) score += 4
        if (hasPlaceHistory && category in setOf(languageCategory, TourismCatalogCategory.RELATED, TourismCatalogCategory.PHOTOS)) {
            score += 6
        }
        return score
    }
}
