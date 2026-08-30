package com.mediinbusan.backend.wellness.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class PapagoDailyQuotaGuard {
    private static final ZoneId KOREA = ZoneId.of("Asia/Seoul");
    private volatile LocalDate exceededDate;

    public boolean isBlockedToday() {
        return LocalDate.now(KOREA).equals(exceededDate);
    }

    public void blockToday() {
        exceededDate = LocalDate.now(KOREA);
    }
}
