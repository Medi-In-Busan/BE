package com.mediinbusan.backend.wellness.dto;

public record WellnessIngestionResponse(
    int tourApiCandidates,
    int kakaoCandidates,
    int inserted,
    int updated,
    int skipped,
    long totalPlaces
) {
}
