package com.mediinbusan.backend.document.dto;

import com.mediinbusan.backend.document.client.ClovaOcrResponse;
import com.mediinbusan.backend.document.exception.DocumentOcrFailedException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentOcrDtoMapperTest {

    @Test
    void lineBreak가_true인_필드_뒤에는_줄바꿈을_넣는다() {
        ClovaOcrResponse response = successResponse(List.of(
            new ClovaOcrResponse.Field("환자명", 0.99, "NORMAL", false),
            new ClovaOcrResponse.Field("홍길동", 0.99, "NORMAL", true),
            new ClovaOcrResponse.Field("진단명", 0.99, "NORMAL", false),
            new ClovaOcrResponse.Field("급성 인두염", 0.99, "NORMAL", true)
        ));

        DocumentOcrResponse result = DocumentOcrDtoMapper.toResponse(response);

        assertThat(result.text()).isEqualTo("환자명 홍길동\n진단명 급성 인두염");
    }

    @Test
    void inferResult가_SUCCESS가_아니면_예외를_던진다() {
        ClovaOcrResponse response = new ClovaOcrResponse(
            "V2", "req-1", 0L,
            List.of(new ClovaOcrResponse.ImageResult("document", "FAILURE", "인식 실패", List.of()))
        );

        assertThatThrownBy(() -> DocumentOcrDtoMapper.toResponse(response))
            .isInstanceOf(DocumentOcrFailedException.class);
    }

    @Test
    void images가_비어있으면_예외를_던진다() {
        ClovaOcrResponse response = new ClovaOcrResponse("V2", "req-1", 0L, List.of());

        assertThatThrownBy(() -> DocumentOcrDtoMapper.toResponse(response))
            .isInstanceOf(DocumentOcrFailedException.class);
    }

    private ClovaOcrResponse successResponse(List<ClovaOcrResponse.Field> fields) {
        return new ClovaOcrResponse(
            "V2", "req-1", 0L,
            List.of(new ClovaOcrResponse.ImageResult("document", "SUCCESS", "SUCCESS", fields))
        );
    }
}
