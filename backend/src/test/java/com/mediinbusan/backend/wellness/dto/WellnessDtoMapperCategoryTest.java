package com.mediinbusan.backend.wellness.dto;

import com.mediinbusan.backend.wellness.domain.WellnessPlaceCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TourAPI cat3 → {@link WellnessPlaceCategory} 매핑.
 *
 * 여기서 지키려는 건 "코드 표가 정확하다"가 아니라 — 그건 실제 응답으로만 확인할 수 있다
 * (WellnessDtoMapper.categoryOf 주석 참고) — <b>모르는 코드가 들어와도 터지지 않고 OTHER로
 * 떨어진다</b>는 쪽이다. 표가 틀려 있어도 목록이 깨지지 않고 세분화만 안 되게 하는 안전장치다.
 */
class WellnessDtoMapperCategoryTest {

    @Test
    @DisplayName("쇼핑_하위_코드는_세부_분류로_옮겨진다")
    void 쇼핑_하위_코드는_세부_분류로_옮겨진다() {
        assertThat(WellnessDtoMapper.categoryOf("A04010300")).isEqualTo(WellnessPlaceCategory.DEPARTMENT_STORE);
        assertThat(WellnessDtoMapper.categoryOf("A04010200")).isEqualTo(WellnessPlaceCategory.TRADITIONAL_MARKET);
        assertThat(WellnessDtoMapper.categoryOf("A04010400")).isEqualTo(WellnessPlaceCategory.DUTY_FREE);
    }

    @Test
    @DisplayName("5일장과_상설시장은_같은_전통시장으로_묶인다")
    void 오일장과_상설시장은_같은_전통시장으로_묶인다() {
        assertThat(WellnessDtoMapper.categoryOf("A04010100"))
            .isEqualTo(WellnessDtoMapper.categoryOf("A04010200"))
            .isEqualTo(WellnessPlaceCategory.TRADITIONAL_MARKET);
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
