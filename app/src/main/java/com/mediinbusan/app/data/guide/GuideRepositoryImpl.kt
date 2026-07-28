package com.mediinbusan.app.data.guide

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// F-008 의료 이용 절차 가이드: 외부 API가 아닌 앱 내부 정적 콘텐츠 (의료 상담이 아닌 일반 절차 안내로 범위 제한)
// TODO: 실제 6단계(입국 전/예약문의/접수/진료/결제/사후관리) x 4개 언어(ko/en/zh/ja) 콘텐츠 필요, 선택 언어 콘텐츠 없으면 기본 언어 폴백
class GuideRepositoryImpl @Inject constructor() : GuideRepository {

    override fun getGuideSteps(languageCode: String): Flow<Result<List<GuideStep>>> = flow {
        emit(Result.Loading)
        emit(Result.Success(sampleSteps))
    }

    companion object {
        // TODO: 4개 언어(ko/en/zh/ja) 전체 콘텐츠 확장 필요, 현재는 한국어 카드 요약 문구만 존재
        private val sampleSteps = listOf(
            GuideStep(
                id = GuidePhase.ENTRY_PREPARATION.name,
                phase = GuidePhase.ENTRY_PREPARATION,
                title = "입국 전 준비",
                content = "예약, 서류, 비자, 보험 등 준비사항",
                languageCode = "ko",
                sortOrder = 0
            ),
            GuideStep(
                id = GuidePhase.RESERVATION_INQUIRY.name,
                phase = GuidePhase.RESERVATION_INQUIRY,
                title = "예약 및 문의",
                content = "병원 문의 전 준비할 정보와 팁",
                languageCode = "ko",
                sortOrder = 1
            ),
            GuideStep(
                id = GuidePhase.HOSPITAL_CHECKIN.name,
                phase = GuidePhase.HOSPITAL_CHECKIN,
                title = "병원 방문 및 접수",
                content = "접수 시 필요한 정보와 절차",
                languageCode = "ko",
                sortOrder = 2
            ),
            GuideStep(
                id = GuidePhase.TREATMENT_EXAMINATION.name,
                phase = GuidePhase.TREATMENT_EXAMINATION,
                title = "진료 및 검사",
                content = "진료 과정 및 주의사항",
                languageCode = "ko",
                sortOrder = 3
            ),
            GuideStep(
                id = GuidePhase.PAYMENT_RECEIPT.name,
                phase = GuidePhase.PAYMENT_RECEIPT,
                title = "결제 및 수납",
                content = "결제 방법과 영수증 확인",
                languageCode = "ko",
                sortOrder = 4
            ),
            GuideStep(
                id = GuidePhase.AFTERCARE_RETURN_CHECK.name,
                phase = GuidePhase.AFTERCARE_RETURN_CHECK,
                title = "진료 후 관리 · 귀국 전 체크",
                content = "약 복용, 서류, 영수증, 환급, 주의사항",
                languageCode = "ko",
                sortOrder = 5
            )
        )
    }
}
