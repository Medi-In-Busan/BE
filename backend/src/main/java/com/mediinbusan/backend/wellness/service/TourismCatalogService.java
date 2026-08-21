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

    private static final double BUSAN_CENTER_LATITUDE = 35.1796;
    private static final double BUSAN_CENTER_LONGITUDE = 129.0756;
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
            case HUBS -> gateway.hubs(resolvedDistrict, resolvedBaseYm);
            case CROWDING -> gateway.crowding(resolvedDistrict);
            case PHOTOS -> gateway.photos("부산");
            case WALKING -> gateway.walkingCourses();
            case AUDIO -> gateway.audio(BUSAN_CENTER_LATITUDE, BUSAN_CENTER_LONGITUDE);
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
        String id = first(item, "contentid", "contentId", "tAtsCd", "hubTatsCd", "rlteTatsCd", "courseNo", "themeId", "storyId", "galContentId");
        if (!hasText(id)) {
            id = Integer.toHexString(item.toString().hashCode());
        }
        String title = first(item, "title", "tAtsNm", "hubTatsNm", "rlteTatsNm", "courseName", "galTitle", "themeName", "storyTitle", "name");
        if (!hasText(title)) {
            title = "관광 데이터 " + (index + 1);
        }

        return new TourismCatalogItemResponse(
            id,
            title,
            first(item, "overview", "courseBrf", "galSearchKeyword", "cat3", "tatsCnctrRate", "daywkDivNm"),
            first(item, "addr1", "baseAddr", "address", "roadAddr"),
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
                "galWebImageUrl", "imageUrl", "mapx", "mapX", "mapy", "mapY", "latitude", "longitude" -> true;
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
