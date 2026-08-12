package com.mediinbusan.backend.document.validation;

import com.mediinbusan.backend.document.exception.InvalidDocumentImageException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentImageValidatorTest {

    private final DocumentImageValidator validator = new DocumentImageValidator(
        new DocumentOcrProperties(1_000L, List.of("image/jpeg", "image/png", "image/webp"))
    );

    @Test
    void 허용된_이미지는_CLOVA_포맷코드를_반환한다() {
        MockMultipartFile file = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[100]);

        String format = validator.validateAndResolveFormat(file);

        assertThat(format).isEqualTo("jpg");
    }

    @Test
    void 빈_파일은_예외를_던진다() {
        MockMultipartFile file = new MockMultipartFile("image", "doc.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> validator.validateAndResolveFormat(file))
            .isInstanceOf(InvalidDocumentImageException.class);
    }

    @Test
    void 허용되지_않는_MIME_타입은_예외를_던진다() {
        MockMultipartFile file = new MockMultipartFile("image", "doc.pdf", "application/pdf", new byte[100]);

        assertThatThrownBy(() -> validator.validateAndResolveFormat(file))
            .isInstanceOf(InvalidDocumentImageException.class);
    }

    @Test
    void 용량_제한을_초과하면_예외를_던진다() {
        MockMultipartFile file = new MockMultipartFile("image", "doc.png", "image/png", new byte[1_001]);

        assertThatThrownBy(() -> validator.validateAndResolveFormat(file))
            .isInstanceOf(InvalidDocumentImageException.class);
    }
}
