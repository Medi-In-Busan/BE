package com.mediinbusan.backend.wellness.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mediinbusan.backend.wellness.dto.TourismExternalResponse;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * WellnessTourismGatewayService.places(...)(TourAPI 원본 관광지 목록, "부산 관광지" 카테고리
 * PLACES_KO/EN/JA/ZH 전용) 응답만을 위한 TTL 기반 인메모리 캐시. 개인화 추천 결과는 이 캐시의
 * 대상이 아니다(그건 Android RecommendTourismCatalogUseCase가 클라이언트에서 계산한다) — 여기엔
 * TourAPI가 내려준 원본 목록 그대로만 들어간다.
 *
 * 관광지 목록은 자주 바뀌지 않는 데이터라 TTL을 6시간으로 넉넉히 잡았다. 다른 카테고리
 * (ACCESSIBLE/RELATED/HUBS/CROWDING/PHOTOS/WALKING/AUDIO)는 이 캐시를 거치지 않는다 —
 * WellnessTourismGatewayService의 다른 메서드는 그대로 두고 places(...) 하나만 감쌌다.
 *
 * page/contentTypeId 조합이 임의로 늘어날 수 있어(요청 파라미터에 상한이 없다) Caffeine의
 * maximumSize로 항목 수를 제한하고 expireAfterWrite로 TTL이 지난 항목을 자동 회수한다 —
 * ConcurrentHashMap을 직접 쓰던 이전 버전은 만료 항목이 재요청 전까지 계속 남아있었다.
 */
@Component
class TourismPlacesCache {

    private static final Duration TTL = Duration.ofHours(6);
    private static final long MAX_ENTRIES = 500;

    private final Cache<String, TourismExternalResponse> store = Caffeine.newBuilder()
        .expireAfterWrite(TTL)
        .maximumSize(MAX_ENTRIES)
        .build();

    /**
     * cacheKey 하나당 단 하나의 로더만 실행되는 single-flight getOrLoad. Caffeine의
     * Cache.get(key, mappingFunction)은 같은 키에 대한 로딩을 원자적으로 직렬화한다 — TTL 만료
     * 직후나 cold start 때 같은 페이지를 보는 여러 요청이 동시에 몰려도 TourAPI 호출은 한 번만
     * 나가고 나머지 요청은 그 결과를 그대로 재사용한다. 다른 cacheKey에 대한 호출은 이 락과
     * 무관하게 동시에 진행된다.
     */
    TourismExternalResponse getOrLoad(String key, Supplier<TourismExternalResponse> loader) {
        return store.get(key, k -> loader.get());
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
}
