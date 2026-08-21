package com.mediinbusan.app.data.recent

import com.mediinbusan.app.data.favorite.FavoriteItemType

data class RecentlyViewed(
    val itemId: String,
    val itemName: String,
    val itemType: FavoriteItemType,
    val imageUrl: String?,
    val viewedAt: Long,
    // 카드에서 의료기관 리스트와 같은 태그/주소/거리 표시를 하기 위한 스냅샷.
    val subtitle: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null
)

fun RecentlyViewedEntity.toDomain(): RecentlyViewed = RecentlyViewed(
    itemId = itemId,
    itemName = itemName,
    itemType = FavoriteItemType.valueOf(itemType),
    imageUrl = imageUrl,
    viewedAt = viewedAt,
    subtitle = subtitle,
    address = address,
    latitude = latitude,
    longitude = longitude
)
