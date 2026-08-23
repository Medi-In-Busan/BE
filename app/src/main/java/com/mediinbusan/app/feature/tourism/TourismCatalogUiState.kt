package com.mediinbusan.app.feature.tourism

import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalog
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory

data class TourismCatalogUiState(
    val category: TourismCatalogCategory? = null,
    val selectedDistrict: BusanDistrict? = null,
    val catalog: TourismCatalog? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
