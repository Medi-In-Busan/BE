package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.wellness.dto.TourismExternalResponse;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WellnessTourismGatewayService.places(...)(TourAPI 원본 관광지 목록, "부산 관광지" 카테고리
 * PLACES_KO/EN/JA/ZH 전용) 응답만을 위한 TTL 기반 인메모리 캐시. 개인화 추천 결과는 이 캐시의
 * 대상이 아니다(그건 Android RecommendTourismCatalogUseCase가 클라이언트에서 계산한다) — 여기엔
 * TourAPI가 내려준 원본 목록 그대로만 들어간다.
 *
 * 관광지 목록은 자주 바뀌지 않는 데이터라 TTL을 6시간으로 넉넉히 잡았다. 다른 카테고리
 * (ACCESSIBLE/RELATED/HUBS/CROWDING/PHOTOS/WALKING/AUDIO)는 이 캐시를 거치지 않는다 —
 * WellnessTourismGatewayService의 다른 메서드는 그대로 두고 places(...) 하나만 감쌌다.
 */
@Component
class TourismPlacesCache {

    private static final Duration TTL = Duration.ofHours(6);

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    Optional<TourismExternalResponse> get(String key) {
        Entry entry = store.get(key);
        if (entry == null) {
            return Optional.empty();
        }
        if (entry.isExpired()) {
            // 만료된 항목은 다음 MISS 때 put()이 덮어쓸 것이므로 여기서 굳이 remove하지 않는다.
            return Optional.empty();
        }
        return Optional.of(entry.value());
    }

    void put(String key, TourismExternalResponse value) {
        store.put(key, new Entry(value, Instant.now().plus(TTL)));
    }

    /** category, language, district, contentTypeId, page, pageSize 등 TourAPI 결과에 영향을 주는
     * 모든 파라미터를 그대로 이어붙인 키 — 하나라도 다르면 다른 캐시 항목이어야 한다. */
    static String key(
        WellnessTourismGatewayService.Language language,
        BusanTourismCodes.District district,
        String contentTypeId,
        int pageNo,
        int pageSize
    ) {
        return String.join(
            "|",
            language.name(),
            district != null ? district.name() : "ALL",
            contentTypeId != null ? contentTypeId : "-",
            String.valueOf(pageNo),
            String.valueOf(pageSize)
        );
    }

    private record Entry(TourismExternalResponse value, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
