package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.PendingTourismCatalogItem
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.i18n.appStringsFor
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.recent.RecentItemType
import com.mediinbusan.app.data.recent.RecentRepository
import com.mediinbusan.app.data.tourism.TourismCatalogRepository
import com.mediinbusan.app.data.tourism.TourismInteractionRepository
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.RankTourismHotPlacesUseCase
import com.mediinbusan.app.domain.tourism.TourismCatalog
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismHotPlace
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.exp
import kotlin.math.ln

@HiltViewModel
class TourismHubViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    interactionRepository: TourismInteractionRepository,
    favoriteRepository: FavoriteRepository,
    recentRepository: RecentRepository,
    private val catalogRepository: TourismCatalogRepository,
    private val rankHotPlaces: RankTourismHotPlacesUseCase,
    private val pendingTourismCatalogItem: PendingTourismCatalogItem
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
                    recent.any { it.itemType == RecentItemType.PLACE }
                val now = System.currentTimeMillis()
                val recoveryStage = inferTourismRecoveryStage(
                    medicalPurpose = preferences.medicalPurpose,
                    lastHospitalViewedAt = recent.firstOrNull {
                        it.itemType == RecentItemType.HOSPITAL
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
                val languageChanged = _uiState.value.language != preferencesState.language
                _uiState.update { current ->
                    preferencesState.copy(
                        hotPlaces = current.hotPlaces,
                        accessiblePlaces = current.accessiblePlaces,
                        isHighlightsLoading = current.isHighlightsLoading,
                        highlightsError = current.highlightsError
                    )
                }
                if (languageChanged) loadHighlights(force = true)
            }
        }
        loadHighlights()
    }

    fun retryHighlights() = loadHighlights(force = true)

    fun selectHotPlace(hotPlace: TourismHotPlace) {
        pendingTourismCatalogItem.setHotPlace(hotPlace)
    }

    private var highlightsJob: Job? = null

    private fun loadHighlights(force: Boolean = false) {
        if (force) highlightsJob?.cancel()
        if (highlightsJob?.isActive == true) return
        highlightsJob = viewModelScope.launch {
            _uiState.update { it.copy(isHighlightsLoading = true, highlightsError = null) }
            supervisorScope {
                val accessibleDeferred = async {
                    catalogRepository.awaitCatalog(TourismCatalogCategory.ACCESSIBLE, null)
                }
                val crowdingDeferred = async {
                    catalogRepository.awaitCatalog(TourismCatalogCategory.CROWDING, null)
                }
                val accessible = accessibleDeferred.await()
                val crowding = crowdingDeferred.await()?.let(::splitCrowdingCatalogByDistrict).orEmpty()
                val hotPlaces = rankHotPlaces(crowding)
                val accessiblePlaces = accessible?.items.orEmpty().take(HIGHLIGHT_LIMIT)
                val hasNoData = hotPlaces.isEmpty() && accessiblePlaces.isEmpty()
                _uiState.update {
                    it.copy(
                        hotPlaces = hotPlaces,
                        accessiblePlaces = accessiblePlaces,
                        isHighlightsLoading = false,
                        highlightsError = if (hasNoData) {
                            appStringsFor(it.language).tourism.recommendationLoadError
                        } else null
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

    private fun splitCrowdingCatalogByDistrict(catalog: TourismCatalog): List<Pair<BusanDistrict, TourismCatalog>> =
        catalog.items.groupBy { item ->
            val districtText = listOfNotNull(
                item.details["signguNm"],
                item.details["signguName"],
                item.address
            ).joinToString(" ")
            BusanDistrict.entries.firstOrNull { districtText.contains(it.label) } ?: BusanDistrict.HAEUNDAE
        }.map { (district, items) -> district to catalog.copy(items = items) }

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
