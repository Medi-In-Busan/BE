package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.document.client.PapagoTranslationClient;
import com.mediinbusan.backend.hospital.domain.Coordinates;
import com.mediinbusan.backend.wellness.domain.WellnessPlace;
import com.mediinbusan.backend.wellness.domain.WellnessPlaceType;
import com.mediinbusan.backend.wellness.dto.WellnessPlaceResponse;
import com.mediinbusan.backend.wellness.repository.WellnessPlaceRepository;
import com.mediinbusan.backend.wellness.repository.WellnessPlaceTranslationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * 다국어 장소 상세가 DB 커넥션 풀을 교착시키지 않는지 확인한다.
 *
 * 예전 구조는 요청 한 건이 커넥션을 2개 썼다 — WellnessService의 readOnly 트랜잭션이 하나를 쥔 채
 * 번역 서비스가 REQUIRES_NEW로 하나를 더 잡았다. 동시 요청이 풀 크기를 넘으면 모두 첫 번째 커넥션을
 * 나눠 가진 채 두 번째를 기다려, 커넥션 획득 타임아웃으로만 풀렸다(k6 베이스라인에서 상세 63% 실패,
 * 무관한 병원 목록까지 500). 풀을 2개로 줄여 그 상황을 만든다.
 *
 * 스레드 간에 커밋된 데이터를 봐야 해서 @Transactional(자동 롤백)을 쓰지 않고 직접 정리한다.
 */
@SpringBootTest(properties = {
    "spring.datasource.hikari.maximum-pool-size=2",
    "spring.datasource.hikari.connection-timeout=1000"
})
class WellnessDetailConnectionPoolTest {

    private static final String PREFIX = "pool-test-";
    private static final long PAPAGO_DELAY_MILLIS = 300;

    @Autowired
    private WellnessService wellnessService;

    @Autowired
    private WellnessPlaceRepository placeRepository;

    @Autowired
    private WellnessPlaceTranslationRepository translationRepository;

    @MockitoBean
    private PapagoTranslationClient papago;

    // 동시에 진행 중인 Papago 호출 수와 그 최댓값 — 번역이 전역 락으로 한 줄로 서는지 본다.
    private final AtomicInteger papagoInFlight = new AtomicInteger();
    private final AtomicInteger papagoMaxInFlight = new AtomicInteger();

    @BeforeEach
    void setUp() {
        // 느린 Papago: 줄마다 "EN:"을 붙여 돌려준다(필드 수가 원문과 같아야 번역으로 인정된다).
        when(papago.translate(anyString(), eq("en"))).thenAnswer(invocation -> {
            papagoMaxInFlight.accumulateAndGet(papagoInFlight.incrementAndGet(), Math::max);
            try {
                Thread.sleep(PAPAGO_DELAY_MILLIS);
            } finally {
                papagoInFlight.decrementAndGet();
            }
            String text = invocation.getArgument(0);
            return Stream.of(text.split("\n", -1)).map(line -> "EN:" + line).collect(Collectors.joining("\n"));
        });
    }

    @AfterEach
    void tearDown() {
        translationRepository.deleteAll(translationRepository.findAll().stream()
            .filter(translation -> translation.contentId().startsWith(PREFIX)).toList());
        placeRepository.deleteAll(placeRepository.findAll().stream()
            .filter(place -> place.getContentId().startsWith(PREFIX)).toList());
    }

    @Test
    void 풀_크기보다_많은_다국어_상세_요청이_동시에_와도_모두_번역되어_성공한다() throws Exception {
        List<String> contentIds = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            String contentId = PREFIX + i;
            placeRepository.save(place(contentId, "장소 " + i));
            contentIds.add(contentId);
        }

        List<WellnessPlaceResponse> results = runConcurrently(contentIds);

        assertThat(results).hasSize(6).allSatisfy(result -> {
            assertThat(result.translated()).isTrue();
            assertThat(result.name()).startsWith("EN:");
        });
    }

    @Test
    void 서로_다른_장소의_번역은_Papago를_한_줄로_세우지_않는다() throws Exception {
        List<String> contentIds = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String contentId = PREFIX + "parallel-" + i;
            placeRepository.save(place(contentId, "병렬 장소 " + i));
            contentIds.add(contentId);
        }

        runConcurrently(contentIds);

        // 예전 JVM 전역 락에서는 항상 1이었다(응답이 300ms × 요청 수로 직렬화).
        assertThat(papagoMaxInFlight.get()).isGreaterThan(1);
    }

    @Test
    void 같은_장소를_동시에_번역해도_실패하지_않고_캐시는_한_건만_남는다() throws Exception {
        String contentId = PREFIX + "same";
        placeRepository.save(place(contentId, "같은 장소"));

        List<WellnessPlaceResponse> results = runConcurrently(List.of(contentId, contentId, contentId));

        assertThat(results).hasSize(3).allSatisfy(result -> assertThat(result.name()).startsWith("EN:"));
        assertThat(translationRepository.findByContentIdAndLanguageCode(contentId, "en")).isPresent();
        assertThat(translationRepository.findAll().stream()
            .filter(translation -> translation.contentId().equals(contentId))).hasSize(1);
    }

    /** 모든 요청을 한 순간에 출발시킨다. 하나라도 예외면 Future.get()이 그대로 던져 테스트가 실패한다. */
    private List<WellnessPlaceResponse> runConcurrently(List<String> contentIds) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(contentIds.size());
        try {
            CountDownLatch start = new CountDownLatch(1);
            List<Future<WellnessPlaceResponse>> futures = new ArrayList<>();
            for (String contentId : contentIds) {
                futures.add(pool.submit(() -> {
                    start.await();
                    return wellnessService.getPlaceDetail(contentId, "en");
                }));
            }
            start.countDown();
            List<WellnessPlaceResponse> results = new ArrayList<>();
            for (Future<WellnessPlaceResponse> future : futures) {
                results.add(future.get(30, TimeUnit.SECONDS));
            }
            return results;
        } finally {
            pool.shutdownNow();
        }
    }

    private WellnessPlace place(String contentId, String name) {
        return new WellnessPlace(
            contentId,
            name,
            WellnessPlaceType.TOURIST_ATTRACTION,
            null,
            "부산 어딘가",
            new Coordinates(35.1587, 129.1604),
            null,
            "설명",
            null,
            LocalDate.now()
        );
    }
}
