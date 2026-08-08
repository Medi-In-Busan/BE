package com.mediinbusan.backend.wellness.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediinbusan.backend.hospital.domain.Coordinates;
import com.mediinbusan.backend.wellness.domain.WellnessPlace;
import com.mediinbusan.backend.wellness.domain.WellnessPlaceType;
import com.mediinbusan.backend.wellness.dto.WellnessIngestionResponse;
import com.mediinbusan.backend.wellness.repository.WellnessPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class WellnessIngestionService {

    private static final String BUSAN_AREA_CODE = "6";
    private static final DateTimeFormatter TOUR_API_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final WellnessPlaceRepository wellnessPlaceRepository;
    private final WellnessIngestionProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WellnessIngestionService(
        WellnessPlaceRepository wellnessPlaceRepository,
        WellnessIngestionProperties properties
    ) {
        this.wellnessPlaceRepository = wellnessPlaceRepository;
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }

    @Transactional
    public WellnessIngestionResponse ingest() {
        if (!properties.hasTourApiKey() && !properties.hasKakaoKey()) {
            throw new ResponseStatusException(BAD_REQUEST, "TOURISM_API_SERVICE_KEY 또는 KAKAO_REST_API_KEY 환경변수가 필요합니다.");
        }

        List<WellnessPlaceCandidate> tourCandidates = properties.hasTourApiKey()
            ? fetchTourApiCandidates()
            : List.of();
        List<WellnessPlaceCandidate> kakaoCandidates = properties.hasKakaoKey()
            ? fetchKakaoCandidates()
            : List.of();

        Map<String, WellnessPlaceCandidate> candidatesById = new LinkedHashMap<>();
        tourCandidates.forEach(candidate -> candidatesById.put(candidate.contentId(), candidate));
        kakaoCandidates.forEach(candidate -> candidatesById.putIfAbsent(candidate.contentId(), candidate));

        int inserted = 0;
        int updated = 0;
        int skipped = 0;

        for (WellnessPlaceCandidate candidate : candidatesById.values()) {
            if (!candidate.isValid()) {
                skipped++;
                continue;
            }

            var existing = wellnessPlaceRepository.findByContentId(candidate.contentId());
            if (existing.isPresent()) {
                existing.get().updateFrom(
                    candidate.name(),
                    candidate.placeType(),
                    candidate.address(),
                    candidate.coordinates(),
                    candidate.imageUrl(),
                    candidate.description(),
                    candidate.phoneNumber(),
                    candidate.modifiedDate()
                );
                updated++;
            } else {
                wellnessPlaceRepository.save(new WellnessPlace(
                    candidate.contentId(),
                    candidate.name(),
                    candidate.placeType(),
                    candidate.address(),
                    candidate.coordinates(),
                    candidate.imageUrl(),
                    candidate.description(),
                    candidate.phoneNumber(),
                    candidate.modifiedDate()
                ));
                inserted++;
            }
        }

        return new WellnessIngestionResponse(
            tourCandidates.size(),
            kakaoCandidates.size(),
            inserted,
            updated,
            skipped,
            wellnessPlaceRepository.count()
        );
    }

    private List<WellnessPlaceCandidate> fetchTourApiCandidates() {
        List<WellnessPlaceCandidate> candidates = new ArrayList<>();
        fetchTourApiList("12", WellnessPlaceType.TOURIST_ATTRACTION, candidates);
        fetchTourApiList("39", WellnessPlaceType.RESTAURANT, candidates);
        fetchTourApiList("32", WellnessPlaceType.LODGING, candidates);
        fetchTourApiList("38", WellnessPlaceType.SHOPPING, candidates);
        return candidates;
    }

    private void fetchTourApiList(String contentTypeId, WellnessPlaceType placeType, List<WellnessPlaceCandidate> candidates) {
        URI uri = UriComponentsBuilder
            .fromUriString(properties.tourApiBaseUrl())
            .path("/areaBasedList2")
            .queryParam("serviceKey", properties.tourApiServiceKey())
            .queryParam("MobileOS", "ETC")
            .queryParam("MobileApp", "MediInBusan")
            .queryParam("_type", "json")
            .queryParam("areaCode", BUSAN_AREA_CODE)
            .queryParam("contentTypeId", contentTypeId)
            .queryParam("numOfRows", properties.tourApiRowsPerType())
            .queryParam("pageNo", 1)
            .build(true)
            .toUri();

        JsonNode items = readJson(uri, null).at("/response/body/items/item");
        asArray(items).forEach(item -> candidates.add(toTourCandidate(item, placeType)));
    }

    private WellnessPlaceCandidate toTourCandidate(JsonNode item, WellnessPlaceType placeType) {
        String contentId = text(item, "contentid");
        return new WellnessPlaceCandidate(
            "tour-" + contentId,
            text(item, "title"),
            placeType,
            firstNonBlank(text(item, "addr1"), text(item, "addr2")),
            coordinates(doubleValue(item, "mapy"), doubleValue(item, "mapx")),
            firstNonBlank(text(item, "firstimage"), text(item, "firstimage2")),
            null,
            text(item, "tel"),
            parseTourApiDate(text(item, "modifiedtime"))
        );
    }

    private List<WellnessPlaceCandidate> fetchKakaoCandidates() {
        List<WellnessPlaceCandidate> candidates = new ArrayList<>();
        for (String keyword : properties.kakaoKeywords()) {
            URI uri = UriComponentsBuilder
                .fromUriString(properties.kakaoLocalBaseUrl())
                .path("/v2/local/search/keyword.json")
                .queryParam("query", "부산 " + keyword)
                .queryParam("size", properties.kakaoRowsPerKeyword())
                .build()
                .toUri();

            JsonNode documents = readJson(uri, "KakaoAK " + properties.kakaoRestApiKey()).path("documents");
            asArray(documents).forEach(item -> candidates.add(toKakaoCandidate(item, keyword)));
        }
        return candidates;
    }

    private WellnessPlaceCandidate toKakaoCandidate(JsonNode item, String keyword) {
        String id = text(item, "id");
        return new WellnessPlaceCandidate(
            "kakao-" + id,
            text(item, "place_name"),
            kakaoType(text(item, "category_group_code"), keyword),
            firstNonBlank(text(item, "road_address_name"), text(item, "address_name")),
            coordinates(doubleValue(item, "y"), doubleValue(item, "x")),
            null,
            text(item, "place_url"),
            text(item, "phone"),
            LocalDate.now()
        );
    }

    private JsonNode readJson(URI uri, String authorization) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(uri).GET();
            if (authorization != null) {
                builder.header("Authorization", authorization);
            }
            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("외부 API 응답 실패: HTTP " + response.statusCode());
            }
            return objectMapper.readTree(response.body());
        } catch (IOException e) {
            throw new IllegalStateException("외부 API 응답을 파싱하지 못했습니다.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("외부 API 호출이 중단되었습니다.", e);
        }
    }

    private WellnessPlaceType kakaoType(String categoryGroupCode, String keyword) {
        String normalized = keyword.toLowerCase();
        if (normalized.contains("스파") || normalized.contains("온천") || normalized.contains("마사지")) {
            return WellnessPlaceType.SPA;
        }
        if (categoryGroupCode == null || categoryGroupCode.isBlank()) {
            return normalized.contains("산책") || normalized.contains("공원")
                ? WellnessPlaceType.WALK
                : WellnessPlaceType.OTHER;
        }
        return switch (categoryGroupCode) {
            case "AT4" -> WellnessPlaceType.TOURIST_ATTRACTION;
            case "FD6", "CE7" -> WellnessPlaceType.RESTAURANT;
            case "AD5" -> WellnessPlaceType.LODGING;
            default -> normalized.contains("산책") || normalized.contains("공원")
                ? WellnessPlaceType.WALK
                : WellnessPlaceType.OTHER;
        };
    }

    private List<JsonNode> asArray(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return List.of();
        }
        if (node.isArray()) {
            List<JsonNode> values = new ArrayList<>();
            node.forEach(values::add);
            return values;
        }
        return List.of(node);
    }

    private Coordinates coordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        return new Coordinates(latitude, longitude);
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isMissingNode() || value.isNull()) {
            return null;
        }
        String text = value.asText();
        return text.isBlank() ? null : text;
    }

    private static Double doubleValue(JsonNode node, String field) {
        String value = text(node, field);
        if (value == null) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static LocalDate parseTourApiDate(String value) {
        if (value == null || value.length() < 8) {
            return LocalDate.now();
        }
        return LocalDate.parse(value.substring(0, 8), TOUR_API_DATE);
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second;
    }
}
