package com.mediinbusan.app.data.favorite

data class Favorite(
    val itemId: String,
    val itemType: FavoriteItemType,
    val name: String,
    val imageUrl: String?,
    val savedAt: Long
)

enum class FavoriteItemType { HOSPITAL, PLACE }
