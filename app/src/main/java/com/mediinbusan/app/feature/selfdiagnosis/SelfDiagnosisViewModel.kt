package com.mediinbusan.app.feature.selfdiagnosis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.diagnosischat.DiagnosisChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 준비 유형 진단 챗봇 화면 상태. 백엔드(diagnosis-chat)는 세션을 저장하지 않으므로(stateless)
 * 대화 상태(messages/slots)는 이 ViewModel이 로컬로만 들고 있다가 매 턴 현재 slots 전체를
 * 실어 보낸다. 첫 인사말은 이 상태에 담지 않고 Screen이 항상 고정으로 먼저 그린다(네트워크 호출
 * 없이) — 그래서 [Restart]도 서버 호출 없이 이 상태를 초기값으로 되돌리는 것뿐이다.
 */
@HiltViewModel
class SelfDiagnosisViewModel @Inject constructor(
    private val diagnosisChatRepository: DiagnosisChatRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelfDiagnosisUiState())
    val uiState: StateFlow<SelfDiagnosisUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SelfDiagnosisEvent>()
    val events: SharedFlow<SelfDiagnosisEvent> = _events.asSharedFlow()

    fun onIntent(intent: SelfDiagnosisIntent) {
        when (intent) {
            is SelfDiagnosisIntent.UpdateInputText -> _uiState.update { it.copy(inputText = intent.text) }
            is SelfDiagnosisIntent.SendMessage -> sendMessage(intent.text)
            is SelfDiagnosisIntent.TapSuggestedReply -> sendMessage(intent.label)
            SelfDiagnosisIntent.Restart -> _uiState.update { SelfDiagnosisUiState() }
            is SelfDiagnosisIntent.ClickCta -> emitEvent(SelfDiagnosisEvent.NavigateToCtaTarget(intent.target))
            SelfDiagnosisIntent.ClickBack -> emitEvent(SelfDiagnosisEvent.NavigateBack)
            SelfDiagnosisIntent.FinishSetup -> emitEvent(SelfDiagnosisEvent.NavigateToHome)
        }
    }

    private fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || _uiState.value.isLoading) return

        // 챗봇 응답 자리를 미리 잡아두는 로딩 말풍선. Gemini 응답이 오면 새 메시지를 추가하는
        // 대신 이 pendingMessage와 같은 id를 가진 항목의 내용만 채운다 — 그래야 LazyColumn이
        // 같은 아이템으로 인식해서 말풍선이 통째로 교체되지 않고, 로딩 점 3개에서 실제 답변으로
        // 부드럽게 전환된다(등장 팝 애니메이션이 다시 재생되지 않음).
        val pendingMessage = ChatMessage(role = ChatMessageRole.ASSISTANT, text = "", isPending = true)

        _uiState.update {
            it.copy(
                messages = it.messages + ChatMessage(ChatMessageRole.USER, trimmed) + pendingMessage,
                inputText = "",
                isLoading = true,
                hasError = false
            )
        }

        viewModelScope.launch {
            val language = userPreferencesRepository.userPreferences.first().languageCode
            val slots = _uiState.value.slots
            diagnosisChatRepository.sendMessage(language, trimmed, slots).collect { result ->
                when (result) {
                    Result.Loading -> Unit
                    is Result.Success -> _uiState.update { state ->
                        state.copy(
                            messages = state.messages.map { message ->
                                if (message.id == pendingMessage.id) {
                                    message.copy(text = result.data.reply, isPending = false)
                                } else {
                                    message
                                }
                            },
                            slots = result.data.slots,
                            resultType = result.data.resultType,
                            isLoading = false
                        )
                    }
                    is Result.Error -> _uiState.update { state ->
                        state.copy(
                            messages = state.messages.filterNot { it.id == pendingMessage.id },
                            isLoading = false,
                            hasError = true
                        )
                    }
                }
            }
        }
    }

    private fun emitEvent(event: SelfDiagnosisEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}
