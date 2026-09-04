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
    boolean translated,
    // WellnessPlaceCategory 이름. contentTypeId(=WellnessPlaceType)보다 한 단계 자세한 분류로,
    // "쇼핑" 안의 백화점/전통시장/면세점을 가른다. 아직 재수집 전이거나 분류를 모르는 장소는 "OTHER".
    String placeCategory
) {
}
