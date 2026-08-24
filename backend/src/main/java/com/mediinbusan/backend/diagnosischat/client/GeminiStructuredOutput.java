package com.mediinbusan.backend.diagnosischat.client;

import com.mediinbusan.backend.diagnosischat.dto.DiagnosisSlotsDto;

/** candidate의 text 파트를 responseSchema대로 파싱한 구조화 출력. */
public record GeminiStructuredOutput(
    String reply,
    DiagnosisSlotsDto slots
) {
}
