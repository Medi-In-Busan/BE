package com.mediinbusan.backend.diagnosischat.controller;

import com.mediinbusan.backend.diagnosischat.exception.DiagnosisChatRateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiagnosisChatRateLimitInterceptorTest {

    @Test
    void 같은_IP가_한도_이내로_요청하면_전부_통과한다() {
        DiagnosisChatRateLimitInterceptor interceptor = new DiagnosisChatRateLimitInterceptor();
        HttpServletRequest request = requestFromIp("1.2.3.4");

        for (int i = 0; i < 20; i++) {
            assertThat(interceptor.preHandle(request, response(), new Object())).isTrue();
        }
    }

    @Test
    void 같은_IP가_한도를_초과하면_예외를_던진다() {
        DiagnosisChatRateLimitInterceptor interceptor = new DiagnosisChatRateLimitInterceptor();
        HttpServletRequest request = requestFromIp("1.2.3.4");

        for (int i = 0; i < 20; i++) {
            interceptor.preHandle(request, response(), new Object());
        }

        assertThatThrownBy(() -> interceptor.preHandle(request, response(), new Object()))
            .isInstanceOf(DiagnosisChatRateLimitExceededException.class);
    }

    @Test
    void 다른_IP는_서로의_한도에_영향을_주지_않는다() {
        DiagnosisChatRateLimitInterceptor interceptor = new DiagnosisChatRateLimitInterceptor();
        HttpServletRequest ipA = requestFromIp("1.1.1.1");
        HttpServletRequest ipB = requestFromIp("2.2.2.2");

        for (int i = 0; i < 20; i++) {
            interceptor.preHandle(ipA, response(), new Object());
        }

        assertThat(interceptor.preHandle(ipB, response(), new Object())).isTrue();
    }

    @Test
    void XForwardedFor_헤더값을_바꿔도_한도_우회가_안된다() {
        // 신뢰할 수 없는 X-Forwarded-For를 카운트 키로 쓰면, 매 요청 다른 값을 보내는 것만으로
        // 한도를 우회할 수 있었다(리뷰 지적 사항) — remoteAddr만 신뢰하므로 헤더값과 무관하게
        // 같은 실제 발신지는 같은 버킷으로 카운트되어야 한다.
        DiagnosisChatRateLimitInterceptor interceptor = new DiagnosisChatRateLimitInterceptor();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        Mockito.when(request.getHeader("X-Forwarded-For")).thenAnswer(invocation -> "spoofed-" + Math.random());

        for (int i = 0; i < 20; i++) {
            interceptor.preHandle(request, response(), new Object());
        }

        assertThatThrownBy(() -> interceptor.preHandle(request, response(), new Object()))
            .isInstanceOf(DiagnosisChatRateLimitExceededException.class);
    }

    @Test
    void 추적_클라이언트_수가_상한을_넘으면_새_클라이언트는_거부된다() {
        DiagnosisChatRateLimitInterceptor interceptor = new DiagnosisChatRateLimitInterceptor();

        // MAX_TRACKED_CLIENTS(10,000)만큼 서로 다른 클라이언트로 한 번씩 채운다 — 정리 주기
        // (1,000요청마다)에 걸리지 않도록 각 클라이언트가 한도 안에서 소진되지 않게 1회씩만 보낸다.
        for (int i = 0; i < 10_000; i++) {
            interceptor.preHandle(requestFromIp("10.0." + (i / 250) + "." + (i % 250)), response(), new Object());
        }

        assertThatThrownBy(() -> interceptor.preHandle(requestFromIp("255.255.255.255"), response(), new Object()))
            .isInstanceOf(DiagnosisChatRateLimitExceededException.class);
    }

    private HttpServletRequest requestFromIp(String ip) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRemoteAddr()).thenReturn(ip);
        return request;
    }

    private HttpServletResponse response() {
        return Mockito.mock(HttpServletResponse.class);
    }
}
