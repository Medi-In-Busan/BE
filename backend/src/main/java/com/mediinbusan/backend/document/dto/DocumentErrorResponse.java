package com.mediinbusan.backend.document.dto;

/** 문서 OCR API 오류 응답. Secret Key, CLOVA 원본 오류 상세 등 민감 정보는 절대 담지 않는다. */
public record DocumentErrorResponse(
    String code,
    String message
) {
}
