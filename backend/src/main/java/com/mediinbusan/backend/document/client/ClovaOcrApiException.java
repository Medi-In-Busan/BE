package com.mediinbusan.backend.document.client;

/** CLOVA OCR 호출/응답 관련 오류(설정 누락, 네트워크 오류, 4xx/5xx, 인식 실패 등)를 통칭한다. */
public class ClovaOcrApiException extends RuntimeException {

    public ClovaOcrApiException(String message) {
        super(message);
    }

    public ClovaOcrApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
