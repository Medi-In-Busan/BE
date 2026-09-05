package com.mediinbusan.app.feature.documentscan

/**
 * OCR 평문 한 덩어리를 화면에 그릴 블록 목록으로 쪼갠다.
 *
 * 백엔드(DocumentTextLayoutBuilder)는 이미 문서의 배치를 복원해서 내려준다 — 표 셀은 파이프로,
 * 같은 줄 안의 열 경계는 공백 두 칸으로 구분된다. 진단서·처방전은 대부분 "라벨 : 값"이 가로로
 * 반복되는 서식이라(예: `환자의 성명 | 홍종민 | 성별 | 남`), 그 구분자를 셀로 되돌린 뒤 앞뒤를
 * 짝지어 항목으로 만든다. 짝이 안 맞거나 구분자가 없는 줄은 문단 그대로 남긴다 — OCR 결과를
 * 억지로 서식에 끼워 맞추다 원문을 잃는 것보다, 못 알아본 줄은 있는 그대로 보여주는 편이 낫다.
 */
sealed interface DocumentTextBlock {

    /** 구분자가 없어 항목으로 못 나눈 줄(제목, 서술형 문장 등). */
    data class Paragraph(val text: String) : DocumentTextBlock

    /** `라벨 | 값` 한 쌍. */
    data class Field(val label: String, val value: String) : DocumentTextBlock
}

/** 열 경계로 쓰인 공백 2칸 이상. 단어 사이 공백 1칸과 구분된다. */
private val ColumnGap = Regex(" {2,}")
private const val CellSeparator = "|"

/** 번역문(영문 등)의 긴 라벨까지 담되 서술형 문장은 라벨로 보지 않는 길이. */
private const val MaxLabelLength = 20

fun parseDocumentText(text: String): List<DocumentTextBlock> =
    text.lineSequence()
        .map(String::trim)
        .filter(String::isNotEmpty)
        .flatMap { line -> parseLine(line).asSequence() }
        .toList()

private fun parseLine(line: String): List<DocumentTextBlock> {
    val cells = splitIntoCells(line)
    if (cells.size < 2) {
        return listOf(DocumentTextBlock.Paragraph(cells.firstOrNull() ?: line))
    }

    // 줄 맨 앞은 서식상 항상 라벨이다.
    val fields = mutableListOf(DocumentTextBlock.Field(label = cells[0], value = cells[1]))
    var index = 2
    while (index < cells.size) {
        val cell = cells[index]
        // 셀을 무조건 라벨/값으로 번갈아 짝지으면, 값이 한 칸 더 쪼개진 줄에서 값 조각이 라벨
        // 자리로 올라간다(예: `환자의 주소 | 부산광역시 북구 시랑로 | 131번길 | 33 전 화 : ...`에서
        // `131번길`). 라벨처럼 보이지 않거나 짝지을 값이 남지 않은 셀은 직전 항목의 값에 이어 붙인다.
        if (index + 1 < cells.size && isLabelLike(cell)) {
            fields += DocumentTextBlock.Field(label = cell, value = cells[index + 1])
            index += 2
        } else {
            val last = fields.removeAt(fields.lastIndex)
            fields += last.copy(value = "${last.value} $cell".trim())
            index += 1
        }
    }
    return fields
}

/** 서식 라벨은 짧고 숫자가 없다 — 날짜·번호·주소 조각을 라벨로 오인하지 않게 하는 최소한의 방어선이다. */
private fun isLabelLike(cell: String): Boolean =
    cell.length <= MaxLabelLength && cell.none(Char::isDigit)

/** 파이프가 있으면 파이프를, 없으면 열 경계 공백을 셀 구분자로 쓴다(둘을 섞으면 값 안의 공백까지 쪼개진다). */
private fun splitIntoCells(line: String): List<String> {
    val cells = if (line.contains(CellSeparator)) line.split(CellSeparator) else line.split(ColumnGap)
    return cells.map { it.replace(ColumnGap, " ").trim() }.filter(String::isNotEmpty)
}
