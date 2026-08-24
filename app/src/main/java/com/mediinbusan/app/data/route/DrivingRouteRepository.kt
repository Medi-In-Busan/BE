package com.mediinbusan.app.data.route

import com.mediinbusan.app.core.common.Result

interface DrivingRouteRepository {
    suspend fun getRoute(
        origin: DrivingRoutePoint,
        stops: List<DrivingRoutePoint>,
        mode: TravelMode
    ): Result<DrivingRoute>
}
