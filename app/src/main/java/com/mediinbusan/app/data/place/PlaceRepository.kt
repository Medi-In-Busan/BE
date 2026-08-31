package com.mediinbusan.app.data.place

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    fun getNearbyPlaces(hospitalId: String, languageCode: String): Flow<Result<List<Place>>>
    fun getPlaceDetail(placeId: String, languageCode: String): Flow<Result<Place>>

    /** 특정 병원에 종속되지 않은 전체 웰니스 장소 조회 — 지도 "전체 브라우징" 화면(하단 탭 '지도')이 쓴다. */
    fun getAllPlaces(languageCode: String): Flow<Result<List<Place>>>
}
