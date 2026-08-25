package com.mediinbusan.backend.wellness.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediinbusan.backend.wellness.domain.TourismCatalogCategory;
import com.mediinbusan.backend.wellness.dto.TourismCatalogItemResponse;
import com.mediinbusan.backend.wellness.dto.TourismCatalogResponse;
import com.mediinbusan.backend.wellness.dto.TourismExternalResponse;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TourismCatalogService {

    private static final int MAX_DETAILS = 8;

    private final WellnessTourismGatewayService gateway;
    private final ObjectMapper objectMapper;

    public TourismCatalogService(WellnessTourismGatewayService gateway) {
        this.gateway = gateway;
        this.objectMapper = new ObjectMapper();
    }

    public TourismCatalogResponse getCatalog(
        TourismCatalogCategory category,
        BusanTourismCodes.District district,
        String baseYm
    ) {
        BusanTourismCodes.District resolvedDistrict = district == null ? BusanTourismCodes.District.HAEUNDAE : district;
        String resolvedBaseYm = hasText(baseYm) ? baseYm : YearMonth.now().minusMonths(2).format(DateTimeFormatter.ofPattern("yyyyMM"));

        TourismExternalResponse external = switch (category) {
            case PLACES_KO -> gateway.places(WellnessTourismGatewayService.Language.KO, district, null);
            case PLACES_EN -> gateway.places(WellnessTourismGatewayService.Language.EN, district, null);
            case PLACES_JA -> gateway.places(WellnessTourismGatewayService.Language.JA, district, null);
            case PLACES_ZH -> gateway.places(WellnessTourismGatewayService.Language.ZH, district, null);
            case ACCESSIBLE -> gateway.accessibility(district);
            case RELATED -> gateway.related(resolvedDistrict, resolvedBaseYm);
            case CROWDING -> gateway.crowding(resolvedDistrict);
            case WALKING -> gateway.walkingCourses();
        };

        List<TourismCatalogItemResponse> items = normalizeItems(objectMapper.valueToTree(external.data()));
        return new TourismCatalogResponse(
            category,
            category.title(),
            category.description(),
            external.source(),
            external.retrievedAt(),
            items
        );
    }

    private List<TourismCatalogItemResponse> normalizeItems(JsonNode body) {
        JsonNode itemNode = body.path("items").path("item");
        List<JsonNode> rawItems = new ArrayList<>();
        if (itemNode.isArray()) {
            itemNode.forEach(rawItems::add);
        } else if (itemNode.isObject()) {
            rawItems.add(itemNode);
        }

        List<TourismCatalogItemResponse> result = new ArrayList<>();
        Map<String, Integer> idOccurrences = new LinkedHashMap<>();
        for (int index = 0; index < rawItems.size(); index++) {
            TourismCatalogItemResponse item = normalizeItem(rawItems.get(index), index);
            int occurrence = idOccurrences.merge(item.id(), 1, Integer::sum);
            if (occurrence > 1) {
                item = withId(item, item.id() + "-" + occurrence);
            }
            result.add(item);
        }
        return result;
    }

    private static TourismCatalogItemResponse withId(TourismCatalogItemResponse item, String id) {
        return new TourismCatalogItemResponse(
            id,
            item.title(),
            item.subtitle(),
            item.address(),
            item.imageUrl(),
            item.latitude(),
            item.longitude(),
            item.details()
        );
    }

    private TourismCatalogItemResponse normalizeItem(JsonNode item, int index) {
        // title과 같은 이유로 id도 rlteTatsCd(실제 연관 관광지 코드)를 tAtsCd(기준 관광지 코드)보다
        // 먼저 봐야 한다 — 그렇지 않으면 RELATED의 모든 row가 같은 id(+dedup suffix)로 뭉뚱그려진다.
        // crsIdx는 WALKING(Durunubi courseList)의 실제 코스 id 필드 — "courseNo"는 어느 API에도
        // 없는 필드라 항상 미스매치였다.
        String id = first(item, "contentid", "contentId", "rlteTatsCd", "tAtsCd", "hubTatsCd", "crsIdx", "themeId", "storyId", "galContentId");
        if (!hasText(id)) {
            id = Integer.toHexString(item.toString().hashCode());
        }
        // RELATED(TarRlteTarService1) 응답엔 기준 관광지 이름(tAtsNm)과 실제 연관 관광지 이름
        // (rlteTatsNm)이 한 row에 같이 들어있다 — tAtsNm을 먼저 보면 매 row가 전부 기준 관광지
        // 이름 하나로만 보여서 "같은 데이터만 반복된다"로 보인다. rlteTatsNm을 먼저 확인한다
        // (다른 카테고리 응답엔 이 필드가 아예 없어서 순서를 바꿔도 영향 없다).
        // crsKorNm은 WALKING의 실제 코스명 필드 — "courseName"은 실존하지 않는 필드라 매번
        // 미스매치되어 WALKING 카테고리가 전부 "관광 데이터 N" 플레이스홀더로만 나오고 있었다.
        String title = first(item, "title", "rlteTatsNm", "tAtsNm", "hubTatsNm", "crsKorNm", "galTitle", "themeName", "storyTitle", "name");
        if (!hasText(title)) {
            title = "관광 데이터 " + (index + 1);
        }

        return new TourismCatalogItemResponse(
            id,
            title,
            // CROWDING(TatsCnctrRateService) 응답의 실제 혼잡도 필드명은 tatsCnctrRate가 아니라
            // cnctrRate다 — 오타 때문에 subtitle이 항상 비어서 날짜별로 다른 카드인데도 구분이 안 됐다.
            // crsSummary는 WALKING의 코스 요약 필드 — "courseBrf"는 실존하지 않는 필드였다.
            first(item, "overview", "crsSummary", "courseBrf", "galSearchKeyword", "cat3", "cnctrRate", "tatsCnctrRate", "daywkDivNm"),
            // sigun은 WALKING의 시/군 필드 — 지금까지 주소 후보에 없어서 항상 null이었다.
            first(item, "addr1", "baseAddr", "address", "roadAddr", "sigun"),
            first(item, "firstimage", "firstimage2", "galWebImageUrl", "imageUrl"),
            number(item, "mapy", "mapY", "latitude", "lat"),
            number(item, "mapx", "mapX", "longitude", "lng"),
            scalarDetails(item)
        );
    }

    private Map<String, String> scalarDetails(JsonNode item) {
        Map<String, String> details = new LinkedHashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = item.fields();
        while (fields.hasNext() && details.size() < MAX_DETAILS) {
            Map.Entry<String, JsonNode> field = fields.next();
            JsonNode value = field.getValue();
            if (!value.isValueNode() || !hasText(value.asText()) || isPresentationField(field.getKey())) {
                continue;
            }
            details.put(field.getKey(), value.asText());
        }
        return details;
    }

    private static boolean isPresentationField(String field) {
        return switch (field) {
            case "contentid", "contentId", "title", "tAtsNm", "hubTatsNm", "rlteTatsNm", "courseName",
                "galTitle", "name", "addr1", "baseAddr", "address", "roadAddr", "firstimage", "firstimage2",
                "galWebImageUrl", "imageUrl", "mapx", "mapX", "mapy", "mapY", "latitude", "longitude",
                "cnctrRate",
                // WALKING(Durunubi courseList) 전용: crsIdx/crsKorNm/crsSummary/sigun은 이미
                // id/title/subtitle/address로 뽑혀서 details에 또 나올 필요가 없고, crsContents·
                // crsTourInfo·travelerinfo·routeIdx·brdDiv는 장문 텍스트/내부 코드라 details 8개
                // 슬롯을 이걸로 채우면 정작 거리·소요시간·난이도·GPX 링크가 밀려서 안 보인다.
                "crsIdx", "crsKorNm", "crsSummary", "sigun",
                "crsContents", "crsTourInfo", "travelerinfo", "routeIdx", "brdDiv" -> true;
            default -> false;
        };
    }

    private static String first(JsonNode item, String... fields) {
        for (String field : fields) {
            JsonNode value = item.path(field);
            if (value.isValueNode() && hasText(value.asText())) {
                return value.asText();
            }
        }
        return null;
    }

    private static Double number(JsonNode item, String... fields) {
        String value = first(item, fields);
        if (!hasText(value)) {
            return null;
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
