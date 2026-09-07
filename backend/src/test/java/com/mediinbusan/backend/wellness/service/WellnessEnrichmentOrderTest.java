package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.hospital.domain.Coordinates;
import com.mediinbusan.backend.wellness.domain.WellnessPlaceType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 상세 보강 한도(기본 300건)를 어느 장소에 쓸지 정하는 순서를 검증한다.
 *
 * 이게 왜 테스트할 가치가 있냐면 — 예전에는 후보 리스트를 앞에서부터 그냥 잘라 썼고, 그 리스트가
 * 관광지 → 음식점 → 숙박 → 쇼핑 순서로 쌓이는 바람에 한도 300건이 관광지(부산 기준 351건)에서
 * 전부 소진되어 음식점·숙박·쇼핑은 매번 한 건도 보강되지 않았다. 원격 DB 실측으로 확인된 증상이다
 * (관광지 351건 중 설명 300건, 나머지 세 유형 전부 0건).
 */
class WellnessEnrichmentOrderTest {

    // 부산 기준 실측 보유량.
    private static final int TOURIST_ATTRACTIONS = 351;
    private static final int RESTAURANTS = 420;
    private static final int LODGINGS = 119;
    private static final int SHOPPINGS = 976;
    private static final int FETCH_LIMIT = 300;

    @Test
    void 한도만큼_잘라도_네_유형이_모두_보강대상에_들어간다() {
        List<WellnessPlaceCandidate> order = WellnessIngestionService.enrichmentOrder(busanCandidates());

        Map<WellnessPlaceType, Long> withinLimit = order.stream()
            .limit(FETCH_LIMIT)
            .collect(Collectors.groupingBy(WellnessPlaceCandidate::placeType, Collectors.counting()));

        assertThat(withinLimit.keySet()).containsExactlyInAnyOrder(
            WellnessPlaceType.TOURIST_ATTRACTION,
            WellnessPlaceType.RESTAURANT,
            WellnessPlaceType.LODGING,
            WellnessPlaceType.SHOPPING
        );
        // 라운드로빈이므로 네 유형이 고르게 나눠 갖는다 — 특히 음식점이 0건이 아니어야 한다.
        assertThat(withinLimit.get(WellnessPlaceType.RESTAURANT)).isEqualTo(FETCH_LIMIT / 4);
    }

    @Test
    void 예전방식처럼_리스트순서대로_자르면_음식점은_한건도_못받는다() {
        // 회귀 방지용 대조군 — 고치기 전 동작을 그대로 재현해 둔다.
        Map<WellnessPlaceType, Long> withinLimit = busanCandidates().stream()
            .limit(FETCH_LIMIT)
            .collect(Collectors.groupingBy(WellnessPlaceCandidate::placeType, Collectors.counting()));

        assertThat(withinLimit).containsOnlyKeys(WellnessPlaceType.TOURIST_ATTRACTION);
        assertThat(withinLimit.get(WellnessPlaceType.RESTAURANT)).isNull();
    }

    @Test
    void 보유량이_적은_유형이_소진되면_남는_몫은_다른_유형으로_흘러간다() {
        List<WellnessPlaceCandidate> candidates = new ArrayList<>();
        candidates.addAll(candidatesOf(WellnessPlaceType.LODGING, 2));
        candidates.addAll(candidatesOf(WellnessPlaceType.SHOPPING, 5));

        List<WellnessPlaceType> types = WellnessIngestionService.enrichmentOrder(candidates).stream()
            .map(WellnessPlaceCandidate::placeType)
            .toList();

        // 숙박 2건이 먼저 끝나고, 그 뒤로는 쇼핑만 이어진다 — 남는 자리를 놀리지 않는다.
        assertThat(types).containsExactly(
            WellnessPlaceType.LODGING, WellnessPlaceType.SHOPPING,
            WellnessPlaceType.LODGING, WellnessPlaceType.SHOPPING,
            WellnessPlaceType.SHOPPING, WellnessPlaceType.SHOPPING, WellnessPlaceType.SHOPPING
        );
    }

    @Test
    void 후보를_하나도_빠뜨리거나_중복시키지_않는다() {
        List<WellnessPlaceCandidate> candidates = busanCandidates();

        List<WellnessPlaceCandidate> order = WellnessIngestionService.enrichmentOrder(candidates);

        assertThat(order).hasSize(candidates.size());
        assertThat(order.stream().map(WellnessPlaceCandidate::contentId).distinct().count())
            .isEqualTo(candidates.size());
    }

    /** fetchTourApiCandidates가 쌓는 것과 같은 순서(관광지 → 음식점 → 숙박 → 쇼핑). */
    private static List<WellnessPlaceCandidate> busanCandidates() {
        List<WellnessPlaceCandidate> candidates = new ArrayList<>();
        candidates.addAll(candidatesOf(WellnessPlaceType.TOURIST_ATTRACTION, TOURIST_ATTRACTIONS));
        candidates.addAll(candidatesOf(WellnessPlaceType.RESTAURANT, RESTAURANTS));
        candidates.addAll(candidatesOf(WellnessPlaceType.LODGING, LODGINGS));
        candidates.addAll(candidatesOf(WellnessPlaceType.SHOPPING, SHOPPINGS));
        return candidates;
    }

    private static List<WellnessPlaceCandidate> candidatesOf(WellnessPlaceType placeType, int count) {
        List<WellnessPlaceCandidate> candidates = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            candidates.add(new WellnessPlaceCandidate(
                "tour-" + placeType.name() + "-" + index,
                placeType.name() + " " + index,
                placeType,
                null,
                "부산광역시",
                new Coordinates(35.1, 129.1),
                null,
                null,
                null,
                LocalDate.now(),
                WellnessVisitInfo.EMPTY
            ));
        }
        return candidates;
    }
}
