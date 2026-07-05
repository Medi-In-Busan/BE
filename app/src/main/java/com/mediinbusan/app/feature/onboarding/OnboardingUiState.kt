package com.mediinbusan.app.feature.onboarding

data class OnboardingUiState(
    val availableLanguages: List<String> = listOf("en", "ja", "zh", "ru", "ko"),
    val selectedLanguage: String = "en",
    val availablePurposes: List<String> = listOf("피부·미용", "건강검진", "치과", "한방", "재활", "웰니스"),
    val selectedPurpose: String? = null
)
