package com.mediinbusan.backend.document.dto;

import com.mediinbusan.backend.document.client.ClovaOcrResponse;
import com.mediinbusan.backend.document.exception.DocumentOcrFailedException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentOcrDtoMapperTest {

    @Test
    void 같은_높이의_단어들을_한_줄로_묶고_x_순서로_정렬한다() {
        // CLOVA가 내려주는 순서가 시각적 순서와 달라도(여기선 일부러 뒤섞음) 좌표대로 복원돼야 한다.
        ClovaOcrResponse response = successResponse(List.of(
            field("홍길동", 200, 100, 260, 120),
            field("처방일", 10, 140, 60, 160),
            field("환자명", 10, 100, 60, 120),
            field("2026-09-05", 200, 140, 300, 160)
        ));

        String result = DocumentOcrDtoMapper.extractText(response);

        assertThat(result).isEqualTo("환자명  홍길동\n처방일  2026-09-05");
    }

    @Test
    void 붙어있는_단어는_한_칸_벌어진_열은_두_칸으로_구분한다() {
        ClovaOcrResponse response = successResponse(List.of(
            field("급성", 10, 100, 50, 120),
            field("인두염", 55, 100, 110, 120),
            field("J029", 300, 100, 350, 120)
        ));

        String result = DocumentOcrDtoMapper.extractText(response);

        assertThat(result).isEqualTo("급성 인두염  J029");
    }

    @Test
    void 세로로_조금_어긋난_단어도_같은_행으로_묶는다() {
        // 사진이 살짝 기울어지면 같은 줄이어도 y가 몇 px씩 밀린다 — 절반 이상 겹치면 같은 행으로 본다.
        ClovaOcrResponse response = successResponse(List.of(
            field("아모크라", 10, 100, 90, 120),
            field("듀오시럽", 95, 104, 175, 126)
        ));

        String result = DocumentOcrDtoMapper.extractText(response);

        assertThat(result).isEqualTo("아모크라 듀오시럽");
    }

    @Test
    void 표는_rowIndex_columnIndex_순서로_렌더링된다() {
        ClovaOcrResponse response = successResponse(
            List.of(field("처방 의약품의 명칭", 10, 100, 200, 120)),
            List.of(new ClovaOcrResponse.Table(List.of(
                cell(1, 1, "1", 200, 230, 400, 260),
                cell(0, 0, "약품명", 10, 200, 200, 230),
                cell(1, 0, "타이레놀정500mg", 10, 230, 200, 260),
                cell(0, 1, "1회 투약량", 200, 200, 400, 230)
            )))
        );

        String result = DocumentOcrDtoMapper.extractText(response);

        assertThat(result).isEqualTo("""
            처방 의약품의 명칭
            약품명 | 1회 투약량
            타이레놀정500mg | 1""");
    }

    @Test
    void 표_영역_안의_field는_표와_중복되므로_본문에서_제외한다() {
        ClovaOcrResponse response = successResponse(
            List.of(
                field("처방 의약품의 명칭", 10, 100, 200, 120),
                field("타이레놀정500mg", 20, 235, 190, 255) // 표 셀 안에 들어가는 단어
            ),
            List.of(new ClovaOcrResponse.Table(List.of(
                cell(0, 0, "약품명", 10, 200, 400, 230),
                cell(1, 0, "타이레놀정500mg", 10, 230, 400, 260)
            )))
        );

        String result = DocumentOcrDtoMapper.extractText(response);

        assertThat(result).isEqualTo("""
            처방 의약품의 명칭
            약품명
            타이레놀정500mg""");
    }

    @Test
    void 좌표가_없는_응답은_lineBreak_기준_폴백을_쓴다() {
        ClovaOcrResponse response = successResponse(List.of(
            new ClovaOcrResponse.Field("환자명", 0.99, "NORMAL", false, null),
            new ClovaOcrResponse.Field("홍길동", 0.99, "NORMAL", true, null),
            new ClovaOcrResponse.Field("진단명", 0.99, "NORMAL", false, null),
            new ClovaOcrResponse.Field("급성 인두염", 0.99, "NORMAL", true, null)
        ));

        String result = DocumentOcrDtoMapper.extractText(response);

        assertThat(result).isEqualTo("환자명 홍길동\n진단명 급성 인두염");
    }

    @Test
    void 좌표가_없어_폴백하더라도_표는_뒤에_붙여_내려준다() {
        ClovaOcrResponse response = successResponse(
            List.of(new ClovaOcrResponse.Field("처방전", 0.99, "NORMAL", true, null)),
            List.of(new ClovaOcrResponse.Table(List.of(
                cell(0, 0, "약품명", 10, 200, 200, 230),
                cell(0, 1, "투약량", 200, 200, 400, 230)
            )))
        );

        String result = DocumentOcrDtoMapper.extractText(response);

        assertThat(result).isEqualTo("처방전\n약품명 | 투약량");
    }

    @Test
    void inferResult가_SUCCESS가_아니면_예외를_던진다() {
        ClovaOcrResponse response = new ClovaOcrResponse(
            "V2", "req-1", 0L,
            List.of(new ClovaOcrResponse.ImageResult("document", "FAILURE", "인식 실패", List.of(), null))
        );

        assertThatThrownBy(() -> DocumentOcrDtoMapper.extractText(response))
            .isInstanceOf(DocumentOcrFailedException.class);
    }

    @Test
    void images가_비어있으면_예외를_던진다() {
        ClovaOcrResponse response = new ClovaOcrResponse("V2", "req-1", 0L, List.of());

        assertThatThrownBy(() -> DocumentOcrDtoMapper.extractText(response))
            .isInstanceOf(DocumentOcrFailedException.class);
    }

    private ClovaOcrResponse successResponse(List<ClovaOcrResponse.Field> fields) {
        return successResponse(fields, null);
    }

    private ClovaOcrResponse successResponse(List<ClovaOcrResponse.Field> fields, List<ClovaOcrResponse.Table> tables) {
        return new ClovaOcrResponse(
            "V2", "req-1", 0L,
            List.of(new ClovaOcrResponse.ImageResult("document", "SUCCESS", "SUCCESS", fields, tables))
        );
    }

    private static ClovaOcrResponse.Field field(String text, double left, double top, double right, double bottom) {
        return new ClovaOcrResponse.Field(text, 0.99, "NORMAL", false, poly(left, top, right, bottom));
    }

    private static ClovaOcrResponse.Cell cell(
        int rowIndex, int columnIndex, String text, double left, double top, double right, double bottom
    ) {
        ClovaOcrResponse.CellWord word = new ClovaOcrResponse.CellWord(text, 0.99, poly(left, top, right, bottom));
        return new ClovaOcrResponse.Cell(
            rowIndex, columnIndex, 1, 1,
            List.of(new ClovaOcrResponse.CellTextLine(List.of(word))),
            poly(left, top, right, bottom)
        );
    }

    private static ClovaOcrResponse.BoundingPoly poly(double left, double top, double right, double bottom) {
        return new ClovaOcrResponse.BoundingPoly(List.of(
            new ClovaOcrResponse.Vertex(left, top),
            new ClovaOcrResponse.Vertex(right, top),
            new ClovaOcrResponse.Vertex(right, bottom),
            new ClovaOcrResponse.Vertex(left, bottom)
        ));
    }
}
