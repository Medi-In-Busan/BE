package com.mediinbusan.backend.wellness.controller;

import com.mediinbusan.backend.wellness.dto.WellnessIngestionResponse;
import com.mediinbusan.backend.wellness.dto.WellnessPlaceResponse;
import com.mediinbusan.backend.wellness.dto.WellnessSnapshotIngestionResponse;
import com.mediinbusan.backend.wellness.dto.WellnessWalkingCourseResponse;
import com.mediinbusan.backend.wellness.dto.TourismExternalResponse;
import com.mediinbusan.backend.wellness.service.BusanTourismCodes;
import com.mediinbusan.backend.wellness.service.WellnessIngestionService;
import com.mediinbusan.backend.wellness.service.WellnessService;
import com.mediinbusan.backend.wellness.service.WellnessSnapshotIngestionService;
import com.mediinbusan.backend.wellness.service.WellnessTourismGatewayService;
import com.mediinbusan.backend.wellness.service.WellnessWalkingCourseService;
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
    private final WellnessTourismGatewayService tourismGatewayService;
    private final WellnessSnapshotIngestionService snapshotIngestionService;
    private final WellnessWalkingCourseService walkingCourseService;

    public WellnessController(
        WellnessService wellnessService,
        WellnessIngestionService wellnessIngestionService,
        WellnessTourismGatewayService tourismGatewayService,
        WellnessSnapshotIngestionService snapshotIngestionService,
        WellnessWalkingCourseService walkingCourseService
    ) {
        this.wellnessService = wellnessService;
        this.wellnessIngestionService = wellnessIngestionService;
        this.tourismGatewayService = tourismGatewayService;
        this.snapshotIngestionService = snapshotIngestionService;
        this.walkingCourseService = walkingCourseService;
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

    @Operation(summary = "부산 웰니스 걷기 코스", description = "두루누비 코스 중 부산 행만 정규화해 반환한다.")
    @GetMapping("/tourism/walking-courses")
    public List<WellnessWalkingCourseResponse> getWalkingCourses() {
        return walkingCourseService.getBusanCourses();
    }

    @PostMapping("/ingest/snapshots")
    public WellnessSnapshotIngestionResponse ingestSnapshots(
        @RequestParam String baseYm,
        @RequestParam String startYmd,
        @RequestParam String endYmd
    ) {
        return snapshotIngestionService.sync(baseYm, startYmd, endYmd);
    }

    @Operation(summary = "다국어 부산 관광지 목록", description = "Kor/Eng/Jpn/Chs TourAPI를 법정동 부산 코드로 조회한다.")
    @GetMapping("/external/places")
    public TourismExternalResponse places(
        @RequestParam(defaultValue = "KO") WellnessTourismGatewayService.Language language,
        @RequestParam(required = false) BusanTourismCodes.District district,
        @RequestParam(required = false) String contentTypeId
    ) {
        return tourismGatewayService.places(language, district, contentTypeId);
    }

    @GetMapping("/external/wellness")
    public TourismExternalResponse wellness(@RequestParam(required = false) BusanTourismCodes.District district) {
        return tourismGatewayService.wellness(district);
    }

    @GetMapping("/external/accessibility")
    public TourismExternalResponse accessibility(@RequestParam(required = false) BusanTourismCodes.District district) {
        return tourismGatewayService.accessibility(district);
    }

    @GetMapping("/external/related")
    public TourismExternalResponse related(
        @RequestParam BusanTourismCodes.District district,
        @RequestParam String baseYm
    ) {
        return tourismGatewayService.related(district, baseYm);
    }

    @GetMapping("/external/hubs")
    public TourismExternalResponse hubs(
        @RequestParam BusanTourismCodes.District district,
        @RequestParam String baseYm
    ) {
        return tourismGatewayService.hubs(district, baseYm);
    }

    @GetMapping("/external/crowding")
    public TourismExternalResponse crowding(@RequestParam BusanTourismCodes.District district) {
        return tourismGatewayService.crowding(district);
    }

    @GetMapping("/external/visitors")
    public TourismExternalResponse visitors(@RequestParam String startYmd, @RequestParam String endYmd) {
        return tourismGatewayService.visitors(startYmd, endYmd);
    }

    @GetMapping("/external/photos")
    public TourismExternalResponse photos(@RequestParam(defaultValue = "부산") String keyword) {
        return tourismGatewayService.photos(keyword);
    }

    @GetMapping("/external/walking-courses")
    public TourismExternalResponse walkingCourses() {
        return tourismGatewayService.walkingCourses();
    }

    @GetMapping("/external/audio")
    public TourismExternalResponse audio(@RequestParam double latitude, @RequestParam double longitude) {
        return tourismGatewayService.audio(latitude, longitude);
    }
}
