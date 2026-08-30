package com.mediinbusan.backend.diagnosischat.exception;

import com.mediinbusan.backend.diagnosischat.dto.DiagnosisChatErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * diagnosischat 패키지 컨트롤러에서 발생한 예외만 {code, message} 형태로 변환한다.
 * hospital/wellness/document 등 다른 기능의 오류 응답 형태에는 영향을 주지 않기 위해 basePackages로 범위를 좁힌다.
 */
@RestControllerAdvice(basePackages = "com.mediinbusan.backend.diagnosischat")
public class DiagnosisChatExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(DiagnosisChatExceptionHandler.class);

    @ExceptionHandler(DiagnosisChatFailedException.class)
    public ResponseEntity<DiagnosisChatErrorResponse> handleChatFailed(DiagnosisChatFailedException e) {
        // 원인(Gemini 인증 실패, 5xx, 네트워크 오류, 구조화 출력 파싱 실패 등)은 로그로만 남기고 클라이언트에는 노출하지 않는다.
        log.error("자가진단 챗봇 처리 실패: status={}", e.getStatus(), e.getCause());
        return ResponseEntity.status(e.getStatus())
            .body(new DiagnosisChatErrorResponse("DIAGNOSIS_CHAT_FAILED", "자가진단 챗봇 처리에 실패했습니다."));
    }

    @ExceptionHandler(DiagnosisChatRateLimitExceededException.class)
    public ResponseEntity<DiagnosisChatErrorResponse> handleRateLimitExceeded(DiagnosisChatRateLimitExceededException e) {
        // Gemini 쪽 사용량 초과(DiagnosisChatFailedException, HttpStatus.TOO_MANY_REQUESTS)와는 원인이
        // 달라서(우리 서버가 IP당 호출 빈도 자체를 막은 것) 별도 코드로 로그/응답을 구분한다.
        log.warn("자가진단 챗봇 IP 레이트리밋 초과: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .body(new DiagnosisChatErrorResponse("RATE_LIMITED", "요청이 너무 잦습니다. 잠시 후 다시 시도해주세요."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DiagnosisChatErrorResponse> handleUnexpected(Exception e) {
        log.error("자가진단 챗봇 API 처리 중 예상치 못한 오류가 발생했습니다.", e);
        return ResponseEntity.internalServerError()
            .body(new DiagnosisChatErrorResponse("INTERNAL_SERVER_ERROR", "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }
}
