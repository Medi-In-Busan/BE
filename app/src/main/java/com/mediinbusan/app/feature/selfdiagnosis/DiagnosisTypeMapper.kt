package com.mediinbusan.app.feature.selfdiagnosis

import com.mediinbusan.app.feature.selfdiagnosis.DiagnosisAnswerOption.EntryStayCondition
import com.mediinbusan.app.feature.selfdiagnosis.DiagnosisAnswerOption.InterpretationNeed
import com.mediinbusan.app.feature.selfdiagnosis.DiagnosisAnswerOption.ReservationStatus
import com.mediinbusan.app.feature.selfdiagnosis.DiagnosisAnswerOption.VisitPurpose

/** 답변 → DiagnosisResultType 매핑. 우선순위는 위에서 아래 순서로 평가한다(첫 매치가 결과). */
object DiagnosisTypeMapper {

    // "아직 모름"류 답변이 이 개수 이상이면 목적이 불분명하다고 보고 보수적으로 B/C를 검토한다.
    private const val UNKNOWN_HEAVY_THRESHOLD = 3

    fun map(selectedAnswers: Map<DiagnosisQuestionId, Set<DiagnosisAnswerOption>>): DiagnosisResultType {
        val visitPurpose = selectedAnswers.singleAnswer<VisitPurpose>(DiagnosisQuestionId.Q1_VISIT_PURPOSE)
        val reservationStatus = selectedAnswers.singleAnswer<ReservationStatus>(DiagnosisQuestionId.Q3_RESERVATION_STATUS)
        val interpretationNeed = selectedAnswers.singleAnswer<InterpretationNeed>(DiagnosisQuestionId.Q4_INTERPRETATION_NEED)
        val entryStayConditions = selectedAnswers.multiAnswer<EntryStayCondition>(DiagnosisQuestionId.Q5_ENTRY_STAY_CONDITION)

        val needsLongTermOrFamilyOrInvitation = entryStayConditions.any {
            it == EntryStayCondition.LONG_TERM_TREATMENT_OVER_91_DAYS ||
                it == EntryStayCondition.ACCOMPANIED_BY_FAMILY ||
                it == EntryStayCondition.INVITATION_DOCUMENT_MAY_BE_NEEDED
        }
        if (needsLongTermOrFamilyOrInvitation) return DiagnosisResultType.TYPE_D

        if (reservationStatus == ReservationStatus.USING_AGENCY_OR_PACKAGE) return DiagnosisResultType.TYPE_C

        if (interpretationNeed == InterpretationNeed.NEEDED ||
            interpretationNeed == InterpretationNeed.WANT_TO_CHECK_SUPPORTED_LANGUAGE
        ) {
            return DiagnosisResultType.TYPE_B
        }

        if (visitPurpose == VisitPurpose.WELLNESS_REST) return DiagnosisResultType.TYPE_E

        val unknownCount = countUnknownAnswers(selectedAnswers)
        if (unknownCount >= UNKNOWN_HEAVY_THRESHOLD) {
            // 통역/유치기관처럼 강하게 갈리는 신호가 없어도, 정보가 부족한 상태로 의료/비자
            // 판단처럼 보이지 않게 B(문의 채널 확인) 쪽으로 보수적으로 안내한다.
            return if (reservationStatus == ReservationStatus.USING_AGENCY_OR_PACKAGE) {
                DiagnosisResultType.TYPE_C
            } else {
                DiagnosisResultType.TYPE_B
            }
        }

        return DiagnosisResultType.TYPE_A
    }

    private fun countUnknownAnswers(selectedAnswers: Map<DiagnosisQuestionId, Set<DiagnosisAnswerOption>>): Int =
        selectedAnswers.values.sumOf { options ->
            options.count { option ->
                when (option) {
                    is VisitPurpose -> option == VisitPurpose.UNKNOWN
                    is DiagnosisAnswerOption.StayDuration -> option == DiagnosisAnswerOption.StayDuration.UNKNOWN
                    is ReservationStatus -> option == ReservationStatus.UNKNOWN
                    is InterpretationNeed -> option == InterpretationNeed.UNKNOWN
                    is EntryStayCondition -> option == EntryStayCondition.UNKNOWN
                }
            }
        }

    private inline fun <reified T : DiagnosisAnswerOption> Map<DiagnosisQuestionId, Set<DiagnosisAnswerOption>>.singleAnswer(
        questionId: DiagnosisQuestionId
    ): T? = this[questionId]?.filterIsInstance<T>()?.firstOrNull()

    private inline fun <reified T : DiagnosisAnswerOption> Map<DiagnosisQuestionId, Set<DiagnosisAnswerOption>>.multiAnswer(
        questionId: DiagnosisQuestionId
    ): Set<T> = this[questionId]?.filterIsInstance<T>()?.toSet().orEmpty()
}