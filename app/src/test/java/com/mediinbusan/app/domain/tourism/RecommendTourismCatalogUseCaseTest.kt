package com.mediinbusan.app.domain.tourism

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendTourismCatalogUseCaseTest {
    private val useCase = RecommendTourismCatalogUseCase()

    @Test
    fun `선택하거나 즐겨찾기와 이름이 겹치는 관광지를 먼저 추천한다`() {
        val catalog = catalog(
            item("beach", "해운대 해수욕장"),
            item("park", "부산 시민공원"),
            item("spa", "동래 온천")
        )
        val profile = TourismInteractionProfile(
            selectedItemIds = setOf("spa"),
            interestKeywords = setOf("온천")
        )

        val result = useCase(
            catalog = catalog,
            profile = profile,
            favoritePlaceNames = listOf("동래 온천"),
            recentPlaceNames = listOf("시민공원")
        )

        assertEquals("spa", result.catalog.items.first().id)
        assertTrue("spa" in result.personalizedItemIds)
        assertTrue("park" in result.personalizedItemIds)
    }

    @Test
    fun `행동 기록이 없으면 공공데이터 원래 순서를 유지한다`() {
        val catalog = catalog(item("a", "첫 번째"), item("b", "두 번째"))

        val result = useCase(catalog, TourismInteractionProfile(), emptyList(), emptyList())

        assertEquals(listOf("a", "b"), result.catalog.items.map { it.id })
        assertTrue(result.personalizedItemIds.isEmpty())
    }

    @Test
    fun `앱 언어를 언어별 관광 카테고리로 변환하고 미지원 값은 한국어로 처리한다`() {
        assertEquals(TourismCatalogCategory.PLACES_KO, tourismCategoryForLanguage("ko"))
        assertEquals(TourismCatalogCategory.PLACES_EN, tourismCategoryForLanguage("en"))
        assertEquals(TourismCatalogCategory.PLACES_JA, tourismCategoryForLanguage("ja"))
        assertEquals(TourismCatalogCategory.PLACES_ZH, tourismCategoryForLanguage("zh"))
        assertEquals(TourismCatalogCategory.PLACES_KO, tourismCategoryForLanguage("unknown"))
    }

    private fun catalog(vararg items: TourismCatalogItem) = TourismCatalog(
        category = TourismCatalogCategory.PLACES_KO,
        title = "부산 관광지",
        description = "",
        source = "tourism-ko",
        retrievedAt = "",
        items = items.toList()
    )

    private fun item(id: String, title: String) = TourismCatalogItem(
        id = id,
        title = title,
        subtitle = null,
        address = null,
        imageUrl = null,
        latitude = null,
        longitude = null,
        details = emptyMap()
    )
}
