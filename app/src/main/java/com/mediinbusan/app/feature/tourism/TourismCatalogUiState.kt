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
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val hasNextPage: Boolean = true,
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
    val recommendedItems: List<TourismCatalogItem> = emptyList(),
    // 그리드 카드(RecommendedPlacesCatalogContent)의 하트 토글 표시용 — FavoriteRepository를
    // 실시간 구독해 채운다(TourismCatalogViewModel init 참고).
    val favoriteItemIds: Set<String> = emptySet(),
    // append(무한 스크롤 다음 페이지)가 아닌 진짜 새 조회(최초 진입·재시도·지역 변경)마다만 1씩
    // 올라간다. catalog는 append 때도 매번 새 인스턴스가 되므로(items 병합) 그리드 카드 리빌
    // 애니메이션의 key로 catalog 대신 이 값을 쓴다 — 안 그러면 스크롤로 페이지가 붙을 때마다
    // 이미 보이던 카드까지 스켈레톤부터 다시 재생돼 버벅인다.
    val loadGeneration: Int = 0
)

/** DISTANCE는 서면 기준점(DefaultSearchOrigin)으로부터의 거리 — 사용자 GPS를 쓰지 않는다(CLAUDE.md §1). */
enum class TourismSortOption {
    DISTANCE, NAME
}
