package com.mediinbusan.backend.document.client;

/** CLOVA OCR이 401/403을 반환했을 때(Secret Key 오설정 등). 원인 상세는 서버 로그에서만 확인한다. */
public class ClovaOcrAuthenticationException extends RuntimeException {

    public ClovaOcrAuthenticationException(String message) {
        super(message);
    }
}
