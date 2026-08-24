package com.mediinbusan.backend.diagnosischat.dto;

/** resultType은 4개 필수 슬롯(entryStayConditions 제외)이 모두 채워지기 전까지 null이다. */
public record DiagnosisChatResponse(
    String reply,
    DiagnosisSlotsDto slots,
    String resultType
) {
}
