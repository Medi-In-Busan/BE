package com.mediinbusan.app.data.hospital

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** MediInBusan 자체 백엔드(backend/, com.mediinbusan.backend.hospital)의 병원 검색/상세 API. */
interface HospitalApi {
    @GET("api/hospitals")
    suspend fun getHospitals(
        @Query("keyword") keyword: String? = null,
        @Query("specialties") specialties: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = DEFAULT_PAGE_SIZE
    ): HospitalPageDto

    @GET("api/hospitals/{regNo}")
    suspend fun getHospitalDetail(@Path("regNo") regNo: String): HospitalDetailDto

    @GET("api/hospitals/nearby")
    suspend fun getNearbyHospitals(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radiusMeters") radiusMeters: Double? = null,
        @Query("specialties") specialties: String? = null
    ): List<HospitalNearbyDto>

    companion object {
        // 실제 페이지네이션 UI가 붙기 전까지는, 현재 시딩 규모(122건)를 한 번에 다 받아오는 값으로 둔다.
        const val DEFAULT_PAGE_SIZE = 200
    }
}
