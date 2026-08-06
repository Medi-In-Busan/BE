package com.mediinbusan.backend.wellness.dto;

import com.mediinbusan.backend.hospital.domain.Coordinates;
import com.mediinbusan.backend.wellness.domain.WellnessPlace;

public final class WellnessDtoMapper {

    private WellnessDtoMapper() {
    }

    public static WellnessPlaceResponse toPlaceResponse(WellnessPlace place, Double distanceFromHospitalMeters) {
        Coordinates coordinates = place.getCoordinates();
        return new WellnessPlaceResponse(
            place.getContentId(),
            place.getName(),
            place.getPlaceType().name(),
            place.getAddress(),
            coordinates != null ? coordinates.getLatitude() : null,
            coordinates != null ? coordinates.getLongitude() : null,
            place.getImageUrl(),
            place.getDescription(),
            place.getPhoneNumber(),
            place.getModifiedDate() != null ? place.getModifiedDate().toString() : null,
            distanceFromHospitalMeters
        );
    }
}
