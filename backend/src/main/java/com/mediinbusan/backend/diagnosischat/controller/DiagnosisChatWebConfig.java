package com.mediinbusan.backend.diagnosischat.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** diagnosis-chat 엔드포인트에만 {@link DiagnosisChatRateLimitInterceptor}를 건다 — 다른 기능
 *  (hospital/wellness/document)의 요청 처리에는 영향을 주지 않는다. */
@Configuration
public class DiagnosisChatWebConfig implements WebMvcConfigurer {

    private final DiagnosisChatRateLimitInterceptor rateLimitInterceptor;

    public DiagnosisChatWebConfig(DiagnosisChatRateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/api/v1/diagnosis-chat");
    }
}
