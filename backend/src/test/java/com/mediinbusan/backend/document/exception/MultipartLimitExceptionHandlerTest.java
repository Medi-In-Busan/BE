package com.mediinbusan.backend.document.exception;

import com.mediinbusan.backend.document.dto.DocumentErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.assertj.core.api.Assertions.assertThat;

class MultipartLimitExceptionHandlerTest {

    private final MultipartLimitExceptionHandler handler = new MultipartLimitExceptionHandler();

    @Test
    void 업로드_용량_초과_예외는_400과_고정_코드로_변환된다() {
        ResponseEntity<DocumentErrorResponse> response =
            handler.handleMaxUploadSizeExceeded(new MaxUploadSizeExceededException(10_485_760L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INVALID_DOCUMENT_IMAGE");
    }
}
