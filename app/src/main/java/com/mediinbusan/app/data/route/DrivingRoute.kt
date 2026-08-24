package com.mediinbusan.app.data.route

data class DrivingRoute(
    val distanceMeters: Int,
    val durationSeconds: Int,
    val path: List<DrivingRouteCoordinate>,
    val sections: List<DrivingRouteSection>
)

data class DrivingRouteCoordinate(
    val latitude: Double,
    val longitude: Double
)

data class DrivingRouteSection(
    val distanceMeters: Int,
    val durationSeconds: Int
)

data class DrivingRoutePoint(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

enum class TravelMode {
    DRIVING,
    WALKING
}
