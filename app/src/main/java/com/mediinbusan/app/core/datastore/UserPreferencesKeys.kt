package com.mediinbusan.app.core.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferencesKeys {
    val LANGUAGE_CODE = stringPreferencesKey("language_code")
    val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    val MEDICAL_PURPOSE = stringPreferencesKey("medical_purpose")
}
