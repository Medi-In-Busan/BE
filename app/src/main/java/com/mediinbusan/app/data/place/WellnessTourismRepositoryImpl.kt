package com.mediinbusan.app.data.place

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WellnessTourismRepositoryImpl @Inject constructor(
    private val tourismApi: TourismApi
) : WellnessTourismRepository {

    override fun getWalkingCourses(): Flow<Result<List<WellnessWalkingCourse>>> = flow {
        emit(Result.Loading)
        try {
            emit(Result.Success(tourismApi.getWellnessWalkingCourses().map { it.toDomain() }))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e, message = "걷기 코스 정보를 불러오지 못했습니다."))
        }
    }
}
