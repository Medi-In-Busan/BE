package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.document.client.PapagoTranslationApiException;
import com.mediinbusan.backend.document.client.PapagoTranslationClient;
import com.mediinbusan.backend.wellness.domain.TourismCatalogCategory;
import com.mediinbusan.backend.wellness.domain.TourismCatalogTranslation;
import com.mediinbusan.backend.wellness.dto.TourismCatalogItemResponse;
import com.mediinbusan.backend.wellness.dto.TourismCatalogResponse;
import com.mediinbusan.backend.wellness.repository.TourismCatalogTranslationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TourismCatalogTranslationServiceTest {
    @Test
    void 같은원문은_DB캐시를사용하고_파파고를재호출하지않는다() {
        TourismCatalogTranslationRepository repository = mock(TourismCatalogTranslationRepository.class);
        PapagoTranslationClient papago = mock(PapagoTranslationClient.class);
        TourismCatalogTranslationService service = new TourismCatalogTranslationService(
            repository, papago, new PapagoDailyQuotaGuard()
        );
        TourismCatalogResponse source = catalog(List.of(item("place-1")));

        when(repository.findByCategoryAndItemIdAndLanguageCode("ACCESSIBLE", "place-1", "en"))
            .thenReturn(Optional.empty());
        when(papago.translate(anyString(), eq("en"))).thenReturn(String.join("\n",
            "Haeundae Beach", "A calm seaside", "Busan Haeundae-gu"
        ));

        TourismCatalogResponse first = service.localize(source, "en");
        ArgumentCaptor<TourismCatalogTranslation> saved = ArgumentCaptor.forClass(TourismCatalogTranslation.class);
        verify(repository).save(saved.capture());
        when(repository.findByCategoryAndItemIdAndLanguageCode("ACCESSIBLE", "place-1", "en"))
            .thenReturn(Optional.of(saved.getValue()));

        TourismCatalogResponse second = service.localize(source, "en");

        assertThat(first.items().getFirst().title()).isEqualTo("Haeundae Beach");
        assertThat(second.items().getFirst().address()).isEqualTo("Busan Haeundae-gu");
        verify(papago, times(1)).translate(anyString(), eq("en"));
    }

    @Test
    void 일일한도초과후_나머지항목은_한국어로폴백하고_재호출하지않는다() {
        TourismCatalogTranslationRepository repository = mock(TourismCatalogTranslationRepository.class);
        PapagoTranslationClient papago = mock(PapagoTranslationClient.class);
        TourismCatalogTranslationService service = new TourismCatalogTranslationService(
            repository, papago, new PapagoDailyQuotaGuard()
        );
        when(repository.findByCategoryAndItemIdAndLanguageCode(eq("ACCESSIBLE"), anyString(), eq("ja")))
            .thenReturn(Optional.empty());
        when(papago.translate(anyString(), eq("ja"))).thenThrow(new PapagoTranslationApiException("HTTP 429"));

        TourismCatalogResponse result = service.localize(catalog(List.of(item("one"), item("two"))), "ja");

        assertThat(result.items()).extracting(TourismCatalogItemResponse::title)
            .containsExactly("해운대해수욕장", "해운대해수욕장");
        verify(papago, times(1)).translate(anyString(), eq("ja"));
    }

    private static TourismCatalogResponse catalog(List<TourismCatalogItemResponse> items) {
        return new TourismCatalogResponse(TourismCatalogCategory.ACCESSIBLE, "무장애 관광", "설명", "TourAPI",
            Instant.now(), items);
    }

    private static TourismCatalogItemResponse item(String id) {
        return new TourismCatalogItemResponse(id, "해운대해수욕장", "잔잔한 바다", "부산 해운대구", null,
            35.1, 129.1, "12", Map.of("signguNm", "해운대구", "baseYmd", "20260830"));
    }
}
