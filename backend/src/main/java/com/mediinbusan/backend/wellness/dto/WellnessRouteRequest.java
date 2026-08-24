package com.mediinbusan.backend.wellness.dto;

import java.util.List;

public record WellnessRouteRequest(
    WellnessRoutePointRequest origin,
    List<WellnessRoutePointRequest> stops,
    WellnessTravelMode mode
) {
}
