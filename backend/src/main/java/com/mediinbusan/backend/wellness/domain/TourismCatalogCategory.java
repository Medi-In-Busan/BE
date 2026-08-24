package com.mediinbusan.backend.wellness.domain;

public enum TourismCatalogCategory {
    PLACES_KO("부산 관광지", "한국어 관광지·음식점·숙박·쇼핑 정보를 둘러봅니다."),
    PLACES_EN("Busan in English", "영문 관광정보를 확인합니다."),
    PLACES_JA("日本語の釜山観光", "일문 관광정보를 확인합니다."),
    PLACES_ZH("中文釜山旅游", "중문 관광정보를 확인합니다."),
    ACCESSIBLE("무장애 관광", "이동 편의 정보를 포함한 부산 관광지를 확인합니다."),
    RELATED("함께 둘러보기", "선택한 지역과 함께 찾는 관광지를 확인합니다."),
    CROWDING("관광지 혼잡도", "관광지별 예상 혼잡 정보를 확인합니다."),
    WALKING("부산 걷기 코스", "두루누비의 부산 걷기 여행길을 확인합니다.");

    private final String title;
    private final String description;

    TourismCatalogCategory(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }
}
