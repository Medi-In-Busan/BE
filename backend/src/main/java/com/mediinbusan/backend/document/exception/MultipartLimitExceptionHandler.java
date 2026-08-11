package com.mediinbusan.backend.document.exception;

import com.mediinbusan.backend.document.dto.DocumentErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * MaxUploadSizeExceededException은 DispatcherServlet이 핸들러를 찾기도 전에
 * multipart 파싱 단계에서 던져지기 때문에, basePackages로 범위를 좁힌 {@link DocumentExceptionHandler}는
 * 이 예외를 잡지 못한다(핸들러 매칭 전이라 어떤 컨트롤러의 요청인지 알 수 없어 스코프 필터가 항상 실패한다).
 * 그래서 이 예외만 전역 advice로 별도로 처리한다.
 */
@RestControllerAdvice
public class MultipartLimitExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MultipartLimitExceptionHandler.class);

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<DocumentErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.info("업로드 용량 제한 초과: {}", e.getClass().getSimpleName());
        return ResponseEntity.badRequest()
            .body(new DocumentErrorResponse("INVALID_DOCUMENT_IMAGE", "이미지 파일 용량이 제한을 초과했습니다."));
    }
}
