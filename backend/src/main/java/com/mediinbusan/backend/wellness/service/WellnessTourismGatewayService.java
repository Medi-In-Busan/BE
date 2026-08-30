package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.wellness.dto.TourismExternalResponse;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class WellnessTourismGatewayService {

    private static final String MOBILE_OS = "ETC";
    private static final String MOBILE_APP = "MediInBusan";

    private final WellnessIngestionProperties properties;
    private final TourismExternalClient externalClient;

    public WellnessTourismGatewayService(WellnessIngestionProperties properties, TourismExternalClient externalClient) {
        this.properties = properties;
        this.externalClient = externalClient;
    }

    public TourismExternalResponse places(Language language, BusanTourismCodes.District district, String contentTypeId) {
        Map<String, Object> params = tourismParams(district);
        params.put("contentTypeId", contentTypeId);
        return request("tourism-" + language.name().toLowerCase(), language.baseUrl(properties), "areaBasedList2", params);
    }

    public TourismExternalResponse accessibility(BusanTourismCodes.District district) {
        return request("accessible-tourism", properties.accessibleTourismBaseUrl(), "areaBasedList2", tourismParams(district));
    }

    public TourismExternalResponse searchPlaces(String keyword, BusanTourismCodes.District district, int pageNo) {
        Map<String, Object> params = tourismParams(district);
        params.put("keyword", keyword);
        params.put("numOfRows", 100);
        params.put("pageNo", pageNo);
        params.put("arrange", "A");
        return request("tourism-ko", properties.tourApiBaseUrl(), "searchKeyword2", params);
    }

    public TourismExternalResponse placeDetail(String contentId) {
        Map<String, Object> params = pageParams();
        params.put("MobileOS", MOBILE_OS);
        params.put("MobileApp", MOBILE_APP);
        params.put("contentId", contentId);
        return request("tourism-ko", properties.tourApiBaseUrl(), "detailCommon2", params);
    }

    public TourismExternalResponse related(BusanTourismCodes.District district, String baseYm) {
        return request("related-tourism", properties.relatedTourismBaseUrl(), "areaBasedList1", bigdataParams(district, baseYm));
    }

    public TourismExternalResponse hubs(BusanTourismCodes.District district, String baseYm) {
        return request("hub-tourism", properties.hubTourismBaseUrl(), "areaBasedList1", bigdataParams(district, baseYm));
    }

    public TourismExternalResponse crowding(BusanTourismCodes.District district) {
        return request("crowding-forecast", properties.crowdingBaseUrl(), "tatsCnctrRatedList", bigdataParams(district, null));
    }

    public TourismExternalResponse photos(String keyword) {
        // PhotoGalleryService1은 Service1세대 API라 lDongRegnCd(법정동 코드)를 모른다 — 붙이면
        // INVALID_REQUEST_PARAMETER_ERROR(lDongRegnCd)로 요청 자체가 거부된다(실제 호출로 확인).
        Map<String, Object> params = pageParams();
        // 장소 하나당 사진이 여러 장 걸려 있어(같은 제목 반복) 기본 50건만 받으면 제목 기준
        // dedupe(TourismCatalogService) 이후 장소 수가 너무 적어진다 — 더 많이 받아온다.
        params.put("numOfRows", 100);
        params.put("MobileOS", MOBILE_OS);
        params.put("MobileApp", MOBILE_APP);
        params.put("keyword", keyword);
        return request("photo-gallery", properties.photoBaseUrl(), "gallerySearchList1", params);
    }

    public TourismExternalResponse walkingCourses() {
        return walkingCourses(1);
    }

    public TourismExternalResponse walkingCourses(int pageNo) {
        Map<String, Object> params = pageParams();
        params.put("pageNo", pageNo);
        params.put("MobileOS", MOBILE_OS);
        params.put("MobileApp", MOBILE_APP);
        return request("durunubi", properties.walkingBaseUrl(), "courseList", params);
    }

    public TourismExternalResponse audio(double latitude, double longitude) {
        // Odii도 Service1세대라 lDongRegnCd는 안 쓰고, 대신 langCode가 필수 파라미터다 — 빠지면
        // NO_MANDATORY_REQUEST_PARAMETERS_ERROR1(langCode)로 실패한다(실제 호출로 확인).
        Map<String, Object> params = pageParams();
        params.put("MobileOS", MOBILE_OS);
        params.put("MobileApp", MOBILE_APP);
        params.put("mapX", longitude);
        params.put("mapY", latitude);
        params.put("radius", 20_000);
        params.put("langCode", "ko");
        return request("odii", properties.audioBaseUrl(), "themeLocationBasedList", params);
    }

    private TourismExternalResponse request(String source, String baseUrl, String operation, Map<String, Object> params) {
        return TourismExternalResponse.of(source, externalClient.get(baseUrl, operation, params));
    }

    private static Map<String, Object> tourismParams(BusanTourismCodes.District district) {
        Map<String, Object> params = pageParams();
        params.put("MobileOS", MOBILE_OS);
        params.put("MobileApp", MOBILE_APP);
        params.put("lDongRegnCd", BusanTourismCodes.LDONG_REGN_CD);
        if (district != null) {
            params.put("lDongSignguCd", district.lDongSignguCd());
        }
        return params;
    }

    private static Map<String, Object> bigdataParams(BusanTourismCodes.District district, String baseYm) {
        Map<String, Object> params = pageParams();
        params.put("MobileOS", MOBILE_OS);
        params.put("MobileApp", MOBILE_APP);
        params.put("areaCd", BusanTourismCodes.BIGDATA_AREA_CD);
        if (district != null) {
            params.put("signguCd", district.bigdataSignguCd());
        }
        params.put("baseYm", baseYm);
        return params;
    }

    private static Map<String, Object> pageParams() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("numOfRows", 50);
        params.put("pageNo", 1);
        return params;
    }

    public enum Language {
        KO, EN, JA, ZH;

        String baseUrl(WellnessIngestionProperties properties) {
            return switch (this) {
                case KO -> properties.tourApiBaseUrl();
                case EN -> properties.englishTourismBaseUrl();
                case JA -> properties.japaneseTourismBaseUrl();
                case ZH -> properties.chineseTourismBaseUrl();
            };
        }
    }
}
