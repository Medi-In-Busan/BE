package com.mediinbusan.app.data.document

import kotlinx.serialization.Serializable

/** POST /api/v1/documents/ocr 응답. 백엔드 DocumentOcrResponse와 1:1 — 번역 없이 원문 텍스트만 내려온다. */
@Serializable
data class DocumentOcrResponseDto(
    val text: String
)
