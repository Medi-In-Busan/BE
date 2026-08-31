package com.mediinbusan.backend.diagnosischat.exception;

/**
 * diagnosis-chat 엔드포인트의 IP당 호출 빈도 제한(DiagnosisChatRateLimitInterceptor)을 초과했을 때
 * 던진다. Gemini 쪽 사용량 초과({@link DiagnosisChatFailedException}, TOO_MANY_REQUESTS)와는 원인이
 * 달라서(우리 서버가 자체적으로 막은 것) 별도 타입으로 구분한다.
 */
public class DiagnosisChatRateLimitExceededException extends RuntimeException {

    public DiagnosisChatRateLimitExceededException(String message) {
        super(message);
    }
}
