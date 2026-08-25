package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.recent.RecentRepository
import com.mediinbusan.app.data.tourism.TourismCatalogRepository
import com.mediinbusan.app.data.tourism.TourismInteractionRepository
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.RankTourismHotPlacesUseCase
import com.mediinbusan.app.domain.tourism.TourismCatalog
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismInteractionProfile
import com.mediinbusan.app.domain.tourism.TourismRecoveryStage
import com.mediinbusan.app.domain.tourism.inferTourismRecoveryStage
import com.mediinbusan.app.domain.tourism.tourismHubCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.exp
import kotlin.math.ln

/**
 * 행동 기반 "맞춤 추천"(방문 기록·즐겨찾기·최근 본 항목 반영)은 feature/tourism-recommendation/84의
 * 몫이라 여기서는 다루지 않는다 — 현재 언어에 맞는 카테고리만 걸러 그룹별로 보여준다.
 */
@HiltViewModel
class TourismHubViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    interactionRepository: TourismInteractionRepository,
    favoriteRepository: FavoriteRepository,
    recentRepository: RecentRepository,
    private val catalogRepository: TourismCatalogRepository,
    private val rankHotPlaces: RankTourismHotPlacesUseCase
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
                val categories = tourismHubCategories(language.code)
                val languageCategory = categories.first()
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
                val ranked = categories.drop(1).sortedByDescending {
                    recommendationScore(
                        category = it,
                        profile = profile,
                        hasPlaceHistory = hasPlaceHistory,
                        medicalPurpose = preferences.medicalPurpose,
                        recoveryStage = recoveryStage,
                        nowEpochMillis = now
                    )
                }
                TourismHubUiState(
                    language = language,
                    featuredCategory = languageCategory,
                    recoveryCategories = ranked.filter {
                        it == TourismCatalogCategory.WALKING
                    },
                    planningCategories = ranked.filter {
                        it == TourismCatalogCategory.RELATED
                    }
                )
            }.collect { preferencesState ->
                _uiState.update { current ->
                    preferencesState.copy(
                        hotPlaces = current.hotPlaces,
                        accessiblePlaces = current.accessiblePlaces,
                        isHighlightsLoading = current.isHighlightsLoading,
                        highlightsError = current.highlightsError
                    )
                }
            }
        }
        loadHighlights()
    }

    fun retryHighlights() = loadHighlights()

    private var highlightsJob: Job? = null

    private fun loadHighlights() {
        if (highlightsJob?.isActive == true) return
        highlightsJob = viewModelScope.launch {
            _uiState.update { it.copy(isHighlightsLoading = true, highlightsError = null) }
            supervisorScope {
                val accessibleDeferred = async {
                    catalogRepository.awaitCatalog(TourismCatalogCategory.ACCESSIBLE, null)
                }
                val crowdingDeferred = BusanDistrict.entries.map { district ->
                    async {
                        catalogRepository.awaitCatalog(TourismCatalogCategory.CROWDING, district)
                            ?.let { district to it }
                    }
                }
                val accessible = accessibleDeferred.await()
                val crowding = crowdingDeferred.awaitAll().filterNotNull()
                val hotPlaces = rankHotPlaces(crowding)
                val accessiblePlaces = accessible?.items.orEmpty().take(HIGHLIGHT_LIMIT)
                val hasNoData = hotPlaces.isEmpty() && accessiblePlaces.isEmpty()
                _uiState.update {
                    it.copy(
                        hotPlaces = hotPlaces,
                        accessiblePlaces = accessiblePlaces,
                        isHighlightsLoading = false,
                        highlightsError = if (hasNoData) "관광 추천 정보를 불러오지 못했습니다." else null
                    )
                }
            }
        }
    }

    private suspend fun TourismCatalogRepository.awaitCatalog(
        category: TourismCatalogCategory,
        district: BusanDistrict?
    ): TourismCatalog? = when (val result = getCatalog(category, district).first { it !is Result.Loading }) {
        is Result.Success -> result.data
        is Result.Error -> null
        Result.Loading -> null
    }

    private fun recommendationScore(
        category: TourismCatalogCategory,
        profile: TourismInteractionProfile,
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
        if (category == TourismCatalogCategory.ACCESSIBLE) score += 5
        if (category == TourismCatalogCategory.WALKING) score += 4
        if (hasPlaceHistory && category == TourismCatalogCategory.RELATED) {
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
        const val HIGHLIGHT_LIMIT = 8
    }
}
