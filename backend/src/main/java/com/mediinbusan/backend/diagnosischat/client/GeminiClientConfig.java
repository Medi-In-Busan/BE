package com.mediinbusan.backend.diagnosischat.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * GeminiClient 전용 RestClient.Builder 빈. 다른 외부 API 클라이언트(Tourism/Kakao/Papago/Clova)가 쓰는
 * {@code RestClientConfig.restClientBuilder()}(타임아웃 미설정)와는 별도로, Gemini 호출에만 타임아웃을
 * 건다 — 타임아웃 없이 Gemini가 응답을 지연시키면 요청이 사실상 무한 대기하고, 자가진단 채팅 화면의
 * 로딩 애니메이션이 끝나지 않는 최악의 상황이 생긴다. connect는 Google 인프라 기준 여유 있게, read는
 * 짧은 JSON 슬롯 추출 응답이 평소보다 느려지는 경우까지 감안해 잡았다 — 둘 다 초과 시
 * RestClientException으로 떨어져 GeminiClient의 기존 catch(RestClientException) 블록이
 * GeminiApiException(502)으로 변환한다.
 */
@Configuration
public class GeminiClientConfig {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(15);

    @Bean
    public RestClient.Builder geminiRestClientBuilder() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        return RestClient.builder().requestFactory(requestFactory);
    }
}
