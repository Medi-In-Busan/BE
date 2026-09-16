package com.mediinbusan.backend.guide.dto;

/** guide API 오류 응답. DocumentErrorResponse와 동일한 {code, message} 형태를 유지한다. */
public record GuideErrorResponse(String code, String message) {
}
