package com.mediinbusan.backend.wellness.domain;

import com.mediinbusan.backend.hospital.domain.Coordinates;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ingest를 여러 번 돌렸을 때 상세 정보가 <b>누적</b>되는지를 지킨다.
 *
 * 설명·전화번호·방문 정보는 목록 응답에 없고 상세 오퍼레이션을 따로 불러야만 채워지는데, 그 호출은
 * 일일 트래픽 한도 때문에 매 실행마다 일부 장소에만 돌아간다. 그래서 "이번 수집에서 값을 못 받았다"와
 * "값이 실제로 없어졌다"를 구분하지 못하면, 이번에 호출 대상이 아니었던 장소의 값이 매번 지워진다 —
 * 실제로 그랬고, 그 탓에 아무리 여러 번 돌려도 저장된 설명 수가 한도(300건)를 넘지 못했다.
 */
class WellnessPlaceTest {

    @Test
    void 이번_수집에서_설명과_전화번호를_못받으면_지난_값을_유지한다() {
        WellnessPlace place = place("해운대 해수욕장", "부산 대표 해수욕장입니다.", "051-000-0000");

        // 상세 조회 한도 밖으로 밀려난 장소 — 목록 응답만 있으므로 설명·전화번호가 null로 들어온다.
        place.updateFrom(
            "해운대 해수욕장", WellnessPlaceType.TOURIST_ATTRACTION, "A01011200", "부산 해운대구 우동",
            new Coordinates(35.1587, 129.1604), "https://img.example/haeundae.jpg", null, null,
            LocalDate.of(2026, 9, 1)
        );

        assertThat(place.getDescription()).isEqualTo("부산 대표 해수욕장입니다.");
        assertThat(place.getPhoneNumber()).isEqualTo("051-000-0000");
        // 목록 응답이 항상 주는 값들은 그대로 갱신된다.
        assertThat(place.getAddress()).isEqualTo("부산 해운대구 우동");
        assertThat(place.getModifiedDate()).isEqualTo(LocalDate.of(2026, 9, 1));
    }

    @Test
    void 새_설명을_받으면_갱신한다() {
        WellnessPlace place = place("해운대 해수욕장", "옛 설명", null);

        place.updateFrom(
            "해운대 해수욕장", WellnessPlaceType.TOURIST_ATTRACTION, null, "부산 해운대구 우동",
            new Coordinates(35.1587, 129.1604), null, "새 설명", "051-111-1111", LocalDate.now()
        );

        assertThat(place.getDescription()).isEqualTo("새 설명");
        assertThat(place.getPhoneNumber()).isEqualTo("051-111-1111");
    }

    @Test
    void 방문정보도_빈_값으로는_지워지지_않는다() {
        WellnessPlace place = place("톤쇼우", null, null);
        place.applyVisitInfo("매일 11:00~21:00", null, "돈카츠", null, null, null);

        // 다음 ingest에서 이 장소가 detailIntro2 대상이 아니었던 경우.
        place.applyVisitInfo(null, "매주 월요일", null, null, "  ", null);

        assertThat(place.getBusinessHours()).isEqualTo("매일 11:00~21:00");
        assertThat(place.getSignatureMenu()).isEqualTo("돈카츠");
        assertThat(place.getRestDate()).isEqualTo("매주 월요일");
        // 공백만 있는 값은 채운 것으로 치지 않는다.
        assertThat(place.getParkingInfo()).isNull();
    }

    private static WellnessPlace place(String name, String description, String phoneNumber) {
        return new WellnessPlace(
            "tour-1", name, WellnessPlaceType.TOURIST_ATTRACTION, null, "부산",
            new Coordinates(35.1, 129.1), null, description, phoneNumber, LocalDate.of(2026, 1, 1)
        );
    }
}
