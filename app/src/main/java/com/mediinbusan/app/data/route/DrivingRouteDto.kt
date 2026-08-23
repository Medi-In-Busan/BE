package com.mediinbusan.app.data.route

import kotlinx.serialization.Serializable

@Serializable
data class DrivingRouteRequestDto(
    val origin: DrivingRoutePointDto,
    val stops: List<DrivingRoutePointDto>,
    val mode: TravelMode
)

@Serializable
data class DrivingRoutePointDto(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class DrivingRouteResponseDto(
    val distanceMeters: Int,
    val durationSeconds: Int,
    val path: List<DrivingRouteCoordinateDto>,
    val sections: List<DrivingRouteSectionDto>
)

@Serializable
data class DrivingRouteCoordinateDto(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class DrivingRouteSectionDto(
    val distanceMeters: Int,
    val durationSeconds: Int
)

fun DrivingRoutePoint.toDto() = DrivingRoutePointDto(name, latitude, longitude)

fun DrivingRouteResponseDto.toDomain() = DrivingRoute(
    distanceMeters = distanceMeters,
    durationSeconds = durationSeconds,
    path = path.map { DrivingRouteCoordinate(it.latitude, it.longitude) },
    sections = sections.map { DrivingRouteSection(it.distanceMeters, it.durationSeconds) }
)
