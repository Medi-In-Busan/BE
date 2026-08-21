package com.mediinbusan.backend.document.exception;

import org.springframework.http.HttpStatus;

/**
 * CLOVA OCR 호출/인식이 실패했을 때 던진다. 클라이언트에는 항상 고정된 일반 메시지만 노출하고,
 * 상세 원인(cause)은 서버 로그에서만 확인한다.
 */
public class DocumentOcrFailedException extends RuntimeException {

    private final HttpStatus status;

    public DocumentOcrFailedException(HttpStatus status) {
        super("문서 OCR 처리에 실패했습니다.");
        this.status = status;
    }

    public DocumentOcrFailedException(HttpStatus status, Throwable cause) {
        super("문서 OCR 처리에 실패했습니다.", cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
