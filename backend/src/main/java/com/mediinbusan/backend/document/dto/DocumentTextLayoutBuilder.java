package com.mediinbusan.backend.document.dto;

import com.mediinbusan.backend.document.client.ClovaOcrResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * CLOVA OCR 결과를 "사람이 읽는 순서"의 평문으로 복원한다.
 *
 * <p>CLOVA는 fields[]를 단어 단위로 쪼개 주고 lineBreak로 줄 끝만 알려주는데, 처방전처럼 표가 있는
 * 문서에서는 그 줄 구분이 시각적인 행과 어긋난다(약품명 열과 투약량 열의 단어가 한 줄로 합쳐지거나,
 * 한 약품이 여러 줄로 쪼개짐). 그래서 여기서는 두 가지를 쓴다:
 * <ul>
 *   <li>표(tables): enableTableDetection으로 받은 셀의 rowIndex/columnIndex를 그대로 격자로 쓴다.
 *       좌표 추정이 필요 없어 가장 정확하므로, 표가 내려오면 항상 이쪽을 우선한다.</li>
 *   <li>본문(fields): boundingPoly의 세로 겹침으로 같은 행을 묶고, 행 안에서는 x 순으로 정렬한다.
 *       표 영역 안에 들어가는 단어는 표 블록과 중복되므로 여기서 제외한다.</li>
 * </ul>
 *
 * <p>좌표가 하나라도 빠진 응답(구버전 응답, 테스트 더블 등)에서는 행 복원을 포기하고 예전처럼
 * lineBreak 기준으로 이어붙인다 — 좌표 없는 단어를 임의의 위치에 끼워넣어 순서를 망치느니 검증된
 * 폴백으로 떨어지는 편이 낫다.
 */
final class DocumentTextLayoutBuilder {

    /** 같은 행으로 볼 최소 세로 겹침 비율(작은 쪽 높이 기준). 0.5면 절반 이상 겹쳐야 같은 줄이다. */
    private static final double SAME_ROW_OVERLAP_RATIO = 0.5;
    /** 글자 높이의 이 배수보다 넓게 벌어지면 단어 사이가 아니라 열 경계로 보고 공백을 두 칸 넣는다. */
    private static final double COLUMN_GAP_RATIO = 1.0;
    /** 표를 평문에 담으면서도 열 경계가 남도록 셀을 파이프로 구분한다. */
    private static final String CELL_SEPARATOR = " | ";
    /** 좌표를 못 구한 표는 위치를 알 수 없으므로 본문 뒤에 붙인다. */
    private static final double UNKNOWN_POSITION = Double.MAX_VALUE;

    private DocumentTextLayoutBuilder() {
    }

    static String build(List<ClovaOcrResponse.Field> fields, List<ClovaOcrResponse.Table> tables) {
        List<Block> tableBlocks = toTableBlocks(tables);
        List<Rect> tableBounds = tableBlocks.stream().map(Block::bounds).filter(Objects::nonNull).toList();

        List<Block> lineBlocks = toLineBlocks(fields, tableBounds);
        if (lineBlocks == null) {
            return appendTables(joinByLineBreak(fields), tableBlocks);
        }

        List<Block> blocks = new ArrayList<>(lineBlocks);
        blocks.addAll(tableBlocks);
        blocks.sort(Comparator.comparingDouble(Block::top));

        StringBuilder text = new StringBuilder();
        for (Block block : blocks) {
            appendLine(text, block.text());
        }
        return text.toString().strip();
    }

    private static String appendTables(String bodyText, List<Block> tableBlocks) {
        StringBuilder text = new StringBuilder(bodyText);
        for (Block table : tableBlocks) {
            appendLine(text, table.text());
        }
        return text.toString().strip();
    }

    private static void appendLine(StringBuilder text, String line) {
        if (line == null || line.isBlank()) {
            return;
        }
        if (!text.isEmpty()) {
            text.append("\n");
        }
        text.append(line);
    }

    // ---------------------------------------------------------------- 본문(fields)

