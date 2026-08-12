package com.mediinbusan.backend.document.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clova.ocr")
public record ClovaOcrProperties(
    String apiUrl,
    String secretKey
) {
    public boolean hasCredentials() {
        return hasText(apiUrl) && hasText(secretKey);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
