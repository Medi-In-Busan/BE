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
        // 방문 정보를 먼저 담아 details 맵의 앞자리를 잡는다 — 상세 화면(DetailInfoCard)은 이 순서
        // 그대로 그리므로, 나중에 담으면 영업시간이 지역·요일 같은 부수 필드 아래로 밀린다.
        Map<String, String> extra = new LinkedHashMap<>();
        addVisitInfo(extra, contentId, merged.path("contenttypeid").asText(""));
        extra.putAll(item.details());
        for (String field : List.of("tel", "cpyrhtDivCd")) {
            String value = merged.path(field).asText("");
            if (!value.isBlank()) extra.put(field, value);
        }
        // homepage는 순수 URL이 아니라 <a href="...">...</a> 앵커 태그로 온다 — 예전엔 raw 값이
        // "http"로 시작하는지만 봐서, 태그가 붙은 대다수 장소에서 "관련 링크 열기" 버튼이 조용히
        // 사라졌다. 수집 경로와 같은 추출기를 쓴다.
        String homepage = WellnessIngestionService.extractUrl(merged.path("homepage").asText(""));
        if (homepage != null) {
            extra.put("homepage", homepage);
        }
        return new TourismPlaceMatchResponse(true, new TourismCatalogItemResponse(
            item.id(), item.title(), item.subtitle(), item.address(), item.imageUrl(),
            item.latitude(), item.longitude(), item.categoryCode(), extra
        ));
    }

    /**
     * detailIntro2의 방문 정보(영업시간·휴무일·대표메뉴·이용요금·주차)를 details 맵에 붙인다.
     *
     * 키 이름은 웰니스 장소 상세(WellnessPlaceResponse)와 똑같이 맞춘다 — 같은 장소를 관광 카탈로그로
     * 들어오든 웰니스 목록으로 들어오든 앱이 같은 이름으로 읽고 같은 라벨(TourismStrings.detailFieldLabels /
     * NearbyStrings)을 붙일 수 있어야 한다.
     *
     * 실패하면 조용히 넘어간다 — 방문 정보 하나 때문에 이미 매칭에 성공한 상세 전체를 못 보게 만들
     * 이유가 없다(사진·주소·지도·소개는 그대로 나간다).
     */
    private void addVisitInfo(Map<String, String> extra, String contentId, String contentTypeId) {
        if (contentTypeId.isBlank()) {
            return;
        }
        try {
            JsonNode body = mapper.valueToTree(gateway.placeIntro(contentId, contentTypeId).data());
            List<JsonNode> rows = items(body);
            if (rows.isEmpty()) {
                return;
            }
            WellnessVisitInfo visitInfo = WellnessIngestionService.toVisitInfo(rows.getFirst());
            putIfPresent(extra, "businessHours", visitInfo.businessHours());
            putIfPresent(extra, "restDate", visitInfo.restDate());
            putIfPresent(extra, "signatureMenu", visitInfo.signatureMenu());
            putIfPresent(extra, "usageFee", visitInfo.usageFee());
            putIfPresent(extra, "parkingInfo", visitInfo.parkingInfo());
        } catch (RuntimeException ignored) {
            // 위 주석 참고.
        }
    }

    private static void putIfPresent(Map<String, String> target, String key, String value) {
        if (value != null && !value.isBlank()) {
            target.put(key, value);
        }
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
