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

class ClovaOcrClientTest {

    private static final String API_URL = "https://ocr.example.com/general";
    private static final ClovaOcrProperties PROPERTIES = new ClovaOcrProperties(API_URL, "secret-key");

    private RestClient.Builder restClientBuilder;
    private MockRestServiceServer server;
    private ClovaOcrClient client;

    @BeforeEach
    void setUp() {
        restClientBuilder = RestClient.builder();
        server = MockRestServiceServer.bindTo(restClientBuilder).build();
        client = new ClovaOcrClient(restClientBuilder, PROPERTIES);
    }

    @Test
    void 요청에_인증_헤더와_멀티파트_바디가_포함된다() {
        server.expect(requestTo(API_URL))
            .andExpect(method(org.springframework.http.HttpMethod.POST))
            .andExpect(header("X-OCR-SECRET", "secret-key"))
            .andExpect(header("Content-Type", org.hamcrest.Matchers.containsString("multipart/form-data")))
            .andRespond(withSuccess(successBody(), MediaType.APPLICATION_JSON));

        client.recognizeText(new byte[]{1, 2, 3}, "jpg");

        server.verify();
    }

    @Test
    void 정상_응답을_ClovaOcrResponse로_역직렬화한다() {
        server.expect(requestTo(API_URL))
            .andRespond(withSuccess(successBody(), MediaType.APPLICATION_JSON));

        ClovaOcrResponse response = client.recognizeText(new byte[]{1, 2, 3}, "jpg");

        assertThat(response.images()).hasSize(1);
        ClovaOcrResponse.ImageResult image = response.images().get(0);
        assertThat(image.isSuccess()).isTrue();
        assertThat(image.fields()).extracting(ClovaOcrResponse.Field::inferText).containsExactly("환자명", "홍길동");
    }

    @Test
    void CLOVA가_401을_반환하면_인증_예외를_던진다() {
        server.expect(requestTo(API_URL))
            .andRespond(withStatus(HttpStatus.UNAUTHORIZED).body("{}").contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.recognizeText(new byte[]{1}, "jpg"))
            .isInstanceOf(ClovaOcrAuthenticationException.class);
    }

    @Test
    void CLOVA가_5xx를_반환하면_API_예외를_던진다() {
        server.expect(requestTo(API_URL)).andRespond(withServerError());

        assertThatThrownBy(() -> client.recognizeText(new byte[]{1}, "jpg"))
            .isInstanceOf(ClovaOcrApiException.class);
    }

    @Test
    void 인증정보가_없으면_CLOVA를_호출하지_않고_예외를_던진다() {
        ClovaOcrClient clientWithoutCredentials = new ClovaOcrClient(
            RestClient.builder(), new ClovaOcrProperties("", "")
        );

        assertThatThrownBy(() -> clientWithoutCredentials.recognizeText(new byte[]{1}, "jpg"))
            .isInstanceOf(ClovaOcrApiException.class);
    }

    private String successBody() {
        return """
            {
              "version": "V2",
              "requestId": "req-1",
              "timestamp": 1699999999999,
              "images": [
                {
                  "name": "document",
                  "inferResult": "SUCCESS",
                  "message": "SUCCESS",
                  "fields": [
                    {"inferText": "환자명", "inferConfidence": 0.99, "type": "NORMAL", "lineBreak": false},
                    {"inferText": "홍길동", "inferConfidence": 0.99, "type": "NORMAL", "lineBreak": true}
                  ]
                }
              ]
            }
            """;
    }
}
