package com.mediinbusan.backend.document.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * CLOVA OCR General 응답 중 텍스트 추출에 필요한 필드만 담는다.
 * 나머지(boundingPoly, validationResult 등)는 우리 서비스에서 쓰지 않으므로 무시한다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ClovaOcrResponse(
    String version,
    String requestId,
    Long timestamp,
    List<ImageResult> images
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ImageResult(
        String name,
        String inferResult,
        String message,
        List<Field> fields
    ) {
        public boolean isSuccess() {
            return "SUCCESS".equalsIgnoreCase(inferResult);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Field(
        String inferText,
        Double inferConfidence,
        String type,
        Boolean lineBreak
    ) {
    }
}
