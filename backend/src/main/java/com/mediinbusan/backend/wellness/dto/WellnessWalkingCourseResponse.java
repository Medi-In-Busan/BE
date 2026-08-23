package com.mediinbusan.backend.wellness.dto;

public record WellnessWalkingCourseResponse(
    String id,
    String name,
    String district,
    Double distanceKm,
    Integer durationMinutes,
    String difficulty,
    String summary,
    String gpxUrl
) {
}
