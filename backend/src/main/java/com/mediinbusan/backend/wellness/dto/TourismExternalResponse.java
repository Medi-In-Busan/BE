package com.mediinbusan.backend.wellness.dto;

import java.time.Instant;

public record TourismExternalResponse(
    String source,
    Instant retrievedAt,
    Object data
) {
    public static TourismExternalResponse of(String source, Object data) {
        return new TourismExternalResponse(source, Instant.now(), data);
    }
}
