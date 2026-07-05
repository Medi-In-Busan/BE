package com.mediinbusan.app.domain.nearby

import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * F-011: 병원 좌표 기준으로 가까운 순으로 관광·웰니스 장소를 정렬한다.
 * TODO: 실제 haversine 거리 계산 로직을 구현한다. 지금은 리포지토리가 반환한 순서를 그대로 통과시킨다.
 */
class GetNearbyPlacesSortedByDistanceUseCase @Inject constructor(
    private val placeRepository: PlaceRepository
) {
    operator fun invoke(hospitalId: String): Flow<Result<List<Place>>> =
        placeRepository.getNearbyPlaces(hospitalId)
}
