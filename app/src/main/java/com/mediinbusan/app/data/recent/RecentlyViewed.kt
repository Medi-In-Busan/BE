package com.mediinbusan.app.data.recent

data class RecentlyViewed(
    val itemId: String,
    val itemName: String,
    val viewedAt: Long
)

fun RecentlyViewedEntity.toDomain(): RecentlyViewed = RecentlyViewed(
    itemId = itemId,
    itemName = itemName,
    viewedAt = viewedAt
)
