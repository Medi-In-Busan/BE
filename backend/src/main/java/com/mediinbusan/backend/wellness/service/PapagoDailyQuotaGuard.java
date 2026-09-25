package com.mediinbusan.backend.wellness.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class PapagoDailyQuotaGuard {
    private static final ZoneId KOREA = ZoneId.of("Asia/Seoul");
    private volatile LocalDate exceededDate;

    public PapagoDailyQuotaGuard() {
        // 1이면 오늘은 Papago 번역을 건너뛰고 한국어 원문으로 폴백 중이다(재시작하면 0으로 초기화된다).
        Gauge.builder("mediinbusan.papago.quota.blocked", this, guard -> guard.isBlockedToday() ? 1 : 0)
            .description("Papago 일일 한도 초과로 번역을 차단 중인지(1/0)")
            .register(Metrics.globalRegistry);
    }

    public boolean isBlockedToday() {
        return LocalDate.now(KOREA).equals(exceededDate);
    }

    public void blockToday() {
        exceededDate = LocalDate.now(KOREA);
    }
}
