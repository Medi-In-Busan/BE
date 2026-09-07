package com.mediinbusan.backend.wellness.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * detailIntro2 필드명 표({@link WellnessIngestionService#toVisitInfo})를 고정한다.
 *
 * 이 표가 이 변경의 핵심이자 가장 깨지기 쉬운 부분이다 — 같은 뜻의 값을 TourAPI가 콘텐츠 타입마다
 * 다른 이름으로 내려주기 때문에, 이름 하나가 어긋나면 컴파일도 통과하고 수집도 성공하는데 화면의
 * 그 줄만 조용히 사라진다. 실제 응답 대신 타입별 응답 모양을 세워두고 매핑만 검증한다.
 */
class WellnessVisitInfoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void 음식점_detailIntro2의_영업시간_대표메뉴_휴무일_주차를_옮긴다() throws Exception {
        WellnessVisitInfo visitInfo = WellnessIngestionService.toVisitInfo(mapper.readTree("""
            {
              "contentid": "1", "contenttypeid": "39",
              "opentimefood": "매일 09:00 ~ 21:00",
              "restdatefood": "매주 화요일",
              "firstmenu": "돼지국밥",
              "treatmenu": "수육백반",
              "parkingfood": "주차 가능(20대)"
            }
            """));

        assertThat(visitInfo.businessHours()).isEqualTo("매일 09:00 ~ 21:00");
        assertThat(visitInfo.restDate()).isEqualTo("매주 화요일");
        // 대표메뉴가 있으면 취급메뉴(treatmenu)로 내려가지 않는다.
        assertThat(visitInfo.signatureMenu()).isEqualTo("돼지국밥");
        assertThat(visitInfo.parkingInfo()).isEqualTo("주차 가능(20대)");
        // 음식점에는 이용요금 필드가 없다.
        assertThat(visitInfo.usageFee()).isNull();
    }

    @Test
    void 관광지_detailIntro2는_음식점과_다른_필드명을_쓴다() throws Exception {
        WellnessVisitInfo visitInfo = WellnessIngestionService.toVisitInfo(mapper.readTree("""
            {
              "contentid": "2", "contenttypeid": "12",
              "usetime": "상시 개방",
              "restdate": "연중무휴",
              "parking": "공영주차장 이용"
            }
            """));

        assertThat(visitInfo.businessHours()).isEqualTo("상시 개방");
        assertThat(visitInfo.restDate()).isEqualTo("연중무휴");
        assertThat(visitInfo.parkingInfo()).isEqualTo("공영주차장 이용");
        assertThat(visitInfo.signatureMenu()).isNull();
    }

    @Test
    void 대표메뉴가_없으면_취급메뉴로_내려간다() throws Exception {
        WellnessVisitInfo visitInfo = WellnessIngestionService.toVisitInfo(mapper.readTree("""
            {"contentid": "3", "contenttypeid": "39", "treatmenu": "밀면, 만두"}
            """));

        assertThat(visitInfo.signatureMenu()).isEqualTo("밀면, 만두");
    }

    @Test
    void 값에_섞여오는_br태그와_HTML엔티티를_정리한다() throws Exception {
        WellnessVisitInfo visitInfo = WellnessIngestionService.toVisitInfo(mapper.readTree("""
            {"contentid": "4", "contenttypeid": "39", "opentimefood": "평일 09:00~18:00<br />주말 휴무"}
            """));

        assertThat(visitInfo.businessHours()).isEqualTo("평일 09:00~18:00\n주말 휴무");
    }

    @Test
    void 빈_응답은_EMPTY와_같다() throws Exception {
        assertThat(WellnessIngestionService.toVisitInfo(mapper.readTree("{}")).isEmpty()).isTrue();
        assertThat(WellnessIngestionService.toVisitInfo(null).isEmpty()).isTrue();
    }

    @Test
    void merge는_빈칸만_채우고_기존값을_지우지_않는다() {
        WellnessVisitInfo busanFood = new WellnessVisitInfo(
            "매일 11:00~22:00", null, "돼지국밥", null, null, "https://example.com"
        );
        WellnessVisitInfo tourApi = new WellnessVisitInfo(null, "매주 월요일", null, null, "가능", null);

        WellnessVisitInfo merged = busanFood.merge(tourApi);

        assertThat(merged.businessHours()).isEqualTo("매일 11:00~22:00");
        assertThat(merged.restDate()).isEqualTo("매주 월요일");
        assertThat(merged.signatureMenu()).isEqualTo("돼지국밥");
        assertThat(merged.parkingInfo()).isEqualTo("가능");
        assertThat(merged.homepageUrl()).isEqualTo("https://example.com");
    }

    @Test
    void homepage는_앵커태그에서_URL만_뽑는다() {
        assertThat(WellnessIngestionService.extractUrl(
            "<a href=\"http://www.busan.go.kr\" target=\"_blank\">www.busan.go.kr</a>"
        )).isEqualTo("http://www.busan.go.kr");
        assertThat(WellnessIngestionService.extractUrl("https://example.com/a?b=1")).isEqualTo("https://example.com/a?b=1");
        // URL이 없으면 링크 버튼을 띄우지 않도록 null이어야 한다.
        assertThat(WellnessIngestionService.extractUrl("문의 바랍니다")).isNull();
        assertThat(WellnessIngestionService.extractUrl("")).isNull();
    }
}
