package com.mediinbusan.app.feature.selfdiagnosis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 준비 유형 진단(인트로 → 5문항 → 준비 유형) 화면 상태. Repository 연동 없이 로컬 답변만으로 동작한다. */
@HiltViewModel
class SelfDiagnosisViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SelfDiagnosisUiState())
    val uiState: StateFlow<SelfDiagnosisUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SelfDiagnosisEvent>()
    val events: SharedFlow<SelfDiagnosisEvent> = _events.asSharedFlow()

    fun onIntent(intent: SelfDiagnosisIntent) {
        when (intent) {
            SelfDiagnosisIntent.StartDiagnosis -> _uiState.update { it.copy(hasStarted = true) }
            is SelfDiagnosisIntent.SelectAnswer -> selectAnswer(intent.questionId, intent.option)
            SelfDiagnosisIntent.GoNext -> goNext()
            SelfDiagnosisIntent.Restart -> restart()
            is SelfDiagnosisIntent.ClickCta -> emitEvent(SelfDiagnosisEvent.NavigateToCtaTarget(intent.target))
            SelfDiagnosisIntent.ClickBack -> clickBack()
        }
    }

    private fun selectAnswer(questionId: DiagnosisQuestionId, option: DiagnosisAnswerOption) {
        _uiState.update { state ->
            val question = state.questions.first { it.id == questionId }
            val current = state.selectedAnswers[questionId].orEmpty()
            val updated = if (question.isMultiSelect) {
                if (option in current) current - option else current + option
            } else {
                setOf(option)
            }
            state.copy(selectedAnswers = state.selectedAnswers + (questionId to updated))
        }
    }

    private fun goNext() {
        _uiState.update { state ->
            if (!state.canGoNext) {
                state
            } else if (state.isLastQuestion) {
                state.copy(resultType = DiagnosisTypeMapper.map(state.selectedAnswers))
            } else {
                state.copy(currentQuestionIndex = state.currentQuestionIndex + 1)
            }
        }
    }

    private fun restart() {
        _uiState.update { SelfDiagnosisUiState() }
    }

    private fun clickBack() {
        val state = _uiState.value
        when {
            state.isResultVisible -> _uiState.update { it.copy(resultType = null) }
            state.isIntroVisible -> emitEvent(SelfDiagnosisEvent.NavigateBack)
            !state.isFirstQuestion -> _uiState.update { it.copy(currentQuestionIndex = it.currentQuestionIndex - 1) }
            else -> _uiState.update { it.copy(hasStarted = false) }
        }
    }

    private fun emitEvent(event: SelfDiagnosisEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}