package com.mediinbusan.backend.diagnosischat.dto;

/** 챗봇 API 오류 응답. Gemini API Key, 원본 프롬프트/응답 등 민감 정보는 절대 담지 않는다. */
public record DiagnosisChatErrorResponse(
    String code,
    String message
) {
}
