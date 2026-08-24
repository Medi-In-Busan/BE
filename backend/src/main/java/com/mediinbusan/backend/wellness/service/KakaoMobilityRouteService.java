package com.mediinbusan.backend.wellness.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediinbusan.backend.wellness.dto.WellnessRouteCoordinateResponse;
import com.mediinbusan.backend.wellness.dto.WellnessRoutePointRequest;
import com.mediinbusan.backend.wellness.dto.WellnessRouteRequest;
import com.mediinbusan.backend.wellness.dto.WellnessRouteResponse;
import com.mediinbusan.backend.wellness.dto.WellnessRouteSectionResponse;
import com.mediinbusan.backend.wellness.dto.WellnessTravelMode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class KakaoMobilityRouteService {

    private static final int MIN_STOPS = 4;
    private static final int MAX_STOPS = 5;

    private final WellnessIngestionProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public KakaoMobilityRouteService(WellnessIngestionProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        this.objectMapper = new ObjectMapper();
    }

    public WellnessRouteResponse route(WellnessRouteRequest request) {
        validate(request);
        return request.mode() == WellnessTravelMode.WALKING ? walkingRoute(request) : drivingRoute(request);
    }

    private WellnessRouteResponse drivingRoute(WellnessRouteRequest request) {
        List<WellnessRoutePointRequest> stops = request.stops();
        WellnessRoutePointRequest destination = stops.getLast();
        String waypoints = stops.subList(0, stops.size() - 1).stream()
            .map(KakaoMobilityRouteService::coordinate)
            .reduce((left, right) -> left + "|" + right)
            .orElse("");

        URI uri = UriComponentsBuilder.fromUriString(properties.kakaoNaviBaseUrl())
            .path("/v1/directions")
            .queryParam("origin", coordinate(request.origin()))
            .queryParam("destination", coordinate(destination))
            .queryParam("waypoints", waypoints)
            .queryParam("priority", "RECOMMEND")
            .queryParam("summary", false)
            .build()
            .encode()
            .toUri();

        HttpRequest apiRequest = HttpRequest.newBuilder(uri)
            .header("Authorization", "KakaoAK " + properties.kakaoRestApiKey())
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(20))
            .GET()
            .build();
        try {
            HttpResponse<String> response = httpClient.send(apiRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "카카오 길찾기 API 호출 실패: HTTP " + response.statusCode()
                );
            }
            return normalizeDriving(objectMapper.readTree(response.body()));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "카카오 길찾기 API 호출이 중단되었습니다.", exception);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "카카오 길찾기 API 응답을 읽지 못했습니다.", exception);
        }
    }

    private WellnessRouteResponse walkingRoute(WellnessRouteRequest request) {
        List<WellnessRoutePointRequest> stops = request.stops();
        WellnessRoutePointRequest destination = stops.getLast();
        List<WellnessRoutePointRequest> waypoints = stops.subList(0, stops.size() - 1);
        URI uri = UriComponentsBuilder.fromUriString(properties.kakaoLocalBaseUrl())
            .path("/v2/routing/walk")
            .queryParam("start_x", request.origin().longitude())
            .queryParam("start_y", request.origin().latitude())
            .queryParam("end_x", destination.longitude())
            .queryParam("end_y", destination.latitude())
            .queryParam("via_x", waypoints.stream().map(point -> Double.toString(point.longitude())).reduce((left, right) -> left + "," + right).orElse(""))
            .queryParam("via_y", waypoints.stream().map(point -> Double.toString(point.latitude())).reduce((left, right) -> left + "," + right).orElse(""))
            .queryParam("route_mode", "ACCESSIBLE")
            .build()
            .encode()
            .toUri();
        return send(uri, "카카오 도보 길찾기", this::normalizeWalking);
    }

    private WellnessRouteResponse send(
        URI uri,
        String apiName,
        java.util.function.Function<JsonNode, WellnessRouteResponse> normalizer
    ) {
        HttpRequest request = HttpRequest.newBuilder(uri)
            .header("Authorization", "KakaoAK " + properties.kakaoRestApiKey())
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(20))
            .GET()
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, apiName + " API 호출 실패: HTTP " + response.statusCode());
            }
            return normalizer.apply(objectMapper.readTree(response.body()));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, apiName + " API 호출이 중단되었습니다.", exception);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, apiName + " API 응답을 읽지 못했습니다.", exception);
        }
    }

    private WellnessRouteResponse normalizeDriving(JsonNode root) {
        JsonNode route = root.path("routes").path(0);
        int resultCode = route.path("result_code").asInt(-1);
        if (resultCode != 0) {
            String message = route.path("result_msg").asText("경로를 찾을 수 없습니다.");
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "카카오 길찾기 실패: " + message);
        }

        JsonNode summary = route.path("summary");
        List<WellnessRouteCoordinateResponse> path = new ArrayList<>();
        List<WellnessRouteSectionResponse> sections = new ArrayList<>();
        for (JsonNode section : route.path("sections")) {
            sections.add(new WellnessRouteSectionResponse(
                section.path("distance").asInt(),
                section.path("duration").asInt()
            ));
            for (JsonNode road : section.path("roads")) {
                JsonNode vertexes = road.path("vertexes");
                for (int index = 0; index + 1 < vertexes.size(); index += 2) {
                    path.add(new WellnessRouteCoordinateResponse(
                        vertexes.get(index + 1).asDouble(),
                        vertexes.get(index).asDouble()
                    ));
                }
            }
        }
        if (path.size() < 2 || sections.size() != summary.path("waypoints").size() + 1) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "카카오 길찾기 API가 완전한 경로를 반환하지 않았습니다.");
        }
        return new WellnessRouteResponse(
            summary.path("distance").asInt(),
            summary.path("duration").asInt(),
            List.copyOf(path),
            List.copyOf(sections)
        );
    }

    private WellnessRouteResponse normalizeWalking(JsonNode root) {
        if (!"OK".equals(root.path("status").asText())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "카카오 도보 경로를 찾을 수 없습니다.");
        }
        JsonNode route = root.path("route");
        JsonNode properties = route.path("properties");
        List<WellnessRouteCoordinateResponse> path = new ArrayList<>();
        List<WellnessRouteSectionResponse> sections = new ArrayList<>();
        for (JsonNode leg : route.path("legs")) {
            JsonNode legProperties = leg.path("properties");
            sections.add(new WellnessRouteSectionResponse(
                legProperties.path("distance").asInt(),
                legProperties.path("time").asInt()
            ));
            for (JsonNode step : leg.path("steps")) {
                for (JsonNode point : step.path("path").path("points")) {
                    if (point.size() >= 2) {
                        path.add(new WellnessRouteCoordinateResponse(point.get(1).asDouble(), point.get(0).asDouble()));
                    }
                }
            }
        }
        if (path.size() < 2 || sections.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "카카오 도보 API가 완전한 경로를 반환하지 않았습니다.");
        }
        return new WellnessRouteResponse(
            properties.path("totalDistance").asInt(),
            properties.path("totalTime").asInt(),
            List.copyOf(path),
            List.copyOf(sections)
        );
    }

    private void validate(WellnessRouteRequest request) {
        if (!properties.hasKakaoKey()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "KAKAO_REST_API_KEY 환경변수가 필요합니다.");
        }
        if (request == null || request.origin() == null || request.stops() == null || request.mode() == null
            || request.stops().size() < MIN_STOPS || request.stops().size() > MAX_STOPS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "코스 장소는 4~5곳이어야 합니다.");
        }
    }

    private static String coordinate(WellnessRoutePointRequest point) {
        return point.longitude() + "," + point.latitude();
    }
}
