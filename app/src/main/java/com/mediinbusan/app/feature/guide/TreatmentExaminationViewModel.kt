package com.mediinbusan.app.feature.guide

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
        viewModelScope.launch { repository.updateField(field, value) }
    }
}
