package com.mediinbusan.backend.wellness.dto;

import com.mediinbusan.backend.hospital.domain.Coordinates;
import com.mediinbusan.backend.wellness.domain.WellnessPlace;

public final class WellnessDtoMapper {

    private WellnessDtoMapper() {
    }

    public static WellnessPlaceResponse toPlaceResponse(WellnessPlace place, Double distanceFromHospitalMeters, String lang) {
        Coordinates coordinates = place.getCoordinates();
        return new WellnessPlaceResponse(
            place.getContentId(),
            nameFor(place, lang),
            place.getPlaceType().name(),
            addressFor(place, lang),
            coordinates != null ? coordinates.getLatitude() : null,
            coordinates != null ? coordinates.getLongitude() : null,
            place.getImageUrl(),
            descriptionFor(place, lang),
            place.getPhoneNumber(),
            place.getModifiedDate() != null ? place.getModifiedDate().toString() : null,
            distanceFromHospitalMeters,
            isTranslated(place, lang)
        );
    }

    // lang=ko는 원문 자체가 한국어라 항상 "번역됨"으로 본다. 다른 언어는 이름 번역 컬럼이 실제로
    // 채워져 있을 때만 true — 지도 "번역된 장소만" 필터(Android MapUiState.visiblePlaces)가 이
    // 값을 그대로 쓴다. 이름 기준으로만 판정한다(주소·설명은 매칭됐어도 detailCommon2가 실패하면
    // 비어있을 수 있어 이름보다 신뢰도가 낮음 — WellnessIngestionService.applyTourTranslationsByLocation 참고).
    private static boolean isTranslated(WellnessPlace place, String lang) {
        return switch (lang) {
            case "en" -> place.getNameEn() != null;
            case "zh" -> place.getNameZh() != null;
            case "ja" -> place.getNameJa() != null;
            default -> true;
        };
    }

    // 번역이 없는 장소(부산맛집 소스가 아니거나 아직 번역이 안 붙은 경우)가 대부분이라, 요청 언어의
    // 값이 비어 있으면 ko(원문)로 폴백한다 — HospitalDtoMapper.descriptionFor/businessHoursFor와 같은 규칙.
    private static String nameFor(WellnessPlace place, String lang) {
        String translated = switch (lang) {
            case "en" -> place.getNameEn();
            case "zh" -> place.getNameZh();
            case "ja" -> place.getNameJa();
            default -> place.getName();
        };
        return translated != null ? translated : place.getName();
    }

    private static String addressFor(WellnessPlace place, String lang) {
        String translated = switch (lang) {
            case "en" -> place.getAddressEn();
            case "zh" -> place.getAddressZh();
            case "ja" -> place.getAddressJa();
            default -> place.getAddress();
        };
        return translated != null ? translated : place.getAddress();
    }

    private static String descriptionFor(WellnessPlace place, String lang) {
        String translated = switch (lang) {
            case "en" -> place.getDescriptionEn();
            case "zh" -> place.getDescriptionZh();
            case "ja" -> place.getDescriptionJa();
            default -> place.getDescription();
        };
        return translated != null ? translated : place.getDescription();
    }
}
