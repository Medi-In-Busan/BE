package com.mediinbusan.app.domain.tourism

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildRecommendedTourismCourseUseCaseTest {
    private val useCase = BuildRecommendedTourismCourseUseCase()

    @Test
    fun `추천 후보에서 좌표가 있는 장소를 최대 다섯 개 코스로 묶는다`() {
        val items = (1..7).map { index ->
            item("place-$index", 35.10 + index * 0.001, 129.10 + index * 0.001)
        }

        val result = requireNotNull(useCase(items))

        assertEquals(5, result.stops.size)
        assertEquals((1..5).toList(), result.stops.map { it.order })
        assertEquals("place-1", result.stops.first().item.id)
        assertTrue(result.totalDistanceKm > 0.0)
        assertTrue(result.estimatedDurationMinutes >= 5 * 45)
    }

    @Test
    fun `최근 병원 좌표가 있으면 추천 순위와 시작 거리를 함께 고려한다`() {
        val ranked = listOf(
            item("far", 35.30, 129.30),
            item("near", 35.101, 129.101),
            item("next", 35.102, 129.102)
        )

        val result = requireNotNull(
            useCase(ranked, TourismReferenceLocation(latitude = 35.10, longitude = 129.10))
        )

        assertEquals("near", result.stops.first().item.id)
    }

    @Test
    fun `좌표가 있는 추천 장소가 세 개 미만이면 코스를 만들지 않는다`() {
        val result = useCase(
            listOf(
                item("one", 35.1, 129.1),
                item("two", 35.2, 129.2),
                TourismCatalogItem("no-coordinate", "좌표 없음", null, null, null, null, null, emptyMap())
            )
        )

        assertNull(result)
    }

    private fun item(id: String, latitude: Double, longitude: Double) = TourismCatalogItem(
        id = id,
        title = id,
        subtitle = null,
        address = null,
        imageUrl = null,
        latitude = latitude,
        longitude = longitude,
        details = emptyMap()
    )
}
