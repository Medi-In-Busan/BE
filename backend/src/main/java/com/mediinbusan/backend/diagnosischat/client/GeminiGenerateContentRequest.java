package com.mediinbusan.backend.diagnosischat.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** Gemini v1beta generateContent 요청 바디. 필드명은 REST API의 camelCase 규약을 따른다. */
public record GeminiGenerateContentRequest(
    @JsonProperty("systemInstruction") GeminiContent systemInstruction,
    @JsonProperty("contents") List<GeminiContent> contents,
    @JsonProperty("generationConfig") GeminiGenerationConfig generationConfig
) {
}
