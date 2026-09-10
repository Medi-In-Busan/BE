package com.mediinbusan.app.feature.tourism

import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem

data class TourismCatalogItemDetailUiState(
    val category: TourismCatalogCategory? = null,
    val item: TourismCatalogItem? = null,
    val consumed: Boolean = false,
    val selectedTitle: String? = null,
    val isLoading: Boolean = false,
    val matchNotFound: Boolean = false,
    val loadFailed: Boolean = false,
    // 최근 본 항목에서 재진입했는데 재조회(findMatchingPlace)를 시도하지 않았거나 실패해서, 저장된
    // 스냅샷(마지막으로 봤을 때의 정보)만 보여주고 있는 상태 — 화면에 "최신 정보가 아닐 수 있음" 안내를 띄운다.
    val isSnapshot: Boolean = false,
    // 이 항목 좌표를 기준으로 조회한, 같은 종류의 주변 장소들. 장소 상세(S-07 하위)의 같은 이름
    // 필드와 같은 성격이다 — 부가 정보라 실패해도 화면 전체를 오류로 만들지 않고 섹션만 사라진다.
    val nearbySamePlaces: List<Place> = emptyList()
)
