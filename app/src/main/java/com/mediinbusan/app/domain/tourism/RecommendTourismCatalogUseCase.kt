package com.mediinbusan.app.domain.tourism

import javax.inject.Inject

data class TourismRecommendation(
    val catalog: TourismCatalog,
    val personalizedItemIds: Set<String>
)

class RecommendTourismCatalogUseCase @Inject constructor() {
    operator fun invoke(
        catalog: TourismCatalog,
        profile: TourismInteractionProfile,
        favoritePlaceNames: List<String>,
        recentPlaceNames: List<String>
    ): TourismRecommendation {
        val strongKeywords = profile.interestKeywords + favoritePlaceNames.flatMap(::tokenize)
        val recentKeywords = recentPlaceNames.flatMap(::tokenize).toSet()
        val scored = catalog.items.mapIndexed { index, item ->
            val searchable = listOfNotNull(item.title, item.subtitle, item.address)
                .plus(item.details.values)
                .joinToString(" ")
                .lowercase()
            var score = if (item.id in profile.selectedItemIds) 100 else 0
            score += strongKeywords.count { it in searchable } * 8
            score += recentKeywords.count { it in searchable } * 4
            ScoredItem(item, score, index)
        }
        val ranked = scored.sortedWith(compareByDescending<ScoredItem> { it.score }.thenBy { it.originalIndex })
        return TourismRecommendation(
            catalog = catalog.copy(items = ranked.map { it.item }),
            personalizedItemIds = ranked.filter { it.score > 0 }.take(5).map { it.item.id }.toSet()
        )
    }

    private fun tokenize(value: String): List<String> = value
        .lowercase()
        .split(Regex("[^가-힣a-z0-9]+"))
        .filter { it.length >= 2 }

    private data class ScoredItem(
        val item: TourismCatalogItem,
        val score: Int,
        val originalIndex: Int
    )
}
