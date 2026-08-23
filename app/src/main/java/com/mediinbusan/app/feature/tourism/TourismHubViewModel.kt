package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.recent.RecentRepository
import com.mediinbusan.app.data.tourism.TourismInteractionRepository
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismInteractionProfile
import com.mediinbusan.app.domain.tourism.TourismRecoveryStage
import com.mediinbusan.app.domain.tourism.inferTourismRecoveryStage
import com.mediinbusan.app.domain.tourism.isLanguageVariant
import com.mediinbusan.app.domain.tourism.tourismCategoryForLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.exp
import kotlin.math.ln

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
                val now = System.currentTimeMillis()
                val recoveryStage = inferTourismRecoveryStage(
                    medicalPurpose = preferences.medicalPurpose,
                    lastHospitalViewedAt = recent.firstOrNull {
                        it.itemType == FavoriteItemType.HOSPITAL
                    }?.viewedAt,
                    nowEpochMillis = now
                )
                TourismHubUiState(
                    language = language,
                    categories = categories,
                    recommendedCategories = categories
                        .sortedByDescending {
                            recommendationScore(
                                category = it,
                                profile = profile,
                                languageCategory = languageCategory,
                                hasPlaceHistory = hasPlaceHistory,
                                medicalPurpose = preferences.medicalPurpose,
                                recoveryStage = recoveryStage,
                                nowEpochMillis = now
                            )
                        }
                        .take(3)
                )
            }.collect { _uiState.value = it }
        }
    }

    private fun recommendationScore(
        category: TourismCatalogCategory,
        profile: TourismInteractionProfile,
        languageCategory: TourismCatalogCategory,
        hasPlaceHistory: Boolean,
        medicalPurpose: MedicalCategory?,
        recoveryStage: TourismRecoveryStage,
        nowEpochMillis: Long
    ): Double {
        val lastViewedAt = profile.categoryLastViewedAt[category]
        val viewDecay = lastViewedAt?.let {
            val age = (nowEpochMillis - it).coerceAtLeast(0L).toDouble()
            exp(-ln(2.0) * age / CATEGORY_HALF_LIFE_MILLIS)
        } ?: LEGACY_VIEW_WEIGHT
        val affinity = profile.categoryAffinityScores[category]
            ?: (profile.categoryViews[category] ?: 0) * LEGACY_VIEW_WEIGHT
        var score = affinity * 10.0 * viewDecay
        if (category == languageCategory) score += 8
        if (category == TourismCatalogCategory.ACCESSIBLE) score += 5
        if (category == TourismCatalogCategory.WALKING) score += 4
        if (hasPlaceHistory && category in setOf(languageCategory, TourismCatalogCategory.RELATED, TourismCatalogCategory.PHOTOS)) {
            score += 6
        }
        if (medicalPurpose == MedicalCategory.WELLNESS && category == TourismCatalogCategory.WALKING) score += 8
        if (medicalPurpose == MedicalCategory.REHABILITATION && category == TourismCatalogCategory.ACCESSIBLE) score += 10
        when (recoveryStage) {
            TourismRecoveryStage.STANDARD -> Unit
            TourismRecoveryStage.GENTLE -> {
                if (category == TourismCatalogCategory.ACCESSIBLE) score += 6
                if (category == TourismCatalogCategory.WALKING) score -= 2
            }
            TourismRecoveryStage.REST_FIRST -> {
                if (category == TourismCatalogCategory.ACCESSIBLE) score += 10
                if (category == TourismCatalogCategory.WALKING) score -= 10
            }
        }
        return score
    }

    private companion object {
        val CATEGORY_HALF_LIFE_MILLIS = TimeUnit.DAYS.toMillis(30).toDouble()
        const val LEGACY_VIEW_WEIGHT = 0.5
    }
}
