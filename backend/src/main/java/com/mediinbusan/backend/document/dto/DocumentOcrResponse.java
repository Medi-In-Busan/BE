package com.mediinbusan.backend.document.dto;

/**
 * Android에 반환하는 OCR/번역 결과. 구조화(항목별 파싱)는 이후 이슈에서 다룬다.
 * targetLang 요청이 없거나 번역이 실패하면 translatedText/targetLanguage는 null이고 text만 채워진다
 * (번역 실패가 OCR 자체를 실패시키지 않는다).
 */
public record DocumentOcrResponse(
    String text,
    String translatedText,
    String targetLanguage
) {
}
