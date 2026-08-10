package com.mediinbusan.app.feature.hospitalsearchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.Result
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
 * 정렬/서버 페이지네이션은 다음 이슈.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class HospitalSearchListViewModel @Inject constructor(
    private val hospitalRepository: HospitalRepository,
    private val favoriteRepository: FavoriteRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HospitalSearchListUiState())
    val uiState: StateFlow<HospitalSearchListUiState> = _uiState

    private var initialized = false

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

    // Home에서 의료목적 칩/웰니스 퀵링크로 진입할 때 해당 필터 칩을 선택 상태로 반영하고,
    // 그 목적에 맞는 결과만 서버에서 걸러 불러온다. 화면 재구성 시 중복 초기화되지 않도록 1회만 실행.
    fun initialize(medicalPurpose: MedicalCategory?) {
        if (initialized) return
        initialized = true
        if (medicalPurpose != null) {
            _uiState.update { state ->
                state.copy(filters = state.filters.map { it.copy(selected = it.label == medicalPurpose.label) })
            }
        }
        loadResults()
        loadAutocompleteSource()
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
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, isError = false, results = result.data, errorMessage = null)
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

    // TODO: 정렬 기준 확정 후 실제 정렬 로직을 연결한다. 지금은 선택 상태만 보관.
    fun onSortSelected(sort: SearchSortOption) {
        _uiState.update { it.copy(selectedSort = sort) }
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
