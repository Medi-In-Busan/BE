package com.mediinbusan.app.domain.tourism

import com.mediinbusan.app.core.common.MedicalCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

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

    @Test
    fun `최근 확인한 병원에서 가까운 관광지를 우선한다`() {
        val near = item("near", "가까운 공원", latitude = 35.10, longitude = 129.10)
        val far = item("far", "먼 공원", latitude = 35.30, longitude = 129.30)

        val result = useCase(
            catalog = catalog(far, near),
            profile = TourismInteractionProfile(),
            favoritePlaceNames = emptyList(),
            recentPlaceNames = emptyList(),
            context = TourismRecommendationContext(
                referenceLocation = TourismReferenceLocation(35.10, 129.10),
                nowEpochMillis = NOW
            )
        )

        assertEquals("near", result.catalog.items.first().id)
    }

    @Test
    fun `오래된 선택보다 최근 선택에 더 높은 시간 가중치를 준다`() {
        val profile = TourismInteractionProfile(
            selectedItemIds = setOf("old", "fresh"),
            itemInteractions = listOf(
                interaction("old", NOW - TimeUnit.DAYS.toMillis(28)),
                interaction("fresh", NOW)
            )
        )

        val result = useCase(
            catalog("old" to "오래된 선택", "fresh" to "최근 선택"),
            profile,
            emptyList(),
            emptyList(),
            TourismRecommendationContext(nowEpochMillis = NOW)
        )

        assertEquals("fresh", result.catalog.items.first().id)
    }

    @Test
    fun `의료 목적과 회복 단계에 맞춰 휴식 장소를 무리한 활동보다 우선한다`() {
        val result = useCase(
            catalog = catalog(
                item("hiking", "급경사 등산 트레킹"),
                item("spa", "실내 스파 휴식")
            ),
            profile = TourismInteractionProfile(),
            favoritePlaceNames = emptyList(),
            recentPlaceNames = emptyList(),
            context = TourismRecommendationContext(
                medicalPurpose = MedicalCategory.SKIN_BEAUTY,
                recoveryStage = TourismRecoveryStage.REST_FIRST,
                nowEpochMillis = NOW
            )
        )

        assertEquals("spa", result.catalog.items.first().id)
    }

    @Test
    fun `회복 단계가 없어도 저장된 의료 목적에 맞는 장소를 우선한다`() {
        val result = useCase(
            catalog = catalog(
                item("generic", "일반 관광 안내소"),
                item("wellness", "자연 속 웰니스 명상")
            ),
            profile = TourismInteractionProfile(),
            favoritePlaceNames = emptyList(),
            recentPlaceNames = emptyList(),
            context = TourismRecommendationContext(
                medicalPurpose = MedicalCategory.WELLNESS,
                recoveryStage = TourismRecoveryStage.STANDARD,
                nowEpochMillis = NOW
            )
        )

        assertEquals("wellness", result.catalog.items.first().id)
    }

    @Test
    fun `최근의 명확한 선택은 단순 거리보다 우선하고 오래되면 거리 영향이 커진다`() {
        val near = item("near", "가까운 일반 장소", latitude = 35.10, longitude = 129.10)
        val far = item("far", "선택한 먼 장소", latitude = 35.30, longitude = 129.30)
        val reference = TourismReferenceLocation(35.10, 129.10)

        val recentResult = useCase(
            catalog = catalog(near, far),
            profile = TourismInteractionProfile(itemInteractions = listOf(interaction("far", NOW))),
            favoritePlaceNames = emptyList(),
            recentPlaceNames = emptyList(),
            context = TourismRecommendationContext(referenceLocation = reference, nowEpochMillis = NOW)
        )
        val staleResult = useCase(
            catalog = catalog(near, far),
            profile = TourismInteractionProfile(
                itemInteractions = listOf(interaction("far", NOW - TimeUnit.DAYS.toMillis(56)))
            ),
            favoritePlaceNames = emptyList(),
            recentPlaceNames = emptyList(),
            context = TourismRecommendationContext(referenceLocation = reference, nowEpochMillis = NOW)
        )

        assertEquals("far", recentResult.catalog.items.first().id)
        assertEquals("near", staleResult.catalog.items.first().id)
    }

    @Test
    fun `비슷한 장소가 연속되지 않도록 다양한 주제의 장소를 재정렬한다`() {
        val profile = TourismInteractionProfile(
            itemInteractions = listOf(
                TourismItemInteraction(
                    itemId = "beach-a",
                    category = TourismCatalogCategory.PLACES_KO,
                    keywords = setOf("해운대", "바다", "해변", "산책"),
                    occurredAtEpochMillis = NOW
                )
            )
        )
        val result = useCase(
            catalog = catalog(
                item("beach-a", "해운대 바다 해변 산책"),
                item("beach-b", "해운대 바다 해변 산책 코스"),
                item("spa", "동래 온천 휴식")
            ),
            profile = profile,
            favoritePlaceNames = listOf("동래 온천 휴식"),
            recentPlaceNames = emptyList(),
            context = TourismRecommendationContext(nowEpochMillis = NOW)
        )

        assertEquals(listOf("beach-a", "spa"), result.catalog.items.take(2).map { it.id })
    }

    @Test
    fun `최근 병원 확인 시 의료 목적에 따라 회복 단계를 보수적으로 정한다`() {
        assertEquals(
            TourismRecoveryStage.REST_FIRST,
            inferTourismRecoveryStage(MedicalCategory.PLASTIC_SURGERY, NOW - TimeUnit.HOURS.toMillis(12), NOW)
        )
        assertEquals(
            TourismRecoveryStage.GENTLE,
            inferTourismRecoveryStage(MedicalCategory.REHABILITATION, NOW - TimeUnit.DAYS.toMillis(3), NOW)
        )
        assertEquals(
            TourismRecoveryStage.STANDARD,
            inferTourismRecoveryStage(MedicalCategory.WELLNESS, NOW, NOW)
        )
    }

    private fun catalog(vararg items: TourismCatalogItem) = TourismCatalog(
        category = TourismCatalogCategory.PLACES_KO,
        title = "부산 관광지",
        description = "",
        source = "tourism-ko",
        retrievedAt = "",
        items = items.toList()
    )

    private fun catalog(vararg items: Pair<String, String>) = catalog(
        *items.map { (id, title) -> item(id, title) }.toTypedArray()
    )

    private fun item(
        id: String,
        title: String,
        latitude: Double? = null,
        longitude: Double? = null
    ) = TourismCatalogItem(
        id = id,
        title = title,
        subtitle = null,
        address = null,
        imageUrl = null,
        latitude = latitude,
        longitude = longitude,
        details = emptyMap()
    )

    private fun interaction(itemId: String, occurredAt: Long) = TourismItemInteraction(
        itemId = itemId,
        category = TourismCatalogCategory.PLACES_KO,
        keywords = emptySet(),
        occurredAtEpochMillis = occurredAt
    )

    private companion object {
        const val NOW = 2_000_000_000_000L
    }
}
