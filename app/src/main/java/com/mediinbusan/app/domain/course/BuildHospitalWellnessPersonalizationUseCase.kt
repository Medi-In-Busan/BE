package com.mediinbusan.app.domain.course

import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.place.PlaceType
import com.mediinbusan.app.data.recent.RecentItemType
import com.mediinbusan.app.data.recent.RecentlyViewed
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismInteractionProfile
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.exp
import kotlin.math.ln

class BuildHospitalWellnessPersonalizationUseCase @Inject constructor() {
    operator fun invoke(
        medicalPurpose: MedicalCategory?,
        profile: TourismInteractionProfile,
        favorites: List<Favorite>,
        recentItems: List<RecentlyViewed>,
        nowEpochMillis: Long = System.currentTimeMillis()
    ): HospitalWellnessPersonalization {
        val keywordWeights = mutableMapOf<String, Double>()
        val typeWeights = mutableMapOf<PlaceType, Double>()

        profile.interestKeywords.forEach { keywordWeights.add(it, LEGACY_KEYWORD_WEIGHT) }
        profile.itemInteractions.forEach { interaction ->
            val decay = timeDecay(interaction.occurredAtEpochMillis, nowEpochMillis, INTERACTION_HALF_LIFE_MILLIS)
            interaction.keywords.forEach { keywordWeights.add(it, INTERACTION_KEYWORD_WEIGHT * decay) }
            addCategoryAffinity(typeWeights, interaction.category, INTERACTION_CATEGORY_WEIGHT * decay)
        }
        profile.categoryAffinityScores.forEach { (category, affinity) ->
            val lastViewedAt = profile.categoryLastViewedAt[category] ?: nowEpochMillis
            val decay = timeDecay(lastViewedAt, nowEpochMillis, CATEGORY_HALF_LIFE_MILLIS)
            addCategoryAffinity(typeWeights, category, affinity.coerceAtMost(5.0) * decay)
        }

        val placeFavorites = favorites.filter { it.itemType == FavoriteItemType.PLACE }
        placeFavorites.forEach { favorite ->
            tokenize(favorite.name).forEach { keywordWeights.add(it, FAVORITE_KEYWORD_WEIGHT) }
            favorite.subtitle.toPlaceType()?.let { typeWeights.add(it, FAVORITE_TYPE_WEIGHT) }
        }

        val placeRecents = recentItems.filter { it.itemType == RecentItemType.PLACE }
        val recentWeights = placeRecents.associate { recent ->
            val decay = timeDecay(recent.viewedAt, nowEpochMillis, RECENT_HALF_LIFE_MILLIS)
            tokenize(recent.itemName).forEach { keywordWeights.add(it, RECENT_KEYWORD_WEIGHT * decay) }
            recent.subtitle.toPlaceType()?.let { typeWeights.add(it, RECENT_TYPE_WEIGHT * decay) }
            recent.itemId to decay
        }

        return HospitalWellnessPersonalization(
            medicalPurpose = medicalPurpose,
            keywordWeights = keywordWeights,
            typeWeights = typeWeights,
            favoritePlaceIds = placeFavorites.mapTo(mutableSetOf()) { it.itemId },
            recentPlaceWeights = recentWeights
        )
    }

    private fun addCategoryAffinity(
        weights: MutableMap<PlaceType, Double>,
        category: TourismCatalogCategory,
        amount: Double
    ) {
        when (category) {
            TourismCatalogCategory.WALKING -> weights.add(PlaceType.WALK, amount)
            TourismCatalogCategory.ACCESSIBLE -> {
                weights.add(PlaceType.WALK, amount * 0.7)
                weights.add(PlaceType.SPA, amount * 0.3)
            }
            TourismCatalogCategory.RELATED -> weights.add(PlaceType.TOURIST_ATTRACTION, amount)
            TourismCatalogCategory.CROWDING -> weights.add(PlaceType.TOURIST_ATTRACTION, amount * 0.5)
            else -> {
                weights.add(PlaceType.TOURIST_ATTRACTION, amount * 0.4)
                weights.add(PlaceType.RESTAURANT, amount * 0.3)
                weights.add(PlaceType.SHOPPING, amount * 0.3)
            }
        }
    }

    private fun String.toPlaceType(): PlaceType? = when {
        contains("맛집") || contains("카페") || contains("음식") -> PlaceType.RESTAURANT
        contains("쇼핑") -> PlaceType.SHOPPING
        contains("스파") || contains("휴식") -> PlaceType.SPA
        contains("산책") || contains("걷기") -> PlaceType.WALK
        contains("관광") -> PlaceType.TOURIST_ATTRACTION
        else -> null
    }

    private fun tokenize(value: String): Set<String> = value.lowercase()
        .split(Regex("[^가-힣ぁ-んァ-ヶ一-龠a-z0-9]+"))
        .filter { it.length >= 2 }
        .toSet()

    private fun timeDecay(occurredAt: Long, now: Long, halfLife: Double): Double =
        exp(-ln(2.0) * (now - occurredAt).coerceAtLeast(0L) / halfLife)

    private fun MutableMap<String, Double>.add(key: String, amount: Double) {
        val normalized = key.lowercase()
        this[normalized] = (this[normalized] ?: 0.0) + amount
    }

    private fun MutableMap<PlaceType, Double>.add(key: PlaceType, amount: Double) {
        this[key] = (this[key] ?: 0.0) + amount
    }

    private companion object {
        val INTERACTION_HALF_LIFE_MILLIS = TimeUnit.DAYS.toMillis(14).toDouble()
        val CATEGORY_HALF_LIFE_MILLIS = TimeUnit.DAYS.toMillis(30).toDouble()
        val RECENT_HALF_LIFE_MILLIS = TimeUnit.DAYS.toMillis(7).toDouble()
        const val LEGACY_KEYWORD_WEIGHT = 4.0
        const val INTERACTION_KEYWORD_WEIGHT = 9.0
        const val INTERACTION_CATEGORY_WEIGHT = 2.0
        const val FAVORITE_KEYWORD_WEIGHT = 12.0
        const val FAVORITE_TYPE_WEIGHT = 4.0
        const val RECENT_KEYWORD_WEIGHT = 6.0
        const val RECENT_TYPE_WEIGHT = 2.0
    }
}
