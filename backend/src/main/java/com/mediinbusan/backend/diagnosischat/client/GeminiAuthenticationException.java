package com.mediinbusan.backend.diagnosischat.client;

/** Gemini가 401/403을 반환했을 때(API Key 오설정 등). 원인 상세는 서버 로그에서만 확인한다. */
public class GeminiAuthenticationException extends RuntimeException {

    public GeminiAuthenticationException(String message) {
        super(message);
    }
}
