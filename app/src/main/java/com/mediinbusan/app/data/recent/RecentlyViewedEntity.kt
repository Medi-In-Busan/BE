package com.mediinbusan.app.data.recent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_viewed")
data class RecentlyViewedEntity(
    @PrimaryKey val itemId: String,
    val itemName: String,
    val itemType: String, // RecentItemType.name ("HOSPITAL" | "PLACE" | "TOURISM_ITEM")
    val imageUrl: String?,
    val viewedAt: Long,
    // 카드에서 의료기관 리스트(SearchResultCard)와 같은 태그/주소/거리 표시를 하기 위한 스냅샷.
    // 조회 당시 값을 그대로 저장한다(병원/장소 상세를 다시 조회하지 않는다).
    val subtitle: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    // itemType == TOURISM_ITEM일 때만 채워진다. 재조회(findMatchingPlace) 시 필요한 컨텍스트 —
    // tourismDistrict가 있으면 tourismCategory+itemName으로 최신 데이터를 다시 찾고, 없으면
    // (구·군 비종속 카테고리) 이 스냅샷만 그대로 보여준다. RecentlyViewed.kt 주석 참고.
    val tourismCategory: String? = null,
    val tourismDistrict: String? = null
)
