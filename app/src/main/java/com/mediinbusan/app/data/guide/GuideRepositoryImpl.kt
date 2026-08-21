package com.mediinbusan.app.data.guide

import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.i18n.GuideStrings
import com.mediinbusan.app.core.i18n.appStringsFor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// F-008 의료 이용 절차 가이드: 외부 API가 아닌 앱 내부 정적 콘텐츠 (의료 상담이 아닌 일반 절차 안내로 범위 제한)
// 카드 제목/요약은 core/i18n/GuideStrings에서 언어별로 조회한다. STEP 상세 콘텐츠(체크리스트 등)는
// feature/guide의 GuideStepDetailContentMapper/GuideItemDetailContentMapper가 Composable에서 직접 다룬다.
class GuideRepositoryImpl @Inject constructor() : GuideRepository {

    override fun getGuideSteps(languageCode: String): Flow<Result<List<GuideStep>>> = flow {
        emit(Result.Loading)
        emit(Result.Success(buildSampleSteps(languageCode)))
    }

    private fun buildSampleSteps(languageCode: String): List<GuideStep> {
        val guide = appStringsFor(languageCode).guide
        return GuidePhase.entries.mapIndexed { index, phase ->
            GuideStep(
                id = phase.name,
                phase = phase,
                title = phase.stepTitle(guide),
                content = phase.stepSummary(guide),
                languageCode = languageCode,
                sortOrder = index
            )
        }
    }

    private fun GuidePhase.stepTitle(guide: GuideStrings): String = when (this) {
        GuidePhase.ENTRY_PREPARATION -> guide.stepEntryPreparationTitle
        GuidePhase.RESERVATION_INQUIRY -> guide.stepReservationInquiryTitle
        GuidePhase.HOSPITAL_CHECKIN -> guide.stepHospitalCheckinTitle
        GuidePhase.TREATMENT_EXAMINATION -> guide.stepTreatmentExaminationTitle
        GuidePhase.PAYMENT_RECEIPT -> guide.stepPaymentReceiptTitle
        GuidePhase.AFTERCARE_RETURN_CHECK -> guide.stepAftercareReturnCheckTitle
    }

    private fun GuidePhase.stepSummary(guide: GuideStrings): String = when (this) {
        GuidePhase.ENTRY_PREPARATION -> guide.stepEntryPreparationSummary
        GuidePhase.RESERVATION_INQUIRY -> guide.stepReservationInquirySummary
        GuidePhase.HOSPITAL_CHECKIN -> guide.stepHospitalCheckinSummary
        GuidePhase.TREATMENT_EXAMINATION -> guide.stepTreatmentExaminationSummary
        GuidePhase.PAYMENT_RECEIPT -> guide.stepPaymentReceiptSummary
        GuidePhase.AFTERCARE_RETURN_CHECK -> guide.stepAftercareReturnCheckSummary
    }
}
