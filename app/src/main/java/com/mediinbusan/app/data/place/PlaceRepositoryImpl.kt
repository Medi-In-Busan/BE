package com.mediinbusan.app.data.place

import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.common.TtlCache
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceRepositoryImpl @Inject constructor(
    private val tourismApi: TourismApi
) : PlaceRepository {

    // 지도 "전체 브라우징"(하단 탭 '지도') 진입마다 부산 전역 웰니스 장소를 매번 네트워크로 새로
    // 받아오면 로딩이 느리게 느껴진다 — 이 리포지토리가 @Singleton이라 앱이 살아있는 동안 유지되는
    // 캐시를 둔다. 웰니스 장소는 관리자 배치(POST /api/wellness/ingest)로만 바뀌는 데이터라 몇 분
    // 지연되어 반영돼도 문제없다(언어별로 응답 내용이 달라 languageCode를 캐시 키로 쓴다).
    private val allPlacesCache = TtlCache<String, List<Place>>(ttlMillis = TimeUnit.MINUTES.toMillis(10))

    override fun getNearbyPlaces(hospitalId: String, languageCode: String): Flow<Result<List<Place>>> = flow {
        emit(Result.Loading)
        try {
            emit(Result.Success(tourismApi.getNearbyWellnessPlaces(hospitalId, language = languageCode).map { it.toDomain() }))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e, message = "주변 웰니스 장소를 불러오지 못했습니다."))
        }
    }

    override fun getPlaceDetail(placeId: String, languageCode: String): Flow<Result<Place>> = flow {
        emit(Result.Loading)
        try {
            emit(Result.Success(tourismApi.getPlaceDetail(placeId, languageCode).toDomain()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e, message = "장소 정보를 찾을 수 없습니다."))
        }
    }

    override fun getAllPlaces(languageCode: String): Flow<Result<List<Place>>> = flow {
        allPlacesCache.get(languageCode)?.let { cached ->
            emit(Result.Success(cached))
            return@flow
        }
        emit(Result.Loading)
        try {
            val places = tourismApi.getPlaces(language = languageCode).map { it.toDomain() }
            allPlacesCache.put(languageCode, places)
            emit(Result.Success(places))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e, message = "웰니스 장소를 불러오지 못했습니다."))
        }
    }
}
