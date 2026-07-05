package com.mediinbusan.app.data.place

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 한국관광공사 관광정보서비스 (F-011, F-012) — 위치기반 목록/상세/사진 조회.
 * TODO: 정확한 엔드포인트 경로/오퍼레이션명/파라미터는 실제 API 문서 확인 후 확정한다.
 * 아래 경로/파라미터명은 확정 전 플레이스홀더다.
 */
interface TourismApi {
    @GET("KorService/locationBasedList")
    suspend fun getPlacesNearLocation(
        @Query("serviceKey") serviceKey: String,
        @Query("mapX") longitude: Double,
        @Query("mapY") latitude: Double,
        @Query("radius") radiusMeters: Int = 3000
    ): List<PlaceDto>

    @GET("KorService/detailCommon")
    suspend fun getPlaceDetail(
        @Query("serviceKey") serviceKey: String,
        @Query("contentId") contentId: String
    ): PlaceDto

    @GET("KorService/detailImage")
    suspend fun getPlaceImages(
        @Query("serviceKey") serviceKey: String,
        @Query("contentId") contentId: String
    ): List<String>
}
