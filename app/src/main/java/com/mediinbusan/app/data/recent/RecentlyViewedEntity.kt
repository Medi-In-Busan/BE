package com.mediinbusan.app.data.recent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_viewed")
data class RecentlyViewedEntity(
    @PrimaryKey val itemId: String,
    val itemName: String,
    val itemType: String, // FavoriteItemType.name ("HOSPITAL" | "PLACE")
    val imageUrl: String?,
    val viewedAt: Long,
    // 카드에서 의료기관 리스트(SearchResultCard)와 같은 태그/주소/거리 표시를 하기 위한 스냅샷.
    // 조회 당시 값을 그대로 저장한다(병원/장소 상세를 다시 조회하지 않는다).
    val subtitle: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null
)
