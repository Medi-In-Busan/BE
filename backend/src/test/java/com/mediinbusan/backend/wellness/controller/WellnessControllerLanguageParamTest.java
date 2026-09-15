package com.mediinbusan.backend.wellness.controller;

import com.mediinbusan.backend.wellness.service.KakaoMobilityRouteService;
import com.mediinbusan.backend.wellness.service.TourismCatalogService;
import com.mediinbusan.backend.wellness.service.WellnessIngestionService;
import com.mediinbusan.backend.wellness.service.WellnessService;
import com.mediinbusan.backend.wellness.service.WellnessSnapshotIngestionService;
import com.mediinbusan.backend.wellness.service.WellnessTourismGatewayService;
import com.mediinbusan.backend.wellness.service.WellnessWalkingCourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 웰니스 장소 엔드포인트의 언어 파라미터 이름을 고정한다.
 *
 * 세 엔드포인트 중 `/places`만 파라미터 이름이 `lang`이었다가, Android `TourismApi`가 셋 다
 * `?language=`로 보내는 바람에 이 엔드포인트만 값을 못 받고 기본값 `ko`로 떨어졌다. 그 결과
 * ① 지도 전체 브라우징의 장소 이름·주소가 다른 언어에서도 한국어로 나오고, ②
 * `WellnessPlaceResponse.translated`가 항상 true가 되어 지도 "번역된 장소만" 필터가 아무것도
 * 걸러내지 못했다. 이름이 틀리면 Spring이 조용히 기본값을 쓰기 때문에 오류 없이 기능만 죽는다 —
 * 그래서 이름 자체를 테스트로 못박는다.
 */
@WebMvcTest(WellnessController.class)
class WellnessControllerLanguageParamTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WellnessService wellnessService;
    @MockitoBean
    private WellnessIngestionService wellnessIngestionService;
    @MockitoBean
    private WellnessTourismGatewayService tourismGatewayService;
    @MockitoBean
    private WellnessSnapshotIngestionService snapshotIngestionService;
    @MockitoBean
    private WellnessWalkingCourseService walkingCourseService;
    @MockitoBean
    private TourismCatalogService tourismCatalogService;
    @MockitoBean
    private KakaoMobilityRouteService routeService;

    @Test
    void 장소_목록은_language_파라미터를_그대로_서비스에_넘긴다() throws Exception {
        when(wellnessService.findPlaces(any(), any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/wellness/places").param("language", "en"))
            .andExpect(status().isOk());

        verify(wellnessService).findPlaces(isNull(), isNull(), isNull(), eq("en"));
    }

    @Test
    void 장소_목록에_language가_없으면_ko로_조회한다() throws Exception {
        when(wellnessService.findPlaces(any(), any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/wellness/places"))
            .andExpect(status().isOk());

        verify(wellnessService).findPlaces(isNull(), isNull(), isNull(), eq("ko"));
    }

    @Test
    void 병원_주변_장소도_같은_language_이름을_쓴다() throws Exception {
        when(wellnessService.getNearbyPlaces(any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/wellness/hospitals/REG-1/places").param("language", "ja"))
            .andExpect(status().isOk());

        verify(wellnessService).getNearbyPlaces(eq("REG-1"), isNull(), eq("ja"));
    }

    @Test
    void 장소_상세도_같은_language_이름을_쓴다() throws Exception {
        when(wellnessService.getPlaceDetail(any(), any())).thenReturn(null);

        mockMvc.perform(get("/api/wellness/places/tour-123").param("language", "zh"))
            .andExpect(status().isOk());

        verify(wellnessService).getPlaceDetail(eq("tour-123"), eq("zh"));
    }
}
