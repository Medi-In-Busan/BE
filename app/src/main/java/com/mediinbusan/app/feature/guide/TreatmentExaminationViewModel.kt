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
import kotlinx.coroutines.Job
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

    // 진행 중인 번역 요청을 들고 있다가 원문이 바뀌거나 번역 결과를 닫을 때 취소한다 — 이 참조가 없으면
    // updateField()가 상태만 초기화해도 기존 collect는 계속 실행되어, 요청이 뒤늦게 끝나면 수정 전
    // 원문의 번역 결과가 다시 나타난다.
    private var translationJob: Job? = null

    fun updateField(field: TreatmentBriefingField, value: String) {
        viewModelScope.launch {
            // 디스크 오류 등으로 저장이 실패해도 화면이 죽지 않도록 방어.
            runCatching { repository.updateField(field, value) }
                .onFailure { Log.w(TAG, "진료 브리핑 저장 실패: $field", it) }
        }
        // 원문을 다시 수정하면 이전 번역 결과가 최신 값과 어긋나므로 진행 중인 번역을 취소하고 비워 재번역을 유도한다.
        cancelTranslation()
    }

    fun translateToKorean() {
        if (_translationUiState.value.isTranslating) return
        translationJob?.cancel()
        // collect의 첫 Result.Loading emit을 기다리지 않고 동기적으로 표시해, 이 함수가 다시 호출되는
        // 사이의 좁은 창에서도 위 가드가 즉시 유효하게 만든다.
        _translationUiState.update { TreatmentTranslationUiState(isTranslating = true) }
        translationJob = viewModelScope.launch {
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
        cancelTranslation()
    }

    private fun cancelTranslation() {
        translationJob?.cancel()
        translationJob = null
        _translationUiState.update { TreatmentTranslationUiState() }
    }

    private companion object {
        const val TAG = "TreatmentExaminationVM"
    }
}
