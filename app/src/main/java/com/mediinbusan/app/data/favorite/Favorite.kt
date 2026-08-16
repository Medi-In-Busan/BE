package com.mediinbusan.app.data.favorite

data class Favorite(
    val itemId: String,
    val itemType: FavoriteItemType,
    val name: String,
    val imageUrl: String?,
    val savedAt: Long,
    // 카드에서 의료기관 리스트와 같은 태그/주소/거리 표시를 하기 위한 스냅샷.
    val subtitle: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null
)

enum class FavoriteItemType { HOSPITAL, PLACE }
