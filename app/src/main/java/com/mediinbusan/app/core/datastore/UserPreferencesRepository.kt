package com.mediinbusan.app.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mediinbusan.app.core.common.MedicalCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** F-002 언어 선택, F-003 의료 목적 선택, F-001 최초 실행 여부를 담는 스칼라 값들. */
data class UserPreferences(
    val languageCode: String = SupportedLanguage.DEFAULT.code,
    val onboardingComplete: Boolean = false,
    val medicalPurpose: MedicalCategory? = null,
    val notificationsEnabled: Boolean = true,
    val diagnosisComplete: Boolean = false
)

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun setLanguageCode(languageCode: String)
    suspend fun setOnboardingComplete(complete: Boolean)
    suspend fun setMedicalPurpose(purpose: MedicalCategory?)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setDiagnosisComplete(complete: Boolean)
}

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    override val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            languageCode = prefs[UserPreferencesKeys.LANGUAGE_CODE] ?: SupportedLanguage.DEFAULT.code,
            onboardingComplete = prefs[UserPreferencesKeys.ONBOARDING_COMPLETE] ?: false,
            medicalPurpose = prefs[UserPreferencesKeys.MEDICAL_PURPOSE]?.let { name ->
                MedicalCategory.entries.find { it.name == name }
            },
            notificationsEnabled = prefs[UserPreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            diagnosisComplete = prefs[UserPreferencesKeys.DIAGNOSIS_COMPLETE] ?: false
        )
    }

    override suspend fun setLanguageCode(languageCode: String) {
        dataStore.edit { it[UserPreferencesKeys.LANGUAGE_CODE] = languageCode }
    }

    override suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[UserPreferencesKeys.ONBOARDING_COMPLETE] = complete }
    }

    override suspend fun setMedicalPurpose(purpose: MedicalCategory?) {
        dataStore.edit { prefs ->
            if (purpose == null) {
                prefs.remove(UserPreferencesKeys.MEDICAL_PURPOSE)
            } else {
                prefs[UserPreferencesKeys.MEDICAL_PURPOSE] = purpose.name
            }
        }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[UserPreferencesKeys.NOTIFICATIONS_ENABLED] = enabled }
    }

    override suspend fun setDiagnosisComplete(complete: Boolean) {
        dataStore.edit { it[UserPreferencesKeys.DIAGNOSIS_COMPLETE] = complete }
    }
}
