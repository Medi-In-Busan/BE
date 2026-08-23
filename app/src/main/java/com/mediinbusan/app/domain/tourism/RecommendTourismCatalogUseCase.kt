package com.mediinbusan.app.domain.tourism

import com.mediinbusan.app.core.common.MedicalCategory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sqrt

data class TourismRecommendation(
    val catalog: TourismCatalog,
    val personalizedItemIds: Set<String>
)

data class TourismRecommendationContext(
    val medicalPurpose: MedicalCategory? = null,
    val referenceLocation: TourismReferenceLocation? = null,
    val recoveryStage: TourismRecoveryStage = TourismRecoveryStage.STANDARD,
    val nowEpochMillis: Long = System.currentTimeMillis()
)

data class TourismReferenceLocation(
    val latitude: Double,
    val longitude: Double
)

enum class TourismRecoveryStage {
    STANDARD,
    GENTLE,
    REST_FIRST
}

/**
 * Uses only local app history. Hospital-view recency is a conservative planning signal,
 * not evidence that treatment actually occurred.
 */
fun inferTourismRecoveryStage(
    medicalPurpose: MedicalCategory?,
    lastHospitalViewedAt: Long?,
    nowEpochMillis: Long = System.currentTimeMillis()
): TourismRecoveryStage {
    if (medicalPurpose == null || lastHospitalViewedAt == null) return TourismRecoveryStage.STANDARD
    val ageDays = ((nowEpochMillis - lastHospitalViewedAt).coerceAtLeast(0L)).toDouble() / DAY_MILLIS
    val restFirstPurposes = setOf(
        MedicalCategory.SKIN_BEAUTY,
        MedicalCategory.DENTAL,
        MedicalCategory.PLASTIC_SURGERY,
        MedicalCategory.OBSTETRICS_GYNECOLOGY,
        MedicalCategory.OPHTHALMOLOGY
    )
    return when {
        medicalPurpose in restFirstPurposes && ageDays <= 2.0 -> TourismRecoveryStage.REST_FIRST
        medicalPurpose != MedicalCategory.WELLNESS && ageDays <= 7.0 -> TourismRecoveryStage.GENTLE
        else -> TourismRecoveryStage.STANDARD
    }
}

class RecommendTourismCatalogUseCase @Inject constructor() {
    operator fun invoke(
        catalog: TourismCatalog,
        profile: TourismInteractionProfile,
        favoritePlaceNames: List<String>,
        recentPlaceNames: List<String>,
        context: TourismRecommendationContext = TourismRecommendationContext()
    ): TourismRecommendation {
        val legacyKeywords = profile.interestKeywords + favoritePlaceNames.flatMap(::tokenize)
        val recentKeywords = recentPlaceNames.flatMap(::tokenize).toSet()
        val medicalKeywords = medicalPurposeKeywords(context.medicalPurpose)
        val eventItemIds = profile.itemInteractions.mapTo(mutableSetOf()) { it.itemId }

        val scored = catalog.items.mapIndexed { index, item ->
            val searchableTokens = searchableTokens(item)
            var score = 0.0

            // IDs without a timestamp are retained as a smaller migration-only signal.
            if (item.id in profile.selectedItemIds && item.id !in eventItemIds) score += LEGACY_SELECTION_SCORE
            score += legacyKeywords.count { it in searchableTokens } * LEGACY_KEYWORD_SCORE
            score += recentKeywords.count { it in searchableTokens } * RECENT_PLACE_SCORE
            score += medicalKeywords.count { it in searchableTokens } * MEDICAL_PURPOSE_SCORE

            profile.itemInteractions.forEach { interaction ->
                val decay = timeDecay(interaction.occurredAtEpochMillis, context.nowEpochMillis)
                if (interaction.itemId == item.id) score += DIRECT_SELECTION_SCORE * decay
                score += interaction.keywords.count { it in searchableTokens } * EVENT_KEYWORD_SCORE * decay
            }

            score += recoveryScore(catalog.category, searchableTokens, context.recoveryStage)
            score += distanceScore(item, context.referenceLocation)
            ScoredItem(item, score, index, searchableTokens)
        }

        val ranked = if (scored.none { it.score != 0.0 }) scored else diversify(scored)
        return TourismRecommendation(
            catalog = catalog.copy(items = ranked.map { it.item }),
            personalizedItemIds = ranked.filter { it.score > 0.0 }.take(5).map { it.item.id }.toSet()
        )
    }

    private fun timeDecay(occurredAt: Long, now: Long): Double {
        val age = (now - occurredAt).coerceAtLeast(0L).toDouble()
        return exp(-ln(2.0) * age / INTERACTION_HALF_LIFE_MILLIS)
    }

    private fun distanceScore(item: TourismCatalogItem, reference: TourismReferenceLocation?): Double {
        val latitude = item.latitude ?: return 0.0
        val longitude = item.longitude ?: return 0.0
        reference ?: return 0.0
        val distanceKm = haversineKm(reference.latitude, reference.longitude, latitude, longitude)
        return MAX_DISTANCE_SCORE / (1.0 + distanceKm / DISTANCE_DECAY_KM)
    }

