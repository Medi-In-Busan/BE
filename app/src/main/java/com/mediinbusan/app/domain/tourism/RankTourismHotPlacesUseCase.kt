package com.mediinbusan.app.domain.tourism

import javax.inject.Inject

data class TourismHotPlace(
    val item: TourismCatalogItem,
    val district: BusanDistrict,
    val congestionRate: Double
)

class RankTourismHotPlacesUseCase @Inject constructor() {
    operator fun invoke(
        catalogs: List<Pair<BusanDistrict, TourismCatalog>>,
        limit: Int = 5
    ): List<TourismHotPlace> = catalogs
        .flatMap { (district, catalog) ->
            catalog.items.mapNotNull { item ->
                item.congestionRateOrNull()?.let { rate ->
                    TourismHotPlace(item = item, district = district, congestionRate = rate)
                }
            }
        }
        .groupBy { it.item.title.trim().lowercase() }
        .values
        .mapNotNull { duplicates -> duplicates.maxByOrNull(TourismHotPlace::congestionRate) }
        .sortedByDescending(TourismHotPlace::congestionRate)
        .take(limit)
}
