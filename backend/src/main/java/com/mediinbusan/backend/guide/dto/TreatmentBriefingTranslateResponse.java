package com.mediinbusan.backend.guide.dto;

/**
 * 항상 한국어(target 고정)로 번역된 값을 담는다. sourceLang이 "ko"면 Papago를 호출하지 않고
 * 요청받은 원문을 그대로 담아 돌려준다. 번역 API 호출이 실패한 필드는 원문을 그대로 담는다
 * (그레이스풀 디그레이드, DocumentOcrService와 동일 정책).
 */
public record TreatmentBriefingTranslateResponse(
    String visitPurpose,
    String symptoms,
    String allergy,
    String medication,
    String memo
) {
}
