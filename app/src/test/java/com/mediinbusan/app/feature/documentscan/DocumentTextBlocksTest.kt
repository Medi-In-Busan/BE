package com.mediinbusan.app.feature.documentscan

import org.junit.Assert.assertEquals
import org.junit.Test

class DocumentTextBlocksTest {

    @Test
    fun `칸 수가 같은 파이프 줄이 이어지면 표로 묶고 첫 행을 헤더로 쓴다`() {
        val blocks = parseDocumentText(
            """
            처방 의약품의 명칭
            약품명 | 1회 투약량 | 1일 투여횟수 | 총 투약일수
            타이레놀정500mg | 1 | 3 | 3
            크라비트정500mg | 1 | 1 | 5
            """.trimIndent()
        )

        assertEquals(DocumentTextBlock.Paragraph("처방 의약품의 명칭"), blocks[0])
        assertEquals(
            DocumentTextBlock.Table(
                header = listOf("약품명", "1회 투약량", "1일 투여횟수", "총 투약일수"),
                rows = listOf(
                    listOf("타이레놀정500mg", "1", "3", "3"),
                    listOf("크라비트정500mg", "1", "1", "5")
                )
            ),
            blocks[1]
        )
        assertEquals(2, blocks.size)
    }

    @Test
    fun `표 행의 빈 칸은 버리지 않는다`() {
        // 백엔드가 병합 셀(columnSpan)이 덮는 자리를 빈 칸으로 남겨 보낸다 — 버리면 열이 밀린다.
        val blocks = parseDocumentText(
            """
            약품명 |  | 1회 투약량
            타이레놀정500mg | 500mg | 1
            """.trimIndent()
        )

        assertEquals(
            DocumentTextBlock.Table(
                header = listOf("약품명", "", "1회 투약량"),
                rows = listOf(listOf("타이레놀정500mg", "500mg", "1"))
            ),
            blocks.single()
        )
    }

    @Test
    fun `두 칸짜리 줄은 표가 아니라 서식 항목으로 본다`() {
        // `병록번호 | 12257`을 표로 보면 값(12257)이 헤더로 올라가버린다.
        val blocks = parseDocumentText(
            """
            병록번호 | 12257
            연번호 | 19-7001
            """.trimIndent()
        )

        assertEquals(DocumentTextBlock.Field("병록번호", "12257"), blocks[0])
        assertEquals(DocumentTextBlock.Field("연번호", "19-7001"), blocks[1])
    }

    @Test
    fun `서식 줄은 라벨과 값을 번갈아 짝짓는다`() {
        val blocks = parseDocumentText("환자의 성명 | 홍종민 | 성별 | 남 | 생년 월일 | 2000년 9월 26일")

        assertEquals(
            listOf(
                DocumentTextBlock.Field("환자의 성명", "홍종민"),
                DocumentTextBlock.Field("성별", "남"),
                DocumentTextBlock.Field("생년 월일", "2000년 9월 26일")
            ),
            blocks
        )
    }

    @Test
    fun `라벨처럼 보이지 않는 셀은 직전 값에 이어 붙인다`() {
        // `131번길`은 숫자를 포함해 라벨이 아니다 — 라벨 자리로 올리면 주소가 두 항목으로 쪼개진다.
        val blocks = parseDocumentText("환자의 주소 | 부산광역시 북구 시랑로 | 131번길 | 33 전 화")

        assertEquals(
            DocumentTextBlock.Field("환자의 주소", "부산광역시 북구 시랑로 131번길 33 전 화"),
            blocks.single()
        )
    }

    @Test
    fun `구분자가 없는 줄은 문단으로 남긴다`() {
        val blocks = parseDocumentText("위와 같이 진단함")

        assertEquals(DocumentTextBlock.Paragraph("위와 같이 진단함"), blocks.single())
    }

    @Test
    fun `공백 두 칸은 열 경계로 보고 나눈다`() {
        val blocks = parseDocumentText("병록번호  12257")

        assertEquals(DocumentTextBlock.Field("병록번호", "12257"), blocks.single())
    }

    @Test
    fun `자간을 벌린 제목은 항목으로 쪼개지 않는다`() {
        // `진   단   서`처럼 자간을 벌려 인쇄한 제목은 백엔드가 열 경계(공백 2칸)로 내려준다 —
        // 그대로 쪼개면 라벨 `진` / 값 `단 서`라는 엉뚱한 항목이 된다.
        assertEquals(DocumentTextBlock.Paragraph("진단서"), parseDocumentText("진   단   서").single())
        assertEquals(DocumentTextBlock.Paragraph("처방전"), parseDocumentText("처  방  전").single())
    }

    @Test
    fun `자간을 벌린 라벨은 되붙여서 값과 짝짓는다`() {
        val blocks = parseDocumentText("병      명  급성 기관지염  질병분류기호  J20.9")

        assertEquals(
            listOf(
                DocumentTextBlock.Field("병명", "급성 기관지염"),
                DocumentTextBlock.Field("질병분류기호", "J20.9")
            ),
            blocks
        )
    }

    @Test
    fun `표의 한 글자 셀은 되붙이지 않는다`() {
        // 자간 병합을 파이프 줄에도 적용하면 `1 | 3 | 3`이 `133`으로 뭉개진다.
        val blocks = parseDocumentText(
            """
            약품명 | 1회 투약량 | 1일 투여횟수 | 총 투약일수
            타이레놀정500mg | 1 | 3 | 3
            """.trimIndent()
        )

        assertEquals(
            DocumentTextBlock.Table(
                header = listOf("약품명", "1회 투약량", "1일 투여횟수", "총 투약일수"),
                rows = listOf(listOf("타이레놀정500mg", "1", "3", "3"))
            ),
            blocks.single()
        )
    }

    @Test
    fun `첫 행에만 값이 있는 열은 버리고 서식으로 되돌린다`() {
        // 진단서를 표로 인식한 경우. 뒤 두 칸은 첫 줄만 쓰고 아래 줄들은 비어 있다 — 그대로 두면
        // 첫 줄이 헤더로 칠해지고 빈 열이 폭을 절반 가까이 가져가 값이 서너 글자마다 줄바꿈된다.
        val blocks = parseDocumentText(
            """
            병록번호 | 12257 | 연번호 | 19-7001
            환자의 성명 | 홍종민 |  | 
            환자의 주소 | 부산광역시 북구 시랑로 131번길 33 |  | 
            """.trimIndent()
        )

        assertEquals(
            listOf(
                DocumentTextBlock.Field("병록번호", "12257"),
                DocumentTextBlock.Field("연번호", "19-7001"),
                DocumentTextBlock.Field("환자의 성명", "홍종민"),
                DocumentTextBlock.Field("환자의 주소", "부산광역시 북구 시랑로 131번길 33")
            ),
            blocks
        )
    }
}
