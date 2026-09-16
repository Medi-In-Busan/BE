package com.mediinbusan.backend.guide.exception;

/** 진료 브리핑 번역 요청의 sourceLang이 비어있거나 지원 목록(ko/en/zh/ja) 밖일 때 던진다. */
public class UnsupportedTranslationLanguageException extends RuntimeException {

    public UnsupportedTranslationLanguageException(String message) {
        super(message);
    }
}
