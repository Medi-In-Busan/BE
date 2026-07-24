package com.mediinbusan.app.feature.settings

import com.mediinbusan.app.core.datastore.SupportedLanguage

data class NotificationSettingsUiState(
    val notificationsEnabled: Boolean = true,
    val selectedLanguage: String = SupportedLanguage.DEFAULT.code
)
