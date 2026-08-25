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
        limit: Int = 8
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

    private fun TourismCatalogItem.congestionRateOrNull(): Double? {
        val rawValue = details.entries.firstOrNull { (key, _) ->
            key.equals("tatsCnctrRate", ignoreCase = true) ||
                key.equals("cnctrRate", ignoreCase = true) ||
                key.equals("congestionRate", ignoreCase = true)
        }?.value ?: subtitle
        return rawValue
            ?.replace(",", "")
            ?.let { NUMBER_PATTERN.find(it)?.value }
            ?.toDoubleOrNull()
    }

    private companion object {
        val NUMBER_PATTERN = Regex("-?\\d+(?:\\.\\d+)?")
    }
}
