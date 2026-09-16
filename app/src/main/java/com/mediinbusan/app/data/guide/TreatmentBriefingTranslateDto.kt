package com.mediinbusan.app.data.guide

import kotlinx.serialization.Serializable

/**
 * POST /api/v1/guide/treatment-briefing/translate 요청.
 * sourceLang은 [com.mediinbusan.app.core.datastore.SupportedLanguage.code](ko/en/zh/ja)를 그대로 보낸다 —
 * zh -> zh-CN 같은 Papago 언어 코드 변환은 백엔드(TreatmentBriefingTranslationService)가 담당하므로
 * 문서스캔의 toPapagoLanguageCode 같은 변환 로직을 여기서 따로 둘 필요가 없다.
 */
@Serializable
data class TreatmentBriefingTranslateRequestDto(
    val sourceLang: String,
    val visitPurpose: String,
    val symptoms: String,
    val allergy: String,
    val medication: String,
    val memo: String
)

/** 응답 필드는 항상 한국어(target 고정)다. sourceLang이 ko면 백엔드가 Papago를 호출하지 않고 원문을 그대로 돌려준다. */
@Serializable
data class TreatmentBriefingTranslateResponseDto(
    val visitPurpose: String,
    val symptoms: String,
    val allergy: String,
    val medication: String,
    val memo: String
)
