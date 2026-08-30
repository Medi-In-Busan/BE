package com.mediinbusan.app.feature.tourism

import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalog
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem

data class TourismCatalogUiState(
    val category: TourismCatalogCategory? = null,
    val selectedDistrict: BusanDistrict? = null,
    val catalog: TourismCatalog? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedSort: TourismSortOption = TourismSortOption.DISTANCE,
    val selectedCategoryCode: String? = null,
    // "부산 관광지"(language variant 카테고리)에서 RecommendTourismCatalogUseCase가 매긴 개인화
    // 점수>0인 상위 항목 id. 다른 카테고리는 항상 비어있다.
    val personalizedItemIds: Set<String> = emptySet(),
    // catalog.items에 검색어(장소명)·카테고리 필터·정렬을 클라이언트에서 적용한 결과.
    // 구·군 필터만 서버 재조회(TourismCatalogViewModel.selectDistrict)를 타고, 나머지는
    // 이미 받아온 목록을 대상으로 로컬 처리한다(TourismCatalogViewModel.applyClientFilters).
    val visibleItems: List<TourismCatalogItem> = emptyList(),
    // "부산 관광지"에서만 채워지는 추천 섹션(personalizedItemIds에 해당하는 항목, 개인화 점수순).
    // 다른 카테고리는 항상 비어있고 visibleItems 하나로만 렌더링한다.
    val recommendedItems: List<TourismCatalogItem> = emptyList()
)

/** DISTANCE는 서면 기준점(DefaultSearchOrigin)으로부터의 거리 — 사용자 GPS를 쓰지 않는다(CLAUDE.md §1). */
enum class TourismSortOption {
    DISTANCE, NAME
}
