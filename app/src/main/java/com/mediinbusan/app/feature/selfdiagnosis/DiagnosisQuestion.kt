package com.mediinbusan.app.feature.selfdiagnosis

import com.mediinbusan.app.core.i18n.DiagnosisQuestionStrings

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

// 질문별 선택지 구성과 다중선택 여부는 언어와 무관한 구조적 정보라 DiagnosisQuestionStrings와 분리해 둔다.
fun DiagnosisQuestionId.options(): List<DiagnosisAnswerOption> = when (this) {
    DiagnosisQuestionId.Q1_VISIT_PURPOSE -> DiagnosisAnswerOption.VisitPurpose.entries
    DiagnosisQuestionId.Q2_STAY_DURATION -> DiagnosisAnswerOption.StayDuration.entries
    DiagnosisQuestionId.Q3_RESERVATION_STATUS -> DiagnosisAnswerOption.ReservationStatus.entries
    DiagnosisQuestionId.Q4_INTERPRETATION_NEED -> DiagnosisAnswerOption.InterpretationNeed.entries
    DiagnosisQuestionId.Q5_ENTRY_STAY_CONDITION -> DiagnosisAnswerOption.EntryStayCondition.entries
}

fun DiagnosisQuestionId.isMultiSelect(): Boolean = this == DiagnosisQuestionId.Q5_ENTRY_STAY_CONDITION

/** 언어별 문항 목록. 순서는 [DiagnosisQuestionId] 선언 순서(Q1~Q5)와 항상 일치한다. */
fun diagnosisQuestions(strings: DiagnosisQuestionStrings): List<DiagnosisQuestion> = listOf(
    DiagnosisQuestion(
        id = DiagnosisQuestionId.Q1_VISIT_PURPOSE,
        title = strings.q1Title,
        description = strings.q1Description,
        options = DiagnosisQuestionId.Q1_VISIT_PURPOSE.options()
    ),
    DiagnosisQuestion(
        id = DiagnosisQuestionId.Q2_STAY_DURATION,
        title = strings.q2Title,
        description = strings.q2Description,
        options = DiagnosisQuestionId.Q2_STAY_DURATION.options()
    ),
    DiagnosisQuestion(
        id = DiagnosisQuestionId.Q3_RESERVATION_STATUS,
        title = strings.q3Title,
        description = strings.q3Description,
        options = DiagnosisQuestionId.Q3_RESERVATION_STATUS.options()
    ),
    DiagnosisQuestion(
        id = DiagnosisQuestionId.Q4_INTERPRETATION_NEED,
        title = strings.q4Title,
        description = strings.q4Description,
        options = DiagnosisQuestionId.Q4_INTERPRETATION_NEED.options(),
        noticeText = strings.q4NoticeText
    ),
    DiagnosisQuestion(
        id = DiagnosisQuestionId.Q5_ENTRY_STAY_CONDITION,
        title = strings.q5Title,
        description = strings.q5Description,
        options = DiagnosisQuestionId.Q5_ENTRY_STAY_CONDITION.options(),
        isMultiSelect = true,
        noticeText = strings.q5NoticeText
    )
)