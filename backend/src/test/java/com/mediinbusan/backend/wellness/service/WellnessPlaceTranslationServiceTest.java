package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.document.client.PapagoTranslationApiException;
import com.mediinbusan.backend.document.client.PapagoTranslationClient;
import com.mediinbusan.backend.wellness.dto.WellnessPlaceResponse;
import com.mediinbusan.backend.wellness.repository.WellnessPlaceTranslationRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WellnessPlaceTranslationServiceTest {

    @Test
    void 여러장소를_한번에번역하고_각필드를_정확히매핑한다() {
        WellnessPlaceTranslationRepository repository = mock(WellnessPlaceTranslationRepository.class);
        PapagoTranslationClient papago = mock(PapagoTranslationClient.class);
        WellnessPlaceTranslationService service = new WellnessPlaceTranslationService(
            repository, papago, new PapagoDailyQuotaGuard()
        );
        WellnessPlaceResponse first = place("place-1", "해운대 해수욕장");
        WellnessPlaceResponse second = place("place-2", "광안리 해수욕장");
        when(repository.findByContentIdAndLanguageCode(anyString(), eq("en"))).thenReturn(Optional.empty());
        when(papago.translate(anyString(), eq("en"))).thenReturn(String.join("\n",
            "Haeundae Beach", "Busan Haeundae-gu", "A seaside walking spot",
            "Gwangalli Beach", "Busan Suyeong-gu", "A beach with a bridge view"
        ));

        List<WellnessPlaceResponse> translated = service.localizeAll(List.of(first, second), "en");

        assertThat(translated).extracting(WellnessPlaceResponse::name)
            .containsExactly("Haeundae Beach", "Gwangalli Beach");
        assertThat(translated.get(1).description()).isEqualTo("A beach with a bridge view");
        verify(papago, times(1)).translate(anyString(), eq("en"));
        verify(repository, times(2)).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void 일일한도초과후_같은날은_파파고를_재호출하지않고_한국어로폴백한다() {
        WellnessPlaceTranslationRepository repository = mock(WellnessPlaceTranslationRepository.class);
        PapagoTranslationClient papago = mock(PapagoTranslationClient.class);
        WellnessPlaceTranslationService service = new WellnessPlaceTranslationService(
            repository,
            papago,
            new PapagoDailyQuotaGuard()
        );
        WellnessPlaceResponse korean = new WellnessPlaceResponse(
            "place-1", "해운대 해수욕장", "TOURIST_ATTRACTION", "부산 해운대구", 35.1, 129.1,
            null, "바닷가 산책 장소", null, "2026-08-30", 100.0, true, "OTHER"
        );

        when(repository.findByContentIdAndLanguageCode("place-1", "en")).thenReturn(Optional.empty());
        when(papago.translate(anyString(), eq("en"))).thenThrow(new PapagoTranslationApiException("HTTP 429"));

        assertThat(service.localize(korean, "en")).isSameAs(korean);
        assertThat(service.localize(korean, "en")).isSameAs(korean);
        verify(papago, times(1)).translate(anyString(), eq("en"));
    }

    private static WellnessPlaceResponse place(String id, String name) {
        return new WellnessPlaceResponse(
            id, name, "TOURIST_ATTRACTION", "부산", 35.1, 129.1,
            null, "바닷가 산책 장소", null, "2026-08-30", 100.0, true, "OTHER"
        );
    }
}
