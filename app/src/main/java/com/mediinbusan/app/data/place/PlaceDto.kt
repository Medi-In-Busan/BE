package com.mediinbusan.app.data.place

import kotlinx.serialization.Serializable

@Serializable
data class PlaceDto(
    val contentId: String? = null,
    val name: String? = null,
    val contentTypeId: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val imageUrl: String? = null,
    val description: String? = null,
    val phoneNumber: String? = null,
    val modifiedDate: String? = null,
    val distanceFromHospitalMeters: Double? = null
)
