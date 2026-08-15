package com.mediinbusan.app.feature.hospitalsearchlist

import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.data.hospital.Hospital

/**
 * S-04 통합 화면(구 HospitalList + Search) UI 상태. query/filters 둘 다 백엔드
 * GET /api/hospitals(keyword OR 검색 + specialties IN 필터)로 그대로 넘어가서, results는
 * 이미 서버에서 필터링된 결과다 — 여기서 다시 클라이언트 필터링하지 않는다.
 */
data class HospitalSearchListUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    val results: List<Hospital> = emptyList(),
    val favoriteHospitalIds: Set<String> = emptySet(),
    val filters: List<SearchFilterChip> = SearchFilterChip.DEFAULTS,
    val selectedSort: SearchSortOption = SearchSortOption.RELEVANCE,
    val selectedLanguage: String = SupportedLanguage.DEFAULT.code,
    // 로드 실패 여부와 서버 메시지를 분리한다. errorMessage가 null이어도 isError가 true면
    // 화면에서 LocalAppStrings 기준 폴백 문구를 그려, 에러 표시 중 언어를 바꿔도 즉시 반영된다.
    val isError: Boolean = false,
    val errorMessage: String? = null,
    // 무한스크롤은 UI 훅만 미리 잡아두는 단계라, 실제로는 항상 마지막 페이지로 취급한다.
    val hasReachedEnd: Boolean = true,
    // 최신순, 최대 10건(SearchHistoryDao 쿼리 기준). 검색창 포커스 + 입력 비어있을 때 노출.
    val recentSearches: List<String> = emptyList(),
    // 타이핑 중 병원명 부분일치 후보. 입력이 비어있으면 항상 빈 리스트.
    val autocompleteSuggestions: List<String> = emptyList(),
    // Home 검색바를 탭해 들어온 경우 true — PendingHospitalSearchEntry.consumeFocusRequest() 참고.
    // ViewModel.initialize()가 채워주기 전(최초 컴포지션)엔 항상 false라, 화면에서 이 값을
    // remember 초기값으로 한 번만 캡처하지 말고 LaunchedEffect로 값이 바뀌는 걸 반응형으로 봐야 한다.
    val shouldAutoFocusSearch: Boolean = false
)

/** MedicalCategory 10종 + 관광지(백엔드 필터 대상 아님, 선택해도 결과에 영향 없음) 스텁. */
data class SearchFilterChip(val label: String, val selected: Boolean = false) {
    companion object {
        val DEFAULTS = (MedicalCategory.entries.map { it.label } + "관광지")
            .map { SearchFilterChip(label = it) }
    }
}

/** DISTANCE는 서면 기준점(DefaultSearchOrigin)으로부터의 거리 — 사용자 GPS를 쓰지 않는다(CLAUDE.md §1). */
enum class SearchSortOption(val label: String) {
    RELEVANCE("관련도순"),
    NAME("이름순"),
    DISTANCE("가까운순")
}
