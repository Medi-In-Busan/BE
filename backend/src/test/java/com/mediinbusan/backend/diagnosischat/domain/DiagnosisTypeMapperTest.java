package com.mediinbusan.backend.diagnosischat.domain;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DiagnosisTypeMapper는 Gemini와 완전히 독립적인 순수 함수다 — 동일한 슬롯 조합이면
 * 대화 경로와 무관하게 항상 같은 TYPE을 반환해야 한다(결정론 회귀 테스트).
 */
class DiagnosisTypeMapperTest {

    @Test
    void 장기체류_가족동반_초청장_조건이_있으면_TYPE_D() {
        DiagnosisSlots slots = new DiagnosisSlots(
            VisitPurpose.DENTAL, StayDuration.DAYS_31_PLUS_OR_UNDECIDED, ReservationStatus.RESERVED,
            InterpretationNeed.NOT_NEEDED, Set.of(EntryStayCondition.LONG_TERM_TREATMENT_OVER_91_DAYS)
        );

        assertThat(DiagnosisTypeMapper.map(slots)).isEqualTo(DiagnosisResultType.TYPE_D);
    }

    @Test
    void 에이전시_패키지_이용시_TYPE_C() {
        DiagnosisSlots slots = new DiagnosisSlots(
            VisitPurpose.SKIN_BEAUTY, StayDuration.DAYS_1_3, ReservationStatus.USING_AGENCY_OR_PACKAGE,
            InterpretationNeed.NOT_NEEDED, Set.of()
        );

        assertThat(DiagnosisTypeMapper.map(slots)).isEqualTo(DiagnosisResultType.TYPE_C);
    }

    @Test
    void 통역이_필요하면_TYPE_B() {
        DiagnosisSlots slots = new DiagnosisSlots(
            VisitPurpose.DENTAL, StayDuration.DAYS_4_7, ReservationStatus.RESERVED,
            InterpretationNeed.NEEDED, Set.of()
        );

        assertThat(DiagnosisTypeMapper.map(slots)).isEqualTo(DiagnosisResultType.TYPE_B);
    }

    @Test
    void 웰니스_휴양_목적이면_TYPE_E() {
        DiagnosisSlots slots = new DiagnosisSlots(
            VisitPurpose.WELLNESS_REST, StayDuration.DAYS_4_7, ReservationStatus.RESERVED,
            InterpretationNeed.NOT_NEEDED, Set.of()
        );

        assertThat(DiagnosisTypeMapper.map(slots)).isEqualTo(DiagnosisResultType.TYPE_E);
    }

    @Test
    void 모름_응답이_3개_이상이면_보수적으로_TYPE_B_또는_C() {
        DiagnosisSlots slots = new DiagnosisSlots(
            VisitPurpose.UNKNOWN, StayDuration.UNKNOWN, ReservationStatus.UNKNOWN,
            InterpretationNeed.NOT_NEEDED, Set.of()
        );

        assertThat(DiagnosisTypeMapper.map(slots)).isEqualTo(DiagnosisResultType.TYPE_B);
    }

    @Test
    void 강한_신호가_없으면_TYPE_A() {
        DiagnosisSlots slots = new DiagnosisSlots(
            VisitPurpose.DENTAL, StayDuration.DAYS_1_3, ReservationStatus.RESERVED,
            InterpretationNeed.NOT_NEEDED, Set.of()
        );

        assertThat(DiagnosisTypeMapper.map(slots)).isEqualTo(DiagnosisResultType.TYPE_A);
    }

    @Test
    void 동일한_슬롯_조합은_항상_동일한_결과를_반환한다() {
        DiagnosisSlots slots = new DiagnosisSlots(
            VisitPurpose.DENTAL, StayDuration.DAYS_1_3, ReservationStatus.RESERVED,
            InterpretationNeed.NOT_NEEDED, Set.of()
        );

        DiagnosisResultType first = DiagnosisTypeMapper.map(slots);
        DiagnosisResultType second = DiagnosisTypeMapper.map(slots);

        assertThat(first).isEqualTo(second);
    }
}
