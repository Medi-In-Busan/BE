package com.mediinbusan.app.feature.selfdiagnosis

/** 5개 진단 질문의 선택지. 질문별로 sealed 하위 enum을 둬 매퍼(DiagnosisTypeMapper)에서
 *  exhaustive when으로 타입 안전하게 분기할 수 있게 한다. */
sealed interface DiagnosisAnswerOption {
    val label: String

    enum class VisitPurpose(override val label: String) : DiagnosisAnswerOption {
        SKIN_BEAUTY("피부·미용"),
        HEALTH_CHECKUP("건강검진"),
        DENTAL("치과"),
        ORIENTAL_RECOVERY("한방·회복 관리"),
        REHABILITATION("재활"),
        WELLNESS_REST("웰니스·휴식"),
        UNKNOWN("아직 모름")
    }

    enum class StayDuration(override val label: String) : DiagnosisAnswerOption {
        SAME_DAY("당일"),
        DAYS_1_3("1~3일"),
        DAYS_4_7("4~7일"),
        DAYS_8_30("8~30일"),
        DAYS_31_PLUS_OR_UNDECIDED("31일 이상 또는 미정"),
        UNKNOWN("아직 모름")
    }

    enum class ReservationStatus(override val label: String) : DiagnosisAnswerOption {
        SEARCHING("아직 병원을 찾는 중"),
        PLANNING_TO_INQUIRE("병원에 직접 문의 예정"),
        RESERVED("병원 예약 완료"),
        USING_AGENCY_OR_PACKAGE("등록 유치기관 또는 패키지 이용 중"),
        UNKNOWN("아직 모름")
    }

    enum class InterpretationNeed(override val label: String) : DiagnosisAnswerOption {
        NEEDED("필요함"),
        WANT_TO_CHECK_SUPPORTED_LANGUAGE("병원 지원 언어를 확인하고 싶음"),
        NOT_NEEDED("필요 없음"),
        UNKNOWN("아직 모름")
    }

    enum class EntryStayCondition(override val label: String) : DiagnosisAnswerOption {
        SHORT_VISIT_NO_VISA("비자 없이 단기 방문 예정"),
        LONG_TERM_TREATMENT_OVER_91_DAYS("치료·요양이 91일 이상 걸릴 수 있음"),
        ACCOMPANIED_BY_FAMILY("동반 가족 또는 보호자가 함께 올 예정"),
        INVITATION_DOCUMENT_MAY_BE_NEEDED("병원 또는 등록 유치기관의 초청 서류가 필요할 수 있음"),
        UNKNOWN("잘 모르겠음")
    }
}