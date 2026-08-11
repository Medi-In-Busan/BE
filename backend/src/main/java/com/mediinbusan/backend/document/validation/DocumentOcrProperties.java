package com.mediinbusan.backend.document.validation;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "document.ocr")
public record DocumentOcrProperties(
    long maxImageSizeBytes,
    List<String> allowedContentTypes
) {
}
