package com.mediinbusan.backend.diagnosischat.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeminiGenerationConfig(
    @JsonProperty("responseMimeType") String responseMimeType,
    @JsonProperty("responseSchema") GeminiSchema responseSchema
) {
}
