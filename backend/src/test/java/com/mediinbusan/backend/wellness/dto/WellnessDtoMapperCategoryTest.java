package com.mediinbusan.backend.wellness.dto;

import com.mediinbusan.backend.wellness.domain.WellnessPlaceCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TourAPI cat3 → {@link WellnessPlaceCategory} 매핑.
 *
 * 두 가지를 지킨다. 하나는 <b>모르는 코드가 들어와도 터지지 않고 OTHER로 떨어진다</b>는 안전장치.
 * 다른 하나는 <b>코드 표가 한국관광공사 서비스 분류체계와 일치한다</b>는 것이다 — 처음엔 서비스키가
 * 없어 표를 검증하지 못한 채 두었는데, 그 사이 대형마트(A04010400)와 면세점(A04010500)이 서로
 * 뒤바뀌고 쇼핑센터(A04010100)가 전통시장으로 분류된 걸 이 테스트가 오히려 굳혀 놓고 있었다.
 * 기대값을 바꿀 때는 코드가 아니라 공식 분류표(data.visitkorea.or.kr/resource/A04)를 먼저 볼 것.
 */
class WellnessDtoMapperCategoryTest {

    @Test
    @DisplayName("쇼핑_하위_코드는_공식_분류_그대로_옮겨진다")
    void 쇼핑_하위_코드는_공식_분류_그대로_옮겨진다() {
        assertThat(WellnessDtoMapper.categoryOf("A04010300")).isEqualTo(WellnessPlaceCategory.DEPARTMENT_STORE);
        assertThat(WellnessDtoMapper.categoryOf("A04010200")).isEqualTo(WellnessPlaceCategory.TRADITIONAL_MARKET);
        assertThat(WellnessDtoMapper.categoryOf("A04010400")).isEqualTo(WellnessPlaceCategory.LARGE_MART);
        assertThat(WellnessDtoMapper.categoryOf("A04010500")).isEqualTo(WellnessPlaceCategory.DUTY_FREE);
        assertThat(WellnessDtoMapper.categoryOf("A04010700")).isEqualTo(WellnessPlaceCategory.CRAFT_WORKSHOP);
        assertThat(WellnessDtoMapper.categoryOf("A04010900")).isEqualTo(WellnessPlaceCategory.LOCAL_PRODUCTS);
    }

    @Test
    @DisplayName("면세점과_사후면세점은_같은_분류로_묶인다")
    void 면세점과_사후면세점은_같은_분류로_묶인다() {
        // WellnessPlaceCategory.DUTY_FREE의 정의가 "면세점(사후면세점 포함)"이다.
        assertThat(WellnessDtoMapper.categoryOf("A04011000"))
            .isEqualTo(WellnessDtoMapper.categoryOf("A04010500"))
            .isEqualTo(WellnessPlaceCategory.DUTY_FREE);
    }

    @Test
    @DisplayName("쇼핑센터와_기념품점은_전문매장_상가로_묶인다")
    void 쇼핑센터와_기념품점은_전문매장_상가로_묶인다() {
        // 쇼핑센터(A04010100)는 시장이 아니라 상가다 — 별도 분류를 두지 않고
        // SPECIALTY_STORE("전문매장·상가")에 넣는다(WellnessDtoMapper.categoryOf 주석 참고).
        assertThat(WellnessDtoMapper.categoryOf("A04010100")).isEqualTo(WellnessPlaceCategory.SPECIALTY_STORE);
        assertThat(WellnessDtoMapper.categoryOf("A04010600")).isEqualTo(WellnessPlaceCategory.SPECIALTY_STORE);
    }

    @Test
    @DisplayName("표에_없는_코드는_OTHER로_떨어진다")
    void 표에_없는_코드는_OTHER로_떨어진다() {
        // 관광지(A01)·숙박(B02) 코드는 아직 세분화 대상이 아니다 — 나중에 표를 늘리면 이 기대값이
        // 바뀐다. 그때까지는 "모르는 코드"와 같은 취급이어야 한다.
        assertThat(WellnessDtoMapper.categoryOf("A01010400")).isEqualTo(WellnessPlaceCategory.OTHER);
        assertThat(WellnessDtoMapper.categoryOf("B02010100")).isEqualTo(WellnessPlaceCategory.OTHER);
        assertThat(WellnessDtoMapper.categoryOf("존재하지않는코드")).isEqualTo(WellnessPlaceCategory.OTHER);
    }

    @Test
    @DisplayName("아직_재수집하지_않아_코드가_없는_장소도_OTHER다")
    void 아직_재수집하지_않아_코드가_없는_장소도_OTHER다() {
        // V11 이전에 저장된 행은 category_code가 NULL이다.
        assertThat(WellnessDtoMapper.categoryOf(null)).isEqualTo(WellnessPlaceCategory.OTHER);
        assertThat(WellnessDtoMapper.categoryOf("")).isEqualTo(WellnessPlaceCategory.OTHER);
        assertThat(WellnessDtoMapper.categoryOf("   ")).isEqualTo(WellnessPlaceCategory.OTHER);
    }
}
