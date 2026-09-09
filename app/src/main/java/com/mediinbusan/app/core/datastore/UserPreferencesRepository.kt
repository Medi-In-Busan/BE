package com.mediinbusan.app.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mediinbusan.app.core.common.MedicalCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** F-002 언어 선택, F-003 의료 목적 선택을 담는 스칼라 값들. */
data class UserPreferences(
    val languageCode: String = SupportedLanguage.DEFAULT.code,
    val medicalPurpose: MedicalCategory? = null,
    val notificationsEnabled: Boolean = true,
    /** 앱 접근권한 사전 고지 화면을 이미 확인했는지(최초 실행 1회 노출 판단용). */
    val permissionNoticeAcknowledged: Boolean = false,
    /** 카메라 권한을 시스템에 한 번이라도 요청했는지(UserPreferencesKeys 주석 참고). */
    val cameraPermissionRequested: Boolean = false
)

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun setLanguageCode(languageCode: String)
    suspend fun setMedicalPurpose(purpose: MedicalCategory?)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setPermissionNoticeAcknowledged(acknowledged: Boolean)
    suspend fun setCameraPermissionRequested(requested: Boolean)
}

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    override val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            languageCode = prefs[UserPreferencesKeys.LANGUAGE_CODE] ?: SupportedLanguage.DEFAULT.code,
            medicalPurpose = prefs[UserPreferencesKeys.MEDICAL_PURPOSE]?.let { stored ->
                // enum 도입 전에는 라벨 문자열("피부·미용" 등)을 그대로 저장했다. 기존 저장값이
                // 업데이트 후 조용히 null이 되지 않도록 name 매칭 실패 시 label로도 조회한다.
                MedicalCategory.entries.find { it.name == stored }
                    ?: MedicalCategory.entries.find { it.label == stored }
            },
            notificationsEnabled = prefs[UserPreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            permissionNoticeAcknowledged = prefs[UserPreferencesKeys.PERMISSION_NOTICE_ACKNOWLEDGED] ?: false,
            cameraPermissionRequested = prefs[UserPreferencesKeys.CAMERA_PERMISSION_REQUESTED] ?: false
        )
    }

    override suspend fun setLanguageCode(languageCode: String) {
        dataStore.edit { it[UserPreferencesKeys.LANGUAGE_CODE] = languageCode }
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

    override suspend fun setPermissionNoticeAcknowledged(acknowledged: Boolean) {
        dataStore.edit { it[UserPreferencesKeys.PERMISSION_NOTICE_ACKNOWLEDGED] = acknowledged }
    }

    override suspend fun setCameraPermissionRequested(requested: Boolean) {
        dataStore.edit { it[UserPreferencesKeys.CAMERA_PERMISSION_REQUESTED] = requested }
    }
}
