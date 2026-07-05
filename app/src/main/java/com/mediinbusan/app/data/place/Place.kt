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
    val lastModified: String? = null
)

enum class PlaceType {
    TOURIST_ATTRACTION, RESTAURANT, SHOPPING, LODGING, SPA, WALK, OTHER
}
