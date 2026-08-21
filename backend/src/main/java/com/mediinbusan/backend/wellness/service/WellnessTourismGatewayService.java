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
        Map<String, Object> params = tourismParams(null);
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
        Map<String, Object> params = tourismParams(null);
        params.put("mapX", longitude);
        params.put("mapY", latitude);
        params.put("radius", 20_000);
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
        params.put("signguCd", district.bigdataSignguCd());
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
