package com.mediinbusan.app.data.recent

import com.mediinbusan.app.data.favorite.FavoriteItemType

data class RecentlyViewed(
    val itemId: String,
    val itemName: String,
    val itemType: FavoriteItemType,
    val imageUrl: String?,
    val viewedAt: Long
)

fun RecentlyViewedEntity.toDomain(): RecentlyViewed = RecentlyViewed(
    itemId = itemId,
    itemName = itemName,
    itemType = FavoriteItemType.valueOf(itemType),
    imageUrl = imageUrl,
    viewedAt = viewedAt
)
