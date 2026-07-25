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
                description = "진료 후 가볍게 바닷가를 걸으며 회복 시간을 보내기 좋은 부산 대표 관광지입니다.",
                phoneNumber = null,
                distanceFromHospitalMeters = 500.0
            ),
            Place(
                id = "place-2",
                name = "동백섬 산책로",
                type = PlaceType.WALK,
                address = "부산광역시 해운대구 동백로 67",
                latitude = 35.1532,
                longitude = 129.1515,
                imageUrl = null,
                description = "완만한 해안 산책로와 전망 포인트가 있어 무리하지 않는 회복형 코스에 적합합니다.",
                phoneNumber = null,
                distanceFromHospitalMeters = 900.0
            ),
            Place(
                id = "place-3",
                name = "스파랜드 센텀시티",
                type = PlaceType.SPA,
                address = "부산광역시 해운대구 센텀남대로 35",
                latitude = 35.1688,
                longitude = 129.1295,
                imageUrl = null,
                description = "휴식 중심 일정에 넣기 좋은 도심형 스파 시설입니다. 시술 직후 이용 가능 여부는 의료진 안내를 우선하세요.",
                phoneNumber = "1668-2850",
                distanceFromHospitalMeters = 1300.0
            ),
            Place(
                id = "place-4",
                name = "센텀시티 카페 거리",
                type = PlaceType.RESTAURANT,
                address = "부산광역시 해운대구 센텀 일대",
                latitude = 35.1697,
                longitude = 129.1326,
                imageUrl = null,
                description = "대기 시간이나 진료 후 짧은 휴식에 맞춰 들르기 쉬운 카페·가벼운 식사 권역입니다.",
                phoneNumber = null,
                distanceFromHospitalMeters = 1500.0
            ),
            Place(
                id = "place-5",
                name = "신세계 센텀시티",
                type = PlaceType.SHOPPING,
                address = "부산광역시 해운대구 센텀남대로 35",
                latitude = 35.1688,
                longitude = 129.1295,
                imageUrl = null,
                description = "실내 이동 중심이라 날씨 영향을 덜 받는 쇼핑·식사·휴식 복합 공간입니다.",
                phoneNumber = "1588-1234",
                distanceFromHospitalMeters = 1400.0
            )
        )
    }
}
