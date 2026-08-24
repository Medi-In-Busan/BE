package com.mediinbusan.backend.diagnosischat.dto;

/**
 * Android가 매 턴 보내는 요청. 대화 히스토리나 "직전 assistant 메시지" 같은 클라이언트 제공
 * 대화 텍스트는 의도적으로 포함하지 않는다 — 다음에 무엇을 물을지는 서버가 slots만 보고 스스로
 * 계산한다(DiagnosisChatService 참고). userMessage는 이번 턴의 사용자 발화 원문만 담는다.
 */
public record DiagnosisChatRequest(
    String language,
    String userMessage,
    DiagnosisSlotsDto slots
) {
}
