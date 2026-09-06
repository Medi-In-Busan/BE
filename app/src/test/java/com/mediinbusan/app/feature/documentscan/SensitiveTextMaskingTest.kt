package com.mediinbusan.app.feature.documentscan

import org.junit.Assert.assertEquals
import org.junit.Test

class SensitiveTextMaskingTest {

    @Test
    fun `주민등록번호는 뒤 6자리를 가린다`() {
        assertEquals("주민등록번호 000926-3******", maskSensitiveText("주민등록번호 000926-3812347"))
    }

    @Test
    fun `하이픈 없는 주민등록번호도 가린다`() {
        assertEquals("000926-3******", maskSensitiveText("0009263812347"))
    }

    @Test
    fun `하이픈 앞뒤에 공백이 있어도 주민등록번호를 가린다`() {
        // OCR이 하이픈 주변에 공백을 끼워 넣는 경우 — 구분자를 한 글자만 허용하면 통째로 새어나간다.
        assertEquals("900101-1******", maskSensitiveText("900101 - 1234567"))
        assertEquals("900101-1******", maskSensitiveText("900101  1234567"))
    }

    @Test
    fun `휴대전화번호는 가운데 네 자리를 가린다`() {
        assertEquals("전 화 : 010-****-7417", maskSensitiveText("전 화 : 01028367417"))
        assertEquals("010-****-5678", maskSensitiveText("010-1234-5678"))
    }

    @Test
    fun `하이픈 앞뒤에 공백이 있어도 휴대전화번호를 가린다`() {
        assertEquals("010-****-5678", maskSensitiveText("010 - 1234 - 5678"))
    }

    @Test
    fun `병원 대표번호는 가리지 않는다`() {
        // 환자가 실제로 걸어야 하는 번호라 가리면 안 된다 — 010 접두사가 개인 번호와 가르는 기준이다.
        assertEquals("전 화 : 051-240-2000", maskSensitiveText("전 화 : 051-240-2000"))
        assertEquals("대표번호 1588-7000", maskSensitiveText("대표번호 1588-7000"))
    }

    @Test
    fun `식별번호가 아닌 숫자는 건드리지 않는다`() {
        assertEquals("병록번호 12257", maskSensitiveText("병록번호 12257"))
        assertEquals("2026 년 7 월 27일", maskSensitiveText("2026 년 7 월 27일"))
        assertEquals("연번호 19-7001", maskSensitiveText("연번호 19-7001"))
    }
}
