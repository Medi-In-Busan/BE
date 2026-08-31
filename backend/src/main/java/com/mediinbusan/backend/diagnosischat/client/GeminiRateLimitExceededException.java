package com.mediinbusan.backend.diagnosischat.client;

/**
 * Gemini가 429(RESOURCE_EXHAUSTED — RPM/RPD/TPM 한도 초과)를 반환했을 때만 던진다. 그 외 4xx/5xx는
 * 여전히 일반 {@link GeminiApiException}으로 처리한다 — 로그/모니터링에서 "쿼터 소진"과 "그 외 API 오류"를
 * 구분해서 볼 수 있게 분리했다.
 */
public class GeminiRateLimitExceededException extends GeminiApiException {

    public GeminiRateLimitExceededException(String message) {
        super(message);
    }
}
