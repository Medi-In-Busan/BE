package com.mediinbusan.app.domain.course

import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildHospitalWellnessRouteUseCaseTest {
    private val useCase = BuildHospitalWellnessRouteUseCase()

    @Test
    fun `병원 주변 추천 장소를 최대 다섯 개 코스로 만든다`() {
        val route = requireNotNull(
            useCase(
                hospital = hospital(),
                places = listOf(
                    place("spa", PlaceType.SPA, 1),
                    place("walk", PlaceType.WALK, 2),
                    place("park", PlaceType.TOURIST_ATTRACTION, 3),
                    place("food", PlaceType.RESTAURANT, 4),
                    place("shop", PlaceType.SHOPPING, 5),
                    place("other", PlaceType.OTHER, 6)
                ),
                medicalPurpose = MedicalCategory.WELLNESS
            )
        )

        assertEquals(5, route.stops.size)
        assertEquals((1..5).toList(), route.stops.map { it.order })
        assertTrue(route.totalDistanceKm > 0.0)
    }

    @Test
    fun `동일 유형만 몰리지 않고 회복 친화 유형을 섞는다`() {
        val restaurants = (1..5).map { place("food-$it", PlaceType.RESTAURANT, it) }
        val route = requireNotNull(
            useCase(
                hospital(),
                restaurants + place("spa", PlaceType.SPA, 6) + place("walk", PlaceType.WALK, 7),
                MedicalCategory.WELLNESS
            )
        )

        assertTrue(route.stops.any { it.place.type == PlaceType.SPA })
        assertTrue(route.stops.any { it.place.type == PlaceType.WALK })
    }

    @Test
    fun `좌표가 있는 장소가 네 개 미만이면 코스를 만들지 않는다`() {
        assertNull(
            useCase(
                hospital(),
                listOf(
                    place("one", PlaceType.SPA, 1),
                    place("two", PlaceType.WALK, 2),
                    place("three", PlaceType.RESTAURANT, 3)
                ),
                null
            )
        )
    }

    private fun hospital() = Hospital(
        id = "hospital",
        name = "테스트 병원",
        specialties = emptyList(),
        address = "부산",
        latitude = 35.10,
        longitude = 129.10,
        phoneNumber = null,
        homepageUrl = null,
        supportedLanguages = emptyList(),
        description = null,
        imageUrl = null,
        lastModified = null
    )

    private fun place(id: String, type: PlaceType, offset: Int) = Place(
        id = id,
        name = id,
        type = type,
        address = "부산",
        latitude = 35.10 + offset * 0.001,
        longitude = 129.10 + offset * 0.001,
        imageUrl = null,
        description = null,
        phoneNumber = null,
        distanceFromHospitalMeters = offset * 150.0
    )
}
