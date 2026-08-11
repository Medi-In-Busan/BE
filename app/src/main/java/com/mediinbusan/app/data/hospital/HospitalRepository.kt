package com.mediinbusan.app.data.hospital

import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow

interface HospitalRepository {
    // keyword는 이름/주소 OR 부분일치, specialties는 IN(OR) 조건. 백엔드 GET /api/hospitals 그대로.
    fun getHospitals(
        keyword: String? = null,
        specialties: List<MedicalCategory> = emptyList(),
        languageCode: String
    ): Flow<Result<List<Hospital>>>

    // 자동완성처럼 로컬 필터링을 위해 병원 전체 목록이 필요한 경우 전용. 내부적으로 서버
    // 페이지를 모두 순회해서 합쳐 반환하므로, 총 병원 수가 한 페이지 크기를 넘어도 누락되지 않는다.
    fun getAllHospitals(languageCode: String): Flow<Result<List<Hospital>>>

    fun getHospitalDetail(hospitalId: String, languageCode: String): Flow<Result<Hospital>>

    // 사용자가 지도에서 이동/탭한 좌표 기준. 기기 GPS는 조회하지 않는다 — 좌표는 항상 호출부(UI)가 넘긴다.
    fun getNearbyHospitals(
        latitude: Double,
        longitude: Double,
        radiusMeters: Double? = null,
        specialties: List<MedicalCategory> = emptyList()
    ): Flow<Result<List<Hospital>>>
}
