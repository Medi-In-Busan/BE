package com.mediinbusan.backend.wellness.controller;

import com.mediinbusan.backend.wellness.domain.TourismCatalogCategory;
import com.mediinbusan.backend.wellness.dto.TourismCatalogResponse;
import com.mediinbusan.backend.wellness.dto.TourismExternalResponse;
import com.mediinbusan.backend.wellness.dto.WellnessIngestionResponse;
import com.mediinbusan.backend.wellness.dto.WellnessPlaceResponse;
import com.mediinbusan.backend.wellness.dto.WellnessSnapshotIngestionResponse;
import com.mediinbusan.backend.wellness.dto.WellnessWalkingCourseResponse;
import com.mediinbusan.backend.wellness.dto.WellnessRouteRequest;
import com.mediinbusan.backend.wellness.dto.WellnessRouteResponse;
import com.mediinbusan.backend.wellness.service.BusanTourismCodes;
import com.mediinbusan.backend.wellness.service.TourismCatalogService;
import com.mediinbusan.backend.wellness.service.WellnessIngestionService;
import com.mediinbusan.backend.wellness.service.WellnessService;
import com.mediinbusan.backend.wellness.service.WellnessSnapshotIngestionService;
import com.mediinbusan.backend.wellness.service.WellnessTourismGatewayService;
import com.mediinbusan.backend.wellness.service.WellnessWalkingCourseService;
import com.mediinbusan.backend.wellness.service.KakaoMobilityRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final TourismCatalogService tourismCatalogService;
    private final KakaoMobilityRouteService routeService;

    public WellnessController(
        WellnessService wellnessService,
        WellnessIngestionService wellnessIngestionService,
        WellnessTourismGatewayService tourismGatewayService,
        WellnessSnapshotIngestionService snapshotIngestionService,
        WellnessWalkingCourseService walkingCourseService,
        TourismCatalogService tourismCatalogService,
        KakaoMobilityRouteService routeService
    ) {
        this.wellnessService = wellnessService;
        this.wellnessIngestionService = wellnessIngestionService;
        this.tourismGatewayService = tourismGatewayService;
        this.snapshotIngestionService = snapshotIngestionService;
        this.walkingCourseService = walkingCourseService;
        this.tourismCatalogService = tourismCatalogService;
        this.routeService = routeService;
    }

    @Operation(
        summary = "병원 주변 웰니스 장소 조회",
        description = "병원 좌표 기준 반경 내 웰니스 장소를 거리순으로 반환한다. "
            + "lang(ko/en/zh/ja, 기본값 ko)에 맞는 이름·주소·설명을 반환하고, 해당 언어 번역이 없으면 ko로 폴백한다."
    )
    @GetMapping("/hospitals/{hospitalRegNo}/places")
    public List<WellnessPlaceResponse> getNearbyPlaces(
        @PathVariable String hospitalRegNo,
        @Parameter(description = "검색 반경(m). 기본값 3000m") @RequestParam(required = false) Double radiusMeters,
        @RequestParam(defaultValue = "ko") String language
    ) {
        return wellnessService.getNearbyPlaces(hospitalRegNo, radiusMeters, language);
    }

    @Operation(summary = "병원 출발 웰니스 코스 경로", description = "Kakao 자동차·도보 길찾기로 4~5개 장소의 실제 이동 경로를 반환한다.")
    @PostMapping("/routes")
    public WellnessRouteResponse getRoute(@RequestBody WellnessRouteRequest request) {
        return routeService.route(request);
    }

    @Operation(
        summary = "웰니스 장소 목록 조회(병원 비종속)",
        description = "latitude/longitude를 넘기면 반경(radiusMeters, 기본 3000m) 내 장소를 거리순으로, "
            + "안 넘기면 전체 장소를 반환한다. 지도 '전체 브라우징' 화면처럼 특정 병원에 종속되지 않은 조회에 쓴다. "
            + "language(ko/en/zh/ja, 기본값 ko)에 맞는 이름·주소·설명을 반환하고, 해당 언어 번역이 없으면 ko로 폴백한다. "
            + "lang은 language의 레거시 별칭이다(deprecated) — 둘 다 오면 language가 이긴다."
    )
    @GetMapping("/places")
    public List<WellnessPlaceResponse> getPlaces(
        @Parameter(description = "기준 위도(선택)") @RequestParam(required = false) Double latitude,
        @Parameter(description = "기준 경도(선택)") @RequestParam(required = false) Double longitude,
        @Parameter(description = "검색 반경(m). 기본값 3000m — latitude/longitude가 있을 때만 적용") @RequestParam(required = false) Double radiusMeters,
        // 이 엔드포인트의 정식 이름은 language다 — 형제 엔드포인트(/hospitals/{regNo}/places,
        // /places/{contentId})와 Android TourismApi의 @Query 이름이 전부 language인데 여기만 lang이라,
        // 앱이 ?language=en을 보내도 서버가 못 읽고 기본값 ko로 떨어졌다. 그 결과 지도 전체
        // 브라우징의 장소 이름·주소가 영어/일어/중어에서도 한국어로 나오고, WellnessPlaceResponse의
        // translated가 항상 true가 되어 지도 "번역된 장소만" 필터가 아무것도 걸러내지 못했다.
        @Parameter(description = "표시 언어(ko/en/zh/ja). 기본값 ko") @RequestParam(required = false) String language,
        // lang은 이름을 바로잡기 전의 별칭으로 남겨둔다. 지금 이 백엔드를 쓰는 클라이언트는 Android
        // 앱뿐이고 그 앱은 language만 보내지만, 이름이 안 맞을 때 Spring이 오류 없이 기본값으로
        // 떨어뜨리는 게 애초에 이 버그를 오래 숨긴 원인이었다 — 같은 방식으로 조용히 깨지는 길을
        // 남기지 않기 위해 받아만 준다. 새 호출부는 language를 쓸 것.
        @Parameter(description = "language의 레거시 별칭(deprecated)") @RequestParam(required = false) String lang
    ) {
        return wellnessService.findPlaces(latitude, longitude, radiusMeters, resolveLanguage(language, lang));
    }

    /**
     * 정식 이름(language)을 우선하고, 없으면 레거시 별칭(lang), 그것도 없으면 ko.
     *
     * language를 `defaultValue = "ko"`로 두지 않는 이유: 그러면 "명시적으로 language=ko"와 "language를
     * 아예 안 보냄"이 서버에서 똑같이 "ko"로 보여서, `?language=ko&lang=en`처럼 둘 다 실린 요청에서
     * 어느 쪽이 사용자의 의도인지 구분할 수 없다. 둘 다 required=false로 받고 여기서 순서를 정한다.
     */
    private static String resolveLanguage(String language, String legacyLang) {
        if (language != null && !language.isBlank()) return language;
        if (legacyLang != null && !legacyLang.isBlank()) return legacyLang;
        return "ko";
    }

    @Operation(
        summary = "웰니스 장소 상세 조회",
        description = "lang(ko/en/zh/ja, 기본값 ko)에 맞는 이름·주소·설명을 반환한다. 해당 언어 번역이 없으면 ko로 폴백한다."
    )
    @GetMapping("/places/{contentId}")
    public WellnessPlaceResponse getPlaceDetail(
        @PathVariable String contentId,
        @RequestParam(defaultValue = "ko") String language
    ) {
        return wellnessService.getPlaceDetail(contentId, language);
    }

    @Operation(summary = "TourAPI/부산맛집정보 웰니스 장소 적재", description = "환경변수의 공식 API 키로 부산 웰니스 장소를 수집해 DB에 upsert한다. 카카오 로컬 검색 소스(kakao-*)는 더 이상 수집하지 않고, 과거 데이터도 이 호출로 정리된다.")
    @PostMapping("/ingest")
    public WellnessIngestionResponse ingest() {
        return wellnessIngestionService.ingest();
    }

    @Operation(summary = "부산 웰니스 걷기 코스", description = "두루누비 코스 중 부산 행만 정규화해 반환한다.")
    @GetMapping("/tourism/walking-courses")
    public List<WellnessWalkingCourseResponse> getWalkingCourses() {
        return walkingCourseService.getBusanCourses();
    }

    @Operation(summary = "관광 데이터 카테고리 조회", description = "관광공사 서비스별 응답을 앱 공통 카드 모델로 정규화해 반환한다.")
    @GetMapping("/tourism/catalog/{category}")
    public TourismCatalogResponse getTourismCatalog(
        @PathVariable TourismCatalogCategory category,
        @RequestParam(required = false) BusanTourismCodes.District district,
        @RequestParam(required = false) String baseYm,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        @RequestParam(defaultValue = "ko") String language
    ) {
        return tourismCatalogService.getCatalog(category, district, baseYm, page, pageSize, language);
    }

    @Operation(summary = "관광공사 외부 API 스냅샷 적재", description = "부산 전 구역 관광 데이터를 wellness_external_snapshot에 upsert한다.")
    @PostMapping("/ingest/snapshots")
    public WellnessSnapshotIngestionResponse ingestSnapshots(@RequestParam String baseYm) {
        return snapshotIngestionService.sync(baseYm);
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
