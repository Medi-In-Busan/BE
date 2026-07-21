package com.mediinbusan.app.feature.settings

import com.mediinbusan.app.core.datastore.SupportedLanguage

data class SettingsUiState(
    val availableLanguages: List<String> = SupportedLanguage.CODES,
    val selectedLanguage: String = SupportedLanguage.DEFAULT.code
)
