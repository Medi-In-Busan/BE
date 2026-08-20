package com.mediinbusan.app.data.guide

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mediinbusan.app.core.datastore.TreatmentBriefingDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

// S-06 STEP04 "내 진료 브리핑 카드" 입력값. 값이 비어있을 때 보여줄 언어별 예시 문구는
// core/i18n/GuideStrings의 treatmentBriefingDefaults를 화면에서 조회한다(리포지토리는 언어를 모른다).
data class TreatmentBriefing(
    val visitPurpose: String = "",
    val symptoms: String = "",
    val allergy: String = "",
    val medication: String = "",
    val returnDate: String = "",
    val memo: String = ""
)

enum class TreatmentBriefingField {
    VISIT_PURPOSE, SYMPTOMS, ALLERGY, MEDICATION, RETURN_DATE, MEMO
}

interface TreatmentBriefingRepository {
    val treatmentBriefing: Flow<TreatmentBriefing>
    suspend fun updateField(field: TreatmentBriefingField, value: String)
}

private object TreatmentBriefingKeys {
    val VISIT_PURPOSE = stringPreferencesKey("visit_purpose")
    val SYMPTOMS = stringPreferencesKey("symptoms")
    val ALLERGY = stringPreferencesKey("allergy")
    val MEDICATION = stringPreferencesKey("medication")
    val RETURN_DATE = stringPreferencesKey("return_date")
    val MEMO = stringPreferencesKey("memo")

    fun keyFor(field: TreatmentBriefingField): Preferences.Key<String> = when (field) {
        TreatmentBriefingField.VISIT_PURPOSE -> VISIT_PURPOSE
        TreatmentBriefingField.SYMPTOMS -> SYMPTOMS
        TreatmentBriefingField.ALLERGY -> ALLERGY
        TreatmentBriefingField.MEDICATION -> MEDICATION
        TreatmentBriefingField.RETURN_DATE -> RETURN_DATE
        TreatmentBriefingField.MEMO -> MEMO
    }
}

class TreatmentBriefingRepositoryImpl @Inject constructor(
    @TreatmentBriefingDataStore private val dataStore: DataStore<Preferences>
) : TreatmentBriefingRepository {

    override val treatmentBriefing: Flow<TreatmentBriefing> = dataStore.data
        // 파일 손상·디스크 오류(IOException) 시 빈 값으로 복구해 기본값으로 대체한다.
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs ->
            val defaults = TreatmentBriefing()
            TreatmentBriefing(
                visitPurpose = prefs[TreatmentBriefingKeys.VISIT_PURPOSE] ?: defaults.visitPurpose,
                symptoms = prefs[TreatmentBriefingKeys.SYMPTOMS] ?: defaults.symptoms,
                allergy = prefs[TreatmentBriefingKeys.ALLERGY] ?: defaults.allergy,
                medication = prefs[TreatmentBriefingKeys.MEDICATION] ?: defaults.medication,
                returnDate = prefs[TreatmentBriefingKeys.RETURN_DATE] ?: defaults.returnDate,
                memo = prefs[TreatmentBriefingKeys.MEMO] ?: defaults.memo
            )
        }

    override suspend fun updateField(field: TreatmentBriefingField, value: String) {
        dataStore.edit { it[TreatmentBriefingKeys.keyFor(field)] = value }
    }
}
