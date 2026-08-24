package com.mediinbusan.backend.diagnosischat.client;

import java.util.List;

/** Gemini v1beta generateContent 응답 바디 중 필요한 부분만 담는다. */
public record GeminiGenerateContentResponse(
    List<Candidate> candidates
) {
    public record Candidate(GeminiContent content) {
    }
}
