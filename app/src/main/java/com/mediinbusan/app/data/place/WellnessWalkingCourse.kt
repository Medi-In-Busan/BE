package com.mediinbusan.app.data.place

data class WellnessWalkingCourse(
    val id: String,
    val name: String,
    val district: String,
    val distanceKm: Double?,
    val durationMinutes: Int?,
    val difficulty: String?,
    val summary: String?,
    val gpxUrl: String?
)

fun WellnessWalkingCourseDto.toDomain() = WellnessWalkingCourse(
    id = id,
    name = name,
    district = district,
    distanceKm = distanceKm,
    durationMinutes = durationMinutes,
    difficulty = difficulty,
    summary = summary,
    gpxUrl = gpxUrl
)
