package com.mediinbusan.backend.diagnosischat.client;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Gemini generateContent 요청의 contents[]/systemInstruction, 응답의 candidate.content에 공통으로 쓰인다. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeminiContent(String role, List<GeminiPart> parts) {

    public static GeminiContent systemInstruction(String text) {
        return new GeminiContent(null, List.of(new GeminiPart(text)));
    }

    public static GeminiContent user(String text) {
        return new GeminiContent("user", List.of(new GeminiPart(text)));
    }
}
