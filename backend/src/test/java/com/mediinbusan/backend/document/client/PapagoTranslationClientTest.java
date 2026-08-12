package com.mediinbusan.backend.document.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PapagoTranslationClientTest {

    private static final String API_URL = "https://papago.example.com/translate";
    private static final PapagoTranslationProperties PROPERTIES =
        new PapagoTranslationProperties(API_URL, "client-id", "client-secret");

    private RestClient.Builder restClientBuilder;
    private MockRestServiceServer server;
    private PapagoTranslationClient client;

    @BeforeEach
    void setUp() {
        restClientBuilder = RestClient.builder();
        server = MockRestServiceServer.bindTo(restClientBuilder).build();
        client = new PapagoTranslationClient(restClientBuilder, PROPERTIES);
    }

    @Test
    void 요청에_인증_헤더와_폼바디가_포함된다() {
        server.expect(requestTo(API_URL))
            .andExpect(method(org.springframework.http.HttpMethod.POST))
            .andExpect(header("X-NCP-APIGW-API-KEY-ID", "client-id"))
            .andExpect(header("X-NCP-APIGW-API-KEY", "client-secret"))
            .andExpect(header("Content-Type", org.hamcrest.Matchers.containsString("application/x-www-form-urlencoded")))
            .andRespond(withSuccess(successBody(), MediaType.APPLICATION_JSON));

        client.translate("환자명 홍길동", "en");

        server.verify();
    }

    @Test
    void 정상_응답에서_번역된_텍스트를_반환한다() {
        server.expect(requestTo(API_URL))
            .andRespond(withSuccess(successBody(), MediaType.APPLICATION_JSON));

        String result = client.translate("환자명 홍길동", "en");

        assertThat(result).isEqualTo("Patient name Hong Gil-dong");
    }

    @Test
    void Papago가_401을_반환하면_인증_예외를_던진다() {
        server.expect(requestTo(API_URL))
            .andRespond(withStatus(HttpStatus.UNAUTHORIZED).body("{}").contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.translate("환자명 홍길동", "en"))
            .isInstanceOf(PapagoTranslationAuthenticationException.class);
    }

    @Test
    void Papago가_5xx를_반환하면_API_예외를_던진다() {
        server.expect(requestTo(API_URL)).andRespond(withServerError());

        assertThatThrownBy(() -> client.translate("환자명 홍길동", "en"))
            .isInstanceOf(PapagoTranslationApiException.class);
    }

    @Test
    void 인증정보가_없으면_Papago를_호출하지_않고_예외를_던진다() {
        PapagoTranslationClient clientWithoutCredentials = new PapagoTranslationClient(
            RestClient.builder(), new PapagoTranslationProperties("", "", "")
        );

        assertThatThrownBy(() -> clientWithoutCredentials.translate("환자명 홍길동", "en"))
            .isInstanceOf(PapagoTranslationApiException.class);
    }

    private String successBody() {
        return """
            {
              "message": {
                "result": {
                  "srcLangType": "ko",
                  "tarLangType": "en",
                  "translatedText": "Patient name Hong Gil-dong"
                }
              }
            }
            """;
    }
}
