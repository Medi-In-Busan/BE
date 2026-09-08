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
    String placeCategory,
    // 아래 6개는 상세 화면의 "방문 정보" 칸이다 — TourAPI detailIntro2와 부산맛집정보에서 온 원문
    // (한국어)이고, 소스가 안 주는 칸은 null이다. 값이 없는 행은 화면에서 통째로 빠진다.
    String businessHours,
    String restDate,
    String signatureMenu,
    String usageFee,
    String parkingInfo,
    String homepageUrl
) {
}
