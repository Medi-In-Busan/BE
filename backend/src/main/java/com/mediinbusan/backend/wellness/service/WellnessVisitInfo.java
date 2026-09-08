package com.mediinbusan.backend.wellness.service;

/**
 * 장소 상세 화면에 그대로 뿌리는 "방문 정보" 묶음.
 *
 * 소스별로 필드 이름이 전부 다르고(TourAPI detailIntro2는 콘텐츠 타입마다 또 다르다), 어떤 소스는
 * 일부만 준다 — 그 차이를 여기서 한 번 흡수해서 아래로는 항상 같은 6칸으로만 흐르게 한다.
 *
 * <p>값은 API 원문(한국어)을 손대지 않고 그대로 담는다. 이름·주소·설명과 달리 다국어 미러 API에서
 * 따로 받아오지 않으므로 EN/JA/ZH 사용자에게도 한국어로 보인다 — 영업시간("매일 09:00~18:00")이나
 * 요금처럼 숫자가 핵심인 값이라 원문 그대로도 읽히는 편이고, 번역을 붙이려면 언어별 detailIntro2를
 * 3배로 더 호출해야 해서(일일 트래픽 한도) 지금은 하지 않는다.
 */
record WellnessVisitInfo(
    /** 관광지 usetime / 쇼핑 opentime / 음식점 opentimefood / 부산맛집 USAGE_DAY_WEEK_AND_TIME. */
    String businessHours,
    /** 휴무일 — 관광지 restdate / 쇼핑 restdateshopping / 음식점 restdatefood. */
    String restDate,
    /** 대표메뉴 — 음식점 firstmenu / 부산맛집 RPRSNTV_MENU. 음식점이 아니면 항상 null이다. */
    String signatureMenu,
    /**
     * 이용요금 — 문화시설/레포츠 usefee. 관광지(12)·음식점(39)·숙박(32)·쇼핑(38) detailIntro2에는
     * 이 필드 자체가 없어서, 수집(ingest) 경로로 들어온 장소에서는 사실상 항상 null이다 — 문화시설·
     * 레포츠가 섞여 들어오는 관광 카탈로그 상세 경로에서만 실제로 채워진다.
     */
    String usageFee,
    /** 주차 안내 — 관광지 parking / 쇼핑 parkingshopping / 음식점 parkingfood. */
    String parkingInfo,
    /** 홈페이지 — detailCommon2 homepage / 부산맛집 HOMEPAGE_URL. */
    String homepageUrl
) {
    static final WellnessVisitInfo EMPTY = new WellnessVisitInfo(null, null, null, null, null, null);

    boolean isEmpty() {
        return !hasText(businessHours)
            && !hasText(restDate)
            && !hasText(signatureMenu)
            && !hasText(usageFee)
            && !hasText(parkingInfo)
            && !hasText(homepageUrl);
    }

    /**
     * [other]의 값을 우선 쓰되, 비어 있는 칸은 이쪽 값을 유지한다.
     *
     * 한 장소의 방문 정보가 두 소스에서 조각조각 오기 때문이다 — 예를 들어 부산맛집에서 온 식당은
     * 영업시간·대표메뉴를 이미 갖고 있고, 같은 가게가 TourAPI에도 있으면 detailIntro2가 휴무일·주차를
     * 더해준다. 덮어쓰기로 처리하면 나중에 온 소스가 안 주는 칸이 통째로 지워진다.
     */
    WellnessVisitInfo merge(WellnessVisitInfo other) {
        if (other == null || other.isEmpty()) {
            return this;
        }
        return new WellnessVisitInfo(
            pick(other.businessHours, businessHours),
            pick(other.restDate, restDate),
            pick(other.signatureMenu, signatureMenu),
            pick(other.usageFee, usageFee),
            pick(other.parkingInfo, parkingInfo),
            pick(other.homepageUrl, homepageUrl)
        );
    }

    private static String pick(String preferred, String fallback) {
        return hasText(preferred) ? preferred : fallback;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
