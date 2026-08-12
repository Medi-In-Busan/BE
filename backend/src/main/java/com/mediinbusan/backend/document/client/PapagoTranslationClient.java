package com.mediinbusan.backend.document.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Naver Papago 번역 API와의 통신만 담당한다. Client ID/Secret은 여기서만 다루고,
 * 원문/번역문은 로그에 남기지 않는다(로그에는 대상 언어 등만 남긴다).
 * 원본 문서(진단서/처방전)는 한국어로 작성되어 있다고 가정해 출발 언어는 항상 한국어로 고정한다.
 */
@Component
public class PapagoTranslationClient {

    private static final Logger log = LoggerFactory.getLogger(PapagoTranslationClient.class);
    private static final String SOURCE_LANGUAGE = "ko";

    private final RestClient restClient;
    private final PapagoTranslationProperties properties;

    public PapagoTranslationClient(RestClient.Builder restClientBuilder, PapagoTranslationProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
    }

    public String translate(String text, String targetLanguage) {
        if (!properties.hasCredentials()) {
            throw new PapagoTranslationApiException(
                "PAPAGO_TRANSLATION_API_URL/CLIENT_ID/CLIENT_SECRET 환경변수가 설정되지 않았습니다.");
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("source", SOURCE_LANGUAGE);
        body.add("target", targetLanguage);
        body.add("text", text);

        try {
            PapagoTranslationResponse response = restClient.post()
                .uri(properties.apiUrl())
                .header("X-NCP-APIGW-API-KEY-ID", properties.clientId())
                .header("X-NCP-APIGW-API-KEY", properties.clientSecret())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .onStatus(status -> status.value() == 401 || status.value() == 403, (req, res) -> {
                    throw new PapagoTranslationAuthenticationException("Papago 번역 인증에 실패했습니다. HTTP " + res.getStatusCode());
                })
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new PapagoTranslationApiException("Papago 번역 호출이 실패했습니다. HTTP " + res.getStatusCode());
                })
                .body(PapagoTranslationResponse.class);

            String translatedText = extractTranslatedText(response);
            log.info("Papago 번역 호출 성공: targetLanguage={}", targetLanguage);
            return translatedText;
        } catch (PapagoTranslationAuthenticationException | PapagoTranslationApiException e) {
            throw e;
        } catch (RestClientException e) {
            log.warn("Papago 번역 호출 중 오류가 발생했습니다: {}", e.getClass().getSimpleName());
            throw new PapagoTranslationApiException("Papago 번역 호출 중 오류가 발생했습니다.", e);
        }
    }

    private String extractTranslatedText(PapagoTranslationResponse response) {
        if (response == null || response.message() == null || response.message().result() == null) {
            throw new PapagoTranslationApiException("Papago 번역 응답 형식이 올바르지 않습니다.");
        }
        return response.message().result().translatedText();
    }
}