package com.mediinbusan.backend.diagnosischat.client;

/** Gemini 호출/응답 관련 오류(설정 누락, 네트워크 오류, 4xx/5xx, 구조화 출력 파싱 실패 등)를 통칭한다. */
public class GeminiApiException extends RuntimeException {

    public GeminiApiException(String message) {
        super(message);
    }

    public GeminiApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
