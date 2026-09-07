package com.mediinbusan.app.data.place

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    fun getNearbyPlaces(hospitalId: String, languageCode: String): Flow<Result<List<Place>>>
    fun getPlaceDetail(placeId: String, languageCode: String): Flow<Result<Place>>

    /** 특정 병원에 종속되지 않은 전체 웰니스 장소 조회 — 지도 "전체 브라우징" 화면(하단 탭 '지도')이 쓴다. */
    fun getAllPlaces(languageCode: String): Flow<Result<List<Place>>>

    /**
     * 임의 좌표 기준 주변 장소 조회. 서버가 Haversine으로 거리를 계산해 반경 안의 것만 거리순으로
     * 내려주고, 그 거리는 [Place.distanceFromHospitalMeters]에 담겨 온다(필드 이름이 병원 기준처럼
     * 보이지만 실제로는 "넘긴 기준점으로부터의 거리"다).
     *
     * 기기 GPS는 조회하지 않는다 — 좌표는 항상 호출부가 넘긴다(HospitalRepository.getNearbyHospitals와
     * 같은 규칙, CLAUDE.md §1).
     */
    fun getPlacesNear(
        latitude: Double,
        longitude: Double,
        radiusMeters: Double? = null,
        languageCode: String
    ): Flow<Result<List<Place>>>
}
