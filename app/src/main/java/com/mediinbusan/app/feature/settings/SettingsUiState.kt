package com.mediinbusan.app.feature.settings

data class SettingsUiState(
    val availableLanguages: List<String> = listOf("en", "ja", "zh", "ru", "ko"),
    val selectedLanguage: String = "en"
)
