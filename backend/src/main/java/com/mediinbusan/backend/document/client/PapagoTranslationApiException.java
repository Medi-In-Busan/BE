package com.mediinbusan.backend.document.client;

/** Papago 번역 호출/응답 관련 오류(설정 누락, 네트워크 오류, 4xx/5xx 등)를 통칭한다. */
public class PapagoTranslationApiException extends RuntimeException {

    public PapagoTranslationApiException(String message) {
        super(message);
    }

    public PapagoTranslationApiException(String message, Throwable cause) {
        super(message, cause);
    }
}