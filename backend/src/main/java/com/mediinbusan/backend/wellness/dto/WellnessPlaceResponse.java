package com.mediinbusan.backend.wellness.dto;

public record WellnessPlaceResponse(
    String contentId,
    String name,
    String contentTypeId,
    String address,
    Double latitude,
    Double longitude,
    String imageUrl,
    String description,
    String phoneNumber,
    String modifiedDate,
    Double distanceFromHospitalMeters
) {
}
