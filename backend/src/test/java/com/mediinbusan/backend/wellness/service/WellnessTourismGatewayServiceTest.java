package com.mediinbusan.backend.wellness.service;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WellnessTourismGatewayServiceTest {
    private final WellnessIngestionProperties properties = mock(WellnessIngestionProperties.class);
    private final TourismExternalClient client = mock(TourismExternalClient.class);
    private final WellnessTourismGatewayService gateway =
        new WellnessTourismGatewayService(properties, client, new TourismPlacesCache());

    @Test
    void keywordSearchUsesBusanDistrictAndRequestedPage() {
        when(properties.tourApiBaseUrl()).thenReturn("https://tour.test");

        gateway.searchPlaces("해운대해수욕장", BusanTourismCodes.District.HAEUNDAE, 2);

        verify(client).get(eq("https://tour.test"), eq("searchKeyword2"), argThat(params ->
            "해운대해수욕장".equals(params.get("keyword"))
                && "26".equals(params.get("lDongRegnCd"))
                && "350".equals(params.get("lDongSignguCd"))
                && Integer.valueOf(2).equals(params.get("pageNo"))
                && Integer.valueOf(100).equals(params.get("numOfRows"))
        ));
    }

    @Test
    void detailRequestUsesMatchedTourismContentId() {
        when(properties.tourApiBaseUrl()).thenReturn("https://tour.test");

        gateway.placeDetail("126081");

        verify(client).get(eq("https://tour.test"), eq("detailCommon2"), argThat(params ->
            "126081".equals(params.get("contentId"))
        ));
    }

    @Test
    void repeatedPlacesRequestWithSameParamsHitsCacheInsteadOfCallingExternalApiAgain() {
        when(properties.tourApiBaseUrl()).thenReturn("https://tour.test");
        when(client.get(any(), any(), any())).thenReturn("first-response");

        gateway.places(WellnessTourismGatewayService.Language.KO, BusanTourismCodes.District.HAEUNDAE, null, 1, 40);
        gateway.places(WellnessTourismGatewayService.Language.KO, BusanTourismCodes.District.HAEUNDAE, null, 1, 40);

        verify(client, times(1)).get(eq("https://tour.test"), eq("areaBasedList2"), any());
    }

    @Test
    void placesRequestWithDifferentPageIsNotServedFromCache() {
        when(properties.tourApiBaseUrl()).thenReturn("https://tour.test");
        when(client.get(any(), any(), any())).thenReturn("some-response");

        gateway.places(WellnessTourismGatewayService.Language.KO, BusanTourismCodes.District.HAEUNDAE, null, 1, 40);
        gateway.places(WellnessTourismGatewayService.Language.KO, BusanTourismCodes.District.HAEUNDAE, null, 2, 40);

        verify(client, times(2)).get(eq("https://tour.test"), eq("areaBasedList2"), any());
    }
}
