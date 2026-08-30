package com.mediinbusan.backend.diagnosischat.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiClientTest {

    private static final String MODEL = "gemini-2.5-flash";
    private static final String API_KEY = "test-key";
    private static final String EXPECTED_URI =
        "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent?key=" + API_KEY;
    private static final GeminiProperties PROPERTIES = new GeminiProperties(API_KEY, MODEL);

    private RestClient.Builder restClientBuilder;
    private MockRestServiceServer server;
    private GeminiClient client;

    @BeforeEach
    void setUp() {
        restClientBuilder = RestClient.builder();
        server = MockRestServiceServer.bindTo(restClientBuilder).build();
        client = new GeminiClient(restClientBuilder, PROPERTIES);
    }

    @Test
    void 요청이_지정된_모델_엔드포인트로_전송된다() {
        server.expect(requestTo(EXPECTED_URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(successBody(), MediaType.APPLICATION_JSON));

        client.extractSlots("system instruction", "피부 시술 받고 싶어요");

        server.verify();
    }

    @Test
    void 구조화_출력을_reply와_slots로_파싱한다() {
        server.expect(requestTo(EXPECTED_URI))
            .andRespond(withSuccess(successBody(), MediaType.APPLICATION_JSON));

        GeminiStructuredOutput output = client.extractSlots("system instruction", "피부 시술 받고 싶어요");

        assertThat(output.reply()).isEqualTo("피부 시술이시군요! 부산엔 며칠 정도 계실 예정인가요?");
        assertThat(output.slots().visitPurpose()).isEqualTo("SKIN_BEAUTY");
        assertThat(output.slots().stayDuration()).isNull();
        assertThat(output.slots().entryStayConditions()).isEmpty();
    }

    @Test
    void Gemini가_401을_반환하면_인증_예외를_던진다() {
        server.expect(requestTo(EXPECTED_URI))
            .andRespond(withStatus(HttpStatus.UNAUTHORIZED).body("{}").contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.extractSlots("system instruction", "hi"))
            .isInstanceOf(GeminiAuthenticationException.class);
    }

    @Test
    void Gemini가_429를_반환하면_사용량_한도_초과_예외를_던진다() {
        server.expect(requestTo(EXPECTED_URI))
            .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS).body("{}").contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.extractSlots("system instruction", "hi"))
            .isInstanceOf(GeminiRateLimitExceededException.class);
    }

    @Test
    void Gemini가_5xx를_반환하면_API_예외를_던진다() {
        server.expect(requestTo(EXPECTED_URI)).andRespond(withServerError());

        assertThatThrownBy(() -> client.extractSlots("system instruction", "hi"))
            .isInstanceOf(GeminiApiException.class);
    }

    @Test
    void 인증정보가_없으면_Gemini를_호출하지_않고_예외를_던진다() {
        GeminiClient clientWithoutCredentials = new GeminiClient(RestClient.builder(), new GeminiProperties("", ""));

        assertThatThrownBy(() -> clientWithoutCredentials.extractSlots("system instruction", "hi"))
            .isInstanceOf(GeminiApiException.class);
    }

    private String successBody() {
        return """
            {
              "candidates": [
                {
                  "content": {
                    "role": "model",
                    "parts": [
                      {
                        "text": "{\\"reply\\":\\"피부 시술이시군요! 부산엔 며칠 정도 계실 예정인가요?\\",\\"slots\\":{\\"visitPurpose\\":\\"SKIN_BEAUTY\\",\\"stayDuration\\":null,\\"reservationStatus\\":null,\\"interpretationNeed\\":null,\\"entryStayConditions\\":[]}}"
                      }
                    ]
                  }
                }
              ]
            }
            """;
    }
}
