package com.mediinbusan.backend.wellness.domain;

/**
 * {@link WellnessPlaceType}보다 한 단계 아래의 세부 분류.
 *
 * 수집되는 장소는 TourAPI contentTypeId 기준 네 종류(12 관광지 / 32 숙박 / 38 쇼핑 / 39 음식점)뿐이라,
 * Android 지도에서 "관광" 하나로 묶이는 덩어리 안에 관광지·숙박·쇼핑이 전부 섞여 있었다. 그중에서도
 * 쇼핑은 백화점과 전통시장과 면세점이 한 이름으로 나와서, 외국인 의료관광객이 목록만 보고 어디를
 * 갈지 고를 단서가 없었다.
 *
 * <p><b>값은 Android {@code data/place/PlaceCategory.kt}와 동일하게 유지해야 한다</b> —
 * {@code MedicalSpecialty} ↔ {@code MedicalCategory}와 같은 규칙이다(backend/CLAUDE.md 참고).
 * 한쪽만 바꾸면 Android가 모르는 이름을 받아 조용히 {@link #OTHER}로 떨어진다.
 *
 * <p>지금은 쇼핑(cat1=A04) 하위만 채운다. cat3 원본 코드는 모든 타입에 대해 저장되므로
 * (wellness_place.category_code), 관광지·숙박 세분화가 필요해지면 값과
 * {@code WellnessDtoMapper.categoryOf}의 분기만 늘리면 된다 — 재수집은 필요 없다.
 */
public enum WellnessPlaceCategory {
    /** 백화점 */
    DEPARTMENT_STORE,
    /** 면세점(사후면세점 포함) */
    DUTY_FREE,
    /** 전통시장 — 5일장과 상설시장을 하나로 묶는다. 방문자 입장에서 둘을 가를 실익이 없다. */
    TRADITIONAL_MARKET,
    /** 대형마트 */
    LARGE_MART,
    /** 전문매장·상가 */
    SPECIALTY_STORE,
    /** 특산물 판매점 */
    LOCAL_PRODUCTS,
    /** 공예·공방 */
    CRAFT_WORKSHOP,
    /** 코드가 없거나(아직 재수집 전) 위 어디에도 해당하지 않는 경우. */
    OTHER
}