    private fun recoveryScore(
        category: TourismCatalogCategory,
        tokens: Set<String>,
        stage: TourismRecoveryStage
    ): Double {
        if (stage == TourismRecoveryStage.STANDARD) return 0.0
        val restfulMatches = RESTFUL_KEYWORDS.count { it in tokens }
        val strenuousMatches = STRENUOUS_KEYWORDS.count { it in tokens }
        val accessibleBonus = if (category == TourismCatalogCategory.ACCESSIBLE) 8.0 else 0.0
        return when (stage) {
            TourismRecoveryStage.STANDARD -> 0.0
            TourismRecoveryStage.GENTLE -> restfulMatches * 6.0 - strenuousMatches * 8.0 + accessibleBonus
            TourismRecoveryStage.REST_FIRST -> restfulMatches * 10.0 - strenuousMatches * 16.0 + accessibleBonus
        }
    }

    private fun diversify(items: List<ScoredItem>): List<ScoredItem> {
        val remaining = items.toMutableList()
        val result = mutableListOf<ScoredItem>()
        while (remaining.isNotEmpty()) {
            val next = remaining.maxWithOrNull(
                compareBy<ScoredItem> { candidate ->
                    val similarity = result.maxOfOrNull { selected -> similarity(candidate, selected) } ?: 0.0
                    candidate.score - similarity * DIVERSITY_PENALTY
                }.thenBy { -it.originalIndex }
            ) ?: break
            result += next
            remaining -= next
        }
        return result
    }

    private fun similarity(first: ScoredItem, second: ScoredItem): Double {
        if (first.item.id == second.item.id) return 1.0
        val union = first.tokens union second.tokens
        if (union.isEmpty()) return 0.0
        return (first.tokens intersect second.tokens).size.toDouble() / union.size
    }

    private fun searchableTokens(item: TourismCatalogItem): Set<String> = listOfNotNull(
        item.title,
        item.subtitle,
        item.address
    ).plus(item.details.values).flatMap(::tokenize).toSet()

    private fun tokenize(value: String): List<String> = value
        .lowercase()
        .split(Regex("[^가-힣ぁ-んァ-ヶ一-龠a-z0-9]+"))
        .filter { it.length >= 2 }

    private fun medicalPurposeKeywords(purpose: MedicalCategory?): Set<String> = when (purpose) {
        MedicalCategory.SKIN_BEAUTY, MedicalCategory.PLASTIC_SURGERY ->
            setOf("실내", "카페", "스파", "휴식", "쇼핑", "indoor", "cafe", "spa", "relax")
        MedicalCategory.HEALTH_CHECKUP ->
            setOf("공원", "해변", "산책", "건강", "park", "beach", "wellness", "walk")
        MedicalCategory.DENTAL ->
            setOf("카페", "공원", "전시", "실내", "cafe", "park", "museum", "indoor")
        MedicalCategory.ORIENTAL_MEDICINE ->
            setOf("한방", "온천", "명상", "치유", "자연", "spa", "meditation", "healing", "nature")
        MedicalCategory.REHABILITATION ->
            setOf("무장애", "공원", "산책", "치유", "accessible", "park", "walk", "healing")
        MedicalCategory.WELLNESS ->
            setOf("웰니스", "온천", "스파", "명상", "요가", "자연", "wellness", "spa", "yoga", "nature")
        MedicalCategory.OBSTETRICS_GYNECOLOGY, MedicalCategory.OPHTHALMOLOGY ->
            setOf("실내", "공원", "휴식", "무장애", "indoor", "park", "relax", "accessible")
        MedicalCategory.ETC, null -> emptySet()
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val latDelta = Math.toRadians(lat2 - lat1)
        val lonDelta = Math.toRadians(lon2 - lon1)
        val firstLat = Math.toRadians(lat1)
        val secondLat = Math.toRadians(lat2)
        val a = sin(latDelta / 2) * sin(latDelta / 2) +
            cos(firstLat) * cos(secondLat) * sin(lonDelta / 2) * sin(lonDelta / 2)
        return EARTH_RADIUS_KM * 2 * asin(sqrt(a))
    }

    private data class ScoredItem(
        val item: TourismCatalogItem,
        val score: Double,
        val originalIndex: Int,
        val tokens: Set<String>
    )

    private companion object {
        val INTERACTION_HALF_LIFE_MILLIS = TimeUnit.DAYS.toMillis(14).toDouble()
        const val DIRECT_SELECTION_SCORE = 60.0
        const val EVENT_KEYWORD_SCORE = 7.0
        const val LEGACY_SELECTION_SCORE = 20.0
        const val LEGACY_KEYWORD_SCORE = 6.0
        const val RECENT_PLACE_SCORE = 4.0
        const val MEDICAL_PURPOSE_SCORE = 5.0
        const val MAX_DISTANCE_SCORE = 36.0
        const val DISTANCE_DECAY_KM = 3.0
        const val DIVERSITY_PENALTY = 18.0
        const val EARTH_RADIUS_KM = 6371.0

        val RESTFUL_KEYWORDS = setOf(
            "휴식", "힐링", "치유", "온천", "스파", "명상", "카페", "공원", "실내",
            "relax", "healing", "spa", "meditation", "cafe", "park", "indoor"
        )
        val STRENUOUS_KEYWORDS = setOf(
            "등산", "트레킹", "장거리", "급경사", "익스트림", "climb", "hiking", "trekking", "extreme"
        )
    }
}

private val DAY_MILLIS = TimeUnit.DAYS.toMillis(1).toDouble()
