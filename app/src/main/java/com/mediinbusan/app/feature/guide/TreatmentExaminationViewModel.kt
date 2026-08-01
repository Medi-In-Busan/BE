package com.mediinbusan.app.feature.guide

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.data.guide.TreatmentBriefing
import com.mediinbusan.app.data.guide.TreatmentBriefingField
import com.mediinbusan.app.data.guide.TreatmentBriefingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TreatmentExaminationViewModel @Inject constructor(
    private val repository: TreatmentBriefingRepository
) : ViewModel() {

    val briefing: StateFlow<TreatmentBriefing> = repository.treatmentBriefing
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TreatmentBriefing())

    fun updateField(field: TreatmentBriefingField, value: String) {
        viewModelScope.launch {
            // 디스크 오류 등으로 저장이 실패해도 화면이 죽지 않도록 방어.
            runCatching { repository.updateField(field, value) }
                .onFailure { Log.w(TAG, "진료 브리핑 저장 실패: $field", it) }
        }
    }

    private companion object {
        const val TAG = "TreatmentExaminationVM"
    }
}
