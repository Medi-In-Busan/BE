package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.wellness.domain.TourismCatalogCategory;
import com.mediinbusan.backend.wellness.domain.WellnessExternalSnapshot;
import com.mediinbusan.backend.wellness.dto.TourismCatalogResponse;
import com.mediinbusan.backend.wellness.repository.WellnessExternalSnapshotRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TourismCatalogCrowdingCacheTest {
    private final WellnessTourismGatewayService gateway = mock(WellnessTourismGatewayService.class);
    private final WellnessExternalSnapshotRepository snapshotRepository = mock(WellnessExternalSnapshotRepository.class);
    private final TourismCatalogTranslationService translationService = passThroughTranslationService();
    private final TourismCatalogService service = new TourismCatalogService(gateway, snapshotRepository, translationService);

    private static TourismCatalogTranslationService passThroughTranslationService() {
        TourismCatalogTranslationService service = mock(TourismCatalogTranslationService.class);
        when(service.localize(any(), anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        return service;
    }

    @Test
    void todaySnapshotSkipsExternalCrowdingApi() {
        WellnessExternalSnapshot snapshot = snapshot(LocalDate.now());
        when(snapshotRepository.findBySnapshotKey(todayKey())).thenReturn(Optional.of(snapshot));

        TourismCatalogResponse response = service.getCatalog(TourismCatalogCategory.CROWDING, null, null);

        assertThat(response.items()).hasSize(2);
        assertThat(response.items()).extracting(item -> item.title())
            .containsExactly("광안리해수욕장", "해운대해수욕장");
        assertThat(response.items().get(1).details()).containsEntry("cnctrRate", "82.4");
        verify(gateway, never()).crowding(any());
    }

    @Test
    void latestRealSnapshotIsReturnedWhenDailyRefreshFails() {
        WellnessExternalSnapshot previousSnapshot = snapshot(LocalDate.now().minusDays(1));
        when(snapshotRepository.findBySnapshotKey(todayKey())).thenReturn(Optional.empty());
        when(gateway.crowding(any())).thenThrow(new IllegalStateException("daily quota exceeded"));
        when(snapshotRepository.findTopBySourceAndScopeOrderBySyncedAtDesc("crowding-catalog", "BUSAN"))
            .thenReturn(Optional.of(previousSnapshot));

        TourismCatalogResponse response = service.getCatalog(TourismCatalogCategory.CROWDING, null, null);

        assertThat(response.items()).extracting(item -> item.title())
            .containsExactly("광안리해수욕장", "해운대해수욕장");
        verify(snapshotRepository, never()).save(any());
    }

    private static WellnessExternalSnapshot snapshot(LocalDate date) {
        return new WellnessExternalSnapshot(
            "crowding-catalog:BUSAN:" + date,
            "crowding-catalog",
            "BUSAN",
            "BUSAN",
            date.toString(),
            "관광지 혼잡도",
            null,
            null,
            cachedItems()
        );
    }

    private static String cachedItems() {
        String today = LocalDate.now().toString().replace("-", "");
        String tomorrow = LocalDate.now().plusDays(1).toString().replace("-", "");
        return """
            [
              {
                "id":"crowding-1",
                "title":"해운대해수욕장",
                "subtitle":"82.4",
                "address":null,
                "imageUrl":null,
                "latitude":null,
                "longitude":null,
                "details":{"baseYmd":"%s","cnctrRate":"82.4","signguNm":"해운대구","imageLookupAttempted":"true"}
              },
              {
                "id":"crowding-2",
                "title":"해운대해수욕장",
                "subtitle":"99.9",
                "address":null,
                "imageUrl":null,
                "latitude":null,
                "longitude":null,
                "details":{"baseYmd":"%s","cnctrRate":"99.9","signguNm":"해운대구","imageLookupAttempted":"true"}
              },
              {
                "id":"crowding-3",
                "title":"광안리해수욕장",
                "subtitle":"90.1",
                "address":null,
                "imageUrl":null,
                "latitude":null,
                "longitude":null,
                "details":{"baseYmd":"%s","cnctrRate":"90.1","signguNm":"수영구","imageLookupAttempted":"true"}
              }
            ]
            """.formatted(today, tomorrow, today);
    }

    private static String todayKey() {
        return "crowding-catalog:BUSAN:" + LocalDate.now();
    }
}
