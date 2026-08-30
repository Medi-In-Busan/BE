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
        @Query("lang") lang: String
    ): List<PlaceDto>

    /** 병원에 종속되지 않은 웰니스 장소 조회. 좌표를 안 넘기면 전체를 반환한다(지도 '전체 브라우징'용). */
    @GET("api/wellness/places")
    suspend fun getPlaces(
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("radiusMeters") radiusMeters: Double? = null,
        @Query("lang") lang: String
    ): List<PlaceDto>

    @GET("api/wellness/places/{contentId}")
    suspend fun getPlaceDetail(
        @Path("contentId") contentId: String,
        @Query("lang") lang: String
    ): PlaceDto
}
