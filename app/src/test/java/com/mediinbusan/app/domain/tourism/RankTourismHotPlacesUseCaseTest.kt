package com.mediinbusan.app.domain.tourism

import org.junit.Assert.assertEquals
import org.junit.Test

class RankTourismHotPlacesUseCaseTest {
    private val useCase = RankTourismHotPlacesUseCase()

    @Test
    fun `구군별 혼잡도 데이터를 합쳐 높은 순서로 정렬한다`() {
        val result = useCase(
            catalogs = listOf(
                BusanDistrict.HAEUNDAE to catalog(
                    item("beach", "해운대 해수욕장", "68.5%")
                ),
                BusanDistrict.SUYEONG to catalog(
                    item("bridge", "광안리 해수욕장", "혼잡도 82%")
                )
            )
        )

        assertEquals(listOf("광안리 해수욕장", "해운대 해수욕장"), result.map { it.item.title })
        assertEquals(82.0, result.first().congestionRate, 0.0)
        assertEquals(BusanDistrict.SUYEONG, result.first().district)
    }

    @Test
    fun `같은 장소는 가장 높은 혼잡도 한 건만 남긴다`() {
        val result = useCase(
            catalogs = listOf(
                BusanDistrict.HAEUNDAE to catalog(item("old", "해운대 해수욕장", "41")),
                BusanDistrict.SUYEONG to catalog(item("new", " 해운대 해수욕장 ", "73"))
            )
        )

        assertEquals(1, result.size)
        assertEquals(73.0, result.single().congestionRate, 0.0)
    }

    @Test
    fun `혼잡도 값이 없는 항목은 핫플레이스에서 제외한다`() {
        val result = useCase(
            catalogs = listOf(
                BusanDistrict.JUNG to catalog(
                    TourismCatalogItem(
                        id = "unknown",
                        title = "용두산공원",
                        subtitle = null,
                        address = null,
                        imageUrl = null,
                        latitude = null,
                        longitude = null,
                        details = mapOf("baseYmd" to "20260824")
                    )
                )
            )
        )

        assertEquals(emptyList<TourismHotPlace>(), result)
    }

    @Test
    fun `기본 결과는 집중도 상위 다섯 곳만 반환한다`() {
        val result = useCase(
            catalogs = listOf(
                BusanDistrict.JUNG to catalog(
                    *(1..7).map { index ->
                        item("place-$index", "관광지 $index", (index * 10).toString())
                    }.toTypedArray()
                )
            )
        )

        assertEquals(5, result.size)
        assertEquals(listOf(70.0, 60.0, 50.0, 40.0, 30.0), result.map { it.congestionRate })
    }

    private fun catalog(vararg items: TourismCatalogItem) = TourismCatalog(
        category = TourismCatalogCategory.CROWDING,
        title = "관광지 혼잡도",
        description = "",
        source = "crowding-forecast",
        retrievedAt = "",
        items = items.toList()
    )

    private fun item(id: String, title: String, rate: String) = TourismCatalogItem(
        id = id,
        title = title,
        subtitle = rate,
        address = null,
        imageUrl = null,
        latitude = null,
        longitude = null,
        details = mapOf("cnctrRate" to rate)
    )
}
