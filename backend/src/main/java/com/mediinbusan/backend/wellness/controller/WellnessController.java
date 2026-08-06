package com.mediinbusan.backend.wellness.controller;

import com.mediinbusan.backend.wellness.dto.WellnessIngestionResponse;
import com.mediinbusan.backend.wellness.dto.WellnessPlaceResponse;
import com.mediinbusan.backend.wellness.service.WellnessIngestionService;
import com.mediinbusan.backend.wellness.service.WellnessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Wellness", description = "병원 주변 웰니스 장소 조회")
@RestController
@RequestMapping("/api/wellness")
public class WellnessController {

    private final WellnessService wellnessService;
    private final WellnessIngestionService wellnessIngestionService;

    public WellnessController(WellnessService wellnessService, WellnessIngestionService wellnessIngestionService) {
        this.wellnessService = wellnessService;
        this.wellnessIngestionService = wellnessIngestionService;
    }

    @Operation(summary = "병원 주변 웰니스 장소 조회", description = "병원 좌표 기준 반경 내 웰니스 장소를 거리순으로 반환한다.")
    @GetMapping("/hospitals/{hospitalRegNo}/places")
    public List<WellnessPlaceResponse> getNearbyPlaces(
        @PathVariable String hospitalRegNo,
        @Parameter(description = "검색 반경(m). 기본값 3000m") @RequestParam(required = false) Double radiusMeters
    ) {
        return wellnessService.getNearbyPlaces(hospitalRegNo, radiusMeters);
    }

    @Operation(summary = "웰니스 장소 상세 조회")
    @GetMapping("/places/{contentId}")
    public WellnessPlaceResponse getPlaceDetail(@PathVariable String contentId) {
        return wellnessService.getPlaceDetail(contentId);
    }

    @Operation(summary = "TourAPI/Kakao Local 웰니스 장소 적재", description = "환경변수의 공식 API 키로 부산 웰니스 장소를 수집해 DB에 upsert한다.")
    @PostMapping("/ingest")
    public WellnessIngestionResponse ingest() {
        return wellnessIngestionService.ingest();
    }
}
