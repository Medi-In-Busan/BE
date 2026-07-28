package com.mediinbusan.app.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** F-002 언어 선택, F-003 의료 목적 선택, F-001 최초 실행 여부를 담는 스칼라 값 3개. */
data class UserPreferences(
    val languageCode: String = SupportedLanguage.DEFAULT.code,
    val onboardingComplete: Boolean = false,
    val medicalPurpose: String? = null,
    val notificationsEnabled: Boolean = true
)

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun setLanguageCode(languageCode: String)
    suspend fun setOnboardingComplete(complete: Boolean)
    suspend fun setMedicalPurpose(purpose: String?)
    suspend fun setNotificationsEnabled(enabled: Boolean)
}

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    override val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            languageCode = prefs[UserPreferencesKeys.LANGUAGE_CODE] ?: SupportedLanguage.DEFAULT.code,
            onboardingComplete = prefs[UserPreferencesKeys.ONBOARDING_COMPLETE] ?: false,
            medicalPurpose = prefs[UserPreferencesKeys.MEDICAL_PURPOSE],
            notificationsEnabled = prefs[UserPreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        )
    }

    override suspend fun setLanguageCode(languageCode: String) {
        dataStore.edit { it[UserPreferencesKeys.LANGUAGE_CODE] = languageCode }
    }

    override suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[UserPreferencesKeys.ONBOARDING_COMPLETE] = complete }
    }

    override suspend fun setMedicalPurpose(purpose: String?) {
        dataStore.edit { prefs ->
            if (purpose == null) {
                prefs.remove(UserPreferencesKeys.MEDICAL_PURPOSE)
            } else {
                prefs[UserPreferencesKeys.MEDICAL_PURPOSE] = purpose
            }
        }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[UserPreferencesKeys.NOTIFICATIONS_ENABLED] = enabled }
    }
}
