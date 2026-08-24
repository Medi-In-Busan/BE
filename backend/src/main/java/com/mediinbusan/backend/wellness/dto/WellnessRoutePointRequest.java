package com.mediinbusan.backend.wellness.dto;

public record WellnessRoutePointRequest(
    String name,
    double latitude,
    double longitude
) {
}

