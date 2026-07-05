package com.mediinbusan.app.data.guide

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow

interface GuideRepository {
    fun getGuideSteps(languageCode: String): Flow<Result<List<GuideStep>>>
}
