package com.mediinbusan.app.data.place

import kotlinx.serialization.Serializable

@Serializable
data class WellnessWalkingCourseDto(
    val id: String,
    val name: String,
    val district: String,
    val distanceKm: Double? = null,
    val durationMinutes: Int? = null,
    val difficulty: String? = null,
    val summary: String? = null,
    val gpxUrl: String? = null
)
