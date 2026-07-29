package com.mediinbusan.app.data.guide

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mediinbusan.app.core.datastore.TreatmentBriefingDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// S-06 STEP04 "내 진료 브리핑 카드" 입력값. 목업의 샘플 문구를 기본값으로 둔다.
data class TreatmentBriefing(
    val visitPurpose: String = "상담/검사 문의",
    val symptoms: String = "피부 트러블, 가려움",
    val allergyMedication: String = "페니실린 알레르기 / 복용약 없음",
    val returnDate: String = "7월 28일",
    val memo: String = "빠른 검사 가능 여부 확인 희망"
)

enum class TreatmentBriefingField {
    VISIT_PURPOSE, SYMPTOMS, ALLERGY_MEDICATION, RETURN_DATE, MEMO
}

interface TreatmentBriefingRepository {
    val treatmentBriefing: Flow<TreatmentBriefing>
    suspend fun updateField(field: TreatmentBriefingField, value: String)
}

private object TreatmentBriefingKeys {
    val VISIT_PURPOSE = stringPreferencesKey("visit_purpose")
    val SYMPTOMS = stringPreferencesKey("symptoms")
    val ALLERGY_MEDICATION = stringPreferencesKey("allergy_medication")
    val RETURN_DATE = stringPreferencesKey("return_date")
    val MEMO = stringPreferencesKey("memo")

    fun keyFor(field: TreatmentBriefingField): Preferences.Key<String> = when (field) {
        TreatmentBriefingField.VISIT_PURPOSE -> VISIT_PURPOSE
        TreatmentBriefingField.SYMPTOMS -> SYMPTOMS
        TreatmentBriefingField.ALLERGY_MEDICATION -> ALLERGY_MEDICATION
        TreatmentBriefingField.RETURN_DATE -> RETURN_DATE
        TreatmentBriefingField.MEMO -> MEMO
    }
}

class TreatmentBriefingRepositoryImpl @Inject constructor(
    @TreatmentBriefingDataStore private val dataStore: DataStore<Preferences>
) : TreatmentBriefingRepository {

    override val treatmentBriefing: Flow<TreatmentBriefing> = dataStore.data.map { prefs ->
        val defaults = TreatmentBriefing()
        TreatmentBriefing(
            visitPurpose = prefs[TreatmentBriefingKeys.VISIT_PURPOSE] ?: defaults.visitPurpose,
            symptoms = prefs[TreatmentBriefingKeys.SYMPTOMS] ?: defaults.symptoms,
            allergyMedication = prefs[TreatmentBriefingKeys.ALLERGY_MEDICATION] ?: defaults.allergyMedication,
            returnDate = prefs[TreatmentBriefingKeys.RETURN_DATE] ?: defaults.returnDate,
            memo = prefs[TreatmentBriefingKeys.MEMO] ?: defaults.memo
        )
    }

    override suspend fun updateField(field: TreatmentBriefingField, value: String) {
        dataStore.edit { it[TreatmentBriefingKeys.keyFor(field)] = value }
    }
}
