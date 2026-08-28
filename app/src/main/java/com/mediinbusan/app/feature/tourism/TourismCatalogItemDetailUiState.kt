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
    val loadFailed: Boolean = false
)
