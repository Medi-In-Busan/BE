package com.mediinbusan.backend.guide.exception;

import com.mediinbusan.backend.guide.dto.GuideErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * guide 패키지 컨트롤러에서 발생한 예외만 {code, message} 형태로 변환한다.
 * document/hospital/wellness 등 다른 기능의 오류 응답 형태에는 영향을 주지 않기 위해
 * basePackages로 범위를 좁힌다(DocumentExceptionHandler와 동일한 패턴).
 */
@RestControllerAdvice(basePackages = "com.mediinbusan.backend.guide")
public class GuideExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GuideExceptionHandler.class);

    @ExceptionHandler(UnsupportedTranslationLanguageException.class)
    public ResponseEntity<GuideErrorResponse> handleUnsupportedLanguage(UnsupportedTranslationLanguageException e) {
        log.info("지원하지 않는 진료 브리핑 번역 sourceLang 요청: {}", e.getMessage());
        return ResponseEntity.badRequest().body(new GuideErrorResponse("INVALID_SOURCE_LANGUAGE", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GuideErrorResponse> handleUnexpected(Exception e) {
        log.error("진료 브리핑 번역 API 처리 중 예상치 못한 오류가 발생했습니다.", e);
        return ResponseEntity.internalServerError()
            .body(new GuideErrorResponse("INTERNAL_SERVER_ERROR", "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }
}
