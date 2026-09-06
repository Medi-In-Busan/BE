package com.mediinbusan.backend.document.client;

import java.util.List;
import java.util.UUID;

/**
 * CLOVA OCR General 요청의 "message" 파트(JSON)에 대응한다.
 * enableTableDetection은 images[] 안이 아니라 <b>요청 루트</b>에 놓는다(공식 문서 기준) —
 * images[] 안에 넣으면 CLOVA가 조용히 무시해서 tables가 안 내려온다.
 */
public record ClovaOcrRequestMessage(
    String version,
    String requestId,
    long timestamp,
    boolean enableTableDetection,
    List<RequestImage> images
) {
    private static final String VERSION = "V2";

    public static ClovaOcrRequestMessage of(String imageFormat, String imageName, boolean enableTableDetection) {
        return new ClovaOcrRequestMessage(
            VERSION,
            UUID.randomUUID().toString(),
            System.currentTimeMillis(),
            enableTableDetection,
            List.of(new RequestImage(imageFormat, imageName))
        );
    }

    public record RequestImage(String format, String name) {
    }
}
