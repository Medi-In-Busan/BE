package com.mediinbusan.app.feature.hospitalsearchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.DefaultSearchOrigin
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.PendingHospitalSearchEntry
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.common.haversineDistanceMeters
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.hospital.HospitalRepository
import com.mediinbusan.app.data.searchhistory.SearchHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * S-04 통합 화면(구 HospitalList F-004/F-005 + Search UI 스켈레톤) ViewModel.
 * query(키워드)와 filters(specialties 선택 상태)가 백엔드 GET /api/hospitals의
 * keyword OR 검색 + specialties IN 필터 그대로 넘어간다 — filters 선택 상태가 검색 조건의
 * 단일 소스라, medicalPurpose는 별도 변수로 안 들고 매번 filters에서 뽑아 쓴다.
 * 필터 칩은 누르는 즉시 재조회되지만, 키워드는 타이핑마다가 아니라 검색 버튼(돋보기 아이콘/키보드
 * 검색 액션)을 눌렀을 때만 onSearchSubmit()으로 재조회한다 — onQueryChanged는 텍스트 상태만 갱신.
 * "관광지" 칩은 백엔드에 대응 카테고리가 없어 선택해도 결과에 영향을 주지 않는다.
 * 정렬(sortedByOption)은 서버에서 받아온 results를 클라이언트에서 재배열할 뿐 재조회하지 않는다.
 * 서버 페이지네이션은 다음 이슈.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class HospitalSearchListViewModel @Inject constructor(
    private val hospitalRepository: HospitalRepository,
    private val favoriteRepository: FavoriteRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val pendingHospitalSearchEntry: PendingHospitalSearchEntry
) : ViewModel() {

    private val _uiState = MutableStateFlow(HospitalSearchListUiState())
    val uiState: StateFlow<HospitalSearchListUiState> = _uiState

    private var initialized = false

    // 서버가 준 원본 순서(관련도순) 그대로 보관한다. uiState.results는 정렬 옵션에 따라 재배열된
    // 값이라, RELEVANCE로 되돌아왔을 때 여기서 다시 꺼내 써야 원래 순서를 복구할 수 있다 — results를
    // results 자기 자신으로 재정렬하면 이미 섞인 순서 위에 또 정렬하는 꼴이라 원본이 영영 사라진다.
    private var lastServerResults: List<Hospital> = emptyList()

    // 자동완성 후보용 전체 병원 스냅샷. uiState.results는 검색/필터 결과로 계속 덮어써지므로
    // 자동완성은 이 별도 캐시를 대상으로 로컬 필터링한다. 화면 진입 시 1회만 채운다.
    private var allHospitalsCache: List<Hospital> = emptyList()

    // 자동완성 후보 계산 전용 입력 소스. 한글은 음절이 완성되기 전 조합 중간 단계
    // (예: "부산" 입력 중 "부"+미완성 자모)도 텍스트필드 값에 그대로 반영되는데, 그 상태로
    // 매번 즉시 필터링하면 매칭이 순간적으로 0건이 됐다가 복구되길 반복해 후보 목록이 깜빡인다.
    // 디바운스로 조합이 잠시 멈춘 뒤에만 필터링해서 이 깜빡임을 없앤다.
    private val queryInput = MutableStateFlow("")

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
        viewModelScope.launch {
            searchHistoryRepository.observeSearchHistory().collect { history ->
                _uiState.update { it.copy(recentSearches = history.map { item -> item.keyword }) }
            }
        }
        viewModelScope.launch {
            queryInput.debounce(AUTOCOMPLETE_DEBOUNCE_MS).collect { query -> updateAutocompleteSuggestions(query) }
        }
    }

    // Home에서 의료목적 칩/검색바로 진입할 때 각각 필터/자동 포커스를 반영한다. 둘 다 Route 인자가
    // 아니라 PendingHospitalSearchEntry를 거쳐 받는다 — 바텀바 "홈" 탭이 정상 동작하려면 이 화면
    // 진입이 다른 탭과 동일하게 navigateToTab(popUpTo+saveState+restoreState)을 써야 하는데
    // (navigateToTab 함수 주석 참고), 그 조합은 예전에 다른 args로 방문한 적이 있으면 restoreState가
    // 그 예전 상태(args 포함)를 그대로 되살려 이번에 새로 넘긴 값이 무시될 수 있다
    // (PendingHospitalSearchEntry 주석 참고). consume*()은 Nav 백스택과 무관한 순수 인메모리 값이라
    // 이 문제에서 자유롭다.
    // purpose가 있을 때는(=Home 칩으로 진입) initialized 여부와 무관하게 매번 필터를 다시 적용하고
    // 재조회한다. purpose가 없을 때(=바텀바 탭 재선택)는 사용자가 직전에 만들어둔 검색/필터 상태를
    // 그대로 두기 위해 최초 1회만 기본 목록을 로드한다. 자동완성 캐시(loadAutocompleteSource)는
    // 어느 경우든 비용이 커서 최초 1회만 채운다.
    fun initialize() {
        val pendingPurpose = pendingHospitalSearchEntry.consumePurpose()
        val shouldAutoFocusSearch = pendingHospitalSearchEntry.consumeFocusRequest()
        if (shouldAutoFocusSearch) {
            _uiState.update { it.copy(shouldAutoFocusSearch = true) }
        }
        if (pendingPurpose != null) {
            // 카테고리 칩 진입은 "이 카테고리 전체 결과"를 보여줘야 한다 — 예전에 남아있던 검색어까지
            // 같이 서버로 나가면 카테고리 필터 + 옛 키워드가 함께 걸려 결과가 의도치 않게 좁아진다.
            queryInput.value = ""
            _uiState.update { state ->
                state.copy(
                    query = "",
                    autocompleteSuggestions = emptyList(),
                    filters = state.filters.map { it.copy(selected = it.label == pendingPurpose.label) }
                )
            }
            loadResults()
        }
        if (initialized) return
        initialized = true
        if (pendingPurpose == null) {
            loadResults()
        }
        loadAutocompleteSource()
    }

    // 화면이 실제로 포커스+키보드를 띄운 직후 호출된다. shouldAutoFocusSearch를 여기서 바로 꺼야
    // 한다 — 이 값을 켠 채로 두면, 병원 상세로 넘어갔다 뒤로가기로 이 화면이 다시 조립될 때(로컬
    // Compose remember는 그때마다 초기화되지만 uiState는 같은 ViewModel에 남아있으므로) 매번 다시
    // "포커스 요청이 있다"고 착각해서 검색 입력 패널(최근 검색어)이 또 열리고 키보드도 다시 뜬다.
    fun onAutoFocusApplied() {
        _uiState.update { it.copy(shouldAutoFocusSearch = false) }
    }

    // 자동완성 후보용 전체 병원 스냅샷을 1회 채운다. getAllHospitals가 서버 페이지를 모두 순회해
    // 합쳐주므로 총 병원 수가 한 페이지 크기를 넘어도 누락되지 않는다. 실패해도 자동완성이
    // 그냥 비어있게 될 뿐, 화면의 검색/필터 기능(loadResults)에는 영향을 주지 않으므로
    // 에러를 별도로 표면화하지 않는다.
    private fun loadAutocompleteSource() {
        viewModelScope.launch {
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            val result = hospitalRepository.getAllHospitals(languageCode = languageCode).first { it !is Result.Loading }
            if (result is Result.Success) {
                allHospitalsCache = result.data
            }
        }
    }

    private fun loadResults() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false, errorMessage = null) }
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            val selectedSpecialties = _uiState.value.filters
                .filter { it.selected }
                .mapNotNull { chip -> MedicalCategory.entries.find { it.label == chip.label } }
            val keyword = _uiState.value.query
            when (
                val result = hospitalRepository.getHospitals(
                    keyword = keyword,
                    specialties = selectedSpecialties,
                    languageCode = languageCode
                ).first { it !is Result.Loading }
            ) {
                is Result.Success -> {
                    lastServerResults = result.data
                    _uiState.update {
                        it.copy(isLoading = false, isError = false, results = result.data.sortedByOption(it.selectedSort), errorMessage = null)
                    }
                }
                is Result.Error -> _uiState.update {
                    // 폴백 문구는 여기서 언어를 고정하지 않고 화면이 LocalAppStrings로 매번 새로 읽는다.
                    it.copy(isLoading = false, isError = true, errorMessage = result.message)
                }
                Result.Loading -> Unit
            }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        queryInput.value = query
    }

    private fun updateAutocompleteSuggestions(query: String) {
        val suggestions = if (query.isBlank()) {
            emptyList()
        } else {
            allHospitalsCache
                .filter { it.name.contains(query, ignoreCase = true) }
                .map { it.name }
                .distinct()
                .take(AUTOCOMPLETE_SUGGESTION_LIMIT)
        }
        _uiState.update { it.copy(autocompleteSuggestions = suggestions) }
    }

    // 돋보기 아이콘 클릭 또는 키보드 검색 액션에서 호출. 여기서만 실제로 재조회한다.
    // 자동완성 후보 탭(onSuggestionSelected)과 최근 검색어 탭(onRecentSearchSelected)도
    // 결국 이 함수로 수렴해서, 검색 실행 경로가 어디든 동일하게 기록/재조회된다.
    fun onSearchSubmit() {
        val keyword = _uiState.value.query
        // 대기 중인 디바운스 콜백이 이전(타이핑 도중) 쿼리로 자동완성 목록을 뒤늦게 덮어쓰지
        // 않도록, 제출 시점의 최종 키워드로 디바운스 소스도 맞춰둔다.
        queryInput.value = keyword
        _uiState.update { it.copy(autocompleteSuggestions = emptyList()) }
        viewModelScope.launch { searchHistoryRepository.recordSearch(keyword) }
        loadResults()
    }

    fun onSuggestionSelected(name: String) {
        _uiState.update { it.copy(query = name) }
        onSearchSubmit()
    }

    fun onRecentSearchSelected(keyword: String) {
        _uiState.update { it.copy(query = keyword) }
        onSearchSubmit()
    }

    fun onRecentSearchDeleted(keyword: String) {
        viewModelScope.launch { searchHistoryRepository.deleteSearch(keyword) }
    }

    fun onFilterToggled(label: String) {
        _uiState.update { state ->
            state.copy(
                filters = state.filters.map { chip ->
                    if (chip.label == label) chip.copy(selected = !chip.selected) else chip
                }
            )
        }
        loadResults()
    }

    // 정렬은 이미 서버에서 받아온 lastServerResults를 클라이언트에서 재배열하는 것으로 처리한다
    // (재조회 불필요) — DISTANCE는 서면 기준점(DefaultSearchOrigin)으로부터의 haversine 거리.
    // 매번 lastServerResults(원본 순서)에서 다시 정렬해야 RELEVANCE로 되돌아왔을 때 원래 순서가
    // 복구된다 — 이미 재배열된 uiState.results를 또 정렬하면 원본이 사라진다.
    fun onSortSelected(sort: SearchSortOption) {
        _uiState.update { it.copy(selectedSort = sort, results = lastServerResults.sortedByOption(sort)) }
    }

    private fun List<Hospital>.sortedByOption(sort: SearchSortOption): List<Hospital> = when (sort) {
        SearchSortOption.RELEVANCE -> this
        SearchSortOption.NAME -> sortedBy { it.name }
        SearchSortOption.DISTANCE -> sortedBy { hospital ->
            val lat = hospital.latitude
            val lng = hospital.longitude
            if (lat == null || lng == null) {
                Double.MAX_VALUE
            } else {
                haversineDistanceMeters(DefaultSearchOrigin.LATITUDE, DefaultSearchOrigin.LONGITUDE, lat, lng)
            }
        }
    }

    fun onResetSearchConditions() {
        queryInput.value = ""
        _uiState.update {
            it.copy(query = "", filters = SearchFilterChip.DEFAULTS, autocompleteSuggestions = emptyList())
        }
        loadResults()
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

    companion object {
        private const val AUTOCOMPLETE_SUGGESTION_LIMIT = 10
        private const val AUTOCOMPLETE_DEBOUNCE_MS = 250L
    }
}
