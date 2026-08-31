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
    Double distanceFromHospitalMeters,
    // 요청한 lang의 이름 번역이 실제로 있는지(ko는 항상 true) — WellnessDtoMapper.isTranslated 참고.
    boolean translated
) {
}
