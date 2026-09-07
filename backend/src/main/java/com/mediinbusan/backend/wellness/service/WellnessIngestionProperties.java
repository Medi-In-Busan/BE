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
    // detailIntro2(영업시간·휴무일·대표메뉴·주차) 보강 호출 수. detailCommon2와는 별개의 오퍼레이션이라
    // 일일 트래픽도 따로 잡히므로 한도도 따로 둔다 — 한쪽이 소진돼도 다른 쪽은 계속 채울 수 있게 한다.
    // 0이면 방문 정보 보강을 통째로 건너뛴다(그동안의 동작과 같아진다).
    int tourApiIntroFetchLimit,
    // detailCommon2/detailIntro2를 몇 개씩 동시에 호출할지. 1이면 예전처럼 완전히 순차 실행이라,
    // 공공 API가 동시 호출을 거부하기 시작하면 이 값만 1로 낮춰 되돌릴 수 있다(재배포 불필요).
    // 일일 한도와는 무관하다 — 총 호출 수는 그대로고 걸리는 시간만 줄어든다.
    int tourApiFetchConcurrency,
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
