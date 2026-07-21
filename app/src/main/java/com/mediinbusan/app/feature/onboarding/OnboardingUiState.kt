package com.mediinbusan.app.feature.onboarding

import com.mediinbusan.app.core.datastore.SupportedLanguage

data class OnboardingUiState(
    val availableLanguages: List<String> = SupportedLanguage.CODES,
    val selectedLanguage: String = SupportedLanguage.DEFAULT.code,
    val availablePurposes: List<String> = listOf("피부·미용", "건강검진", "치과", "한방", "재활", "웰니스"),
    val selectedPurpose: String? = null
)
