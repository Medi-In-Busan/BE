package com.mediinbusan.backend.wellness.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * "부산 관광지"(PLACES_KO) 목록 화면을 서버 기동 직후 처음 여는 사용자도 TourismPlacesCache HIT을
 * 보게 하려고, 기본 지역(HAEUNDAE) 1페이지를 미리 한 번 요청해 캐시를 채워둔다. WellnessDataInitializer와
 * 같은 패턴 — 외부 API 실패가 서버 기동 자체를 막아서는 안 된다.
 */
@Component
public class TourismPlacesCacheWarmup implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TourismPlacesCacheWarmup.class);

    // Android(TourismCatalogViewModel.PAGE_SIZE)가 실제로 요청하는 값과 맞춰야 캐시가 그대로
    // 재사용된다 — 다르면 warm-up이 채운 키를 아무도 못 찾고 그냥 낭비된다.
    private static final int WARMUP_PAGE_SIZE = 40;

    private final WellnessTourismGatewayService gateway;
    private final WellnessIngestionProperties properties;

    public TourismPlacesCacheWarmup(WellnessTourismGatewayService gateway, WellnessIngestionProperties properties) {
        this.gateway = gateway;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.hasTourApiKey()) {
            return;
        }
        try {
            gateway.places(
                WellnessTourismGatewayService.Language.KO,
                BusanTourismCodes.District.HAEUNDAE,
                null,
                1,
                WARMUP_PAGE_SIZE
            );
            log.info("Warmed up TourismPlacesCache for PLACES_KO / HAEUNDAE / page 1");
        } catch (RuntimeException exception) {
            log.warn("Could not warm up TourismPlacesCache; first request will fall through to a live TourAPI call", exception);
        }
    }
}
