package com.mediinbusan.backend.wellness.dto;

import com.mediinbusan.backend.wellness.domain.TourismCatalogCategory;

import java.time.Instant;
import java.util.List;

public record TourismCatalogResponse(
    TourismCatalogCategory category,
    String title,
    String description,
    String source,
    Instant retrievedAt,
    List<TourismCatalogItemResponse> items
) {
}
