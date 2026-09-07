package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.DefaultSearchOrigin
import com.mediinbusan.app.core.common.PendingTourismCatalogItem
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.common.haversineDistanceMeters
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.core.i18n.appStringsFor
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.recent.RecentItemType
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
import com.mediinbusan.app.domain.tourism.placeCategoryCodes
import com.mediinbusan.app.domain.tourism.tourismCategoryForLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private var loadMoreJob: Job? = null

    init {
        viewModelScope.launch {
            favoriteRepository.observeFavorites().collect { favorites ->
                _uiState.update { it.copy(favoriteItemIds = favorites.mapTo(mutableSetOf()) { favorite -> favorite.itemId }) }
            }
        }
    }

    fun load(categoryName: String, initialCategoryCode: String? = null, initialSearchQuery: String? = null) {
        viewModelScope.launch {
            val preferences = userPreferencesRepository.userPreferences.first()
            val requestedCategory = runCatching { TourismCatalogCategory.valueOf(categoryName) }.getOrNull()
            if (requestedCategory == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = appStringsFor(preferences.languageCode).tourism.unsupportedDataError
                    )
                }
                return@launch
            }
            val category = if (requestedCategory.isLanguageVariant) {
                tourismCategoryForLanguage(preferences.languageCode)
            } else {
                requestedCategory
            }
            // 웰니스 필터 원형 버튼(관광지/숙박/맛집)에서 넘어온 경우 — 아래 "관광지 기본 선택"
            // 로직(selectedCategoryCode == null일 때만 동작)보다 먼저 걸어서 그 기본값을 덮는다.
            if (initialCategoryCode != null) {
                _uiState.update { it.copy(selectedCategoryCode = initialCategoryCode) }
            }
            // 웰니스 서치바에서 검색 실행 후 넘어온 경우 — loadCatalog()의 Result.Success 마지막에
            // applyClientFilters()가 이 값을 읽어 그대로 필터링해준다.
            if (initialSearchQuery != null) {
                _uiState.update { it.copy(searchQuery = initialSearchQuery) }
            }
            // "부산 관광지"도 다른 구·군 지원 카테고리처럼 첫 진입부터 특정 지역(해운대구) 중심으로
            // 보여준다(wellness_tourism_recommendation_list.png 기준 — "전체"가 아니라 해운대구가
            // 기본 체크돼 있음). "전체"는 지역 드롭다운에서 사용자가 직접 선택했을 때만 적용된다.
            val district = when {
                category == TourismCatalogCategory.CROWDING -> null
                category.isLanguageVariant || category.supportsDistrict -> _uiState.value.selectedDistrict ?: BusanDistrict.HAEUNDAE
                else -> null
            }
            // "부산 관광지" 화면의 카테고리 필터 3종(관광지/숙박/맛집) 중 "관광지" 기본 선택은
            // loadCatalog()의 Result.Success에서 실제 데이터를 받은 뒤에 적용한다(아래 참고) —
            // 데이터가 오기 전에 미리 정해두면, 언어별 TourAPI 소스(EN/JA/ZH)가 해당 지역·카테고리에
            // 항목이 아예 없을 때 사용자가 아무 필터도 안 건드렸는데 "검색 결과 없음"으로 막힌다.
            loadCatalog(category, district)
        }
    }

    fun selectDistrict(district: BusanDistrict?) {
        val category = _uiState.value.category ?: return
        loadCatalog(category, district)
    }

    /** 그리드 카드의 하트 토글 — FavoriteRepository.toggleFavorite이 존재 여부로 추가/제거를 알아서 판단한다. */
    fun toggleFavorite(item: TourismCatalogItem) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(
                Favorite(
                    itemId = item.id,
                    itemType = FavoriteItemType.PLACE,
                    name = item.title,
                    imageUrl = item.imageUrl,
                    savedAt = System.currentTimeMillis(),
                    subtitle = item.subtitle.orEmpty(),
                    address = item.address.orEmpty(),
                    latitude = item.latitude,
                    longitude = item.longitude
                )
            )
        }
    }

    fun retry() {
        val category = _uiState.value.category ?: return
        loadCatalog(category, _uiState.value.selectedDistrict)
    }

    fun loadNextPage() {
        val state = _uiState.value
        val category = state.category ?: return
        if (!supportsInfiniteScroll(category) || state.isLoading || state.isLoadingMore || !state.hasNextPage) return
        loadCatalog(category, state.selectedDistrict, page = state.currentPage + 1, append = true)
    }

    fun selectItem(item: TourismCatalogItem) {
        val category = _uiState.value.category ?: return
        pendingTourismCatalogItem.set(category, item, _uiState.value.selectedDistrict)
        viewModelScope.launch {
            interactionRepository.recordItemSelection(category, item)
        }
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

    private fun loadCatalog(
        category: TourismCatalogCategory,
        district: BusanDistrict?,
        page: Int = 1,
        append: Boolean = false
    ) {
        if (append) {
            loadMoreJob?.cancel()
        } else {
            loadJob?.cancel()
            loadMoreJob?.cancel()
        }
        val job = viewModelScope.launch {
            if (!append) interactionRepository.recordCategoryView(category)
            repository.getCatalog(category, district, page, PAGE_SIZE).collect { result ->
                when (result) {
                    Result.Loading -> _uiState.update { state ->
                        if (append) {
                            state.copy(isLoadingMore = true)
                        } else {
                            state.copy(
                                category = category,
                                selectedDistrict = district,
                                isLoading = true,
                                isLoadingMore = false,
                                currentPage = 0,
                                hasNextPage = true,
                                errorMessage = null,
                                // append(무한 스크롤 페이지 추가)가 아니라 진짜 새 조회일 때만 올린다 —
                                // 그리드 카드 리빌 애니메이션(rememberRevealedCount)의 key로 써서,
                                // 스크롤로 다음 페이지가 붙을 때마다 이미 보이던 카드까지 처음부터
                                // 다시 스켈레톤→페이드인되며 버벅이던 문제를 없앤다.
                                loadGeneration = state.loadGeneration + 1
                            )
                        }
                    }
                    is Result.Success -> {
                        // append일 때 이번 페이지가 실제로 "새" 항목을 몇 개 가져왔는지 세어둔다 —
                        // TourAPI가 마지막 페이지 이후로도 이전 페이지와 같은 항목을 다시 내려주는
                        // 경우(끝에 도달했는데도 raw 개수만 PAGE_SIZE 이상인 경우), 이 값이 0이면
                        // 더 가져올 게 없다는 뜻이라 hasNextPage를 꺼서 무한 로딩 스피너를 막는다.
                        var newItemCount = result.data.items.size
                        val mergedCatalog = if (append) {
                            val current = _uiState.value.catalog
                            if (current == null) {
                                result.data
                            } else {
                                val existingIds = current.items.mapTo(mutableSetOf()) { it.id }
                                val newItems = result.data.items.distinctBy { it.id }.filterNot { it.id in existingIds }
                                newItemCount = newItems.size
                                current.copy(items = current.items + newItems)
                            }
                        } else {
                            // 첫 페이지(또는 지역 변경/재시도)도 append 경로와 동일하게 distinctBy가
                            // 필요하다 — 원본 API 응답에 중복 id가 섞여 오면 그리드의
                            // key = { item.id }가 그대로 크래시(IllegalArgumentException)로 이어진다.
                            result.data.copy(items = result.data.items.distinctBy { it.id })
                        }
                        // "부산 관광지"만 개인화 점수로 재정렬 — 점수>0인 상위 항목이 추천 섹션으로
                        // 상단에 뜨고(applyClientFilters), 나머지는 그 아래 일반 섹션에 남는다.
                        val (catalog, personalizedItemIds) = if (category.isLanguageVariant) {
                            val recommendation = recommendPlaces(mergedCatalog)
                            recommendation.catalog to recommendation.personalizedItemIds
                        } else {
                            mergedCatalog to emptySet()
                        }
                        // "관광지" 기본 선택(wellness_tourism_recommendation_list.png 기준)은 실제로
                        // 받아온 첫 페이지에 그 카테고리 항목이 있을 때만 건다 — 사용자가 아직 아무
                        // 필터도 안 건드린 최초 진입(append 아님)에서만 해당하고, 이미 뭔가 선택
                        // 중이면(다른 칩 선택·언어 전환 재진입) 덮어쓰지 않는다. PLACES_KO와
                        // PLACES_EN/JA/ZH는 TourAPI contenttypeid 체계 자체가 달라서(placeCategoryCodes
                        // 참고) "관광지"에 해당하는 실제 코드값도 카테고리별로 다르다.
                        if (!append && category.isLanguageVariant && _uiState.value.selectedCategoryCode == null) {
                            val defaultCategoryCode = category.placeCategoryCodes().spot
                            val hasDefaultCategoryItems = catalog.items.any { it.categoryCode == defaultCategoryCode }
                            if (hasDefaultCategoryItems) {
                                _uiState.update { it.copy(selectedCategoryCode = defaultCategoryCode) }
                            }
                        }
                        _uiState.update { state ->
                            state.copy(
                                category = category,
                                selectedDistrict = district,
                                catalog = catalog,
                                personalizedItemIds = personalizedItemIds,
                                isLoading = false,
                                isLoadingMore = false,
                                currentPage = page,
                                hasNextPage = supportsInfiniteScroll(category) &&
                                    result.data.items.size >= PAGE_SIZE &&
                                    newItemCount > 0,
                                errorMessage = null
                            )
                        }
                        applyClientFilters()
                    }
                    is Result.Error -> {
                        val fallbackMessage = appStringsFor(
                            userPreferencesRepository.userPreferences.first().languageCode
                        ).tourism.catalogLoadError
                        _uiState.update { state ->
                            if (append) {
                                state.copy(isLoadingMore = false, hasNextPage = false)
                            } else {
                                state.copy(
                                    category = category,
                                    selectedDistrict = district,
                                    isLoading = false,
                                    isLoadingMore = false,
                                    errorMessage = result.message ?: fallbackMessage
                                )
                            }
                        }
                    }
                }
            }
        }
        if (append) loadMoreJob = job else loadJob = job
    }

    private fun supportsInfiniteScroll(category: TourismCatalogCategory): Boolean =
        category == TourismCatalogCategory.ACCESSIBLE || category.isLanguageVariant

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
            .filter { it.itemType == RecentItemType.PLACE }
            .map { it.itemName }
        val recentHospital = recent
            .filter {
                it.itemType == RecentItemType.HOSPITAL &&
                    it.latitude != null && it.longitude != null
            }
            .maxByOrNull { it.viewedAt }
        val reference = recentHospital?.let {
            TourismReferenceLocation(requireNotNull(it.latitude), requireNotNull(it.longitude))
        }
        val now = System.currentTimeMillis()
        // RecommendTourismCatalogUseCase의 diversify()는 항목 수가 늘어날수록(무한 스크롤로 누적)
        // 비용이 급격히 커지는 연산이라, viewModelScope 기본 디스패처(Main.immediate)에서 그대로
        // 돌리면 Main Thread를 오래 막아 ANR(Input dispatching timed out)로 이어진다 — 계산만
        // Dispatchers.Default로 옮긴다. 알고리즘·결과는 그대로다.
        withContext(Dispatchers.Default) {
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
    }

    // 매번 catalog.items(서버 원본 순서, 부산 관광지는 개인화 점수순)에서 다시 필터링해야 한다 —
    // 이미 필터링된 결과를 또 필터링하면 검색어를 지우거나 필터를 해제했을 때 원본으로 돌아가지 못한다.
    // catalog가 무한 스크롤로 커질수록 filter/sortedBy 비용도 커지므로, recommendPlaces와 같은
    // 이유로 실제 계산은 Dispatchers.Default에서 하고 결과만 state에 반영한다. 검색창 타이핑처럼
    // 짧은 시간에 여러 번 불릴 수 있어 filterJob으로 이전 계산은 취소하고 마지막 요청만 반영한다.
    private var filterJob: Job? = null

    private fun applyClientFilters() {
        val state = _uiState.value
        val catalog: TourismCatalog = state.catalog ?: return
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            val query = state.searchQuery.trim()
            val isLanguageVariant = state.category?.isLanguageVariant == true

            val (recommended, visible) = withContext(Dispatchers.Default) {
                val filtered = catalog.items
                    .filter { item -> query.isBlank() || item.title.contains(query, ignoreCase = true) }
                    .filter { item -> state.selectedCategoryCode == null || item.categoryCode == state.selectedCategoryCode }

                // "부산 관광지"는 정렬 선택지가 없다 — 추천 섹션은 개인화 점수순, 나머지는
                // catalog.items의 원본(서버) 순서를 그대로 따른다.
                if (isLanguageVariant) {
                    val recommended = filtered.filter { it.id in state.personalizedItemIds }
                    val rest = filtered.filterNot { it.id in state.personalizedItemIds }
                    recommended to rest
                } else {
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
                    emptyList<TourismCatalogItem>() to sorted
                }
            }

            _uiState.update { it.copy(recommendedItems = recommended, visibleItems = visible) }
        }
    }

    private companion object {
        // 백엔드가 "부산 관광지" 카테고리를 TourAPI에 실시간으로 프록시하고(캐싱 없음), 무한
        // 스크롤 페이지 하나당 왕복 지연이 그대로 체감된다 — 페이지 크기를 키워 왕복 횟수 자체를
        // 줄인다(백엔드 상한 50 이내). 근본 해결(백엔드 캐시/사전 적재)은 별도로 논의 필요.
        const val PAGE_SIZE = 40
    }
}
