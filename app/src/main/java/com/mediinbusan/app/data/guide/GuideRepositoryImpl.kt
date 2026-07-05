package com.mediinbusan.app.data.guide

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * F-008: 의료 이용 절차 가이드는 외부 API가 아니라 앱 내부에서 직접 작성·관리하는 정적 콘텐츠다
 * (의료 상담이 아닌 일반 절차 안내로 범위를 제한한다).
 * TODO: 실제 5단계(입국 전/접수/진료/결제/사후관리) x 5개 언어 콘텐츠를 채운다.
 * 선택한 언어 콘텐츠가 없으면 기본 언어로 폴백한다.
 */
class GuideRepositoryImpl @Inject constructor() : GuideRepository {

    override fun getGuideSteps(languageCode: String): Flow<Result<List<GuideStep>>> = flow {
        emit(Result.Loading)
        emit(Result.Success(sampleSteps))
    }

    companion object {
        private val sampleSteps = GuidePhase.entries.mapIndexed { index, phase ->
            GuideStep(
                id = phase.name,
                phase = phase,
                title = phase.name,
                content = "TODO: ${phase.name} 단계 안내 콘텐츠",
                languageCode = "en",
                sortOrder = index
            )
        }
    }
}
