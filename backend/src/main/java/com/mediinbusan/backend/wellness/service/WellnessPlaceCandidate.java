package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.hospital.domain.Coordinates;
import com.mediinbusan.backend.wellness.domain.WellnessPlaceType;

import java.time.LocalDate;

record WellnessPlaceCandidate(
    String contentId,
    String name,
    WellnessPlaceType placeType,
    String address,
    Coordinates coordinates,
    String imageUrl,
    String description,
    String phoneNumber,
    LocalDate modifiedDate
) {
    boolean isValid() {
        return hasText(contentId)
            && hasText(name)
            && placeType != null
            && hasText(address);
    }

    /**
     * areaBasedList2엔 전화번호·상세설명이 없어, detailCommon2로 따로 받아온 값을 채워 넣을 때 쓴다.
     * 두 값 다 null이면(그 항목만 detailCommon2 조회를 못 했거나 실제로 비어있는 경우) 기존 값을 유지한다.
     */
    WellnessPlaceCandidate withDetail(String phoneNumber, String description) {
        return new WellnessPlaceCandidate(
            contentId,
            name,
            placeType,
            address,
            coordinates,
            imageUrl,
            description != null ? description : this.description,
            phoneNumber != null ? phoneNumber : this.phoneNumber,
            modifiedDate
        );
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
