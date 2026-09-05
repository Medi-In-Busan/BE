package com.mediinbusan.backend.document.service;

import com.mediinbusan.backend.document.client.ClovaOcrApiException;
import com.mediinbusan.backend.document.client.ClovaOcrAuthenticationException;
import com.mediinbusan.backend.document.client.ClovaOcrClient;
import com.mediinbusan.backend.document.client.ClovaOcrResponse;
import com.mediinbusan.backend.document.client.PapagoTranslationAuthenticationException;
import com.mediinbusan.backend.document.client.PapagoTranslationClient;
import com.mediinbusan.backend.document.dto.DocumentOcrResponse;
import com.mediinbusan.backend.document.exception.DocumentOcrFailedException;
import com.mediinbusan.backend.document.exception.InvalidDocumentImageException;
import com.mediinbusan.backend.document.validation.DocumentImageValidator;
import com.mediinbusan.backend.document.validation.DocumentOcrProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentOcrServiceTest {

    private final DocumentImageValidator imageValidator = new DocumentImageValidator(
        new DocumentOcrProperties(1_000_000L, List.of("image/jpeg", "image/png", "image/webp"))
    );

    @Mock
    private ClovaOcrClient clovaOcrClient;

    @Mock
    private PapagoTranslationClient papagoTranslationClient;

    private DocumentOcrService service;

    @Test
    void CLOVA_응답에서_추출한_텍스트를_반환한다() {
        service = new DocumentOcrService(imageValidator, clovaOcrClient, papagoTranslationClient);
        MockMultipartFile image = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(clovaOcrClient.recognizeText(any(), any())).thenReturn(successResponse());

        DocumentOcrResponse response = service.extractText(image, null);

        assertThat(response.text()).isEqualTo("환자명 홍길동");
        assertThat(response.translatedText()).isNull();
        assertThat(response.targetLanguage()).isNull();
    }

    @Test
    void 대상_언어가_지정되면_번역_결과를_함께_반환한다() {
        service = new DocumentOcrService(imageValidator, clovaOcrClient, papagoTranslationClient);
        MockMultipartFile image = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(clovaOcrClient.recognizeText(any(), any())).thenReturn(successResponse());
        when(papagoTranslationClient.translate("환자명 홍길동", "en")).thenReturn("Patient name Hong Gil-dong");

        DocumentOcrResponse response = service.extractText(image, "en");

        assertThat(response.text()).isEqualTo("환자명 홍길동");
        assertThat(response.translatedText()).isEqualTo("Patient name Hong Gil-dong");
        assertThat(response.targetLanguage()).isEqualTo("en");
    }

    @Test
    void 대상_언어가_없으면_번역을_시도하지_않는다() {
        service = new DocumentOcrService(imageValidator, clovaOcrClient, papagoTranslationClient);
        MockMultipartFile image = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(clovaOcrClient.recognizeText(any(), any())).thenReturn(successResponse());

        service.extractText(image, null);

        verify(papagoTranslationClient, never()).translate(any(), any());
    }

    @Test
    void 번역_인증_실패시_원문만_반환하고_예외가_전파되지_않는다() {
        service = new DocumentOcrService(imageValidator, clovaOcrClient, papagoTranslationClient);
        MockMultipartFile image = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(clovaOcrClient.recognizeText(any(), any())).thenReturn(successResponse());
        when(papagoTranslationClient.translate(any(), any())).thenThrow(new PapagoTranslationAuthenticationException("인증 실패"));

        DocumentOcrResponse response = service.extractText(image, "en");

        assertThat(response.text()).isEqualTo("환자명 홍길동");
        assertThat(response.translatedText()).isNull();
        assertThat(response.targetLanguage()).isNull();
    }

    @Test
    void 유효하지_않은_이미지는_CLOVA_호출_전에_거부된다() {
        service = new DocumentOcrService(imageValidator, clovaOcrClient, papagoTranslationClient);
        MockMultipartFile invalid = new MockMultipartFile("image", "doc.pdf", "application/pdf", new byte[]{1});

        assertThatThrownBy(() -> service.extractText(invalid, null))
            .isInstanceOf(InvalidDocumentImageException.class);
        verify(clovaOcrClient, never()).recognizeText(any(), any());
    }

    @Test
    void CLOVA_인증_실패는_500_예외로_변환된다() {
        service = new DocumentOcrService(imageValidator, clovaOcrClient, papagoTranslationClient);
        MockMultipartFile image = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[]{1});
        when(clovaOcrClient.recognizeText(any(), any())).thenThrow(new ClovaOcrAuthenticationException("인증 실패"));

        assertThatThrownBy(() -> service.extractText(image, null))
            .isInstanceOf(DocumentOcrFailedException.class)
            .extracting(e -> ((DocumentOcrFailedException) e).getStatus())
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void CLOVA_API_오류는_502_예외로_변환된다() {
        service = new DocumentOcrService(imageValidator, clovaOcrClient, papagoTranslationClient);
        MockMultipartFile image = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[]{1});
        when(clovaOcrClient.recognizeText(any(), any())).thenThrow(new ClovaOcrApiException("호출 실패"));

        assertThatThrownBy(() -> service.extractText(image, null))
            .isInstanceOf(DocumentOcrFailedException.class)
            .extracting(e -> ((DocumentOcrFailedException) e).getStatus())
            .isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    private ClovaOcrResponse successResponse() {
        return new ClovaOcrResponse(
            "V2", "req-1", 0L,
            List.of(new ClovaOcrResponse.ImageResult(
                "document", "SUCCESS", "SUCCESS",
                List.of(
                    new ClovaOcrResponse.Field("환자명", 0.99, "NORMAL", false, null),
                    new ClovaOcrResponse.Field("홍길동", 0.99, "NORMAL", true, null)
                ),
                null
            ))
        );
    }
}
