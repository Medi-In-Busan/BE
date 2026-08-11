package com.mediinbusan.backend.hospital.dto;

import java.util.List;

/** description/businessHours는 요청 lang에 맞는 값 하나만 내려준다(없으면 ko로 폴백). */
public record HospitalDetailResponse(
    String regNo,
    String name,
    String institutionType,
    String address,
    Double latitude,
    Double longitude,
    String phone,
    String website,
    String businessHours,
    String description,
    List<String> specialties,
    List<String> targetCountries
) {
}
