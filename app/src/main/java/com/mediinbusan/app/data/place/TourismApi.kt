package com.mediinbusan.app.data.place

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** MediInBusan 자체 백엔드의 웰니스 장소 API. */
interface TourismApi {
    @GET("api/wellness/tourism/walking-courses")
    suspend fun getWellnessWalkingCourses(): List<WellnessWalkingCourseDto>

    @GET("api/wellness/hospitals/{hospitalRegNo}/places")
    suspend fun getNearbyWellnessPlaces(
        @Path("hospitalRegNo") hospitalRegNo: String,
        @Query("radiusMeters") radiusMeters: Double? = null,
        @Query("language") language: String = "ko"
    ): List<PlaceDto>

    @GET("api/wellness/places/{contentId}")
    suspend fun getPlaceDetail(
        @Path("contentId") contentId: String,
        @Query("language") language: String = "ko"
    ): PlaceDto
}
