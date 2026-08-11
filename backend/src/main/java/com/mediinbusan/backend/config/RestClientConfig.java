package com.mediinbusan.backend.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;

/**
 * spring-boot-starter-webmvc는 (구버전 spring-boot-starter-web과 달리) RestClient.Builder를
 * 자동구성해주지 않아서, 외부 API를 호출하는 Client 빈들이 공유해서 쓸 프로토타입 빈을 직접 등록한다.
 * 컴포넌트마다 새 Builder 인스턴스를 받도록 prototype 스코프로 둔다(빌더는 상태를 갖는 mutable 객체).
 */
@Configuration
public class RestClientConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
