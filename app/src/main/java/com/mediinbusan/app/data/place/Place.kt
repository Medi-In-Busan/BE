package com.mediinbusan.app.data.place

data class Place(
    val id: String,
    val name: String,
    val type: PlaceType,
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
    val imageUrl: String?,
    val description: String?,
    val phoneNumber: String?,
    val distanceFromHospitalMeters: Double? = null,
    val lastModified: String? = null,
    // 현재 조회 언어로 실제 번역된 이름인지(한국어일 땐 항상 true) — 지도 "번역된 장소만" 필터가 쓴다.
    val isTranslated: Boolean = true
)

enum class PlaceType {
    TOURIST_ATTRACTION, RESTAURANT, SHOPPING, LODGING, SPA, WALK, OTHER
}
