package com.mediinbusan.backend.diagnosischat.dto;

import java.util.List;

/**
 * 슬롯을 원시 문자열(enum 상수명)로 주고받는 wire 형태. 실제 enum이 아니라 String으로 둔 이유는
 * Jackson이 알 수 없는 값을 만나 역직렬화 자체를 예외로 실패시키지 않게 하기 위함이다 — 값 검증은
 * DiagnosisChatDtoMapper의 화이트리스트 체크에서 명시적으로 수행하고, 못 알아듣는 값은 그냥 null 취급한다.
 */
public record DiagnosisSlotsDto(
    String visitPurpose,
    String stayDuration,
    String reservationStatus,
    String interpretationNeed,
    List<String> entryStayConditions
) {
    public static DiagnosisSlotsDto empty() {
        return new DiagnosisSlotsDto(null, null, null, null, List.of());
    }
}
