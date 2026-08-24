package com.mediinbusan.app.domain.course

import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.place.PlaceType
import com.mediinbusan.app.data.recent.RecentlyViewed
import com.mediinbusan.app.domain.tourism.TourismInteractionProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class BuildHospitalWellnessPersonalizationUseCaseTest {
    private val useCase = BuildHospitalWellnessPersonalizationUseCase()
    private val now = 1_800_000_000_000L

    @Test
    fun `즐겨찾기와 최근 조회를 장소 및 유형 선호로 변환한다`() {
        val context = useCase(
            medicalPurpose = null,
            profile = TourismInteractionProfile(),
            favorites = listOf(favorite("spa-favorite", "스파")),
            recentItems = listOf(recent("walk-recent", "산책", now - TimeUnit.DAYS.toMillis(1))),
            nowEpochMillis = now
        )

        assertEquals(setOf("spa-favorite"), context.favoritePlaceIds)
        assertTrue(context.typeWeights.getValue(PlaceType.SPA) > 0.0)
        assertTrue(context.typeWeights.getValue(PlaceType.WALK) > 0.0)
        assertTrue(context.recentPlaceWeights.getValue("walk-recent") > 0.8)
    }

    @Test
    fun `오래된 조회보다 최근 조회에 더 큰 시간 감쇠 가중치를 준다`() {
        val context = useCase(
            medicalPurpose = null,
            profile = TourismInteractionProfile(),
            favorites = emptyList(),
            recentItems = listOf(
                recent("recent", "관광지", now - TimeUnit.DAYS.toMillis(1)),
                recent("old", "관광지", now - TimeUnit.DAYS.toMillis(21))
            ),
            nowEpochMillis = now
        )

        assertTrue(context.recentPlaceWeights.getValue("recent") > context.recentPlaceWeights.getValue("old"))
    }

    private fun favorite(id: String, subtitle: String) = Favorite(
        itemId = id,
        itemType = FavoriteItemType.PLACE,
        name = id,
        imageUrl = null,
        savedAt = now,
        subtitle = subtitle
    )

    private fun recent(id: String, subtitle: String, viewedAt: Long) = RecentlyViewed(
        itemId = id,
        itemName = id,
        itemType = FavoriteItemType.PLACE,
        imageUrl = null,
        viewedAt = viewedAt,
        subtitle = subtitle
    )
}
