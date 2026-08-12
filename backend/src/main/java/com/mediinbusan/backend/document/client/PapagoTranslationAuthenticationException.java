package com.mediinbusan.backend.document.client;

/** Papago 번역 API가 401/403을 반환했을 때(Client ID/Secret 오설정 등). 원인 상세는 서버 로그에서만 확인한다. */
public class PapagoTranslationAuthenticationException extends RuntimeException {

    public PapagoTranslationAuthenticationException(String message) {
        super(message);
    }
}