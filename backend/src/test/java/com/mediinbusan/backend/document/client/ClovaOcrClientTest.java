package com.mediinbusan.backend.document.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ClovaOcrClientTest {

    private static final String API_URL = "https://ocr.example.com/general";
    private static final ClovaOcrProperties PROPERTIES = new ClovaOcrProperties(API_URL, "secret-key", true);

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
    void 표_인식_설정이_요청_메시지_루트에_실린다() {
        // images[] 안이 아니라 루트에 있어야 CLOVA가 tables를 내려준다(공식 문서 기준).
        server.expect(requestTo(API_URL))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("\"enableTableDetection\":true")))
            .andRespond(withSuccess(successBody(), MediaType.APPLICATION_JSON));

        client.recognizeText(new byte[]{1, 2, 3}, "jpg");

        server.verify();
    }

    @Test
    void 표_인식을_끄면_요청에_false로_실린다() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer disabledServer = MockRestServiceServer.bindTo(builder).build();
        ClovaOcrClient clientWithoutTableDetection =
            new ClovaOcrClient(builder, new ClovaOcrProperties(API_URL, "secret-key", false));
        disabledServer.expect(requestTo(API_URL))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("\"enableTableDetection\":false")))
            .andRespond(withSuccess(successBody(), MediaType.APPLICATION_JSON));

        clientWithoutTableDetection.recognizeText(new byte[]{1, 2, 3}, "jpg");

        disabledServer.verify();
    }

    @Test
    void 표_응답의_셀과_boundingPoly를_역직렬화한다() {
        server.expect(requestTo(API_URL))
            .andRespond(withSuccess(tableBody(), MediaType.APPLICATION_JSON));

        ClovaOcrResponse response = client.recognizeText(new byte[]{1, 2, 3}, "jpg");

        ClovaOcrResponse.ImageResult image = response.images().get(0);
        assertThat(image.fields().get(0).boundingPoly().vertices()).hasSize(4);
        assertThat(image.tables()).hasSize(1);
        ClovaOcrResponse.Cell cell = image.tables().get(0).cells().get(0);
        assertThat(cell.rowIndex()).isZero();
        assertThat(cell.columnIndex()).isZero();
        assertThat(cell.cellTextLines().get(0).cellWords().get(0).inferText()).isEqualTo("약품명");
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
            RestClient.builder(), new ClovaOcrProperties("", "", true)
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

    private String tableBody() {
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
                    {
                      "inferText": "처방전",
                      "inferConfidence": 0.99,
                      "type": "NORMAL",
                      "lineBreak": true,
                      "boundingPoly": {
                        "vertices": [{"x": 10, "y": 20}, {"x": 90, "y": 20}, {"x": 90, "y": 45}, {"x": 10, "y": 45}]
                      }
                    }
                  ],
                  "tables": [
                    {
                      "cells": [
                        {
                          "rowIndex": 0,
                          "columnIndex": 0,
                          "rowSpan": 1,
                          "columnSpan": 1,
                          "boundingPoly": {
                            "vertices": [{"x": 10, "y": 100}, {"x": 200, "y": 100}, {"x": 200, "y": 130}, {"x": 10, "y": 130}]
                          },
                          "cellTextLines": [
                            {
                              "cellWords": [
                                {"inferText": "약품명", "inferConfidence": 0.98}
                              ]
                            }
                          ]
                        }
                      ]
                    }
                  ]
                }
              ]
            }
            """;
    }
}
