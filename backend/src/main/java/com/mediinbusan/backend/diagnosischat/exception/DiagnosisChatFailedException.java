package com.mediinbusan.backend.diagnosischat.exception;

import org.springframework.http.HttpStatus;

/**
 * Gemini 호출/처리가 실패했을 때 던진다. 클라이언트에는 항상 고정된 일반 메시지만 노출하고,
 * 상세 원인(cause)은 서버 로그에서만 확인한다.
 */
public class DiagnosisChatFailedException extends RuntimeException {

    private final HttpStatus status;

    public DiagnosisChatFailedException(HttpStatus status, Throwable cause) {
        super("자가진단 챗봇 처리에 실패했습니다.", cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
