package com.mediinbusan.backend.wellness.dto;

import java.util.Map;

public record TourismCatalogItemResponse(
    String id,
    String title,
    String subtitle,
    String address,
    String imageUrl,
    Double latitude,
    Double longitude,
    Map<String, String> details
) {
}
