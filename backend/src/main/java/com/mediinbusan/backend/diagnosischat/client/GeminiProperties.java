package com.mediinbusan.backend.diagnosischat.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gemini")
public record GeminiProperties(
    String apiKey,
    // 2026-08 확인: gemini-2.5-flash는 신규 사용자에게 더 이상 제공되지 않아 gemini-3.6-flash로
    // 교체함(기본값은 application.yml gemini.model 참고). ListModels 목록에 있다고 generateContent
    // 호출까지 되는 건 아니므로, 모델을 바꿀 땐 반드시 실제 generateContent 호출까지 확인할 것.
    String model
) {
    public boolean hasCredentials() {
        return hasText(apiKey) && hasText(model);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
