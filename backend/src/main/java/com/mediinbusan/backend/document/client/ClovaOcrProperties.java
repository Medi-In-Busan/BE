package com.mediinbusan.backend.document.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param enableTableDetection 표 인식(요청 루트의 enableTableDetection) 사용 여부. 처방전 약품 표를
 *                             셀 단위로 받기 위해 기본값은 true지만, 계정 도메인이 표 인식을 지원하지
 *                             않아 호출이 실패하면 재배포 없이 환경변수로 끌 수 있게 설정으로 뺐다.
 */
@ConfigurationProperties(prefix = "clova.ocr")
public record ClovaOcrProperties(
    String apiUrl,
    String secretKey,
    boolean enableTableDetection
) {
    public boolean hasCredentials() {
        return hasText(apiUrl) && hasText(secretKey);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
