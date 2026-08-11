package com.mediinbusan.app.data.document

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/** MediInBusan 자체 백엔드(backend/, com.mediinbusan.backend.document)의 CLOVA OCR 프록시 API. */
interface DocumentOcrApi {
    @Multipart
    @POST("api/v1/documents/ocr")
    suspend fun extractText(@Part image: MultipartBody.Part): DocumentOcrResponseDto
}
