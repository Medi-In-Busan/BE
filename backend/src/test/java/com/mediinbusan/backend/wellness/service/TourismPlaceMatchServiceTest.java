package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.wellness.dto.TourismExternalResponse;
import com.mediinbusan.backend.wellness.dto.TourismPlaceMatchResponse;
import com.mediinbusan.backend.wellness.repository.WellnessExternalSnapshotRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TourismPlaceMatchServiceTest {
    private final WellnessTourismGatewayService gateway = mock(WellnessTourismGatewayService.class);
    private final WellnessExternalSnapshotRepository snapshotRepository = mock(WellnessExternalSnapshotRepository.class);
    private final TourismCatalogTranslationService translationService = passThroughTranslationService();
    private final TourismCatalogService catalogService = new TourismCatalogService(gateway, snapshotRepository, translationService);
    private final TourismPlaceMatchService service = new TourismPlaceMatchService(gateway, catalogService);

    private static TourismCatalogTranslationService passThroughTranslationService() {
        TourismCatalogTranslationService service = mock(TourismCatalogTranslationService.class);
        when(service.localize(any(), anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        return service;
    }

    @Test
    void exactNameAndDistrictReturnTourismDetailInsteadOfCrowdingItem() {
        when(gateway.searchPlaces(anyString(), eq(BusanTourismCodes.District.HAEUNDAE), eq(1)))
            .thenReturn(response(body(List.of(place("126081", "해운대해수욕장", "350")), 1)));
        when(gateway.placeDetail("126081")).thenReturn(response(body(List.of(detail(
            "126081", "해운대해수욕장", "350", "<b>대표</b><br>관광지"
        )), 1)));

        TourismPlaceMatchResponse result = service.find("부산 해운대 해수욕장", BusanTourismCodes.District.HAEUNDAE);

        assertThat(result.matched()).isTrue();
        assertThat(result.item().id()).isEqualTo("126081");
        assertThat(result.item().imageUrl()).isEqualTo("https://example.com/126081.jpg");
        assertThat(result.item().address()).isEqualTo("부산광역시 해운대구 관광로 1");
        assertThat(result.item().latitude()).isEqualTo(35.159);
        assertThat(result.item().longitude()).isEqualTo(129.16);
        assertThat(result.item().subtitle()).isEqualTo("대표\n관광지");
    }

    @Test
    void partialNameDoesNotMatchAnotherPlace() {
        stubSearch(List.of(place("shop", "다비치안경 해운대해수욕장입구점", "350")), 1);

        assertThat(service.find("해운대해수욕장", BusanTourismCodes.District.HAEUNDAE).matched()).isFalse();
        verify(gateway, never()).placeDetail(anyString());
    }

    @Test
    void sameNameInAnotherDistrictDoesNotMatch() {
        stubSearch(List.of(place("other", "중앙공원", "110")), 1);

        assertThat(service.find("중앙공원", BusanTourismCodes.District.HAEUNDAE).matched()).isFalse();
    }

    @Test
    void ambiguousSameNameDoesNotPickArbitrarily() {
        stubSearch(List.of(
            place("one", "중앙 공원", "350"),
            place("two", "중앙공원", "350")
        ), 2);

        assertThat(service.find("중앙공원", BusanTourismCodes.District.HAEUNDAE).matched()).isFalse();
        verify(gateway, never()).placeDetail(anyString());
    }

    @Test
    void englishSeaLifeBrandMatchesKoreanTourApiTitle() {
        when(gateway.searchPlaces(anyString(), eq(BusanTourismCodes.District.HAEUNDAE), eq(1)))
            .thenReturn(response(body(List.of(), 0)));
        when(gateway.searchPlaces("씨라이프부산아쿠아리움", BusanTourismCodes.District.HAEUNDAE, 1))
            .thenReturn(response(body(List.of(place("229912", "씨라이프부산아쿠아리움", "350")), 1)));
        when(gateway.placeDetail("229912")).thenReturn(response(body(List.of(detail(
            "229912", "씨라이프부산아쿠아리움", "350", "아쿠아리움 설명"
        )), 1)));

        TourismPlaceMatchResponse result = service.find(
            "SEA LIFE 부산아쿠아리움", BusanTourismCodes.District.HAEUNDAE
        );

        assertThat(result.matched()).isTrue();
        assertThat(result.item().id()).isEqualTo("229912");
    }

    @Test
    void duplicateCatalogEntriesPreferUniqueTouristAttraction() {
        Map<String, Object> attraction = place("2679008", "KT&G 상상마당 부산", "230");
        attraction.put("contenttypeid", "12");
        Map<String, Object> shopping = place("4011128", "KT&G 상상마당 부산", "230");
        shopping.put("contenttypeid", "38");
        when(gateway.searchPlaces(anyString(), eq(BusanTourismCodes.District.BUSANJIN), eq(1)))
            .thenReturn(response(body(List.of(attraction, shopping), 2)));
        Map<String, Object> detail = detail("2679008", "KT&G 상상마당 부산", "230", "문화 공간 설명");
        detail.put("contenttypeid", "12");
        when(gateway.placeDetail("2679008")).thenReturn(response(body(List.of(detail), 1)));

        TourismPlaceMatchResponse result = service.find(
            "KT&G 상상마당 부산", BusanTourismCodes.District.BUSANJIN
        );

        assertThat(result.matched()).isTrue();
        assertThat(result.item().id()).isEqualTo("2679008");
    }

    @Test
    void searchesFollowingPagesBeforeDeclaringNotFound() {
        List<Map<String, Object>> firstPage = new ArrayList<>();
        for (int index = 0; index < 100; index++) {
            firstPage.add(place("other-" + index, "다른 장소 " + index, "350"));
        }
        when(gateway.searchPlaces("해운대해수욕장", BusanTourismCodes.District.HAEUNDAE, 1))
            .thenReturn(response(body(firstPage, 101)));
        when(gateway.searchPlaces("해운대해수욕장", BusanTourismCodes.District.HAEUNDAE, 2))
            .thenReturn(response(body(List.of(place("126081", "해운대해수욕장", "350")), 101)));
        when(gateway.placeDetail("126081"))
            .thenReturn(response(body(List.of(detail("126081", "해운대해수욕장", "350", "설명")), 1)));

        assertThat(service.find("해운대해수욕장", BusanTourismCodes.District.HAEUNDAE).matched()).isTrue();
    }

    @Test
    void truncatedSearchIsNotTreatedAsUnique() {
        List<Map<String, Object>> fullPage = new ArrayList<>();
        fullPage.add(place("target", "중앙공원", "350"));
        for (int index = 1; index < 100; index++) fullPage.add(place("other-" + index, "다른 장소 " + index, "350"));
        when(gateway.searchPlaces(anyString(), eq(BusanTourismCodes.District.HAEUNDAE), anyInt()))
            .thenReturn(response(body(fullPage, 400)));

        assertThat(service.find("중앙공원", BusanTourismCodes.District.HAEUNDAE).matched()).isFalse();
        verify(gateway, never()).placeDetail(anyString());
    }

    @Test
    void detailWithDifferentIdIsRejected() {
        stubSearch(List.of(place("expected", "중앙공원", "350")), 1);
        when(gateway.placeDetail("expected"))
            .thenReturn(response(body(List.of(detail("different", "중앙공원", "350", "설명")), 1)));

        assertThat(service.find("중앙공원", BusanTourismCodes.District.HAEUNDAE).matched()).isFalse();
    }

    @Test
    void detailWithChangedNameIsRejected() {
        stubSearch(List.of(place("expected", "중앙공원", "350")), 1);
        when(gateway.placeDetail("expected"))
            .thenReturn(response(body(List.of(detail("expected", "중앙공원 매점", "350", "설명")), 1)));

        assertThat(service.find("중앙공원", BusanTourismCodes.District.HAEUNDAE).matched()).isFalse();
    }

    @Test
    void invalidNameIsRejectedBeforeCallingExternalApi() {
        assertThatThrownBy(() -> service.find(" ", BusanTourismCodes.District.HAEUNDAE))
            .isInstanceOf(ResponseStatusException.class);
        verify(gateway, never()).searchPlaces(anyString(), eq(BusanTourismCodes.District.HAEUNDAE), anyInt());
    }

    private void stubSearch(List<Map<String, Object>> items, int total) {
        when(gateway.searchPlaces(anyString(), eq(BusanTourismCodes.District.HAEUNDAE), eq(1)))
            .thenReturn(response(body(items, total)));
    }

    private static TourismExternalResponse response(Map<String, Object> body) {
        return TourismExternalResponse.of("tourism-ko", body);
    }

    private static Map<String, Object> body(List<Map<String, Object>> items, int total) {
        return Map.of("items", Map.of("item", items), "totalCount", total);
    }

    private static Map<String, Object> place(String id, String title, String districtCode) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("contentid", id);
        item.put("title", title);
        item.put("lDongRegnCd", "26");
        item.put("lDongSignguCd", districtCode);
        item.put("addr1", "부산광역시 해운대구 관광로 1");
        item.put("firstimage", "https://example.com/" + id + ".jpg");
        item.put("mapx", "129.16");
        item.put("mapy", "35.159");
        return item;
    }

    private static Map<String, Object> detail(String id, String title, String districtCode, String overview) {
        Map<String, Object> item = place(id, title, districtCode);
        item.put("overview", overview);
        item.put("homepage", "https://example.com/" + id);
        return item;
    }
}
