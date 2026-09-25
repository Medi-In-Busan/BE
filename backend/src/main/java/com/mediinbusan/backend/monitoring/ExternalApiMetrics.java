package com.mediinbusan.backend.monitoring;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;

import java.util.function.Supplier;

/**
 * 외부 API(Papago·CLOVA·Gemini·TourAPI·Kakao) 호출 지표.
 *
 * <ul>
 *   <li>{@value #CALL} (Timer) — 끝난 호출의 소요 시간. 태그 client/operation/outcome/exception</li>
 *   <li>{@value #ACTIVE} (LongTaskTimer) — <b>아직 안 끝난</b> 호출 수와 그중 가장 오래된 것의 경과 시간.
 *       Timer는 호출이 끝나야 기록되므로 타임아웃 없는 클라이언트가 멈춰 있으면 Timer에는 아무것도 안 찍힌다 —
 *       그 상태를 보려면 이 지표(`_active_count`, `_duration_sum`, `_max`)를 봐야 한다.</li>
 * </ul>
 *
 * Spring의 RestClient 자동 관측(http.client.requests)을 쓰지 않고 직접 기록하는 이유: 그 지표의 uri 태그에
 * 요청 URL이 실리는데, Gemini는 쿼리스트링에 API 키가 있고 CLOVA는 호출 URL 자체가 GitHub Secrets로 관리하는
 * 값이다. 여기서는 client/operation을 고정 문자열로만 태깅해 URL·본문·키가 지표로 새지 않게 한다.
 *
 * 레지스트리는 {@link Metrics#globalRegistry}를 쓴다 — Spring Boot가 Prometheus 레지스트리를 여기에 붙여주므로
 * 운영에서는 그대로 수집되고, 클라이언트를 {@code new}로 만드는 단위 테스트에서는 아무 레지스트리도 없어 no-op이다
 * (생성자 시그니처를 바꾸지 않기 위한 선택).
 */
public final class ExternalApiMetrics {

    public static final String CALL = "mediinbusan.external.api";
    public static final String ACTIVE = "mediinbusan.external.api.active";

    private ExternalApiMetrics() {
    }

    public static <T> T record(String client, String operation, Supplier<T> call) {
        MeterRegistry registry = Metrics.globalRegistry;
        LongTaskTimer.Sample active = LongTaskTimer.builder(ACTIVE)
            .description("진행 중인 외부 API 호출")
            .tags("client", client, "operation", operation)
            .register(registry)
            .start();
        Timer.Sample sample = Timer.start(registry);
        String outcome = "error";
        String exception = "none";
        try {
            T result = call.get();
            outcome = "success";
            return result;
        } catch (RuntimeException e) {
            exception = e.getClass().getSimpleName();
            throw e;
        } finally {
            active.stop();
            sample.stop(Timer.builder(CALL)
                .description("외부 API 호출 소요 시간")
                .tags("client", client, "operation", operation, "outcome", outcome, "exception", exception)
                .register(registry));
        }
    }
}
