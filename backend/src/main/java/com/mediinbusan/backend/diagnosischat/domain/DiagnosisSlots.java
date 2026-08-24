package com.mediinbusan.backend.diagnosischat.domain;

import java.util.Set;

/**
 * 자가진단 챗봇이 대화에서 채워나가는 5개 슬롯. entryStayConditions만 다중선택(빈 집합 허용)이고
 * 나머지 4개는 단일 값이다. resultType 판정에는 entryStayConditions를 제외한 4개가 전부
 * non-null이어야 한다({@link #isComplete()}) — entryStayConditions는 끝까지 빈 집합이어도 유효하다.
 */
public record DiagnosisSlots(
    VisitPurpose visitPurpose,
    StayDuration stayDuration,
    ReservationStatus reservationStatus,
    InterpretationNeed interpretationNeed,
    Set<EntryStayCondition> entryStayConditions
) {
    public static DiagnosisSlots empty() {
        return new DiagnosisSlots(null, null, null, null, Set.of());
    }

    public boolean isComplete() {
        return visitPurpose != null && stayDuration != null && reservationStatus != null && interpretationNeed != null;
    }
}
