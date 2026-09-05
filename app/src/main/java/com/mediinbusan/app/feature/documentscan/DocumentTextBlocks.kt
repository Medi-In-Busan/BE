package com.mediinbusan.app.feature.documentscan

/**
 * OCR 평문 한 덩어리를 화면에 그릴 블록 목록으로 쪼갠다.
 *
 * 백엔드(DocumentTextLayoutBuilder)는 이미 문서의 배치를 복원해서 내려준다 — 표 셀은 파이프로,
 * 같은 줄 안의 열 경계는 공백 두 칸으로 구분된다. 여기서 그 구분자를 셀로 되돌린 뒤 세 가지로 나눈다.
 *
 * - [DocumentTextBlock.Table]: 처방전 약품 목록처럼 **열이 실제로 맞물리는 격자**. 첫 행이 헤더다.
 * - [DocumentTextBlock.Field]: 진단서 서식처럼 `라벨 : 값`이 가로로 반복되는 줄.
 * - [DocumentTextBlock.Paragraph]: 구분자가 없어 나눌 수 없는 줄(제목, 서술형 문장).
 *
 * 표와 서식을 가르는 근거는 **"칸 수가 같은 줄이 연달아 나오는가"** 하나뿐이다. 평문으로 내려온
 * 이상 그 이상은 알 수 없고, 억지로 서식에 끼워 맞추다 원문을 잃는 것보다 못 알아본 줄을 있는
 * 그대로 보여주는 편이 낫다. 그래서 판정이 애매한 2칸짜리 줄은 표로 보지 않고 서식으로 남긴다.
 */
sealed interface DocumentTextBlock {

    /** 구분자가 없어 항목으로 못 나눈 줄(제목, 서술형 문장 등). */
    data class Paragraph(val text: String) : DocumentTextBlock

    /** `라벨 | 값` 한 쌍. */
    data class Field(val label: String, val value: String) : DocumentTextBlock

    /** 첫 행이 헤더인 격자. 모든 행의 칸 수는 [header]와 같다. */
    data class Table(val header: List<String>, val rows: List<List<String>>) : DocumentTextBlock
}

/** 열 경계로 쓰인 공백 2칸 이상. 단어 사이 공백 1칸과 구분된다. */
private val ColumnGap = Regex(" {2,}")
private const val CellSeparator = "|"

/** 번역문(영문 등)의 긴 라벨까지 담되 서술형 문장은 라벨로 보지 않는 길이. */
private const val MaxLabelLength = 20

/**
 * 표로 인정할 최소 열 수. 2칸짜리 줄은 `병록번호 | 12257`처럼 서식의 라벨/값과 구분이 되지 않아
 * 표로 보면 첫 줄의 값이 헤더로 올라가버린다 — 그런 줄은 [DocumentTextBlock.Field]로 남긴다.
 */
private const val MinTableColumns = 3

/** 표로 인정할 최소 행 수. 한 줄만으로는 헤더인지 서식 한 줄인지 알 수 없다. */
private const val MinTableRows = 2

fun parseDocumentText(text: String): List<DocumentTextBlock> {
    val lines = text.lineSequence()
        .map(String::trim)
        .filter(String::isNotEmpty)
        .toList()

    val blocks = mutableListOf<DocumentTextBlock>()
    var index = 0
    while (index < lines.size) {
        val tableRows = tableRunAt(lines, index)
        if (tableRows != null) {
            blocks += DocumentTextBlock.Table(header = tableRows.first(), rows = tableRows.drop(1))
            index += tableRows.size
        } else {
            blocks += parseLine(lines[index])
            index++
        }
    }
    return blocks
}

/**
 * [start]부터 칸 수가 같은 파이프 줄이 연달아 몇 줄인지 보고, 표로 볼 만하면 그 행들을 돌려준다.
 * @return 표로 판정된 행들(헤더 포함), 아니면 null.
 */
private fun tableRunAt(lines: List<String>, start: Int): List<List<String>>? {
    val first = tableCells(lines[start]) ?: return null

    val rows = mutableListOf(first)
    var index = start + 1
    while (index < lines.size) {
        val cells = tableCells(lines[index])
        if (cells == null || cells.size != first.size) {
            break
        }
        rows += cells
        index++
    }
    return if (rows.size >= MinTableRows) rows else null
}

/**
 * 표 행 후보로서의 셀 분해. 서식 줄과 달리 **빈 칸을 버리지 않는다** — 백엔드가 병합 셀이 덮는
 * 자리를 빈 칸으로 남겨 보내므로, 그걸 버리면 열이 앞으로 밀려 헤더와 데이터가 어긋난다.
 */
private fun tableCells(line: String): List<String>? {
    if (!line.contains(CellSeparator)) {
        return null
    }
    val cells = line.split(CellSeparator).map { it.replace(ColumnGap, " ").trim() }
    return if (cells.size >= MinTableColumns && cells.any(String::isNotEmpty)) cells else null
}

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
