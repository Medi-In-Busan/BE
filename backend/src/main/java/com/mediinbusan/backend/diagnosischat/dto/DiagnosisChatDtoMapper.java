package com.mediinbusan.backend.diagnosischat.dto;

import com.mediinbusan.backend.diagnosischat.domain.DiagnosisSlots;
import com.mediinbusan.backend.diagnosischat.domain.EntryStayCondition;
import com.mediinbusan.backend.diagnosischat.domain.InterpretationNeed;
import com.mediinbusan.backend.diagnosischat.domain.ReservationStatus;
import com.mediinbusan.backend.diagnosischat.domain.StayDuration;
import com.mediinbusan.backend.diagnosischat.domain.VisitPurpose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * wire 형태(DiagnosisSlotsDto, 원시 문자열)와 도메인(DiagnosisSlots, 검증된 enum) 사이를 변환한다.
 * Gemini가 뭐라고 응답하든, 여기서 각 enum 화이트리스트에 없는 값은 조용히 버리고(null 처리) 절대
 * 그대로 신뢰하지 않는다 — 이게 프롬프트 인젝션/모델 오류에 대한 1차 구조적 방어선이다.
 */
public final class DiagnosisChatDtoMapper {

    private static final Logger log = LoggerFactory.getLogger(DiagnosisChatDtoMapper.class);

    private DiagnosisChatDtoMapper() {
    }

    /** 클라이언트가 보낸 "지금까지 알려진 slots" 원문을 도메인으로 변환한다(신뢰하되 여전히 화이트리스트 검증). */
    public static DiagnosisSlots toDomain(DiagnosisSlotsDto dto) {
        if (dto == null) {
            return DiagnosisSlots.empty();
        }
        return new DiagnosisSlots(
            parseOrNull(VisitPurpose.class, dto.visitPurpose(), "visitPurpose"),
            parseOrNull(StayDuration.class, dto.stayDuration(), "stayDuration"),
            parseOrNull(ReservationStatus.class, dto.reservationStatus(), "reservationStatus"),
            parseOrNull(InterpretationNeed.class, dto.interpretationNeed(), "interpretationNeed"),
            parseEntryStayConditions(dto.entryStayConditions())
        );
    }

    /**
     * Gemini가 이번 턴에 새로 추출한 값(extractedDto)을 이전 슬롯(previous)에 병합한다.
     * 정책: 단일 슬롯은 이번 턴 값이 non-null이면 덮어쓰고, null이면 이전 값을 유지한다(never regress).
     * entryStayConditions(다중선택)는 이전 선택 + 이번 턴 새로 언급된 항목의 합집합으로 누적한다.
     */
    public static DiagnosisSlots merge(DiagnosisSlots previous, DiagnosisSlotsDto extractedDto) {
        VisitPurpose visitPurpose = parseOrNull(VisitPurpose.class, extractedDto.visitPurpose(), "visitPurpose");
        StayDuration stayDuration = parseOrNull(StayDuration.class, extractedDto.stayDuration(), "stayDuration");
        ReservationStatus reservationStatus = parseOrNull(ReservationStatus.class, extractedDto.reservationStatus(), "reservationStatus");
        InterpretationNeed interpretationNeed = parseOrNull(InterpretationNeed.class, extractedDto.interpretationNeed(), "interpretationNeed");
        Set<EntryStayCondition> extractedConditions = parseEntryStayConditions(extractedDto.entryStayConditions());

        Set<EntryStayCondition> mergedConditions = new LinkedHashSet<>(previous.entryStayConditions());
        mergedConditions.addAll(extractedConditions);

        return new DiagnosisSlots(
            visitPurpose != null ? visitPurpose : previous.visitPurpose(),
            stayDuration != null ? stayDuration : previous.stayDuration(),
            reservationStatus != null ? reservationStatus : previous.reservationStatus(),
            interpretationNeed != null ? interpretationNeed : previous.interpretationNeed(),
            Set.copyOf(mergedConditions)
        );
    }

    public static DiagnosisSlotsDto toDto(DiagnosisSlots slots) {
        return new DiagnosisSlotsDto(
            nameOrNull(slots.visitPurpose()),
            nameOrNull(slots.stayDuration()),
            nameOrNull(slots.reservationStatus()),
            nameOrNull(slots.interpretationNeed()),
            slots.entryStayConditions().stream().map(Enum::name).sorted().toList()
        );
    }

    private static Set<EntryStayCondition> parseEntryStayConditions(List<String> values) {
        if (values == null || values.isEmpty()) {
            return Set.of();
        }
        Set<EntryStayCondition> result = new LinkedHashSet<>();
        for (String value : values) {
            EntryStayCondition parsed = parseOrNull(EntryStayCondition.class, value, "entryStayConditions");
            if (parsed != null) {
                result.add(parsed);
            }
        }
        return Set.copyOf(result);
    }

    private static <E extends Enum<E>> E parseOrNull(Class<E> enumClass, String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            // 원문(Gemini 응답/사용자 발화)은 로그에 남기지 않고, 어떤 필드가 거부됐는지만 남긴다.
            log.warn("화이트리스트에 없는 슬롯 값이 거부되었습니다: field={}", fieldName);
            return null;
        }
    }

    private static String nameOrNull(Enum<?> value) {
        return value != null ? value.name() : null;
    }
}
