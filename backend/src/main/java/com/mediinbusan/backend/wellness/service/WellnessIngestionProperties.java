package com.mediinbusan.backend.wellness.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "wellness.ingestion")
public record WellnessIngestionProperties(
    String tourApiBaseUrl,
    String accessibleTourismBaseUrl,
    String relatedTourismBaseUrl,
    String crowdingBaseUrl,
    String walkingBaseUrl,
    String englishTourismBaseUrl,
    String japaneseTourismBaseUrl,
    String chineseTourismBaseUrl,
    String tourApiServiceKey,
    String kakaoLocalBaseUrl,
    String kakaoNaviBaseUrl,
    String kakaoRestApiKey,
    int tourApiRowsPerType,
    int kakaoRowsPerKeyword,
    List<String> kakaoKeywords
) {
    public boolean hasTourApiKey() {
        return hasText(tourApiServiceKey);
    }

    public boolean hasKakaoKey() {
        return hasText(kakaoRestApiKey);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
