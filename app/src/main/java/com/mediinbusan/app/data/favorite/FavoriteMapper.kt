package com.mediinbusan.app.data.favorite

fun FavoriteEntity.toDomain(): Favorite = Favorite(
    itemId = itemId,
    itemType = FavoriteItemType.valueOf(itemType),
    name = name,
    imageUrl = imageUrl,
    savedAt = savedAt,
    subtitle = subtitle,
    address = address,
    latitude = latitude,
    longitude = longitude
)

fun Favorite.toEntity(): FavoriteEntity = FavoriteEntity(
    itemId = itemId,
    itemType = itemType.name,
    name = name,
    imageUrl = imageUrl,
    savedAt = savedAt,
    subtitle = subtitle,
    address = address,
    latitude = latitude,
    longitude = longitude
)
