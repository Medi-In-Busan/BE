package com.mediinbusan.backend.wellness.service;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 상세 보강 호출을 병렬로 돌리는 부분의 계약을 고정한다.
 *
 * 두 가지가 깨지면 조용히 잘못된 결과가 나온다: (1) 작업이 다 끝나기 전에 반환하면 결과 배열을
 * 반쯤 채운 채로 읽게 되고, (2) 동시 호출 수가 설정값을 넘으면 공공 API가 거부하기 시작하는데
 * 그 실패는 개별 try/catch에 삼켜져 "왜 안 채워지지"로만 나타난다.
 */
class WellnessFetchTaskRunnerTest {

    @Test
    void 모든_작업이_끝난_뒤에_반환한다() {
        AtomicInteger done = new AtomicInteger();
        List<Runnable> tasks = new ArrayList<>();
        for (int index = 0; index < 50; index++) {
            tasks.add(() -> {
                sleepBriefly();
                done.incrementAndGet();
            });
        }

        WellnessIngestionService.runFetchTasks(tasks, 6);

        // 반환 시점에 이미 전부 끝나 있어야 결과 배열을 그대로 읽어도 안전하다.
        assertThat(done.get()).isEqualTo(50);
    }

    @Test
    void 동시_실행_수가_설정값을_넘지_않는다() {
        AtomicInteger running = new AtomicInteger();
        AtomicInteger peak = new AtomicInteger();
        List<Runnable> tasks = new ArrayList<>();
        for (int index = 0; index < 60; index++) {
            tasks.add(() -> {
                peak.accumulateAndGet(running.incrementAndGet(), Math::max);
                sleepBriefly();
                running.decrementAndGet();
            });
        }

        WellnessIngestionService.runFetchTasks(tasks, 4);

        assertThat(peak.get()).isLessThanOrEqualTo(4);
        assertThat(running.get()).isZero();
    }

    @Test
    void 동시성이_1이면_호출_스레드에서_순차로_실행한다() {
        Set<String> threads = ConcurrentHashMap.newKeySet();
        List<Runnable> tasks = new ArrayList<>();
        for (int index = 0; index < 10; index++) {
            tasks.add(() -> threads.add(Thread.currentThread().getName()));
        }

        int used = WellnessIngestionService.runFetchTasks(tasks, 1);

        assertThat(used).isEqualTo(1);
        // 풀을 아예 만들지 않는다 — 공공 API가 동시 호출을 거부할 때 이 값만 1로 낮춰 예전 동작으로
        // 정확히 되돌릴 수 있어야 한다.
        assertThat(threads).containsExactly(Thread.currentThread().getName());
    }

    @Test
    void 잘못된_동시성_값도_최소_1로_보정한다() {
        AtomicInteger done = new AtomicInteger();
        List<Runnable> tasks = List.of(done::incrementAndGet, done::incrementAndGet);

        assertThat(WellnessIngestionService.runFetchTasks(tasks, 0)).isEqualTo(1);
        assertThat(WellnessIngestionService.runFetchTasks(tasks, -5)).isEqualTo(1);
        assertThat(done.get()).isEqualTo(4);
    }

    @Test
    void 작업이_없거나_하나뿐이면_풀을_만들지_않는다() {
        AtomicInteger done = new AtomicInteger();

        assertThat(WellnessIngestionService.runFetchTasks(Collections.emptyList(), 6)).isEqualTo(1);
        assertThat(WellnessIngestionService.runFetchTasks(List.of(done::incrementAndGet), 6)).isEqualTo(1);
        assertThat(done.get()).isEqualTo(1);
    }

    @Test
    void 실제로_병렬로_돈다() throws Exception {
        // 동시성만큼의 작업이 서로를 기다릴 수 있어야 한다 — 순차 실행이면 이 래치가 절대 안 풀린다.
        CountDownLatch latch = new CountDownLatch(4);
        List<Runnable> tasks = new ArrayList<>();
        for (int index = 0; index < 4; index++) {
            tasks.add(() -> {
                latch.countDown();
                try {
                    latch.await(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        WellnessIngestionService.runFetchTasks(tasks, 4);

        assertThat(latch.getCount()).isZero();
    }

    private static void sleepBriefly() {
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
