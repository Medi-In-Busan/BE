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
    void XForwardedFor_헤더가_있으면_그_원클라이언트_IP_기준으로_카운트한다() {
        DiagnosisChatRateLimitInterceptor interceptor = new DiagnosisChatRateLimitInterceptor();
        HttpServletRequest viaProxyA = Mockito.mock(HttpServletRequest.class);
        Mockito.when(viaProxyA.getHeader("X-Forwarded-For")).thenReturn("9.9.9.9, 10.0.0.1");
        Mockito.when(viaProxyA.getRemoteAddr()).thenReturn("10.0.0.1"); // 프록시 주소(공통)

        for (int i = 0; i < 20; i++) {
            interceptor.preHandle(viaProxyA, response(), new Object());
        }

        assertThatThrownBy(() -> interceptor.preHandle(viaProxyA, response(), new Object()))
            .isInstanceOf(DiagnosisChatRateLimitExceededException.class);
    }

    private HttpServletRequest requestFromIp(String ip) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        Mockito.when(request.getRemoteAddr()).thenReturn(ip);
        return request;
    }

    private HttpServletResponse response() {
        return Mockito.mock(HttpServletResponse.class);
    }
}
