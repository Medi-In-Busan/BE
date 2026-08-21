package com.mediinbusan.app.data.document

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/** MediInBusan 자체 백엔드(backend/, com.mediinbusan.backend.document)의 CLOVA OCR 프록시 API. */
interface DocumentOcrApi {
    @Multipart
    @POST("api/v1/documents/ocr")
    suspend fun extractText(
        @Part image: MultipartBody.Part,
        // Papago 언어 코드(en/ja/zh-CN 등). null/미지정이면 백엔드가 번역을 시도하지 않는다.
        @Query("targetLang") targetLang: String? = null
    ): DocumentOcrResponseDto
}
