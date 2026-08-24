package com.mediinbusan.backend.diagnosischat.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediinbusan.backend.diagnosischat.domain.EntryStayCondition;
import com.mediinbusan.backend.diagnosischat.domain.InterpretationNeed;
import com.mediinbusan.backend.diagnosischat.domain.ReservationStatus;
import com.mediinbusan.backend.diagnosischat.domain.StayDuration;
import com.mediinbusan.backend.diagnosischat.domain.VisitPurpose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Google Gemini generateContent API와의 통신만 담당한다. API Key는 여기서만 다루고, 사용자 발화/모델
 * 응답 원문은 로그에 남기지 않는다(로그에는 상태 코드/길이 등만 남긴다). 구조화 출력(responseSchema)으로
 * 형식은 강제하지만, 내용 검증(enum 화이트리스트)은 여기서 하지 않고 호출자(DiagnosisChatService)가
 * DiagnosisChatDtoMapper를 통해 수행한다 — 이 클라이언트는 순수 HTTP 통신 계층이다.
 */
@Component
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);
    private static final String GENERATE_CONTENT_URL_TEMPLATE =
        "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    private final RestClient restClient;
    private final GeminiProperties properties;
    private final ObjectMapper objectMapper;

    public GeminiClient(RestClient.Builder restClientBuilder, GeminiProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public GeminiStructuredOutput extractSlots(String systemInstructionText, String userMessage) {
        if (!properties.hasCredentials()) {
            throw new GeminiApiException("GEMINI_API_KEY 또는 GEMINI_MODEL 환경변수가 설정되지 않았습니다.");
        }

        String requestBody = writeJson(buildRequest(systemInstructionText, userMessage));
        String uri = GENERATE_CONTENT_URL_TEMPLATE.formatted(properties.model(), properties.apiKey());
        log.info("Gemini 호출: model={}", properties.model());

        try {
            GeminiGenerateContentResponse response = restClient.post()
                .uri(URI.create(uri))
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .onStatus(status -> status.value() == 401 || status.value() == 403, (req, res) -> {
                    throw new GeminiAuthenticationException("Gemini 인증에 실패했습니다. HTTP " + res.getStatusCode());
                })
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new GeminiApiException("Gemini 호출이 실패했습니다. HTTP " + res.getStatusCode());
                })
                .body(GeminiGenerateContentResponse.class);

            String text = extractText(response);
            log.info("Gemini 호출 성공: textLength={}", text.length());
            return parseStructuredOutput(text);
        } catch (GeminiAuthenticationException | GeminiApiException e) {
            throw e;
        } catch (RestClientException e) {
            log.warn("Gemini 호출 중 오류가 발생했습니다: {}", e.getClass().getSimpleName());
            throw new GeminiApiException("Gemini 호출 중 오류가 발생했습니다.", e);
        }
    }

    private String extractText(GeminiGenerateContentResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            throw new GeminiApiException("Gemini 응답에 candidate가 없습니다.");
        }
        GeminiContent content = response.candidates().get(0).content();
        if (content == null || content.parts() == null || content.parts().isEmpty()) {
            throw new GeminiApiException("Gemini 응답에 content part가 없습니다.");
        }
        String text = content.parts().get(0).text();
        if (text == null || text.isBlank()) {
            throw new GeminiApiException("Gemini 응답 text가 비어있습니다.");
        }
        return text;
    }

    private GeminiStructuredOutput parseStructuredOutput(String text) {
        try {
            return objectMapper.readValue(text, GeminiStructuredOutput.class);
        } catch (JsonProcessingException e) {
            throw new GeminiApiException("Gemini 구조화 출력 파싱에 실패했습니다.", e);
        }
    }

    private GeminiGenerateContentRequest buildRequest(String systemInstructionText, String userMessage) {
        return new GeminiGenerateContentRequest(
            GeminiContent.systemInstruction(systemInstructionText),
            List.of(GeminiContent.user(userMessage)),
            new GeminiGenerationConfig("application/json", buildResponseSchema())
        );
    }

    // enum 값 목록을 하드코딩하지 않고 Java enum 클래스에서 직접 뽑아, 도메인 enum이 바뀌면 스키마도
    // 자동으로 같이 바뀌게 한다. 이 스키마는 1차 방어일 뿐이고, 실제 검증은 DiagnosisChatDtoMapper가 한다.
    private GeminiSchema buildResponseSchema() {
        GeminiSchema slotsSchema = GeminiSchema.object(
            Map.of(
                "visitPurpose", GeminiSchema.nullableEnumString(enumNames(VisitPurpose.class)),
                "stayDuration", GeminiSchema.nullableEnumString(enumNames(StayDuration.class)),
                "reservationStatus", GeminiSchema.nullableEnumString(enumNames(ReservationStatus.class)),
                "interpretationNeed", GeminiSchema.nullableEnumString(enumNames(InterpretationNeed.class)),
                "entryStayConditions", GeminiSchema.arrayOfEnumStrings(enumNames(EntryStayCondition.class))
            ),
            List.of("visitPurpose", "stayDuration", "reservationStatus", "interpretationNeed", "entryStayConditions")
        );

        return GeminiSchema.object(
            Map.of(
                "reply", GeminiSchema.string(),
                "slots", slotsSchema
            ),
            List.of("reply", "slots")
        );
    }

    private <E extends Enum<E>> List<String> enumNames(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toList();
    }

    private String writeJson(GeminiGenerateContentRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new GeminiApiException("Gemini 요청 바디를 생성하지 못했습니다.", e);
        }
    }
}
