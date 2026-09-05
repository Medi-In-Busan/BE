package com.mediinbusan.backend.document.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Naver CLOVA OCR General API와의 통신만 담당한다. Secret Key는 여기서만 다루고,
 * 이미지 원본/OCR 결과 원문은 로그에 남기지 않는다(로그에는 상태 코드/길이 등만 남긴다).
 */
@Component
public class ClovaOcrClient {

    private static final Logger log = LoggerFactory.getLogger(ClovaOcrClient.class);
    private static final String REQUEST_IMAGE_NAME = "document";

    private final RestClient restClient;
    private final ClovaOcrProperties properties;
    private final ObjectMapper objectMapper;

    // spring-boot-starter-jackson은 Jackson 3(tools.jackson.databind.ObjectMapper) 빈만 자동구성해서
    // 이 Jackson 2 ObjectMapper는 DI로 주입받지 않고 WellnessIngestionService와 같은 방식으로 직접 만든다.
    public ClovaOcrClient(RestClient.Builder restClientBuilder, ClovaOcrProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
    }

    public ClovaOcrResponse recognizeText(byte[] imageBytes, String imageFormat) {
        if (!properties.hasCredentials()) {
            throw new ClovaOcrApiException("CLOVA_OCR_API_URL 또는 CLOVA_OCR_SECRET_KEY 환경변수가 설정되지 않았습니다.");
        }

        MultiValueMap<String, Object> body = buildRequestBody(imageBytes, imageFormat);

        try {
            ClovaOcrResponse response = restClient.post()
                .uri(properties.apiUrl())
                .header("X-OCR-SECRET", properties.secretKey())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .onStatus(status -> status.value() == 401 || status.value() == 403, (req, res) -> {
                    throw new ClovaOcrAuthenticationException("CLOVA OCR 인증에 실패했습니다. HTTP " + res.getStatusCode());
                })
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new ClovaOcrApiException("CLOVA OCR 호출이 실패했습니다. HTTP " + res.getStatusCode());
                })
                .body(ClovaOcrResponse.class);
            // 표 인식은 계정 도메인 설정을 타므로, 실제로 tables가 내려왔는지 개수만 남겨둔다
            // (0이 계속 찍히면 enableTableDetection이 먹지 않는 계정이라는 신호다).
            log.info("CLOVA OCR 호출 성공: imageCount={}, tableCount={}", imageCount(response), tableCount(response));
            return response;
        } catch (ClovaOcrAuthenticationException | ClovaOcrApiException e) {
            throw e;
        } catch (RestClientException e) {
            log.warn("CLOVA OCR 호출 중 오류가 발생했습니다: {}", e.getClass().getSimpleName());
            throw new ClovaOcrApiException("CLOVA OCR 호출 중 오류가 발생했습니다.", e);
        }
    }

    private int imageCount(ClovaOcrResponse response) {
        return response != null && response.images() != null ? response.images().size() : 0;
    }

    private int tableCount(ClovaOcrResponse response) {
        if (response == null || response.images() == null) {
            return 0;
        }
        return response.images().stream()
            .map(ClovaOcrResponse.ImageResult::tables)
            .filter(tables -> tables != null)
            .mapToInt(List::size)
            .sum();
    }

    private MultiValueMap<String, Object> buildRequestBody(byte[] imageBytes, String imageFormat) {
        ClovaOcrRequestMessage message =
            ClovaOcrRequestMessage.of(imageFormat, REQUEST_IMAGE_NAME, properties.enableTableDetection());

        HttpHeaders messagePartHeaders = new HttpHeaders();
        messagePartHeaders.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("message", new HttpEntity<>(writeJson(message), messagePartHeaders));
        body.add("file", new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return REQUEST_IMAGE_NAME + "." + imageFormat;
            }
        });
        return body;
    }

    private String writeJson(ClovaOcrRequestMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new ClovaOcrApiException("CLOVA OCR 요청 메시지를 생성하지 못했습니다.", e);
        }
    }
}
