package com.mediinbusan.backend.diagnosischat.controller;

import com.mediinbusan.backend.diagnosischat.exception.DiagnosisChatRateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * diagnosis-chat 엔드포인트를 IP당 분당 {@link #MAX_REQUESTS_PER_WINDOW}회로 제한한다. 칩 탭 기반 정상
 * 사용 흐름은 대화 하나가 몇 번 안 되는 호출로 끝나므로(DiagnosisChatService의 정적 패스트패스 참고)
 * 정상 사용자는 이 한도에 걸릴 일이 없다 — 이 제한의 목적은 어뷰징/버그로 인한 재시도 루프가 공유
 * Gemini 쿼터를 혼자 다 소진하는 것을 막는 최소한의 안전장치다.
 *
 * 단일 인스턴스 배포(H2/로컬 docker-compose) 기준의 인메모리 구현이다 — 여러 백엔드 인스턴스로 수평
 * 확장하면 인스턴스별로 따로 카운트되어 실제 한도가 인스턴스 수만큼 느슨해진다. 그 정도 트래픽 규모가
 * 되면 Redis 등 공유 저장소 기반으로 옮겨야 한다. IP별 엔트리는 별도로 청소하지 않는데, 이 챗봇의
 * 실제 트래픽 규모(데모/소규모 서비스)에서는 고유 IP 수가 많아도 메모리에 무시할 수준이라 굳이
 * 정리 스케줄러를 두지 않았다.
 */
@Component
public class DiagnosisChatRateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS_PER_WINDOW = 20;
    private static final long WINDOW_MILLIS = Duration.ofMinutes(1).toMillis();

    private final ConcurrentMap<String, RequestWindow> windowsByIp = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientIp = resolveClientIp(request);
        RequestWindow window = windowsByIp.computeIfAbsent(clientIp, ip -> new RequestWindow());
        if (!window.tryConsume()) {
            throw new DiagnosisChatRateLimitExceededException(
                "IP당 분당 " + MAX_REQUESTS_PER_WINDOW + "회 한도를 초과했습니다: ip=" + clientIp
            );
        }
        return true;
    }

    // 프록시/로드밸런서를 거치면 remoteAddr이 프록시 주소로 잡히므로 X-Forwarded-For가 있으면 그
    // 첫 번째 값(원 클라이언트)을 우선한다. 이 헤더는 클라이언트가 임의로 조작해 보낼 수 있어 엄격한
    // 스푸핑 방어로는 쓸 수 없지만, 여기서의 목적은 봇 차단이 아니라 "우연한 재시도 루프/실수성
    // 스팸으로부터 공유 쿼터 보호"라 이 정도 신뢰 수준으로 충분하다.
    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static final class RequestWindow {
        private long windowStartMillis = System.currentTimeMillis();
        private int count = 0;

        synchronized boolean tryConsume() {
            long now = System.currentTimeMillis();
            if (now - windowStartMillis >= WINDOW_MILLIS) {
                windowStartMillis = now;
                count = 0;
            }
            if (count >= MAX_REQUESTS_PER_WINDOW) {
                return false;
            }
            count++;
            return true;
        }
    }
}