    /** @return 행 복원 결과. 좌표가 없는 field가 하나라도 있으면 null(= 폴백하라는 뜻)을 반환한다. */
    private static List<Block> toLineBlocks(List<ClovaOcrResponse.Field> fields, List<Rect> tableBounds) {
        if (fields == null || fields.isEmpty()) {
            return List.of();
        }

        List<Word> words = new ArrayList<>();
        for (ClovaOcrResponse.Field field : fields) {
            Rect rect = Rect.of(field.boundingPoly());
            if (rect == null) {
                return null;
            }
            if (isInsideAny(rect, tableBounds)) {
                continue; // 표 블록으로 이미 렌더링되는 단어다.
            }
            if (field.inferText() != null && !field.inferText().isBlank()) {
                words.add(new Word(field.inferText(), rect));
            }
        }

        List<Block> blocks = new ArrayList<>();
        for (List<Word> row : groupIntoRows(words)) {
            blocks.add(toLineBlock(row));
        }
        return blocks;
    }

    private static List<List<Word>> groupIntoRows(List<Word> words) {
        List<Word> sorted = new ArrayList<>(words);
        sorted.sort(Comparator.comparingDouble((Word word) -> word.rect().top())
            .thenComparingDouble(word -> word.rect().left()));

        List<List<Word>> rows = new ArrayList<>();
        Rect band = null;
        for (Word word : sorted) {
            // top 기준 정렬이라 새 단어는 직전 행에만 붙을 수 있다 — 전체 행을 다시 훑을 필요가 없다.
            if (band != null && band.overlapsVertically(word.rect(), SAME_ROW_OVERLAP_RATIO)) {
                rows.get(rows.size() - 1).add(word);
                band = band.union(word.rect());
            } else {
                List<Word> row = new ArrayList<>();
                row.add(word);
                rows.add(row);
                band = word.rect();
            }
        }
        return rows;
    }

    private static Block toLineBlock(List<Word> row) {
        row.sort(Comparator.comparingDouble(word -> word.rect().left()));

        double averageHeight = row.stream().mapToDouble(word -> word.rect().height()).average().orElse(1.0);
        double columnGap = Math.max(averageHeight, 1.0) * COLUMN_GAP_RATIO;

        StringBuilder text = new StringBuilder();
        Word previous = null;
        for (Word word : row) {
            if (previous != null) {
                text.append(word.rect().left() - previous.rect().right() > columnGap ? "  " : " ");
            }
            text.append(word.text());
            previous = word;
        }

        double top = row.stream().mapToDouble(word -> word.rect().top()).min().orElse(0.0);
        return new Block(top, null, text.toString().strip());
    }

