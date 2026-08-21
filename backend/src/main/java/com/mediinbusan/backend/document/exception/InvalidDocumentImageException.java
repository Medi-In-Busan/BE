package com.mediinbusan.backend.document.exception;

/** 요청 이미지가 비어있거나, 지원하지 않는 형식이거나, 용량 제한을 초과했을 때 CLOVA 호출 전에 던진다. */
public class InvalidDocumentImageException extends RuntimeException {

    public InvalidDocumentImageException(String message) {
        super(message);
    }
}
