package com.mediinbusan.backend.wellness.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

/** 관광공사 GW 응답을 그대로 전달하는 웰니스 도메인 전용 클라이언트. */
@Component
public class TourismExternalClient {

    private final WellnessIngestionProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public TourismExternalClient(WellnessIngestionProperties properties) {
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    public Object get(String baseUrl, String operation, Map<String, ?> queryParameters) {
        if (!properties.hasTourApiKey()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TOURISM_API_SERVICE_KEY 환경변수가 필요합니다.");
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
            .path("/" + operation)
            .queryParam("serviceKey", properties.tourApiServiceKey())
            .queryParam("_type", "json");
        queryParameters.forEach((key, value) -> {
            if (value != null) {
                // serviceKey는 공공데이터포털에서 제공한 인코딩 값을 유지하고,
                // 검색어 같은 일반 파라미터만 UTF-8로 인코딩한다.
                builder.queryParam(key, UriUtils.encodeQueryParam(value.toString(), StandardCharsets.UTF_8));
            }
        });

        try {
            URI uri = builder.build(true).toUri();
            HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(12))
                .GET()
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "관광공사 API 호출 실패: HTTP " + response.statusCode());
            }

            JsonNode responseJson = objectMapper.readTree(response.body());
            JsonNode header = responseJson.path("response").path("header");
            if (header.hasNonNull("resultCode") && !"0000".equals(header.path("resultCode").asText())) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "관광공사 API 오류: " + header.path("resultMsg").asText());
            }
            if (responseJson.hasNonNull("resultCode") && !"0000".equals(responseJson.path("resultCode").asText())) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "관광공사 API 오류: " + responseJson.path("resultMsg").asText());
            }
            JsonNode serviceError = responseJson.path("OpenAPI_ServiceResponse").path("cmmMsgHeader");
            if (!serviceError.isMissingNode() && serviceError.hasNonNull("returnReasonCode")) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "관광공사 API 오류: " + serviceError.path("returnAuthMsg").asText());
            }
            return objectMapper.convertValue(responseJson.path("response").path("body"), Object.class);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "관광공사 API 응답을 읽지 못했습니다.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "관광공사 API 호출이 중단되었습니다.", e);
        }
    }
}
