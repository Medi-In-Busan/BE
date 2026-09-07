package com.mediinbusan.backend.wellness.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(TourismExternalClient.class);

    // apis.data.go.kr은 TCP 연결이 붙은 뒤 TLS 핸드셰이크에만 9초 넘게 걸리는 경우가 실측된다.
    // JDK HttpClient의 connectTimeout은 TCP + TLS 핸드셰이크를 함께 덮으므로, 예전 값(5초)은
    // 서버가 정상 응답하는 상황에서도 핸드셰이크 도중 HttpConnectTimeoutException으로 끊었다.
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(12);
    // HttpRequest.timeout은 연결 수립까지 포함한 "요청 전체"의 상한이라 CONNECT_TIMEOUT보다
    // 반드시 커야 한다 — 더 작으면 이쪽이 먼저 터져서 connectTimeout 설정이 무의미해진다.
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(20);
    // 연결 자체가 간헐적으로 실패하는 API라 IOException(연결/읽기 실패)만 한 번 더 시도한다.
    // API가 에러 코드를 담아 정상 응답한 경우는 재시도 대상이 아니다(같은 답이 온다).
    // 시도 수를 늘리면 최악 지연이 MAX_ATTEMPTS * REQUEST_TIMEOUT까지 늘어나므로 2를 넘기지 말 것.
    private static final int MAX_ATTEMPTS = 2;
    private static final Duration RETRY_BACKOFF = Duration.ofMillis(500);

    private final WellnessIngestionProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public TourismExternalClient(WellnessIngestionProperties properties) {
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();
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

        HttpResponse<String> response = send(builder.build(true).toUri());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "관광공사 API 호출 실패: HTTP " + response.statusCode());
        }

        try {
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
        }
    }

    /**
     * 연결/읽기 실패(IOException)만 MAX_ATTEMPTS까지 재시도한다. URI에는 serviceKey가 들어있으므로
     * 로그에 절대 싣지 않고 예외 요약만 남긴다.
     */
    private HttpResponse<String> send(URI uri) {
        IOException lastFailure = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .build();
            try {
                return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                lastFailure = e;
                log.warn(
                    "관광공사 API 호출 실패 ({}/{} 시도): {}",
                    attempt,
                    MAX_ATTEMPTS,
                    e.getClass().getSimpleName() + ": " + e.getMessage()
                );
                if (attempt < MAX_ATTEMPTS) {
                    backOff();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "관광공사 API 호출이 중단되었습니다.", e);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "관광공사 API 응답을 읽지 못했습니다.", lastFailure);
    }

    private static void backOff() {
        try {
            Thread.sleep(RETRY_BACKOFF.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "관광공사 API 호출이 중단되었습니다.", e);
        }
    }
}
