package com.mediinbusan.app.domain.tourism

data class TourismInteractionProfile(
    val categoryViews: Map<TourismCatalogCategory, Int> = emptyMap(),
    val selectedItemIds: Set<String> = emptySet(),
    val interestKeywords: Set<String> = emptySet(),
    val preferredDistrict: BusanDistrict? = null
)
