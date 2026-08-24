package com.mediinbusan.backend.diagnosischat.domain;

/**
 * Android feature/selfdiagnosis/DiagnosisTypeMapper.kt를 그대로 포팅한 결정론적 분류 로직.
 * 우선순위는 위에서 아래 순서로 평가한다(첫 매치가 결과). LLM은 슬롯 추출만 담당하고
 * 최종 TYPE 판정은 이 순수 함수가 전담한다 — 슬롯 값이 같으면 항상 같은 결과를 반환한다.
 */
public final class DiagnosisTypeMapper {

    // "아직 모름"류 답변이 이 개수 이상이면 목적이 불분명하다고 보고 보수적으로 B/C를 검토한다.
    private static final int UNKNOWN_HEAVY_THRESHOLD = 3;

    private DiagnosisTypeMapper() {
    }

    public static DiagnosisResultType map(DiagnosisSlots slots) {
        boolean needsLongTermOrFamilyOrInvitation = slots.entryStayConditions().stream().anyMatch(condition ->
            condition == EntryStayCondition.LONG_TERM_TREATMENT_OVER_91_DAYS
                || condition == EntryStayCondition.ACCOMPANIED_BY_FAMILY
                || condition == EntryStayCondition.INVITATION_DOCUMENT_MAY_BE_NEEDED
        );
        if (needsLongTermOrFamilyOrInvitation) {
            return DiagnosisResultType.TYPE_D;
        }

        if (slots.reservationStatus() == ReservationStatus.USING_AGENCY_OR_PACKAGE) {
            return DiagnosisResultType.TYPE_C;
        }

        if (slots.interpretationNeed() == InterpretationNeed.NEEDED
            || slots.interpretationNeed() == InterpretationNeed.WANT_TO_CHECK_SUPPORTED_LANGUAGE) {
            return DiagnosisResultType.TYPE_B;
        }

        if (slots.visitPurpose() == VisitPurpose.WELLNESS_REST) {
            return DiagnosisResultType.TYPE_E;
        }

        int unknownCount = countUnknownAnswers(slots);
        if (unknownCount >= UNKNOWN_HEAVY_THRESHOLD) {
            // 통역/유치기관처럼 강하게 갈리는 신호가 없어도, 정보가 부족한 상태로 의료/비자
            // 판단처럼 보이지 않게 B(문의 채널 확인) 쪽으로 보수적으로 안내한다.
            return slots.reservationStatus() == ReservationStatus.USING_AGENCY_OR_PACKAGE
                ? DiagnosisResultType.TYPE_C
                : DiagnosisResultType.TYPE_B;
        }

        return DiagnosisResultType.TYPE_A;
    }

    private static int countUnknownAnswers(DiagnosisSlots slots) {
        int count = 0;
        if (slots.visitPurpose() == VisitPurpose.UNKNOWN) count++;
        if (slots.stayDuration() == StayDuration.UNKNOWN) count++;
        if (slots.reservationStatus() == ReservationStatus.UNKNOWN) count++;
        if (slots.interpretationNeed() == InterpretationNeed.UNKNOWN) count++;
        if (slots.entryStayConditions().contains(EntryStayCondition.UNKNOWN)) count++;
        return count;
    }
}
