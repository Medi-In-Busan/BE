package com.mediinbusan.app.feature.guide

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.guide.TreatmentBriefing
import com.mediinbusan.app.data.guide.TreatmentBriefingField
import com.mediinbusan.app.data.guide.TreatmentBriefingRepository
import com.mediinbusan.app.data.guide.TreatmentBriefingTranslation
import com.mediinbusan.app.data.guide.TreatmentBriefingTranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** "한국어로 보기" 결과. 원문(TreatmentBriefing)과 별도로 들고 있어 원문을 절대 덮어쓰지 않는다. */
data class TreatmentTranslationUiState(
    val isTranslating: Boolean = false,
    val translation: TreatmentBriefingTranslation? = null,
    val isTranslationError: Boolean = false
)

@HiltViewModel
class TreatmentExaminationViewModel @Inject constructor(
    private val repository: TreatmentBriefingRepository,
    private val translationRepository: TreatmentBriefingTranslationRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val briefing: StateFlow<TreatmentBriefing> = repository.treatmentBriefing
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TreatmentBriefing())

    private val _translationUiState = MutableStateFlow(TreatmentTranslationUiState())
    val translationUiState: StateFlow<TreatmentTranslationUiState> = _translationUiState

    fun updateField(field: TreatmentBriefingField, value: String) {
        viewModelScope.launch {
            // 디스크 오류 등으로 저장이 실패해도 화면이 죽지 않도록 방어.
            runCatching { repository.updateField(field, value) }
                .onFailure { Log.w(TAG, "진료 브리핑 저장 실패: $field", it) }
        }
        // 원문을 다시 수정하면 이전 번역 결과가 최신 값과 어긋나므로 비워 재번역을 유도한다.
        _translationUiState.update { TreatmentTranslationUiState() }
    }

    fun translateToKorean() {
        if (_translationUiState.value.isTranslating) return
        viewModelScope.launch {
            val sourceLanguage = userPreferencesRepository.userPreferences.first().languageCode
            translationRepository.translateToKorean(briefing.value, sourceLanguage).collect { result ->
                _translationUiState.update { state ->
                    when (result) {
                        is Result.Loading -> state.copy(isTranslating = true, isTranslationError = false)
                        is Result.Success -> TreatmentTranslationUiState(translation = result.data)
                        is Result.Error -> {
                            Log.w(TAG, "진료 브리핑 번역 실패", result.throwable)
                            state.copy(isTranslating = false, isTranslationError = true)
                        }
                    }
                }
            }
        }
    }

    fun dismissTranslation() {
        _translationUiState.update { TreatmentTranslationUiState() }
    }

    private companion object {
        const val TAG = "TreatmentExaminationVM"
    }
}
