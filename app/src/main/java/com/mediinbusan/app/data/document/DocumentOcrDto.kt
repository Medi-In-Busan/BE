package com.mediinbusan.app.data.document

import kotlinx.serialization.Serializable

/**
 * POST /api/v1/documents/ocr 응답. 백엔드 DocumentOcrResponse와 1:1.
 * translatedText/targetLanguage는 요청에 targetLang을 실어 보냈고 번역이 성공했을 때만 채워진다.
 */
@Serializable
data class DocumentOcrResponseDto(
    val text: String,
    val translatedText: String? = null,
    val targetLanguage: String? = null
)
