package com.mediinbusan.app.domain.tourism

data class TourismInteractionProfile(
    val categoryViews: Map<TourismCatalogCategory, Int> = emptyMap(),
    val categoryAffinityScores: Map<TourismCatalogCategory, Double> = emptyMap(),
    val categoryLastViewedAt: Map<TourismCatalogCategory, Long> = emptyMap(),
    val selectedItemIds: Set<String> = emptySet(),
    val interestKeywords: Set<String> = emptySet(),
    val preferredDistrict: BusanDistrict? = null,
    val itemInteractions: List<TourismItemInteraction> = emptyList()
)

data class TourismItemInteraction(
    val itemId: String,
    val category: TourismCatalogCategory,
    val keywords: Set<String>,
    val occurredAtEpochMillis: Long
)
