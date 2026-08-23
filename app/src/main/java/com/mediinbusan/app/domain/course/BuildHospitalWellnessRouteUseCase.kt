package com.mediinbusan.app.domain.course

import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceType
import javax.inject.Inject
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class HospitalWellnessRoute(
    val hospital: Hospital,
    val stops: List<HospitalWellnessStop>,
    val totalDistanceKm: Double,
    val estimatedDurationMinutes: Int,
    val roadPath: List<HospitalWellnessRoutePoint> = emptyList(),
    val hasActualDrivingRoute: Boolean = false
)

data class HospitalWellnessRoutePoint(
    val latitude: Double,
    val longitude: Double
)

data class HospitalWellnessStop(
    val order: Int,
    val place: Place,
    val distanceFromPreviousKm: Double,
    val transferMinutes: Int
)

/** 병원을 출발점으로 회복 친화도·개인 관심·거리·유형 다양성을 반영한 4~5개 장소 코스를 만든다. */
class BuildHospitalWellnessRouteUseCase @Inject constructor() {
    operator fun invoke(
        hospital: Hospital,
        places: List<Place>,
        medicalPurpose: MedicalCategory?,
        interestKeywords: Set<String> = emptySet()
    ): HospitalWellnessRoute? {
        val hospitalLatitude = hospital.latitude ?: return null
        val hospitalLongitude = hospital.longitude ?: return null
        val candidates = places
            .filter { it.latitude != null && it.longitude != null && it.type != PlaceType.LODGING }
            .distinctBy { it.id }
        if (candidates.size < MIN_STOPS) return null

        val targetSize = minOf(MAX_STOPS, candidates.size)
        val selected = mutableListOf<Place>()
        val remaining = candidates.toMutableList()
        while (selected.size < targetSize) {
            val next = remaining.maxBy { place ->
                recommendationScore(
                    place = place,
                    hospitalLatitude = hospitalLatitude,
                    hospitalLongitude = hospitalLongitude,
                    medicalPurpose = medicalPurpose,
                    interestKeywords = interestKeywords
                ) - selected.count { it.type == place.type } * SAME_TYPE_PENALTY
            }
            selected += next
            remaining -= next
        }

        val ordered = mutableListOf<Place>()
        val routeRemaining = selected.toMutableList()
        var previousLatitude = hospitalLatitude
        var previousLongitude = hospitalLongitude
        while (routeRemaining.isNotEmpty()) {
            val next = routeRemaining.minBy { place ->
                distanceKm(previousLatitude, previousLongitude, requireNotNull(place.latitude), requireNotNull(place.longitude))
            }
            ordered += next
            routeRemaining -= next
            previousLatitude = requireNotNull(next.latitude)
            previousLongitude = requireNotNull(next.longitude)
        }

        var totalDistanceKm = 0.0
        previousLatitude = hospitalLatitude
        previousLongitude = hospitalLongitude
        val stops = ordered.mapIndexed { index, place ->
            val distance = distanceKm(
                previousLatitude,
                previousLongitude,
                requireNotNull(place.latitude),
                requireNotNull(place.longitude)
            )
            totalDistanceKm += distance
            previousLatitude = requireNotNull(place.latitude)
            previousLongitude = requireNotNull(place.longitude)
            HospitalWellnessStop(
                order = index + 1,
                place = place,
                distanceFromPreviousKm = distance,
                transferMinutes = estimateTransferMinutes(distance)
            )
        }
        return HospitalWellnessRoute(
            hospital = hospital,
            stops = stops,
            totalDistanceKm = totalDistanceKm,
            estimatedDurationMinutes = stops.sumOf { it.transferMinutes } + stops.size * VISIT_MINUTES_PER_STOP
        )
    }

    private fun recommendationScore(
        place: Place,
        hospitalLatitude: Double,
        hospitalLongitude: Double,
        medicalPurpose: MedicalCategory?,
        interestKeywords: Set<String>
    ): Double {
        val distanceKm = place.distanceFromHospitalMeters?.div(1_000.0) ?: distanceKm(
            hospitalLatitude,
            hospitalLongitude,
            requireNotNull(place.latitude),
            requireNotNull(place.longitude)
        )
        val typeScore = when (place.type) {
            PlaceType.SPA -> 22.0
            PlaceType.WALK -> 20.0
            PlaceType.TOURIST_ATTRACTION -> 14.0
            PlaceType.RESTAURANT -> 10.0
            PlaceType.SHOPPING -> 6.0
            PlaceType.OTHER -> 4.0
            PlaceType.LODGING -> -20.0
        }
        val purposeScore = when (medicalPurpose) {
            MedicalCategory.WELLNESS, MedicalCategory.ORIENTAL_MEDICINE -> when (place.type) {
                PlaceType.SPA, PlaceType.WALK -> 12.0
                else -> 0.0
            }
            MedicalCategory.REHABILITATION -> when (place.type) {
                PlaceType.WALK -> 10.0
                PlaceType.SPA -> 6.0
                else -> 0.0
            }
            MedicalCategory.SKIN_BEAUTY, MedicalCategory.PLASTIC_SURGERY -> when (place.type) {
                PlaceType.SPA, PlaceType.RESTAURANT -> 7.0
                else -> 0.0
            }
            else -> 0.0
        }
        val searchable = tokenize(listOfNotNull(place.name, place.description, place.address).joinToString(" "))
        val interestScore = interestKeywords.count { keyword -> keyword.lowercase() in searchable } * INTEREST_KEYWORD_SCORE
        val distanceScore = MAX_DISTANCE_SCORE / (1.0 + distanceKm / DISTANCE_DECAY_KM)
        return typeScore + purposeScore + interestScore + distanceScore
    }

    private fun tokenize(value: String): Set<String> = value
        .lowercase()
        .split(Regex("[^가-힣ぁ-んァ-ヶ一-龠a-z0-9]+"))
        .filter { it.length >= 2 }
        .toSet()

    private fun estimateTransferMinutes(distanceKm: Double): Int =
        (distanceKm / ASSUMED_CITY_SPEED_KMH * 60.0).roundToInt().coerceAtLeast(MIN_TRANSFER_MINUTES)

    private fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val latitudeDelta = Math.toRadians(lat2 - lat1)
        val longitudeDelta = Math.toRadians(lon2 - lon1)
        val firstLatitude = Math.toRadians(lat1)
        val secondLatitude = Math.toRadians(lat2)
        val haversine = sin(latitudeDelta / 2) * sin(latitudeDelta / 2) +
            cos(firstLatitude) * cos(secondLatitude) *
            sin(longitudeDelta / 2) * sin(longitudeDelta / 2)
        return EARTH_RADIUS_KM * 2 * asin(sqrt(haversine))
    }

    private companion object {
        const val MIN_STOPS = 4
        const val MAX_STOPS = 5
        const val MAX_DISTANCE_SCORE = 36.0
        const val DISTANCE_DECAY_KM = 2.0
        const val SAME_TYPE_PENALTY = 16.0
        const val INTEREST_KEYWORD_SCORE = 6.0
        const val ASSUMED_CITY_SPEED_KMH = 24.0
        const val MIN_TRANSFER_MINUTES = 3
        const val VISIT_MINUTES_PER_STOP = 45
        const val EARTH_RADIUS_KM = 6371.0
    }
}
