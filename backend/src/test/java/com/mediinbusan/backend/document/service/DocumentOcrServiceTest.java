package com.mediinbusan.backend.document.service;

import com.mediinbusan.backend.document.client.ClovaOcrApiException;
import com.mediinbusan.backend.document.client.ClovaOcrAuthenticationException;
import com.mediinbusan.backend.document.client.ClovaOcrClient;
import com.mediinbusan.backend.document.client.ClovaOcrResponse;
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

    private DocumentOcrService service;

    @Test
    void CLOVA_응답에서_추출한_텍스트를_반환한다() {
        service = new DocumentOcrService(imageValidator, clovaOcrClient);
        MockMultipartFile image = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(clovaOcrClient.recognizeText(any(), any())).thenReturn(successResponse());

        DocumentOcrResponse response = service.extractText(image);

        assertThat(response.text()).isEqualTo("환자명 홍길동");
    }

    @Test
    void 유효하지_않은_이미지는_CLOVA_호출_전에_거부된다() {
        service = new DocumentOcrService(imageValidator, clovaOcrClient);
        MockMultipartFile invalid = new MockMultipartFile("image", "doc.pdf", "application/pdf", new byte[]{1});

        assertThatThrownBy(() -> service.extractText(invalid))
            .isInstanceOf(InvalidDocumentImageException.class);
        verify(clovaOcrClient, never()).recognizeText(any(), any());
    }

    @Test
    void CLOVA_인증_실패는_500_예외로_변환된다() {
        service = new DocumentOcrService(imageValidator, clovaOcrClient);
        MockMultipartFile image = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[]{1});
        when(clovaOcrClient.recognizeText(any(), any())).thenThrow(new ClovaOcrAuthenticationException("인증 실패"));

        assertThatThrownBy(() -> service.extractText(image))
            .isInstanceOf(DocumentOcrFailedException.class)
            .extracting(e -> ((DocumentOcrFailedException) e).getStatus())
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void CLOVA_API_오류는_502_예외로_변환된다() {
        service = new DocumentOcrService(imageValidator, clovaOcrClient);
        MockMultipartFile image = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[]{1});
        when(clovaOcrClient.recognizeText(any(), any())).thenThrow(new ClovaOcrApiException("호출 실패"));

        assertThatThrownBy(() -> service.extractText(image))
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
                    new ClovaOcrResponse.Field("환자명", 0.99, "NORMAL", false),
                    new ClovaOcrResponse.Field("홍길동", 0.99, "NORMAL", true)
                )
            ))
        );
    }
}
