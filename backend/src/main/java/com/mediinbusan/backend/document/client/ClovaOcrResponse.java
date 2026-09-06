package com.mediinbusan.backend.document.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * CLOVA OCR General 응답 중 텍스트 추출에 필요한 필드만 담는다.
 * 나머지(validationResult, convertedImageInfo 등)는 우리 서비스에서 쓰지 않으므로 무시한다.
 *
 * <p>boundingPoly는 원래 무시했지만, 처방전처럼 표로 된 문서는 좌표 없이 fields를 읽기 순서로만
 * 이어붙이면 열이 서로 섞여버려서(약품명 열과 투약량 열이 한 줄로 합쳐짐) 지금은 유지한다 —
 * DocumentTextLayoutBuilder가 이 좌표로 행/열을 복원한다.
 * tables는 enableTableDetection=true일 때만 채워지고, 꺼져 있거나 표가 없는 문서에서는 null/빈 배열이다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ClovaOcrResponse(
    String version,
    String requestId,
    Long timestamp,
    List<ImageResult> images
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ImageResult(
        String name,
        String inferResult,
        String message,
        List<Field> fields,
        List<Table> tables
    ) {
        public boolean isSuccess() {
            return "SUCCESS".equalsIgnoreCase(inferResult);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Field(
        String inferText,
        Double inferConfidence,
        String type,
        Boolean lineBreak,
        BoundingPoly boundingPoly
    ) {
    }

    /** enableTableDetection=true일 때 내려오는 표 하나. 셀은 rowIndex/columnIndex로 위치가 확정된다. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Table(
        List<Cell> cells
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cell(
        Integer rowIndex,
        Integer columnIndex,
        Integer rowSpan,
        Integer columnSpan,
        List<CellTextLine> cellTextLines,
        BoundingPoly boundingPoly
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CellTextLine(
        List<CellWord> cellWords
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CellWord(
        String inferText,
        Double inferConfidence,
        BoundingPoly boundingPoly
    ) {
    }

    /** vertices는 보통 시계방향 4점이지만 개수를 가정하지 않고 min/max만 취한다(기울어진 사진 대응). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BoundingPoly(
        List<Vertex> vertices
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Vertex(
        Double x,
        Double y
    ) {
    }
}
