package com.mediinbusan.backend.diagnosischat.controller;

import com.mediinbusan.backend.diagnosischat.dto.DiagnosisChatResponse;
import com.mediinbusan.backend.diagnosischat.dto.DiagnosisSlotsDto;
import com.mediinbusan.backend.diagnosischat.exception.DiagnosisChatFailedException;
import com.mediinbusan.backend.diagnosischat.service.DiagnosisChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiagnosisChatController.class)
class DiagnosisChatControllerTest {

    // 이 슬라이스 테스트 시점에 어떤 Jackson ObjectMapper 빈이 자동구성되는지 불확실해서(Jackson 2/3 공존,
    // ClovaOcrClient의 주석 참고) 요청 바디는 ObjectMapper 직렬화 대신 원시 JSON 문자열로 직접 작성한다.
    private static final String EMPTY_SLOTS_JSON = """
        {"visitPurpose":null,"stayDuration":null,"reservationStatus":null,"interpretationNeed":null,"entryStayConditions":[]}""";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiagnosisChatService diagnosisChatService;

    @Test
    void 요청을_보내면_reply와_slots를_반환한다() throws Exception {
        String requestJson = """
            {"language":"ko","userMessage":"피부 시술 받고 싶어요","slots":%s}""".formatted(EMPTY_SLOTS_JSON);
        when(diagnosisChatService.converse(any())).thenReturn(new DiagnosisChatResponse(
            "피부 시술이시군요! 며칠 정도 계실 예정인가요?",
            new DiagnosisSlotsDto("SKIN_BEAUTY", null, null, null, List.of()),
            null
        ));

        mockMvc.perform(post("/api/v1/diagnosis-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reply").value("피부 시술이시군요! 며칠 정도 계실 예정인가요?"))
            .andExpect(jsonPath("$.slots.visitPurpose").value("SKIN_BEAUTY"))
            .andExpect(jsonPath("$.resultType").doesNotExist());
    }

    @Test
    void 슬롯이_모두_채워지면_resultType을_함께_반환한다() throws Exception {
        String requestJson = """
            {"language":"ko","userMessage":"네","slots":{"visitPurpose":"SKIN_BEAUTY","stayDuration":"DAYS_4_7","reservationStatus":"SEARCHING","interpretationNeed":"NOT_NEEDED","entryStayConditions":[]}}""";
        when(diagnosisChatService.converse(any())).thenReturn(new DiagnosisChatResponse(
            "안내를 마쳤습니다.",
            new DiagnosisSlotsDto("SKIN_BEAUTY", "DAYS_4_7", "SEARCHING", "NOT_NEEDED", List.of()),
            "TYPE_A"
        ));

        mockMvc.perform(post("/api/v1/diagnosis-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultType").value("TYPE_A"));
    }

    @Test
    void 챗봇_처리_실패시_지정된_상태코드와_고정_메시지를_반환한다() throws Exception {
        String requestJson = """
            {"language":"ko","userMessage":"hi","slots":%s}""".formatted(EMPTY_SLOTS_JSON);
        when(diagnosisChatService.converse(any())).thenThrow(new DiagnosisChatFailedException(HttpStatus.BAD_GATEWAY, new RuntimeException("boom")));

        mockMvc.perform(post("/api/v1/diagnosis-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadGateway())
            .andExpect(jsonPath("$.code").value("DIAGNOSIS_CHAT_FAILED"));
    }

    @Test
    void 예상치_못한_오류는_500과_고정_메시지를_반환한다() throws Exception {
        String requestJson = """
            {"language":"ko","userMessage":"hi","slots":%s}""".formatted(EMPTY_SLOTS_JSON);
        when(diagnosisChatService.converse(any())).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/api/v1/diagnosis-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
    }
}
