package com.mediinbusan.backend.document.controller;

import com.mediinbusan.backend.document.dto.DocumentOcrResponse;
import com.mediinbusan.backend.document.exception.DocumentOcrFailedException;
import com.mediinbusan.backend.document.exception.InvalidDocumentImageException;
import com.mediinbusan.backend.document.service.DocumentOcrService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentOcrController.class)
class DocumentOcrControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentOcrService documentOcrService;

    @Test
    void 이미지를_업로드하면_추출된_텍스트를_반환한다() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(documentOcrService.extractText(any(), any())).thenReturn(new DocumentOcrResponse("환자명 홍길동", null, null));

        mockMvc.perform(multipart("/api/v1/documents/ocr").file(image))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.text").value("환자명 홍길동"))
            .andExpect(jsonPath("$.translatedText").doesNotExist());
    }

    @Test
    void targetLang을_지정하면_번역_결과도_함께_반환한다() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(documentOcrService.extractText(any(), eq("en")))
            .thenReturn(new DocumentOcrResponse("환자명 홍길동", "Patient name Hong Gil-dong", "en"));

        mockMvc.perform(multipart("/api/v1/documents/ocr").file(image).param("targetLang", "en"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.text").value("환자명 홍길동"))
            .andExpect(jsonPath("$.translatedText").value("Patient name Hong Gil-dong"))
            .andExpect(jsonPath("$.targetLanguage").value("en"));
    }

    @Test
    void image_파트가_없으면_400과_에러코드를_반환한다() throws Exception {
        mockMvc.perform(multipart("/api/v1/documents/ocr"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_DOCUMENT_IMAGE"));
    }

    @Test
    void 유효성_예외가_발생하면_400과_에러코드를_반환한다() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "doc.pdf", "application/pdf", new byte[]{1});
        when(documentOcrService.extractText(any(), any())).thenThrow(new InvalidDocumentImageException("지원하지 않는 이미지 형식입니다."));

        mockMvc.perform(multipart("/api/v1/documents/ocr").file(image))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_DOCUMENT_IMAGE"));
    }

    @Test
    void CLOVA_OCR_실패시_지정된_상태코드와_고정_메시지를_반환한다() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[]{1});
        when(documentOcrService.extractText(any(), any())).thenThrow(new DocumentOcrFailedException(HttpStatus.BAD_GATEWAY));

        mockMvc.perform(multipart("/api/v1/documents/ocr").file(image))
            .andExpect(status().isBadGateway())
            .andExpect(jsonPath("$.code").value("DOCUMENT_OCR_FAILED"))
            .andExpect(jsonPath("$.message").value("문서 OCR 처리에 실패했습니다."));
    }

    @Test
    void 예상치_못한_오류는_500과_고정_메시지를_반환한다() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[]{1});
        when(documentOcrService.extractText(any(), any())).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(multipart("/api/v1/documents/ocr").file(image))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
    }
}
