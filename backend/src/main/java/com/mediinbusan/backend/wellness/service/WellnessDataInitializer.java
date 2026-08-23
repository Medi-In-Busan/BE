package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.wellness.repository.WellnessPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Keeps a fresh deployment from serving an empty nearby screen. Existing data is never replaced
 * during startup, and external API failures do not prevent the backend from starting.
 */
@Component
public class WellnessDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(WellnessDataInitializer.class);

    private final WellnessPlaceRepository placeRepository;
    private final WellnessIngestionProperties properties;
    private final WellnessIngestionService ingestionService;

    public WellnessDataInitializer(
        WellnessPlaceRepository placeRepository,
        WellnessIngestionProperties properties,
        WellnessIngestionService ingestionService
    ) {
        this.placeRepository = placeRepository;
        this.properties = properties;
        this.ingestionService = ingestionService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (placeRepository.count() > 0 || (!properties.hasTourApiKey() && !properties.hasKakaoKey())) {
            return;
        }

        try {
            var result = ingestionService.ingest();
            log.info("Initialized nearby wellness data from public APIs: {} places", result.totalPlaces());
        } catch (RuntimeException exception) {
            log.warn("Could not initialize nearby wellness data; manual POST /api/wellness/ingest remains available", exception);
        }
    }
}
