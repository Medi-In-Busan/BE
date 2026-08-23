package com.mediinbusan.app.data.place

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow

interface WellnessTourismRepository {
    fun getWalkingCourses(): Flow<Result<List<WellnessWalkingCourse>>>
}
