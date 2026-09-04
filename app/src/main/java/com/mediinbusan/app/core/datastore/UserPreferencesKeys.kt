package com.mediinbusan.app.core.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferencesKeys {
    val LANGUAGE_CODE = stringPreferencesKey("language_code")
    val MEDICAL_PURPOSE = stringPreferencesKey("medical_purpose")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
}
