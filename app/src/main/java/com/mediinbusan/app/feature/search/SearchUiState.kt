package com.mediinbusan.app.feature.search

import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.data.hospital.Hospital

/** 검색(S-04와 별도) UI 상태. 카테고리 필터·정렬은 스텁이라 결과에 실제 영향을 주지 않는다. */
data class SearchUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    val results: List<Hospital> = emptyList(),
    val favoriteHospitalIds: Set<String> = emptySet(),
    val filters: List<SearchFilterChip> = SearchFilterChip.DEFAULTS,
    val selectedSort: SearchSortOption = SearchSortOption.RELEVANCE,
    val selectedLanguage: String = SupportedLanguage.DEFAULT.code,
    val errorMessage: String? = null,
    // 무한스크롤은 UI 훅만 미리 잡아두는 단계라, 실제로는 항상 마지막 페이지로 취급한다.
    val hasReachedEnd: Boolean = true
) {
    val filteredResults: List<Hospital>
        get() = if (query.isBlank()) results else results.filter { it.name.contains(query, ignoreCase = true) }
}

/** TODO: 병원 6개 카테고리 + 관광지 스텁. 실제 필터링 연결은 다음 이슈. */
data class SearchFilterChip(val label: String, val selected: Boolean = false) {
    companion object {
        val DEFAULTS = listOf("피부·미용", "건강검진", "치과", "한방", "재활", "웰니스", "관광지")
            .map { SearchFilterChip(label = it) }
    }
}

/** TODO: 정렬 기준 미확정. 선택 상태만 보관하고 실제 정렬 로직은 다음 이슈. */
enum class SearchSortOption(val label: String) {
    RELEVANCE("관련도순"),
    NAME("이름순"),
    DISTANCE("가까운순")
}
