package com.mediinbusan.app.data.tourism

import com.mediinbusan.app.domain.tourism.TourismCatalog
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import kotlinx.serialization.Serializable

@Serializable
data class TourismCatalogDto(
    val category: String,
    val title: String,
    val description: String,
    val source: String,
    val retrievedAt: String,
    val items: List<TourismCatalogItemDto> = emptyList()
)

@Serializable
data class TourismPlaceMatchDto(
    val matched: Boolean,
    val item: TourismCatalogItemDto? = null
)

@Serializable
data class TourismCatalogItemDto(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val address: String? = null,
    val imageUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val categoryCode: String? = null,
    val details: Map<String, String> = emptyMap()
)

fun TourismCatalogDto.toDomain(): TourismCatalog = TourismCatalog(
    category = TourismCatalogCategory.valueOf(category),
    title = title,
    description = description,
    source = source,
    retrievedAt = retrievedAt,
    items = items.map { it.toDomain() }
)

fun TourismCatalogItemDto.toDomain(): TourismCatalogItem = TourismCatalogItem(
    id = id,
    title = title,
    subtitle = subtitle?.takeUnless { it.matches(TOUR_API_INTERNAL_CODE) },
    address = address,
    imageUrl = imageUrl,
    latitude = latitude,
    longitude = longitude,
    categoryCode = categoryCode,
    details = details
)

private val TOUR_API_INTERNAL_CODE = Regex("[A-Za-z]\\d{7,}")
