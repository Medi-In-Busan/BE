package com.mediinbusan.backend.wellness.dto;

import java.util.List;

public record WellnessRouteResponse(
    int distanceMeters,
    int durationSeconds,
    List<WellnessRouteCoordinateResponse> path,
    List<WellnessRouteSectionResponse> sections
) {
}

