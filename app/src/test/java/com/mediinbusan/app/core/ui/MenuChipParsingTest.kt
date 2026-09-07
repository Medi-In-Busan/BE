package com.mediinbusan.app.core.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * 대표메뉴 문자열을 칩으로 쪼개는 규칙을 고정한다.
 *
 * 공공 API가 주는 자유 텍스트라 형태가 제각각이고, 실제로 한 번 깨졌다 — 천 단위 쉼표를 메뉴
 * 구분자로 잘못 읽어 "킹크랩 12,000"이 "킹크랩 12"와 "000" 두 칩으로 갈렸다.
 */
class MenuChipParsingTest {

    @Test
    fun `이름과 가격이 붙어 오면 떼어낸다`() {
        assertEquals(
            listOf(MenuItem("킹크랩", "₩12,000")),
            "킹크랩 W12,000".toMenuChips()
        )
    }

    @Test
    fun `천 단위 쉼표를 메뉴 구분자로 읽지 않는다`() {
        // 고치기 전에는 "킹크랩 12"와 "000"으로 갈렸다.
        assertEquals(
            listOf(MenuItem("킹크랩", "12,000")),
            "킹크랩 12,000".toMenuChips()
        )
    }

    @Test
    fun `메뉴 사이 쉼표는 그대로 구분자로 쓴다`() {
        assertEquals(
            listOf(MenuItem("돼지국밥", "9,000원"), MenuItem("밀면", "8,000원")),
            "돼지국밥 9,000원, 밀면 8,000원".toMenuChips()
        )
        // 숫자로 끝난 뒤 오는 쉼표(뒤가 공백)도 구분자다.
        assertEquals(
            listOf(MenuItem("밀면", "8000"), MenuItem("만두", "6000")),
            "밀면 8000, 만두 6000".toMenuChips()
        )
    }

    @Test
    fun `통화 기호가 어떤 표기로 와도 하나로 모은다`() {
        val expected = listOf(MenuItem("대게", "₩30,000"))
        assertEquals(expected, "대게 W30,000".toMenuChips())
        assertEquals(expected, "대게 ₩30,000".toMenuChips())
        // 역슬래시 표기 — 폰트에 따라 원화 기호로 보여서 그대로 실려 오는 경우가 있다.
        assertEquals(expected, ("대게 " + Char(0x5C) + "30,000").toMenuChips())
    }

    @Test
    fun `숫자 사이가 공백으로 온 가격도 붙여서 뗀다`() {
        assertEquals(
            listOf(MenuItem("킹크랩", "₩12 000")),
            "킹크랩 W12 000".toMenuChips()
        )
    }

    @Test
    fun `가격 없는 메뉴는 이름만 남는다`() {
        assertEquals(
            listOf(MenuItem("돼지국밥", null), MenuItem("수육백반", null)),
            "돼지국밥, 수육백반".toMenuChips()
        )
    }

    @Test
    fun `이름 안의 숫자를 가격으로 잘못 떼지 않는다`() {
        // 앞에 공백이 없으면 이름의 일부다.
        assertEquals(listOf(MenuItem("커피1+1", null)), "커피1+1".toMenuChips())
        assertEquals(listOf(MenuItem("밀면2인분", null)), "밀면2인분".toMenuChips())
        // 단위가 뒤에 남으면 가격이 아니다.
        assertEquals(listOf(MenuItem("밀면 2인분", null)), "밀면 2인분".toMenuChips())
    }

    @Test
    fun `가격만 있고 이름이 없으면 쪼개지 않는다`() {
        assertEquals(listOf(MenuItem("₩12,000", null)), "₩12,000".toMenuChips())
    }

    @Test
    fun `구분자가 없는 메뉴 하나도 칩으로 만든다`() {
        // 가장 흔한 형태다 — 예전에는 나열이 아니라는 이유로 원문 그대로 나가서 이름과 가격이 붙어 보였다.
        assertEquals(listOf(MenuItem("킹크랩", "₩12,000")), "킹크랩 W12,000".toMenuChips())
    }

    @Test
    fun `메뉴 나열이 아니라 설명문이면 칩으로 만들지 않는다`() {
        assertNull("신선한 제철 해산물을 매일 아침 직접 골라 손질해 내는 집입니다".toMenuChips())
        assertNull("".toMenuChips())
    }

    @Test
    fun `가운뎃점과 슬래시도 구분자로 쓴다`() {
        assertEquals(
            listOf(MenuItem("밀면", null), MenuItem("만두", null), MenuItem("수육", null)),
            "밀면 · 만두 / 수육".toMenuChips()
        )
    }
}
