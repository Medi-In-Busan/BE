package com.mediinbusan.app.domain.course

import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceRepository
import com.mediinbusan.app.data.place.PlaceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class WellnessCourse(
    val id: String,
    val name: String,
    val places: List<Place>,
    val estimatedDurationMinutes: Int,
    val recommendationReason: String,
    val caution: String?
)

/**
 * F-014: 진료 후 무리 없이 방문 가능한 산책·해변·스파·카페 중심의 회복형 코스를 조립한다.
 * OpenAPI 실제 연동 전에는 주변 장소의 유형과 거리 데이터를 이용해 데모 가능한 회복형 코스를 만든다.
 */
class AssembleWellnessCourseUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(
        hospitalId: String,
        languageCode: String? = null
    ): Flow<Result<List<WellnessCourse>>> = flow {
        val resolvedLanguageCode = languageCode
            ?: userPreferencesRepository.userPreferences.first().languageCode

        emitAll(
            placeRepository.getNearbyPlaces(
                hospitalId,
                resolvedLanguageCode
            ).map { result ->
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data.toWellnessCourses())
                    }

                    is Result.Error -> result
                    is Result.Loading -> result
                }
            }
        )
    }
}

private fun List<Place>.toWellnessCourses(): List<WellnessCourse> {
    if (isEmpty()) return emptyList()

    val byDistance = sortedBy {
        it.distanceFromHospitalMeters ?: Double.MAX_VALUE
    }

    val lightRecoveryPlaces = byDistance.filter {
        it.type == PlaceType.WALK ||
            it.type == PlaceType.SPA ||
            it.type == PlaceType.RESTAURANT
    }.ifEmpty {
        byDistance.take(3)
    }

    val shortVisitPlaces = byDistance.filter {
        it.type == PlaceType.TOURIST_ATTRACTION ||
            it.type == PlaceType.SHOPPING ||
            it.type == PlaceType.RESTAURANT
    }.ifEmpty {
        byDistance.take(2)
    }

    return listOf(
        WellnessCourse(
            id = "recovery-light",
            name = "진료 후 가벼운 회복 코스",
            places = lightRecoveryPlaces.take(3),
            estimatedDurationMinutes = 150,
            recommendationReason = "병원 근처에서 이동 부담이 적은 산책·휴식 장소를 먼저 묶었습니다.",
            caution = "시술 직후 사우나·장시간 보행은 의료진 안내를 우선하세요."
        ),
        WellnessCourse(
            id = "short-busan",
            name = "짧게 즐기는 부산 코스",
            places = shortVisitPlaces.take(3),
            estimatedDurationMinutes = 120,
            recommendationReason = "대기 시간이나 진료 전후 짧은 여유 시간에 맞춰 방문하기 쉬운 장소입니다.",
            caution = null
        )
    ).filter {
        it.places.isNotEmpty()
    }
}