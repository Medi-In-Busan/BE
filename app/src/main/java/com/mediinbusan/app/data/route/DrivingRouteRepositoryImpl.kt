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
        // 메시지를 여기서 만들지 않는다 — 리포지토리가 한국어 문구를 들고 있으면 4개 언어
        // 사용자에게도 그대로 나가고("실제 도로 경로를..."), 도보 모드에서도 "도로"라고 나왔다.
        // 뷰모델이 core/i18n의 routeLoadError/routeChangeError로 채운다.
        Result.Error(exception)
    }
}
