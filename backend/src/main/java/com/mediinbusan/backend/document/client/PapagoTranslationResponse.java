package com.mediinbusan.backend.document.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Papago 번역 응답 중 번역 결과 텍스트에 필요한 필드만 담는다.
 * 필드 구조는 NCP Papago NMT API 공식 문서(message.result.{srcLangType,tarLangType,translatedText}) 기준.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PapagoTranslationResponse(
    Message message
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(
        Result result
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
        String srcLangType,
        String tarLangType,
        String translatedText
    ) {
    }
}