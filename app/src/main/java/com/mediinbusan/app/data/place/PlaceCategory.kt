package com.mediinbusan.app.data.place

/**
 * [PlaceType]보다 한 단계 아래의 세부 분류.
 *
 * 수집되는 장소는 TourAPI contentTypeId 기준 네 종류(12 관광지 / 32 숙박 / 38 쇼핑 / 39 음식점)뿐이라,
 * 지도(S-08)에서 "관광" 하나로 묶이는 덩어리 안에 관광지·숙박·쇼핑이 전부 섞여 있었다. 그중에서도
 * 쇼핑은 백화점과 전통시장과 면세점이 한 이름으로 나와서, 목록만 보고 어디를 갈지 고를 단서가 없었다.
 *
 * **값은 백엔드 `wellness/domain/WellnessPlaceCategory.java`와 동일하게 유지해야 한다** —
 * `MedicalCategory` ↔ `MedicalSpecialty`와 같은 규칙이다(CLAUDE.md §7). 한쪽만 바꾸면 모르는 이름을
 * 받아 조용히 [OTHER]로 떨어진다.
 *
 * 지금은 쇼핑 하위만 채워진다. 분류의 근거가 되는 TourAPI cat3 코드는 백엔드가 장소 종류와 무관하게
 * 전부 저장하므로, 관광지·숙박 세분화가 필요해지면 양쪽 enum과 백엔드 매핑만 늘리면 된다.
 */
enum class PlaceCategory {
    /** 백화점 */
    DEPARTMENT_STORE,

    /** 면세점(사후면세점 포함) */
    DUTY_FREE,

    /** 전통시장 — 5일장과 상설시장을 하나로 묶는다. */
    TRADITIONAL_MARKET,

    /** 대형마트 */
    LARGE_MART,

    /** 전문매장·상가 */
    SPECIALTY_STORE,

    /** 특산물 판매점 */
    LOCAL_PRODUCTS,

    /** 공예·공방 */
    CRAFT_WORKSHOP,

    /**
     * 세부 분류를 모르는 경우. 백엔드가 아직 재수집하지 않은 장소, TourAPI가 아닌 소스에서 온 장소,
     * 그리고 아직 세분화 대상이 아닌 종류(관광지·숙박·음식점)가 전부 여기로 온다 — 즉 **기본값이자
     * 대다수**다. 화면에서는 이 값일 때 세부 분류를 표시하지 않고 [PlaceType] 라벨로 되돌아간다.
     */
    OTHER
}

/**
 * 백엔드가 내려준 `placeCategory` 문자열을 [PlaceCategory]로 옮긴다. 모르는 이름(백엔드가 먼저
 * 값을 추가한 경우)은 [PlaceCategory.OTHER]로 떨어뜨려, 구버전 앱이 새 분류 때문에 깨지지 않게 한다.
 */
fun String?.toPlaceCategory(): PlaceCategory =
    PlaceCategory.entries.firstOrNull { it.name == this } ?: PlaceCategory.OTHER
