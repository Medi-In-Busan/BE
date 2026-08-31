package com.mediinbusan.backend.diagnosischat.controller;

import com.mediinbusan.backend.diagnosischat.exception.DiagnosisChatRateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * diagnosis-chat 엔드포인트를 클라이언트 주소당 분당 {@link #MAX_REQUESTS_PER_WINDOW}회로 제한한다. 칩
 * 탭 기반 정상 사용 흐름은 대화 하나가 몇 번 안 되는 호출로 끝나므로(DiagnosisChatService의 정적
 * 패스트패스 참고) 정상 사용자는 이 한도에 걸릴 일이 없다 — 이 제한의 목적은 어뷰징/버그로 인한 재시도
 * 루프가 공유 Gemini 쿼터를 혼자 다 소진하는 것을 막는 최소한의 안전장치다.
 *
 * <p>클라이언트 식별은 {@link HttpServletRequest#getRemoteAddr()}만 쓴다 — {@code X-Forwarded-For}
 * 헤더는 호출자가 요청마다 임의의 값을 넣어 보낼 수 있어(신뢰 가능한 리버스 프록시가 그 값을 반드시
 * 덮어쓴다는 보장이 없는 한) 이 헤더를 카운트 키로 쓰면 매 요청 다른 값을 보내는 것만으로 한도 자체를
 * 우회할 수 있고, 동시에 아래 맵도 무한정 커진다(예전 구현의 실수 — 리뷰로 지적받아 제거함). 앞단에
 * 신뢰된 리버스 프록시(nginx 등)가 붙고 그 프록시가 실제 클라이언트 IP를 항상 덮어써서 내려주는 헤더가
 * 확정되면, 그 헤더만 신뢰하도록 다시 바꿔야 한다 — 그 전까지는 프록시 뒤에서 모든 요청이 프록시의
 * 주소 하나로 잡혀 이 제한이 사실상 "서비스 전체 기준 분당 N회"로 동작한다(개별 사용자별이 아님).
 *
 * <p>단일 인스턴스 배포(H2/로컬 docker-compose) 기준의 인메모리 구현이다 — 여러 백엔드 인스턴스로 수평
 * 확장하면 인스턴스별로 따로 카운트되어 실제 한도가 인스턴스 수만큼 느슨해진다. 그 정도 트래픽 규모가
 * 되면 Redis 등 공유 저장소 기반으로 옮겨야 한다. {@link #windowsByIp}는 {@link #MAX_TRACKED_CLIENTS}로
 * 크기 상한을 두고 주기적으로 오래된 항목을 정리해 무한정 커지지 않게 한다(이것도 리뷰로 지적받아
 * 추가함 — 예전 구현은 정리 로직이 아예 없었다).
 */
@Component
public class DiagnosisChatRateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS_PER_WINDOW = 20;
    private static final long WINDOW_MILLIS = Duration.ofMinutes(1).toMillis();

    // 이 이상 다른 클라이언트를 추적해야 하면 더 오래된 항목부터 정리하거나(청소 주기 안이면) 새
    // 클라이언트는 일단 거부한다 — 맵이 이 크기를 넘어서기 전에 항상 걸러내는 상한선.
    private static final int MAX_TRACKED_CLIENTS = 10_000;
    // 윈도우가 끝난 지 이만큼 지난 항목은 더 이상 쓸모없다고 보고 정리 대상으로 삼는다.
    private static final long STALE_AFTER_MILLIS = WINDOW_MILLIS * 2;
    // 매 요청마다 맵 전체를 훑으면 낭비이므로, 이 요청 수마다 한 번씩만 정리를 시도한다.
    private static final long CLEANUP_EVERY_N_REQUESTS = 1_000;

    private final ConcurrentMap<String, RequestWindow> windowsByIp = new ConcurrentHashMap<>();
    private final AtomicLong requestCounter = new AtomicLong();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientIp = resolveClientIp(request);
        maybeEvictStaleEntries();

        if (!windowsByIp.containsKey(clientIp) && windowsByIp.size() >= MAX_TRACKED_CLIENTS) {
            throw new DiagnosisChatRateLimitExceededException(
                "추적 가능한 클라이언트 수 한도를 초과했습니다. 잠시 후 다시 시도해주세요."
            );
        }

        RequestWindow window = windowsByIp.computeIfAbsent(clientIp, ip -> new RequestWindow());
        if (!window.tryConsume()) {
            throw new DiagnosisChatRateLimitExceededException(
                "클라이언트당 분당 " + MAX_REQUESTS_PER_WINDOW + "회 한도를 초과했습니다: ip=" + clientIp
            );
        }
        return true;
    }

    private void maybeEvictStaleEntries() {
        if (requestCounter.incrementAndGet() % CLEANUP_EVERY_N_REQUESTS != 0) {
            return;
        }
        long now = System.currentTimeMillis();
        windowsByIp.entrySet().removeIf(entry -> entry.getValue().isStale(now));
    }

    private String resolveClientIp(HttpServletRequest request) {
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

        synchronized boolean isStale(long now) {
            return now - windowStartMillis >= STALE_AFTER_MILLIS;
        }
    }
}
