package com.mediinbusan.backend.hospital.dto;

import java.util.List;

/** 임의 좌표(latitude/longitude) 기준 반경 검색 결과. HospitalListItemResponse + 그 좌표로부터의 거리(m). */
public record HospitalNearbyResponse(
    String regNo,
    String name,
    String institutionType,
    String address,
    Double latitude,
    Double longitude,
    String phone,
    List<String> specialties,
    Double distanceMeters
) {
}
