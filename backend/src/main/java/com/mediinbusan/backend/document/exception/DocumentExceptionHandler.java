package com.mediinbusan.backend.document.exception;

import com.mediinbusan.backend.document.dto.DocumentErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * document 패키지 컨트롤러에서 발생한 예외만 {code, message} 형태로 변환한다.
 * hospital/wellness 등 다른 기능의 오류 응답 형태에는 영향을 주지 않기 위해 basePackages로 범위를 좁힌다.
 */
@RestControllerAdvice(basePackages = "com.mediinbusan.backend.document")
public class DocumentExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(DocumentExceptionHandler.class);

    @ExceptionHandler({InvalidDocumentImageException.class, MissingServletRequestPartException.class})
    public ResponseEntity<DocumentErrorResponse> handleInvalidImage(Exception e) {
        log.info("잘못된 문서 OCR 요청: {}", e.getClass().getSimpleName());
        return ResponseEntity.badRequest()
            .body(new DocumentErrorResponse("INVALID_DOCUMENT_IMAGE", invalidImageMessage(e)));
    }

    @ExceptionHandler(DocumentOcrFailedException.class)
    public ResponseEntity<DocumentErrorResponse> handleOcrFailed(DocumentOcrFailedException e) {
        // 원인(CLOVA 인증 실패, 5xx, 네트워크 오류 등)은 로그로만 남기고 클라이언트에는 노출하지 않는다.
        log.error("문서 OCR 처리 실패: status={}", e.getStatus(), e.getCause());
        return ResponseEntity.status(e.getStatus())
            .body(new DocumentErrorResponse("DOCUMENT_OCR_FAILED", "문서 OCR 처리에 실패했습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DocumentErrorResponse> handleUnexpected(Exception e) {
        log.error("문서 OCR API 처리 중 예상치 못한 오류가 발생했습니다.", e);
        return ResponseEntity.internalServerError()
            .body(new DocumentErrorResponse("INTERNAL_SERVER_ERROR", "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }

    private String invalidImageMessage(Exception e) {
        return e instanceof InvalidDocumentImageException ? e.getMessage() : "image 파트가 필요합니다.";
    }
}
