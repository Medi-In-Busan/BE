package com.mediinbusan.app.domain.tourism

import javax.inject.Inject
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class RecommendedTourismCourse(
    val stops: List<RecommendedTourismStop>,
    val totalDistanceKm: Double,
    val estimatedDurationMinutes: Int
)

data class RecommendedTourismStop(
    val order: Int,
    val item: TourismCatalogItem,
    val distanceFromPreviousKm: Double?,
    val transferMinutes: Int?
)

/**
 * 추천 정렬 결과에서 좌표가 있는 장소를 3~5개 고른 뒤, 추천 순위와 이동 부담을 함께 고려해
 * 방문 순서를 만든다. 도로 경로 API를 사용하지 않으므로 거리는 공개 좌표 간 직선거리다.
 */
class BuildRecommendedTourismCourseUseCase @Inject constructor() {
    operator fun invoke(
        rankedItems: List<TourismCatalogItem>,
        referenceLocation: TourismReferenceLocation? = null
    ): RecommendedTourismCourse? {
        val candidates = rankedItems
            .filter { it.latitude != null && it.longitude != null }
            .distinctBy { it.id }
            .take(MAX_CANDIDATES)
        if (candidates.size < MIN_STOPS) return null

        val rankById = candidates.mapIndexed { index, item -> item.id to index }.toMap()
        val start = referenceLocation?.let { reference ->
            candidates.minBy { item ->
                distanceKm(reference.latitude, reference.longitude, item) +
                    (rankById.getValue(item.id) * RANK_DISTANCE_PENALTY_KM)
            }
        } ?: candidates.first()

        val ordered = mutableListOf(start)
        val remaining = candidates.toMutableList().apply { remove(start) }
        while (ordered.size < MAX_STOPS && remaining.isNotEmpty()) {
            val current = ordered.last()
            val next = remaining.minBy { candidate ->
                distanceKm(current, candidate) +
                    (rankById.getValue(candidate.id) * RANK_DISTANCE_PENALTY_KM)
            }
            ordered += next
            remaining -= next
        }

        var totalDistanceKm = 0.0
        val stops = ordered.mapIndexed { index, item ->
            val legDistance = ordered.getOrNull(index - 1)?.let { previous -> distanceKm(previous, item) }
            if (legDistance != null) totalDistanceKm += legDistance
            RecommendedTourismStop(
                order = index + 1,
                item = item,
                distanceFromPreviousKm = legDistance,
                transferMinutes = legDistance?.let(::estimateTransferMinutes)
            )
        }
        val transferMinutes = stops.sumOf { it.transferMinutes ?: 0 }
        return RecommendedTourismCourse(
            stops = stops,
            totalDistanceKm = totalDistanceKm,
            estimatedDurationMinutes = transferMinutes + stops.size * VISIT_MINUTES_PER_STOP
        )
    }

    private fun estimateTransferMinutes(distanceKm: Double): Int =
        (distanceKm / ASSUMED_CITY_SPEED_KMH * 60.0).roundToInt().coerceAtLeast(MIN_TRANSFER_MINUTES)

    private fun distanceKm(first: TourismCatalogItem, second: TourismCatalogItem): Double = distanceKm(
        requireNotNull(first.latitude),
        requireNotNull(first.longitude),
        second
    )

    private fun distanceKm(latitude: Double, longitude: Double, item: TourismCatalogItem): Double {
        val itemLatitude = requireNotNull(item.latitude)
        val itemLongitude = requireNotNull(item.longitude)
        val latitudeDelta = Math.toRadians(itemLatitude - latitude)
        val longitudeDelta = Math.toRadians(itemLongitude - longitude)
        val firstLatitude = Math.toRadians(latitude)
        val secondLatitude = Math.toRadians(itemLatitude)
        val haversine = sin(latitudeDelta / 2) * sin(latitudeDelta / 2) +
            cos(firstLatitude) * cos(secondLatitude) *
            sin(longitudeDelta / 2) * sin(longitudeDelta / 2)
        return EARTH_RADIUS_KM * 2 * asin(sqrt(haversine))
    }

    private companion object {
        const val MIN_STOPS = 3
        const val MAX_STOPS = 5
        const val MAX_CANDIDATES = 12
        const val RANK_DISTANCE_PENALTY_KM = 0.35
        const val ASSUMED_CITY_SPEED_KMH = 24.0
        const val MIN_TRANSFER_MINUTES = 3
        const val VISIT_MINUTES_PER_STOP = 45
        const val EARTH_RADIUS_KM = 6371.0
    }
}
