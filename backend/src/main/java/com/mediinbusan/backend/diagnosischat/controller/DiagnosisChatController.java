package com.mediinbusan.backend.diagnosischat.controller;

import com.mediinbusan.backend.diagnosischat.dto.DiagnosisChatRequest;
import com.mediinbusan.backend.diagnosischat.dto.DiagnosisChatResponse;
import com.mediinbusan.backend.diagnosischat.service.DiagnosisChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "DiagnosisChat", description = "AI 자가진단 챗봇 (Gemini 연동)")
@RestController
@RequestMapping("/api/v1/diagnosis-chat")
public class DiagnosisChatController {

    private final DiagnosisChatService diagnosisChatService;

    public DiagnosisChatController(DiagnosisChatService diagnosisChatService) {
        this.diagnosisChatService = diagnosisChatService;
    }

    @Operation(
        summary = "자가진단 챗봇 한 턴 처리",
        description = "stateless 엔드포인트다. 클라이언트가 매 턴 현재까지 알려진 slots와 이번 턴 사용자 발화를 "
            + "보내면, 서버가 slots만 보고 스스로 다음 질문 대상을 정해 Gemini로 슬롯을 추출하고, "
            + "4개 필수 슬롯이 모두 채워지면 resultType(TYPE_A~E)을 함께 반환한다."
    )
    @PostMapping
    public DiagnosisChatResponse converse(@RequestBody DiagnosisChatRequest request) {
        return diagnosisChatService.converse(request);
    }
}
