package com.mediinbusan.app.feature.selfdiagnosis

import com.mediinbusan.app.data.diagnosischat.DiagnosisSlotsDto

enum class ChatMessageRole { USER, ASSISTANT }

/** role은 순수 로컬 UI 개념(말풍선 정렬)일 뿐 서버로 전송되지 않는다 — 서버에는 그 턴의
 *  userMessage 하나만 보낸다(DiagnosisChatRequestDto 참고). */
data class ChatMessage(
    val role: ChatMessageRole,
    val text: String
)

data class SelfDiagnosisUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val slots: DiagnosisSlotsDto = DiagnosisSlotsDto(),
    val resultType: DiagnosisResultType? = null
) {
    val isResultVisible: Boolean
        get() = resultType != null

    val canSend: Boolean
        get() = inputText.isNotBlank() && !isLoading

    /** 현재 채워지지 않은 첫 슬롯의 선택지를 추천 답변 칩으로 노출한다. 서버가 다음 질문 대상을
     *  계산하는 고정 순서(visitPurpose -> stayDuration -> reservationStatus -> interpretationNeed)와
     *  동일한 순서를 클라이언트에서도 그대로 따른다 — 서버는 이 칩 데이터를 내려주지 않는다. */
    val suggestedOptions: List<DiagnosisAnswerOption>?
        get() = when {
            isResultVisible -> null
            slots.visitPurpose == null -> DiagnosisAnswerOption.VisitPurpose.entries.toList()
            slots.stayDuration == null -> DiagnosisAnswerOption.StayDuration.entries.toList()
            slots.reservationStatus == null -> DiagnosisAnswerOption.ReservationStatus.entries.toList()
            slots.interpretationNeed == null -> DiagnosisAnswerOption.InterpretationNeed.entries.toList()
            else -> null
        }
}
