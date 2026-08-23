package com.mediinbusan.app.feature.tourism

import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory

data class TourismHubUiState(
    val language: SupportedLanguage = SupportedLanguage.DEFAULT,
    val featuredCategory: TourismCatalogCategory = TourismCatalogCategory.PLACES_KO,
    val recoveryCategories: List<TourismCatalogCategory> = emptyList(),
    val planningCategories: List<TourismCatalogCategory> = emptyList()
)
