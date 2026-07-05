package com.mediinbusan.app.domain.course

import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceRepository
import kotlinx.coroutines.flow.Flow
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
 * TODO: 사전 선정 코스 데이터 + [PlaceRepository] 장소 정보를 조합하는 실제 큐레이션 로직을 구현한다.
 */
class AssembleWellnessCourseUseCase @Inject constructor(
    private val placeRepository: PlaceRepository
) {
    operator fun invoke(hospitalId: String): Flow<Result<List<WellnessCourse>>> =
        placeRepository.getNearbyPlaces(hospitalId).map { result ->
            when (result) {
                is Result.Success -> Result.Success(
                    listOf(
                        WellnessCourse(
                            id = "sample-course",
                            name = "TODO 회복 코스",
                            places = result.data,
                            estimatedDurationMinutes = 120,
                            recommendationReason = "TODO: 추천 이유",
                            caution = null
                        )
                    )
                )
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
}
