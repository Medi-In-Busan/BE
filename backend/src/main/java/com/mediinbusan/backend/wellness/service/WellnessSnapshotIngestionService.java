package com.mediinbusan.backend.wellness.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediinbusan.backend.wellness.domain.WellnessExternalSnapshot;
import com.mediinbusan.backend.wellness.dto.TourismExternalResponse;
import com.mediinbusan.backend.wellness.dto.WellnessSnapshotIngestionResponse;
import com.mediinbusan.backend.wellness.repository.WellnessExternalSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class WellnessSnapshotIngestionService {
    private final WellnessTourismGatewayService gateway;
    private final WellnessExternalSnapshotRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WellnessSnapshotIngestionService(WellnessTourismGatewayService gateway, WellnessExternalSnapshotRepository repository) {
        this.gateway = gateway; this.repository = repository;
    }

    @Transactional
    public WellnessSnapshotIngestionResponse sync(String baseYm) {
        Counter counter = new Counter();
        List<String> failures = new ArrayList<>();
        syncSafely("accessibility", "BUSAN", baseYm, () -> gateway.accessibility(null), counter, failures);
        for (WellnessTourismGatewayService.Language language : WellnessTourismGatewayService.Language.values()) {
            syncSafely("places-" + language.name().toLowerCase(), "BUSAN", baseYm, () -> gateway.places(language, null, null), counter, failures);
        }
        syncSafely("photos", "BUSAN", baseYm, () -> gateway.photos("부산"), counter, failures);
        syncSafely("walking", "BUSAN", baseYm, gateway::walkingCourses, counter, failures);
        syncSafely("audio", "BUSAN", baseYm, () -> gateway.audio(35.1796, 129.0756), counter, failures);
        for (BusanTourismCodes.District district : BusanTourismCodes.districts()) {
            String scope = district.name();
            syncSafely("related", scope, baseYm, () -> gateway.related(district, baseYm), counter, failures);
            syncSafely("hubs", scope, baseYm, () -> gateway.hubs(district, baseYm), counter, failures);
            syncSafely("crowding", scope, LocalDate.now().toString(), () -> gateway.crowding(district), counter, failures);
        }
        return new WellnessSnapshotIngestionResponse(counter.inserted, counter.updated, failures.size(), failures);
    }

    private void sync(String source, String scope, String periodKey, TourismExternalResponse response, Counter counter, List<String> failures) {
        try {
            for (JsonNode item : items(objectMapper.valueToTree(response.data()))) {
                String externalId = first(item, "contentId", "contentid", "tAtsCd", "hubTatsCd", "rlteTatsCd", "courseNo", "themeId", "storyId", "galContentId");
                if (externalId == null) externalId = Integer.toHexString(item.toString().hashCode());
                String key = source + ":" + scope + ":" + periodKey + ":" + externalId;
                String title = first(item, "title", "tAtsNm", "hubTatsNm", "rlteTatsNm", "courseName", "galTitle", "name");
                Double latitude = number(item, "mapY", "mapy", "latitude", "lat");
                Double longitude = number(item, "mapX", "mapx", "longitude", "lng");
                String payload = objectMapper.writeValueAsString(item);
                var existing = repository.findBySnapshotKey(key);
                if (existing.isPresent()) {
                    existing.get().refresh(title, latitude, longitude, payload);
                    counter.updated++;
                } else {
                    repository.save(new WellnessExternalSnapshot(key, source, externalId, scope, periodKey, title, latitude, longitude, payload));
                    counter.inserted++;
                }
            }
        } catch (Exception e) { failures.add(source + "/" + scope + ": " + e.getMessage()); }
    }

    private void syncSafely(String source, String scope, String periodKey, Supplier<TourismExternalResponse> sourceCall, Counter counter, List<String> failures) {
        try { sync(source, scope, periodKey, sourceCall.get(), counter, failures); }
        catch (Exception e) { failures.add(source + "/" + scope + ": " + e.getMessage()); }
    }

    private static List<JsonNode> items(JsonNode root) {
        JsonNode items = root.path("items").path("item");
        if (items.isArray()) { List<JsonNode> result = new ArrayList<>(); items.forEach(result::add); return result; }
        if (items.isObject()) return List.of(items);
        return List.of();
    }
    private static String first(JsonNode item, String... fields) { for (String field : fields) { String value = item.path(field).asText(null); if (value != null && !value.isBlank()) return value; } return null; }
    private static Double number(JsonNode item, String... fields) { for (String field : fields) try { String value = item.path(field).asText(null); if (value != null && !value.isBlank()) return Double.valueOf(value); } catch (NumberFormatException ignored) {} return null; }
    private static final class Counter { int inserted; int updated; }
}
