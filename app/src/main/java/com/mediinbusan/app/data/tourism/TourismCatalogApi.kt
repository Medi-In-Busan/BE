package com.mediinbusan.app.data.tourism

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TourismCatalogApi {
    @GET("api/wellness/tourism/matched-place")
    suspend fun findMatchingPlace(
        @Query("title") title: String,
        @Query("district") district: String
    ): TourismPlaceMatchDto

    @GET("api/wellness/tourism/catalog/{category}")
    suspend fun getCatalog(
        @Path("category") category: String,
        @Query("district") district: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("language") language: String = "ko"
    ): TourismCatalogDto
}
