package com.mediinbusan.backend.document.client;

import java.util.List;
import java.util.UUID;

/** CLOVA OCR General 요청의 "message" 파트(JSON)에 대응한다. */
public record ClovaOcrRequestMessage(
    String version,
    String requestId,
    long timestamp,
    List<RequestImage> images
) {
    private static final String VERSION = "V2";

    public static ClovaOcrRequestMessage of(String imageFormat, String imageName) {
        return new ClovaOcrRequestMessage(
            VERSION,
            UUID.randomUUID().toString(),
            System.currentTimeMillis(),
            List.of(new RequestImage(imageFormat, imageName))
        );
    }

    public record RequestImage(String format, String name) {
    }
}
