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

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
