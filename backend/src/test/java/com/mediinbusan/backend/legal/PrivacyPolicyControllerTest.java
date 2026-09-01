package com.mediinbusan.backend.legal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrivacyPolicyController.class)
class PrivacyPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 개인정보처리방침을_로그인없이_HTML로_반환한다() throws Exception {
        mockMvc.perform(get("/privacy"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", containsString("text/html")))
            .andExpect(content().string(containsString("개인정보처리방침")))
            .andExpect(content().string(containsString("MADIN BUSAN")))
            .andExpect(content().string(containsString("Privacy inquiries")))
            .andExpect(content().string(containsString("选择语言")))
            .andExpect(content().string(containsString("言語を選択")))
            .andExpect(content().string(containsString("language-select")))
            .andExpect(content().string(containsString("support@medinbusan.kr")));
    }
}
