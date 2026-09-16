package com.mediinbusan.backend.guide.dto;

/**
 * S-06 STEP04 "내 진료 카드" 번역 요청. sourceLang은 Android SupportedLanguage.code(ko/en/zh/ja)를
 * 그대로 받는다 — Papago 언어 코드(zh-CN 등) 변환은 서비스에서 처리한다.
 * returnDate(귀국·체류 일정)는 날짜 데이터라 번역 대상이 아니므로 포함하지 않는다.
 */
public record TreatmentBriefingTranslateRequest(
    String sourceLang,
    String visitPurpose,
    String symptoms,
    String allergy,
    String medication,
    String memo
) {
}
