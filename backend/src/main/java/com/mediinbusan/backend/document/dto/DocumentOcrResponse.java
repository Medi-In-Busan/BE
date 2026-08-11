package com.mediinbusan.backend.document.dto;

/** Android에 반환하는 OCR 결과. 번역/구조화는 이후 이슈에서 다루며, 이번 단계는 텍스트 추출까지만 담당한다. */
public record DocumentOcrResponse(
    String text
) {
}
