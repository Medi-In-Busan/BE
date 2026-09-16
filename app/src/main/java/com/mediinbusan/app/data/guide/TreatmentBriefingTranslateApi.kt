package com.mediinbusan.app.data.guide

import retrofit2.http.Body
import retrofit2.http.POST

/** MediInBusan 자체 백엔드(backend/, com.mediinbusan.backend.guide)의 진료 브리핑 카드 번역 API. */
interface TreatmentBriefingTranslateApi {
    @POST("api/v1/guide/treatment-briefing/translate")
    suspend fun translate(@Body request: TreatmentBriefingTranslateRequestDto): TreatmentBriefingTranslateResponseDto
}
