package com.mediinbusan.app.feature.settings

import com.mediinbusan.app.core.datastore.SupportedLanguages

data class SettingsUiState(
    val availableLanguages: List<String> = SupportedLanguages.CODES,
    val selectedLanguage: String = SupportedLanguages.DEFAULT
)
