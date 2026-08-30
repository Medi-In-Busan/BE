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
    // TourAPI contenttypeid(12=관광지, 14=문화시설, 25=여행코스, 28=레포츠, 32=숙박, 38=쇼핑,
    // 39=음식점). scalarDetails()의 8개 캡에 걸려 details 맵에서 누락될 수 있어 별도 필드로 뽑는다.
    String categoryCode,
    Map<String, String> details
) {
}
