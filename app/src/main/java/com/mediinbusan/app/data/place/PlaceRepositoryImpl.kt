package com.mediinbusan.app.data.place

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
    private val tourismApi: TourismApi
) : PlaceRepository {

    override fun getNearbyPlaces(hospitalId: String): Flow<Result<List<Place>>> = flow {
        emit(Result.Loading)
        try {
            emit(Result.Success(tourismApi.getNearbyWellnessPlaces(hospitalId).map { it.toDomain() }))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e, message = "주변 웰니스 장소를 불러오지 못했습니다."))
        }
    }

    override fun getPlaceDetail(placeId: String): Flow<Result<Place>> = flow {
        emit(Result.Loading)
        try {
            emit(Result.Success(tourismApi.getPlaceDetail(placeId).toDomain()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e, message = "장소 정보를 찾을 수 없습니다."))
        }
    }
}
