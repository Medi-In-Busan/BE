package com.mediinbusan.app.data.guide

data class GuideStep(
    val id: String,
    val phase: GuidePhase,
    val title: String,
    val content: String,
    val languageCode: String,
    val sortOrder: Int
)

enum class GuidePhase {
    BEFORE_ARRIVAL, RECEPTION, TREATMENT, PAYMENT, AFTERCARE
}
