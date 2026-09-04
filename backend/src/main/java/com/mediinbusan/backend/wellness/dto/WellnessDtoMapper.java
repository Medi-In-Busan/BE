package com.mediinbusan.backend.wellness.dto;

import com.mediinbusan.backend.hospital.domain.Coordinates;
import com.mediinbusan.backend.wellness.domain.WellnessPlace;
import com.mediinbusan.backend.wellness.domain.WellnessPlaceCategory;

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
            isTranslated(place, lang),
            categoryOf(place.getCategoryCode()).name()
        );
    }

    /**
     * TourAPI cat3 코드를 앱이 아는 세부 분류로 옮긴다.
     *
     * 수집 시점이 아니라 여기(응답 생성)에서 변환하는 이유: 아래 코드 표가 틀린 걸 나중에 발견해도
     * 코드만 고쳐 배포하면 끝난다(원본 cat3는 wellness_place.category_code에 그대로 남아 있다).
     * 수집 시점에 변환해 저장했다면 TourAPI를 일일 트래픽 한도를 써가며 다시 전부 긁어야 한다.
     *
     * <p>코드-분류 대응은 한국관광공사 서비스 분류체계(data.visitkorea.or.kr/resource/A04) 기준이다:
     * A04010100 쇼핑센터 / A04010200 시장 / A04010300 백화점 / A04010400 대형마트 / A04010500 면세점 /
     * A04010600 외국인전용 기념품점 / A04010700 전통공예품점 / A04010900 특산품판매점 / A04011000 사후면세점.
     * 표에 없는 코드는 전부 {@link WellnessPlaceCategory#OTHER}로 떨어지므로, 새 코드가 생겨도 목록이
     * 깨지지는 않고 세분화만 안 될 뿐이다.
     */
    static WellnessPlaceCategory categoryOf(String cat3) {
        if (cat3 == null || cat3.isBlank()) {
            return WellnessPlaceCategory.OTHER;
        }
        return switch (cat3) {
            // 시장(A04010200)만 전통시장이다. 쇼핑센터(A04010100)는 상가 성격이라 전문매장·상가로 묶는다
            // — 별도 분류를 만들면 Android PlaceCategory와 4개 언어 라벨까지 같이 늘려야 하는데,
            // 방문자가 목록에서 "쇼핑센터"와 "전문매장·상가"를 가려 고를 실익이 크지 않다.
            case "A04010200" -> WellnessPlaceCategory.TRADITIONAL_MARKET;
            case "A04010300" -> WellnessPlaceCategory.DEPARTMENT_STORE;
            case "A04010400" -> WellnessPlaceCategory.LARGE_MART;
            // 면세점(A04010500)과 사후면세점(A04011000)을 한 묶음으로 — enum 주석의 정의 그대로다.
            case "A04010500", "A04011000" -> WellnessPlaceCategory.DUTY_FREE;
            case "A04010100", "A04010600" -> WellnessPlaceCategory.SPECIALTY_STORE;
            case "A04010700" -> WellnessPlaceCategory.CRAFT_WORKSHOP;
            case "A04010900" -> WellnessPlaceCategory.LOCAL_PRODUCTS;
            default -> WellnessPlaceCategory.OTHER;
        };
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
