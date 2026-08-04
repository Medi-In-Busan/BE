package com.mediinbusan.backend.hospital.dto;

import java.util.List;

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
    String descriptionKo,
    String descriptionEn,
    List<String> specialties,
    List<String> targetCountries
) {
}
