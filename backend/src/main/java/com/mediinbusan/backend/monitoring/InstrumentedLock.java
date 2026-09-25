package com.mediinbusan.backend.monitoring;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 지표를 남기는 {@code synchronized} 대체재. 비공정(non-fair)·재진입·인터럽트 불가라는 점에서
 * 메서드 레벨 {@code synchronized}와 의미가 같다 — 동작은 그대로 두고 "얼마나 기다렸나"만 보이게 한다.
 *
 * <ul>
 *   <li>{@code mediinbusan.lock.wait} (Timer) — 락을 얻기까지 기다린 시간</li>
 *   <li>{@code mediinbusan.lock.held} (Timer) — 락을 쥐고 있던 시간(= 그동안 다른 요청은 전부 대기)</li>
 *   <li>{@code mediinbusan.lock.queue} (Gauge) — 지금 이 락을 기다리는 스레드 수</li>
 * </ul>
 * 모두 {@code lock} 태그로 구분한다. 레지스트리를 {@link Metrics#globalRegistry}로 쓰는 이유는
 * {@link ExternalApiMetrics} 주석 참고.
 */
public final class InstrumentedLock {

    private final ReentrantLock lock = new ReentrantLock();
    private final Timer waitTimer;
    private final Timer heldTimer;
    // 락을 쥔 스레드만 읽고 쓴다(락이 가시성을 보장한다).
    private long acquiredAtNanos;

    public InstrumentedLock(String name) {
        this.waitTimer = Timer.builder("mediinbusan.lock.wait")
            .description("락 획득까지 대기한 시간")
            .tag("lock", name)
            .register(Metrics.globalRegistry);
        this.heldTimer = Timer.builder("mediinbusan.lock.held")
            .description("락을 점유한 시간")
            .tag("lock", name)
            .register(Metrics.globalRegistry);
        Gauge.builder("mediinbusan.lock.queue", lock, ReentrantLock::getQueueLength)
            .description("락을 기다리는 스레드 수")
            .tag("lock", name)
            .register(Metrics.globalRegistry);
    }

    public void lock() {
        long start = System.nanoTime();
        lock.lock();
        if (lock.getHoldCount() == 1) {
            long now = System.nanoTime();
            waitTimer.record(now - start, TimeUnit.NANOSECONDS);
            acquiredAtNanos = now;
        }
    }

    public void unlock() {
        if (lock.getHoldCount() == 1) {
            heldTimer.record(System.nanoTime() - acquiredAtNanos, TimeUnit.NANOSECONDS);
        }
        lock.unlock();
    }
}
