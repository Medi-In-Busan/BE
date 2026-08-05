package com.mediinbusan.backend.hospital.dto;

import java.util.List;

/** 리스트/검색 결과 카드용 경량 응답. description·businessHours 같은 무거운 필드는 상세 응답에서만 내려준다. */
public record HospitalListItemResponse(
    String regNo,
    String name,
    String institutionType,
    String address,
    Double latitude,
    Double longitude,
    String phone,
    List<String> specialties
) {
}
