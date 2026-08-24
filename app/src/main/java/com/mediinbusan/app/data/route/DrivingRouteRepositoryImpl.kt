package com.mediinbusan.app.data.route

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class DrivingRouteRepositoryImpl @Inject constructor(
    private val api: DrivingRouteApi
) : DrivingRouteRepository {
    override suspend fun getRoute(
        origin: DrivingRoutePoint,
        stops: List<DrivingRoutePoint>,
        mode: TravelMode
    ): Result<DrivingRoute> = try {
        val response = api.getDrivingRoute(
            DrivingRouteRequestDto(origin = origin.toDto(), stops = stops.map { it.toDto() }, mode = mode)
        )
        Result.Success(response.toDomain())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.Error(exception, "실제 도로 경로를 불러오지 못했습니다.")
    }
}
