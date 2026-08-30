package com.mediinbusan.backend.wellness.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wellness.ingestion")
public record WellnessIngestionProperties(
    String tourApiBaseUrl,
    String accessibleTourismBaseUrl,
    String relatedTourismBaseUrl,
    String hubTourismBaseUrl,
    String crowdingBaseUrl,
    String photoBaseUrl,
    String walkingBaseUrl,
    String audioBaseUrl,
    String englishTourismBaseUrl,
    String japaneseTourismBaseUrl,
    String chineseTourismBaseUrl,
    String tourApiServiceKey,
    String kakaoLocalBaseUrl,
    String kakaoNaviBaseUrl,
    String kakaoRestApiKey,
    String busanFoodApiBaseUrl,
    String busanFoodApiServiceKey,
    int tourApiRowsPerPage,
    // areaBasedList2는 전화번호를 안 주기 때문에 detailCommon2를 콘텐츠ID별로 추가 호출해서 채운다.
    // 이 오퍼레이션은 areaBasedList2와 별개의 일일 트래픽 한도를 쓰므로(활용신청 페이지 기준 개발계정
    // 1,000/일), 후보 전체를 다 부르지 않고 이 개수만큼만 부른다 — 0이면 전화번호 보강을 아예 건너뛴다.
    int tourApiDetailFetchLimit,
    int busanFoodRowsPerPage
) {
    public boolean hasTourApiKey() {
        return hasText(tourApiServiceKey);
    }

    // 웰니스 장소 수집(WellnessIngestionService)에는 더 이상 카카오 로컬 검색을 쓰지 않는다 — 이
    // 메서드는 카카오 길찾기(KakaoMobilityRouteService)가 여전히 쓴다(kakaoRestApiKey/kakaoLocalBaseUrl/
    // kakaoNaviBaseUrl은 그쪽 전용으로 남겨둔다).
    public boolean hasKakaoKey() {
        return hasText(kakaoRestApiKey);
    }

    public boolean hasBusanFoodApiKey() {
        return hasText(busanFoodApiServiceKey);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
