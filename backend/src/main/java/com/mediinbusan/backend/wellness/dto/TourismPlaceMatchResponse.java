package com.mediinbusan.backend.wellness.dto;

public record TourismPlaceMatchResponse(boolean matched, TourismCatalogItemResponse item) {
    public static TourismPlaceMatchResponse notFound() {
        return new TourismPlaceMatchResponse(false, null);
    }
}
