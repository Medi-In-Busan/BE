package com.mediinbusan.app.data.diagnosischat

import retrofit2.http.Body
import retrofit2.http.POST

/** MediInBusan 자체 백엔드(backend/, com.mediinbusan.backend.diagnosischat)의 Gemini 진단 챗봇 프록시 API. */
interface DiagnosisChatApi {
    @POST("api/v1/diagnosis-chat")
    suspend fun sendMessage(@Body request: DiagnosisChatRequestDto): DiagnosisChatResponseDto
}
