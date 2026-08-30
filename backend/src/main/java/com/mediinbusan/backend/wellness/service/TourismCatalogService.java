package com.mediinbusan.backend.wellness.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mediinbusan.backend.wellness.domain.WellnessExternalSnapshot;
import com.mediinbusan.backend.wellness.domain.TourismCatalogCategory;
import com.mediinbusan.backend.wellness.dto.TourismCatalogItemResponse;
import com.mediinbusan.backend.wellness.dto.TourismCatalogResponse;
import com.mediinbusan.backend.wellness.dto.TourismExternalResponse;
import com.mediinbusan.backend.wellness.repository.WellnessExternalSnapshotRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.LocalDate;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class TourismCatalogService {

    private static final double BUSAN_CENTER_LATITUDE = 35.1796;
    private static final double BUSAN_CENTER_LONGITUDE = 129.0756;
    private static final int MAX_DETAILS = 8;
    private static final String CROWDING_CACHE_SOURCE = "crowding-catalog";
    private static final String CROWDING_CACHE_SCOPE = "BUSAN";
    private static final int HOT_PLACE_LIMIT = 5;
    private static final String IMAGE_LOOKUP_ATTEMPTED = "imageLookupAttempted";

    private final WellnessTourismGatewayService gateway;
    private final WellnessExternalSnapshotRepository snapshotRepository;
    private final ObjectMapper objectMapper;

    public TourismCatalogService(
        WellnessTourismGatewayService gateway,
        WellnessExternalSnapshotRepository snapshotRepository
    ) {
        this.gateway = gateway;
        this.snapshotRepository = snapshotRepository;
        this.objectMapper = new ObjectMapper();
    }

    public TourismCatalogResponse getCatalog(
        TourismCatalogCategory category,
        BusanTourismCodes.District district,
        String baseYm
    ) {
        if (category == TourismCatalogCategory.CROWDING && district == null) {
            return getBusanCrowdingCatalog();
        }
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
        if (category == TourismCatalogCategory.CROWDING) {
            items = todayCrowdingItems(items, LocalDate.now());
        }
        return new TourismCatalogResponse(
            category,
            category.title(),
            category.description(),
            external.source(),
            external.retrievedAt(),
            items
        );
    }

    private synchronized TourismCatalogResponse getBusanCrowdingCatalog() {
        LocalDate today = LocalDate.now();
        String snapshotKey = crowdingSnapshotKey(today);
        var todaySnapshot = snapshotRepository.findBySnapshotKey(snapshotKey);
        if (todaySnapshot.isPresent()) {
            TourismCatalogResponse cached = cachedCrowdingResponse(todaySnapshot.get());
            List<TourismCatalogItemResponse> enrichedItems = enrichHotPlaceImages(cached.items());
            if (!enrichedItems.equals(cached.items())) {
                todaySnapshot.get().refresh(
                    TourismCatalogCategory.CROWDING.title(),
                    null,
                    null,
                    serializeCrowdingItems(enrichedItems)
                );
                snapshotRepository.save(todaySnapshot.get());
                return withCrowdingItems(cached, enrichedItems, todaySnapshot.get().syncedAt());
            }
            return cached;
        }

        List<TourismCatalogItemResponse> items = new ArrayList<>();
        RuntimeException firstFailure = null;
        Instant retrievedAt = null;
        for (BusanTourismCodes.District district : BusanTourismCodes.districts()) {
            try {
                TourismExternalResponse external = gateway.crowding(district);
                items.addAll(normalizeItems(objectMapper.valueToTree(external.data())));
                if (retrievedAt == null) {
                    retrievedAt = external.retrievedAt();
                }
            } catch (RuntimeException exception) {
                firstFailure = exception;
                break;
            }
        }

        if (firstFailure == null && !items.isEmpty()) {
            items = enrichHotPlaceImages(todayCrowdingItems(items, today));
            TourismCatalogResponse response = new TourismCatalogResponse(
                TourismCatalogCategory.CROWDING,
                TourismCatalogCategory.CROWDING.title(),
                TourismCatalogCategory.CROWDING.description(),
                "crowding-forecast",
                retrievedAt,
                items
            );
            snapshotRepository.save(new WellnessExternalSnapshot(
                snapshotKey,
                CROWDING_CACHE_SOURCE,
                CROWDING_CACHE_SCOPE,
                CROWDING_CACHE_SCOPE,
                today.toString(),
                TourismCatalogCategory.CROWDING.title(),
                null,
                null,
                serializeCrowdingItems(items)
            ));
            return response;
        }

        var latestSnapshot = snapshotRepository
            .findTopBySourceAndScopeOrderBySyncedAtDesc(CROWDING_CACHE_SOURCE, CROWDING_CACHE_SCOPE);
        if (latestSnapshot.isPresent()) {
            return cachedCrowdingResponse(latestSnapshot.get());
        }
        if (firstFailure != null) {
            throw firstFailure;
        }
        return new TourismCatalogResponse(
            TourismCatalogCategory.CROWDING,
            TourismCatalogCategory.CROWDING.title(),
            TourismCatalogCategory.CROWDING.description(),
            "crowding-forecast",
            retrievedAt,
            List.of()
        );
    }

    private String serializeCrowdingItems(List<TourismCatalogItemResponse> items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (Exception exception) {
            throw new IllegalStateException("혼잡도 캐시를 직렬화하지 못했습니다.", exception);
        }
    }

    private TourismCatalogResponse cachedCrowdingResponse(WellnessExternalSnapshot snapshot) {
        try {
            List<TourismCatalogItemResponse> cachedItems = objectMapper.readValue(
                snapshot.payload(),
                new TypeReference<>() {}
            );
            List<TourismCatalogItemResponse> items = cachedItems.stream()
                .map(item -> new TourismCatalogItemResponse(
                    item.id(),
                    item.title(),
                    item.subtitle(),
                    item.address(),
                    secureImageUrl(item.imageUrl()),
                    item.latitude(),
                    item.longitude(),
                    item.categoryCode(),
                    item.details()
                ))
                .toList();
            items = todayCrowdingItems(items, LocalDate.now());
            return new TourismCatalogResponse(
                TourismCatalogCategory.CROWDING,
                TourismCatalogCategory.CROWDING.title(),
                TourismCatalogCategory.CROWDING.description(),
                "crowding-forecast",
                snapshot.syncedAt(),
                items
            );
        } catch (Exception exception) {
            throw new IllegalStateException("저장된 혼잡도 캐시를 읽지 못했습니다.", exception);
        }
    }

    private TourismCatalogResponse withCrowdingItems(
        TourismCatalogResponse response,
        List<TourismCatalogItemResponse> items,
        Instant retrievedAt
    ) {
        return new TourismCatalogResponse(
            response.category(),
            response.title(),
            response.description(),
            response.source(),
            retrievedAt,
            items
        );
    }

    private List<TourismCatalogItemResponse> enrichHotPlaceImages(List<TourismCatalogItemResponse> items) {
        List<TourismCatalogItemResponse> candidates = items.stream()
            .collect(java.util.stream.Collectors.toMap(
                item -> canonicalTitle(item.title()),
                item -> item,
                (left, right) -> crowdingRate(left) >= crowdingRate(right) ? left : right,
                LinkedHashMap::new
            ))
            .values()
            .stream()
            .sorted(Comparator.comparingDouble(TourismCatalogService::crowdingRate).reversed())
            .limit(HOT_PLACE_LIMIT)
            .toList();

        Map<String, TourismCatalogItemResponse> matches = new HashMap<>();
        Map<String, Boolean> attemptedTitles = new HashMap<>();
        for (TourismCatalogItemResponse candidate : candidates) {
            if (candidate.imageUrl() != null || "true".equals(candidate.details().get(IMAGE_LOOKUP_ATTEMPTED))) {
                continue;
            }
            String titleKey = canonicalTitle(candidate.title());
            attemptedTitles.put(titleKey, true);
            BusanTourismCodes.District district = districtForCrowdingItem(candidate);
            try {
                String keyword = tourismSearchKeyword(candidate.title());
                TourismExternalResponse search = gateway.searchPlaces(keyword, district, 1);
                List<TourismCatalogItemResponse> searchItems = normalizeItems(objectMapper.valueToTree(search.data()));
                TourismCatalogItemResponse match = searchItems.stream()
                    .filter(item -> canonicalTitle(item.title()).equals(canonicalTitle(keyword)))
                    .max(Comparator.comparing(item -> item.imageUrl() != null))
                    .orElseGet(() -> searchItems.stream()
                        .filter(item -> item.imageUrl() != null)
                        .filter(item -> canonicalTitle(item.title()).contains(canonicalTitle(keyword)) ||
                            canonicalTitle(keyword).contains(canonicalTitle(item.title())))
                        .findFirst()
                        .orElse(null));
                if (match != null) {
                    matches.put(titleKey, match);
                }
            } catch (RuntimeException ignored) {
                // 혼잡도 자체는 유효하므로 사진 보강 실패가 TOP 5 응답을 막지 않게 한다.
            }
        }

        if (attemptedTitles.isEmpty()) {
            return items;
        }
        return items.stream().map(item -> {
            String titleKey = canonicalTitle(item.title());
            if (!attemptedTitles.containsKey(titleKey)) {
                return item;
            }
            TourismCatalogItemResponse match = matches.get(titleKey);
            Map<String, String> details = new LinkedHashMap<>(item.details());
            details.put(IMAGE_LOOKUP_ATTEMPTED, "true");
            if (match == null) {
                return new TourismCatalogItemResponse(
                    item.id(), item.title(), item.subtitle(), item.address(), item.imageUrl(),
                    item.latitude(), item.longitude(), item.categoryCode(), details
                );
            }
            return new TourismCatalogItemResponse(
                item.id(),
                item.title(),
                item.subtitle(),
                match.address(),
                match.imageUrl(),
                match.latitude(),
                match.longitude(),
                item.categoryCode(),
                details
            );
        }).toList();
    }

    private static List<TourismCatalogItemResponse> todayCrowdingItems(
        List<TourismCatalogItemResponse> items,
        LocalDate today
    ) {
        String targetDate = today.format(DateTimeFormatter.BASIC_ISO_DATE);
        return items.stream()
            .filter(item -> targetDate.equals(normalizeDate(item.details().get("baseYmd"))))
            .collect(java.util.stream.Collectors.toMap(
                item -> canonicalTitle(item.title()),
                item -> item,
                (left, right) -> crowdingRate(left) >= crowdingRate(right) ? left : right,
                LinkedHashMap::new
            ))
            .values()
            .stream()
            .sorted(Comparator.comparingDouble(TourismCatalogService::crowdingRate).reversed())
            .toList();
    }

    private static String normalizeDate(String rawDate) {
        return rawDate == null ? "" : rawDate.replaceAll("[^0-9]", "");
    }

    private static BusanTourismCodes.District districtForCrowdingItem(TourismCatalogItemResponse item) {
        String signguCode = item.details().get("signguCd");
        for (BusanTourismCodes.District district : BusanTourismCodes.District.values()) {
            if (district.bigdataSignguCd().equals(signguCode)) {
                return district;
            }
        }
        return BusanTourismCodes.District.HAEUNDAE;
    }

    private static String tourismSearchKeyword(String title) {
        if (title.toUpperCase().contains("SEA LIFE")) {
            return "씨라이프부산아쿠아리움";
        }
        return title.replaceAll("\\s*\\([^)]*\\)\\s*", "").trim();
    }

    private static String canonicalTitle(String title) {
        return title == null ? "" : title
            .toLowerCase()
            .replaceAll("\\([^)]*\\)", "")
            .replaceAll("[^0-9a-z가-힣]", "");
    }

    private static double crowdingRate(TourismCatalogItemResponse item) {
        String raw = item.details().getOrDefault("cnctrRate", item.subtitle());
        if (!hasText(raw)) {
            return Double.NEGATIVE_INFINITY;
        }
        try {
            return Double.parseDouble(raw.replace(",", ""));
        } catch (NumberFormatException ignored) {
            return Double.NEGATIVE_INFINITY;
        }
    }

    private static String crowdingSnapshotKey(LocalDate date) {
        return CROWDING_CACHE_SOURCE + ":" + CROWDING_CACHE_SCOPE + ":" + date;
    }

    List<TourismCatalogItemResponse> normalizeItems(JsonNode body) {
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
            item.categoryCode(),
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
            secureImageUrl(first(item, "firstimage", "firstimage2", "galWebImageUrl", "imageUrl")),
            number(item, "mapy", "mapY", "latitude", "lat"),
            number(item, "mapx", "mapX", "longitude", "lng"),
            // contenttypeid(12=관광지, 14=문화시설, 25=여행코스, 28=레포츠, 32=숙박, 38=쇼핑,
            // 39=음식점) — PLACES_KO/ACCESSIBLE 카테고리 필터 칩에 쓴다. scalarDetails()의 8개
            // 캡에 걸려 누락될 수 있어 별도 필드로 명시적으로 뽑는다.
            first(item, "contenttypeid"),
            scalarDetails(item)
        );
    }

    private static String secureImageUrl(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("http://")) {
            return imageUrl;
        }
        return "https://" + imageUrl.substring("http://".length());
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
                "cnctrRate", "contenttypeid",
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
