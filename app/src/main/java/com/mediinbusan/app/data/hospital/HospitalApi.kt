package com.mediinbusan.app.data.hospital

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 한국관광공사 의료관광정보 서비스 (F-004, F-006, F-007, F-009).
 * TODO: 정확한 엔드포인트 경로/오퍼레이션명/파라미터는 실제 API 문서 확인 후 확정한다.
 * 아래 경로/파라미터명은 확정 전 플레이스홀더다.
 */
interface HospitalApi {
    @GET("MedicalTourismService/getHospitalList")
    suspend fun getHospitalList(
        @Query("serviceKey") serviceKey: String,
        @Query("areaCode") areaCode: String = BUSAN_AREA_CODE,
        @Query("langCode") langCode: String,
        @Query("medicalPurpose") medicalPurpose: String? = null
    ): List<HospitalDto>

    @GET("MedicalTourismService/getHospitalDetail")
    suspend fun getHospitalDetail(
        @Query("serviceKey") serviceKey: String,
        @Query("contentId") contentId: String,
        @Query("langCode") langCode: String
    ): HospitalDto

    companion object {
        const val BUSAN_AREA_CODE = "6"
    }
}
