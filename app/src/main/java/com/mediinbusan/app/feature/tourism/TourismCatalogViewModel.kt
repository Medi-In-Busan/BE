package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.DefaultSearchOrigin
import com.mediinbusan.app.core.common.PendingTourismCatalogItem
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.common.haversineDistanceMeters
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

/**
 * 구·군 필터만 서버 재조회(getCatalog)를 다시 태운다 — 백엔드가 lDongSignguCd로 이미 필터링된
 * 목록을 내려주기 때문. 검색어/카테고리 칩/정렬은 이미 받아온 catalog.items를 클라이언트에서
 * 다시 조회 없이 처리한다(HospitalSearchListViewModel의 로컬 필터링 패턴과 동일).
 *
 * "부산 관광지"(언어별 PLACES_KO/EN/JA/ZH) 카테고리만 RecommendTourismCatalogUseCase로 개인화
 * 재정렬을 태운다(RecommendedCourseViewModel과 같은 신호 소스 — 즐겨찾기/최근 본 항목/의료목적/
 * 최근 병원 위치). 다른 카테고리는 이 재정렬 없이 기존 서버 순서를 그대로 쓴다.
 */
@HiltViewModel
class TourismCatalogViewModel @Inject constructor(
    private val repository: TourismCatalogRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val pendingTourismCatalogItem: PendingTourismCatalogItem,
    private val interactionRepository: TourismInteractionRepository,
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository,
    private val recommendTourismCatalog: RecommendTourismCatalogUseCase
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

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyClientFilters()
    }

    fun onSortSelected(sort: TourismSortOption) {
        _uiState.update { it.copy(selectedSort = sort) }
        applyClientFilters()
    }

    /** 같은 칩을 다시 누르면 전체 보기로 해제한다 — HospitalSearchList의 필터 칩과 달리 단일 선택. */
    fun onCategoryFilterSelected(categoryCode: String?) {
        _uiState.update { state ->
            state.copy(selectedCategoryCode = if (state.selectedCategoryCode == categoryCode) null else categoryCode)
        }
        applyClientFilters()
    }

    fun onResetFilters() {
        _uiState.update { it.copy(searchQuery = "", selectedCategoryCode = null) }
        applyClientFilters()
    }

    private fun loadCatalog(category: TourismCatalogCategory, district: BusanDistrict?) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            repository.getCatalog(category, district).collect { result ->
                when (result) {
                    Result.Loading -> _uiState.update { state ->
                        state.copy(category = category, selectedDistrict = district, isLoading = true, errorMessage = null)
                    }
                    is Result.Success -> {
                        // "부산 관광지"만 개인화 점수로 재정렬 — 점수>0인 상위 항목이 추천 섹션으로
                        // 상단에 뜨고(applyClientFilters), 나머지는 그 아래 일반 섹션에 남는다.
                        val (catalog, personalizedItemIds) = if (category.isLanguageVariant) {
                            val recommendation = recommendPlaces(result.data)
                            recommendation.catalog to recommendation.personalizedItemIds
                        } else {
                            result.data to emptySet()
                        }
                        _uiState.update { state ->
                            state.copy(
                                category = category,
                                selectedDistrict = district,
                                catalog = catalog,
                                personalizedItemIds = personalizedItemIds,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        applyClientFilters()
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

    // RecommendedCourseViewModel과 같은 신호 소스(즐겨찾기 장소명·최근 본 장소명·의료목적·최근 본
    // 병원 위치)로 개인화 점수를 매긴다. 신호가 하나도 없으면(신규 사용자 등) 전부 점수 0이라
    // personalizedItemIds가 비고, catalog.items는 서버 원본 순서 그대로 유지된다.
    private suspend fun recommendPlaces(catalog: TourismCatalog) = run {
        val preferences = userPreferencesRepository.userPreferences.first()
        val profile = interactionRepository.profile.first()
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
        recommendTourismCatalog(
            catalog = catalog,
            profile = profile,
            favoritePlaceNames = favorites,
            recentPlaceNames = recentPlaceNames,
            context = TourismRecommendationContext(
                medicalPurpose = preferences.medicalPurpose,
                referenceLocation = reference,
                recoveryStage = inferTourismRecoveryStage(preferences.medicalPurpose, recentHospital?.viewedAt, now),
                nowEpochMillis = now
            )
        )
    }

    // 매번 catalog.items(서버 원본 순서, 부산 관광지는 개인화 점수순)에서 다시 필터링해야 한다 —
    // 이미 필터링된 결과를 또 필터링하면 검색어를 지우거나 필터를 해제했을 때 원본으로 돌아가지 못한다.
    private fun applyClientFilters() {
        val state = _uiState.value
        val catalog: TourismCatalog = state.catalog ?: return
        val query = state.searchQuery.trim()

        val filtered = catalog.items
            .filter { item -> query.isBlank() || item.title.contains(query, ignoreCase = true) }
            .filter { item -> state.selectedCategoryCode == null || item.categoryCode == state.selectedCategoryCode }

        // "부산 관광지"는 정렬 선택지가 없다 — 추천 섹션은 개인화 점수순, 나머지는 catalog.items의
        // 원본(서버) 순서를 그대로 따른다.
        if (state.category?.isLanguageVariant == true) {
            val recommended = filtered.filter { it.id in state.personalizedItemIds }
            val rest = filtered.filterNot { it.id in state.personalizedItemIds }
            _uiState.update { it.copy(recommendedItems = recommended, visibleItems = rest) }
            return
        }

        val sorted = when (state.selectedSort) {
            TourismSortOption.NAME -> filtered.sortedBy { it.title }
            TourismSortOption.DISTANCE -> filtered.sortedBy { item ->
                val lat = item.latitude
                val lng = item.longitude
                if (lat == null || lng == null) {
                    Double.MAX_VALUE
                } else {
                    haversineDistanceMeters(DefaultSearchOrigin.LATITUDE, DefaultSearchOrigin.LONGITUDE, lat, lng)
                }
            }
        }

        _uiState.update { it.copy(recommendedItems = emptyList(), visibleItems = sorted) }
    }
}
