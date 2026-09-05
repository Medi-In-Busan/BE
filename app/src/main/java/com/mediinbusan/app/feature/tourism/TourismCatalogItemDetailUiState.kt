package com.mediinbusan.app.feature.tourism

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
    val isSnapshot: Boolean = false
)
