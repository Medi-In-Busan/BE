package com.mediinbusan.backend.document.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Naver Cloud Platform Papago NMT(번역) API 설정.
 * apiUrl은 CLOVA OCR과 달리 계정별 커스텀 URL이 아닌 고정된 공개 엔드포인트
 * (https://papago.apigw.ntruss.com/nmt/v1/translation, application.yml 기본값 참고)라 보통 그대로 쓰면 되고,
 * clientId/clientSecret만 NCP 콘솔에서 발급받아 환경변수로 주입한다.
 */
@ConfigurationProperties(prefix = "papago.translation")
public record PapagoTranslationProperties(
    String apiUrl,
    String clientId,
    String clientSecret
) {
    public boolean hasCredentials() {
        return hasText(apiUrl) && hasText(clientId) && hasText(clientSecret);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}