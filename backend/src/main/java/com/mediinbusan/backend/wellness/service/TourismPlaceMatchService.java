package com.mediinbusan.backend.wellness.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mediinbusan.backend.wellness.dto.TourismCatalogItemResponse;
import com.mediinbusan.backend.wellness.dto.TourismPlaceMatchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class TourismPlaceMatchService {
    private static final int PAGE_SIZE = 100;
    private static final int MAX_PAGES = 3;
    private static final String TOURIST_ATTRACTION_CONTENT_TYPE_ID = "12";
    private final WellnessTourismGatewayService gateway;
    private final TourismCatalogService catalogService;
    private final ObjectMapper mapper = new ObjectMapper();

    public TourismPlaceMatchService(WellnessTourismGatewayService gateway, TourismCatalogService catalogService) {
        this.gateway = gateway;
        this.catalogService = catalogService;
    }

    public TourismPlaceMatchResponse find(String title, BusanTourismCodes.District district) {
        if (title == null || title.isBlank() || title.length() > 200 || district == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A place name and Busan district are required.");
        }
        String searchName = Normalizer.normalize(title, Normalizer.Form.NFKC).strip()
            .replaceFirst("^(부산광역시|부산)\\s+", "");
        String normalizedName = normalizedName(searchName);
        if (normalizedName.length() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The place name is too short.");
        }

        // Big-data tAtsCd and TourAPI contentid are different namespaces. Never join them by ID.
        var keywords = searchKeywords(searchName, normalizedName);
        Map<String, JsonNode> matches = new LinkedHashMap<>();
        for (String keyword : keywords) {
            boolean complete = false;
            for (int page = 1; page <= MAX_PAGES; page++) {
                JsonNode body = mapper.valueToTree(gateway.searchPlaces(keyword, district, page).data());
                List<JsonNode> items = items(body);
                for (JsonNode item : items) {
                    if (samePlace(item, normalizedName, district) && !item.path("contentid").asText().isBlank()) {
                        matches.put(item.path("contentid").asText(), item);
                    }
                }
                int total = body.path("totalCount").asInt(-1);
                if ((total >= 0 && page * PAGE_SIZE >= total) || (total < 0 && items.size() < PAGE_SIZE)) {
                    complete = true;
                    break;
                }
                if (items.isEmpty()) break;
            }
            // A truncated result set cannot establish that a same-name match is unique.
            if (!complete) return TourismPlaceMatchResponse.notFound();
        }
        JsonNode selectedMatch = selectUniqueMatch(matches);
        if (selectedMatch == null) return TourismPlaceMatchResponse.notFound();

        String contentId = selectedMatch.path("contentid").asText();
        JsonNode detailBody = mapper.valueToTree(gateway.placeDetail(contentId).data());
        List<JsonNode> details = items(detailBody);
        if (details.size() != 1) return TourismPlaceMatchResponse.notFound();
        JsonNode detail = details.getFirst();
        if (!contentId.equals(detail.path("contentid").asText()) || !samePlace(detail, normalizedName, district)) {
            return TourismPlaceMatchResponse.notFound();
        }

        ObjectNode merged = ((ObjectNode) selectedMatch).deepCopy();
        detail.fields().forEachRemaining(field -> {
            if (field.getValue().isValueNode() && !field.getValue().asText().isBlank()) {
                merged.set(field.getKey(), field.getValue());
            }
        });
        String overview = merged.path("overview").asText("");
        if (!overview.isBlank()) {
            merged.put("overview", HtmlUtils.htmlUnescape(overview.replaceAll("(?i)<br\\s*/?>", "\n")
                .replaceAll("<[^>]+>", "")).strip());
        }
        ObjectNode normalizedBody = mapper.createObjectNode();
        normalizedBody.putObject("items").putArray("item").add(merged);
        TourismCatalogItemResponse item = catalogService.normalizeItems(normalizedBody).getFirst();
        Map<String, String> extra = new LinkedHashMap<>(item.details());
        for (String field : List.of("tel", "cpyrhtDivCd")) {
            String value = merged.path(field).asText("");
            if (!value.isBlank()) extra.put(field, value);
        }
        String homepage = merged.path("homepage").asText("").strip();
        if (homepage.startsWith("https://") || homepage.startsWith("http://")) {
            extra.put("homepage", homepage);
        }
        return new TourismPlaceMatchResponse(true, new TourismCatalogItemResponse(
            item.id(), item.title(), item.subtitle(), item.address(), item.imageUrl(),
            item.latitude(), item.longitude(), extra
        ));
    }

    private static boolean samePlace(JsonNode item, String name, BusanTourismCodes.District district) {
        return canonicalName(item.path("title").asText()).equals(canonicalName(name))
            && BusanTourismCodes.LDONG_REGN_CD.equals(item.path("lDongRegnCd").asText())
            && district.lDongSignguCd().equals(item.path("lDongSignguCd").asText());
    }

    private static LinkedHashSet<String> searchKeywords(String searchName, String normalizedName) {
        var keywords = new LinkedHashSet<>(List.of(searchName, normalizedName));
        if (normalizedName.startsWith("sealife")) {
            keywords.add("씨라이프" + normalizedName.substring("sealife".length()));
        }
        return keywords;
    }

    private static JsonNode selectUniqueMatch(Map<String, JsonNode> matches) {
        if (matches.size() == 1) return matches.values().iterator().next();
        List<JsonNode> attractions = matches.values().stream()
            .filter(item -> TOURIST_ATTRACTION_CONTENT_TYPE_ID.equals(item.path("contenttypeid").asText()))
            .toList();
        return attractions.size() == 1 ? attractions.getFirst() : null;
    }

    private static String canonicalName(String title) {
        return normalizedName(title).replaceFirst("^sealife", "씨라이프");
    }

    private static String normalizedName(String title) {
        return Normalizer.normalize(HtmlUtils.htmlUnescape(title), Normalizer.Form.NFKC)
            .strip().replaceFirst("^(부산광역시|부산)\\s+", "")
            .replaceAll("[^\\p{L}\\p{N}]", "").toLowerCase(Locale.ROOT);
    }

    private static List<JsonNode> items(JsonNode body) {
        JsonNode rows = body.path("items").path("item");
        if (rows.isObject()) return List.of(rows);
        List<JsonNode> result = new ArrayList<>();
        if (rows.isArray()) rows.forEach(result::add);
        return result;
    }
}
