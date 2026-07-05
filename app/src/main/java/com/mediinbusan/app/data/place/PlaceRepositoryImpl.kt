package com.mediinbusan.app.data.place

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * TODO(F-011,F-012): [TourismApi] 실제 연동 + domain/nearby 거리 정렬 UseCase 적용으로 교체한다.
 * 지금은 화면 스캐폴드 검증을 위한 샘플 데이터를 반환한다. 주변 장소가 없을 때의 "부산 대표 관광지 표시"
 * 폴백(F-011)도 실제 연동 시 이 구현에 반영한다.
 */
class PlaceRepositoryImpl @Inject constructor(
    private val tourismApi: TourismApi
) : PlaceRepository {

    override fun getNearbyPlaces(hospitalId: String): Flow<Result<List<Place>>> = flow {
        emit(Result.Loading)
        emit(Result.Success(samplePlaces))
    }

    override fun getPlaceDetail(placeId: String): Flow<Result<Place>> = flow {
        emit(Result.Loading)
        val place = samplePlaces.firstOrNull { it.id == placeId }
        if (place != null) {
            emit(Result.Success(place))
        } else {
            emit(Result.Error(message = "장소 정보를 찾을 수 없습니다."))
        }
    }

    companion object {
        private val samplePlaces = listOf(
            Place(
                id = "place-1",
                name = "해운대 해수욕장",
                type = PlaceType.TOURIST_ATTRACTION,
                address = "부산광역시 해운대구",
                latitude = 35.1587,
                longitude = 129.1604,
                imageUrl = null,
                description = "실제 데이터 연동 전 표시되는 샘플 장소입니다.",
                phoneNumber = null,
                distanceFromHospitalMeters = 500.0
            )
        )
    }
}
