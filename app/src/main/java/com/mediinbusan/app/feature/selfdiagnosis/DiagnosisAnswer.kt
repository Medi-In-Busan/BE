package com.mediinbusan.app.feature.selfdiagnosis

import com.mediinbusan.app.core.i18n.DiagnosisAnswerOptionStrings

/** 5개 진단 질문의 선택지. 질문별로 sealed 하위 enum을 둬 매퍼(DiagnosisTypeMapper)에서
 *  exhaustive when으로 타입 안전하게 분기할 수 있게 한다. 표시 라벨은 언어에 따라 바뀌므로
 *  enum 자체에는 담지 않고 [label] 함수로 DiagnosisAnswerOptionStrings에서 조회한다. */
sealed interface DiagnosisAnswerOption {

    enum class VisitPurpose : DiagnosisAnswerOption {
        SKIN_BEAUTY, HEALTH_CHECKUP, DENTAL, ORIENTAL_RECOVERY, REHABILITATION, WELLNESS_REST, UNKNOWN
    }

    enum class StayDuration : DiagnosisAnswerOption {
        SAME_DAY, DAYS_1_3, DAYS_4_7, DAYS_8_30, DAYS_31_PLUS_OR_UNDECIDED, UNKNOWN
    }

    enum class ReservationStatus : DiagnosisAnswerOption {
        SEARCHING, PLANNING_TO_INQUIRE, RESERVED, USING_AGENCY_OR_PACKAGE, UNKNOWN
    }

    enum class InterpretationNeed : DiagnosisAnswerOption {
        NEEDED, WANT_TO_CHECK_SUPPORTED_LANGUAGE, NOT_NEEDED, UNKNOWN
    }

    enum class EntryStayCondition : DiagnosisAnswerOption {
        SHORT_VISIT_NO_VISA, LONG_TERM_TREATMENT_OVER_91_DAYS, ACCOMPANIED_BY_FAMILY, INVITATION_DOCUMENT_MAY_BE_NEEDED, UNKNOWN
    }
}

fun DiagnosisAnswerOption.label(strings: DiagnosisAnswerOptionStrings): String = when (this) {
    DiagnosisAnswerOption.VisitPurpose.SKIN_BEAUTY -> strings.visitPurposeSkinBeauty
    DiagnosisAnswerOption.VisitPurpose.HEALTH_CHECKUP -> strings.visitPurposeHealthCheckup
    DiagnosisAnswerOption.VisitPurpose.DENTAL -> strings.visitPurposeDental
    DiagnosisAnswerOption.VisitPurpose.ORIENTAL_RECOVERY -> strings.visitPurposeOrientalRecovery
    DiagnosisAnswerOption.VisitPurpose.REHABILITATION -> strings.visitPurposeRehabilitation
    DiagnosisAnswerOption.VisitPurpose.WELLNESS_REST -> strings.visitPurposeWellnessRest
    DiagnosisAnswerOption.VisitPurpose.UNKNOWN -> strings.visitPurposeUnknown
    DiagnosisAnswerOption.StayDuration.SAME_DAY -> strings.stayDurationSameDay
    DiagnosisAnswerOption.StayDuration.DAYS_1_3 -> strings.stayDuration1To3Days
    DiagnosisAnswerOption.StayDuration.DAYS_4_7 -> strings.stayDuration4To7Days
    DiagnosisAnswerOption.StayDuration.DAYS_8_30 -> strings.stayDuration8To30Days
    DiagnosisAnswerOption.StayDuration.DAYS_31_PLUS_OR_UNDECIDED -> strings.stayDuration31PlusOrUndecided
    DiagnosisAnswerOption.StayDuration.UNKNOWN -> strings.stayDurationUnknown
    DiagnosisAnswerOption.ReservationStatus.SEARCHING -> strings.reservationStatusSearching
    DiagnosisAnswerOption.ReservationStatus.PLANNING_TO_INQUIRE -> strings.reservationStatusPlanningToInquire
    DiagnosisAnswerOption.ReservationStatus.RESERVED -> strings.reservationStatusReserved
    DiagnosisAnswerOption.ReservationStatus.USING_AGENCY_OR_PACKAGE -> strings.reservationStatusUsingAgencyOrPackage
    DiagnosisAnswerOption.ReservationStatus.UNKNOWN -> strings.reservationStatusUnknown
    DiagnosisAnswerOption.InterpretationNeed.NEEDED -> strings.interpretationNeedNeeded
    DiagnosisAnswerOption.InterpretationNeed.WANT_TO_CHECK_SUPPORTED_LANGUAGE -> strings.interpretationNeedWantToCheckSupportedLanguage
    DiagnosisAnswerOption.InterpretationNeed.NOT_NEEDED -> strings.interpretationNeedNotNeeded
    DiagnosisAnswerOption.InterpretationNeed.UNKNOWN -> strings.interpretationNeedUnknown
    DiagnosisAnswerOption.EntryStayCondition.SHORT_VISIT_NO_VISA -> strings.entryStayConditionShortVisitNoVisa
    DiagnosisAnswerOption.EntryStayCondition.LONG_TERM_TREATMENT_OVER_91_DAYS -> strings.entryStayConditionLongTermTreatmentOver91Days
    DiagnosisAnswerOption.EntryStayCondition.ACCOMPANIED_BY_FAMILY -> strings.entryStayConditionAccompaniedByFamily
    DiagnosisAnswerOption.EntryStayCondition.INVITATION_DOCUMENT_MAY_BE_NEEDED -> strings.entryStayConditionInvitationDocumentMayBeNeeded
    DiagnosisAnswerOption.EntryStayCondition.UNKNOWN -> strings.entryStayConditionUnknown
}