    private static boolean isInsideAny(Rect rect, List<Rect> bounds) {
        for (Rect bound : bounds) {
            if (bound.contains(rect.centerX(), rect.centerY())) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------- 표(tables)

    private static List<Block> toTableBlocks(List<ClovaOcrResponse.Table> tables) {
        if (tables == null || tables.isEmpty()) {
            return List.of();
        }

        List<Block> blocks = new ArrayList<>();
        for (ClovaOcrResponse.Table table : tables) {
            Block block = toTableBlock(table);
            if (block != null) {
                blocks.add(block);
            }
        }
        return blocks;
    }

    private static Block toTableBlock(ClovaOcrResponse.Table table) {
        List<ClovaOcrResponse.Cell> cells = table == null ? null : table.cells();
        if (cells == null || cells.isEmpty()) {
            return null;
        }

        // rowIndex/columnIndex는 CLOVA가 확정해준 논리적 위치라 좌표 추정 없이 그대로 격자로 쓴다.
        // 인덱스가 없는 셀은 순서를 알 수 없으니 버리지 않고 맨 뒤 행/열로 모아둔다.
        Map<Integer, Map<Integer, List<String>>> grid = new TreeMap<>();
        Rect bounds = null;
        for (ClovaOcrResponse.Cell cell : cells) {
            String cellText = cellText(cell);
            if (cellText.isBlank()) {
                continue;
            }
            int rowIndex = cell.rowIndex() != null ? cell.rowIndex() : Integer.MAX_VALUE;
            int columnIndex = cell.columnIndex() != null ? cell.columnIndex() : Integer.MAX_VALUE;
            grid.computeIfAbsent(rowIndex, key -> new TreeMap<>())
                .computeIfAbsent(columnIndex, key -> new ArrayList<>())
                .add(cellText);
            bounds = Rect.union(bounds, Rect.of(cell.boundingPoly()));
        }

        if (grid.isEmpty()) {
            return null;
        }

        StringBuilder text = new StringBuilder();
        for (Map<Integer, List<String>> row : grid.values()) {
            List<String> renderedCells = new ArrayList<>();
            for (List<String> cellTexts : row.values()) {
                renderedCells.add(String.join(" ", cellTexts));
            }
            appendLine(text, String.join(CELL_SEPARATOR, renderedCells));
        }

        return new Block(bounds != null ? bounds.top() : UNKNOWN_POSITION, bounds, text.toString());
    }

    private static String cellText(ClovaOcrResponse.Cell cell) {
        List<ClovaOcrResponse.CellTextLine> lines = cell.cellTextLines();
        if (lines == null) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        for (ClovaOcrResponse.CellTextLine line : lines) {
            if (line == null || line.cellWords() == null) {
                continue;
            }
            for (ClovaOcrResponse.CellWord word : line.cellWords()) {
                if (word == null || word.inferText() == null || word.inferText().isBlank()) {
                    continue;
                }
                if (!text.isEmpty()) {
                    text.append(" ");
                }
                text.append(word.inferText());
            }
        }
        return text.toString().strip();
    }

    // ---------------------------------------------------------------- 폴백

    /** boundingPoly가 없던 시절의 방식: 읽기 순서대로 잇고 lineBreak에서만 줄을 바꾼다. */
    private static String joinByLineBreak(List<ClovaOcrResponse.Field> fields) {
        if (fields == null || fields.isEmpty()) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        for (ClovaOcrResponse.Field field : fields) {
            if (field.inferText() != null) {
                text.append(field.inferText());
            }
            text.append(Boolean.TRUE.equals(field.lineBreak()) ? "\n" : " ");
        }
        return text.toString().strip();
    }

    // ---------------------------------------------------------------- 값 객체

    private record Word(String text, Rect rect) {
    }

    /** 정렬 키(top)와 렌더링된 텍스트를 함께 든 문서 조각. bounds는 표에만 있고 본문 줄에는 null이다. */
    private record Block(double top, Rect bounds, String text) {
    }

    private record Rect(double left, double top, double right, double bottom) {

        /** vertices 개수를 가정하지 않고 min/max만 취한다 — 기울어져 찍힌 사진도 축 정렬 사각형으로 다룬다. */
        static Rect of(ClovaOcrResponse.BoundingPoly poly) {
            List<ClovaOcrResponse.Vertex> vertices = poly == null ? null : poly.vertices();
            if (vertices == null || vertices.isEmpty()) {
                return null;
            }

            double left = Double.MAX_VALUE;
            double top = Double.MAX_VALUE;
            double right = -Double.MAX_VALUE;
            double bottom = -Double.MAX_VALUE;
            for (ClovaOcrResponse.Vertex vertex : vertices) {
                if (vertex == null || vertex.x() == null || vertex.y() == null) {
                    return null;
                }
                left = Math.min(left, vertex.x());
                top = Math.min(top, vertex.y());
                right = Math.max(right, vertex.x());
                bottom = Math.max(bottom, vertex.y());
            }
            return new Rect(left, top, right, bottom);
        }

        static Rect union(Rect first, Rect second) {
            if (first == null) {
                return second;
            }
            if (second == null) {
                return first;
            }
            return first.union(second);
        }

        Rect union(Rect other) {
            return new Rect(
                Math.min(left, other.left), Math.min(top, other.top),
                Math.max(right, other.right), Math.max(bottom, other.bottom)
            );
        }

        double height() {
            return bottom - top;
        }

        double centerX() {
            return (left + right) / 2;
        }

        double centerY() {
            return (top + bottom) / 2;
        }

        boolean contains(double x, double y) {
            return x >= left && x <= right && y >= top && y <= bottom;
        }

        boolean overlapsVertically(Rect other, double minRatio) {
            double overlap = Math.min(bottom, other.bottom) - Math.max(top, other.top);
            if (overlap <= 0) {
                return false;
            }
            // 높이가 0인 축퇴 박스(한 점만 찍힌 경우)에서 0으로 나누지 않도록 최소 1px로 본다.
            double reference = Math.max(Math.min(height(), other.height()), 1.0);
            return overlap >= reference * minRatio;
        }
    }
}
