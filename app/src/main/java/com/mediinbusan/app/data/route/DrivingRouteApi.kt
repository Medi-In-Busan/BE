package com.mediinbusan.app.data.route

import retrofit2.http.Body
import retrofit2.http.POST

interface DrivingRouteApi {
    @POST("api/wellness/routes")
    suspend fun getDrivingRoute(@Body request: DrivingRouteRequestDto): DrivingRouteResponseDto
}
