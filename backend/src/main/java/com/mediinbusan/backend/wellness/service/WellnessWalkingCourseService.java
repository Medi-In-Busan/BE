package com.mediinbusan.backend.wellness.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediinbusan.backend.wellness.dto.WellnessWalkingCourseResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WellnessWalkingCourseService {
    private final WellnessTourismGatewayService gateway;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WellnessWalkingCourseService(WellnessTourismGatewayService gateway) {
        this.gateway = gateway;
    }

    public List<WellnessWalkingCourseResponse> getBusanCourses() {
        List<WellnessWalkingCourseResponse> courses = new ArrayList<>();
        int pageNo = 1;
        int totalCount;
        do {
            JsonNode body = objectMapper.valueToTree(gateway.walkingCourses(pageNo).data());
            JsonNode items = body.path("items").path("item");
            totalCount = body.path("totalCount").asInt();
            if (!items.isArray()) {
                break;
            }

            for (JsonNode item : items) {
                String district = item.path("sigun").asText();
                if (!district.startsWith("부산")) {
                    continue;
                }
                courses.add(new WellnessWalkingCourseResponse(
                    item.path("crsIdx").asText(),
                    item.path("crsKorNm").asText(),
                    district,
                    number(item, "crsDstnc"),
                    integer(item, "crsTotlRqrmHour"),
                    item.path("crsLevel").asText(null),
                    cleanText(item.path("crsSummary").asText(null)),
                    item.path("gpxpath").asText(null)
                ));
            }
            pageNo++;
        } while ((pageNo - 1) * 50 < totalCount);
        return courses;
    }

    private static Double number(JsonNode item, String field) {
        try {
            return Double.valueOf(item.path(field).asText());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static Integer integer(JsonNode item, String field) {
        try {
            return Integer.valueOf(item.path(field).asText());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String cleanText(String value) {
        return value == null ? null : value.replaceAll("<br\\s*/?>", " ").replaceAll("\\s+", " ").trim();
    }
}
