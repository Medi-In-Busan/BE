package com.mediinbusan.app.feature.selfdiagnosis

enum class DiagnosisQuestionId {
    Q1_VISIT_PURPOSE,
    Q2_STAY_DURATION,
    Q3_RESERVATION_STATUS,
    Q4_INTERPRETATION_NEED,
    Q5_ENTRY_STAY_CONDITION
}

data class DiagnosisQuestion(
    val id: DiagnosisQuestionId,
    val title: String,
    val description: String,
    val options: List<DiagnosisAnswerOption>,
    // Q5(입국·체류 관련 상황)만 해당 사항을 복수로 고를 수 있어 체크박스형, 나머지는 라디오형.
    val isMultiSelect: Boolean = false,
    val noticeText: String? = null
)

val DiagnosisQuestions: List<DiagnosisQuestion> = listOf(
    DiagnosisQuestion(
        id = DiagnosisQuestionId.Q1_VISIT_PURPOSE,
        title = "어떤 방문 목적을 고려하고 있나요?",
        description = "가장 가까운 목적을 선택해 주세요. 나중에 다시 변경할 수 있습니다.",
        options = DiagnosisAnswerOption.VisitPurpose.entries
    ),
    DiagnosisQuestion(
        id = DiagnosisQuestionId.Q2_STAY_DURATION,
        title = "한국에 얼마나 머무를 예정인가요?",
        description = "체류기간에 따라 준비해야 할 항목이 달라질 수 있어요.",
        options = DiagnosisAnswerOption.StayDuration.entries
    ),
    DiagnosisQuestion(
        id = DiagnosisQuestionId.Q3_RESERVATION_STATUS,
        title = "현재 병원 예약 상태는 어떤가요?",
        description = "현재 상황에 맞는 준비 흐름을 안내해드릴게요.",
        options = DiagnosisAnswerOption.ReservationStatus.entries
    ),
    DiagnosisQuestion(
        id = DiagnosisQuestionId.Q4_INTERPRETATION_NEED,
        title = "진료 시 통역이나 외국어 지원이 필요한가요?",
        description = "병원마다 지원 언어와 통역 가능 여부가 다를 수 있습니다.",
        options = DiagnosisAnswerOption.InterpretationNeed.entries,
        noticeText = "메디인부산은 통역사를 매칭하지 않습니다. 병원별 지원 언어와 문의 채널 확인을 도와드립니다."
    ),
    DiagnosisQuestion(
        id = DiagnosisQuestionId.Q5_ENTRY_STAY_CONDITION,
        title = "입국·체류와 관련해 해당되는 상황이 있나요?",
        description = "국적, 체류기간, 방문 목적에 따라 공식 확인이 필요할 수 있습니다.",
        options = DiagnosisAnswerOption.EntryStayCondition.entries,
        isMultiSelect = true,
        noticeText = "메디인부산은 비자 발급 가능 여부를 판단하거나 대행하지 않습니다."
    )
)