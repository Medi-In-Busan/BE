package com.mediinbusan.app.data.guide

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/** returnDate는 번역 대상이 아니라서 포함하지 않는다 — 화면에서는 원문을 그대로 유지한다. */
data class TreatmentBriefingTranslation(
    val visitPurpose: String,
    val symptoms: String,
    val allergy: String,
    val medication: String,
    val memo: String
)

/**
 * S-06 STEP04 "내 진료 카드"를 한국어로 번역하는 책임만 담당한다 — 원문 저장/조회는
 * [TreatmentBriefingRepository](DataStore)가 그대로 맡고, 이 리포지토리는 번역 결과를 저장하지
 * 않는다(사용자가 즉석에서 입력하는 값이라 캐싱 이점이 없다).
 */
interface TreatmentBriefingTranslationRepository {
    // sourceLanguage에는 SupportedLanguage.code(ko/en/zh/ja)를 그대로 넘긴다.
    fun translateToKorean(briefing: TreatmentBriefing, sourceLanguage: String): Flow<Result<TreatmentBriefingTranslation>>
}

class TreatmentBriefingTranslationRepositoryImpl @Inject constructor(
    private val api: TreatmentBriefingTranslateApi
) : TreatmentBriefingTranslationRepository {

    override fun translateToKorean(
        briefing: TreatmentBriefing,
        sourceLanguage: String
    ): Flow<Result<TreatmentBriefingTranslation>> = flow {
        emit(Result.Loading)
        try {
            val response = api.translate(
                TreatmentBriefingTranslateRequestDto(
                    sourceLang = sourceLanguage,
                    visitPurpose = briefing.visitPurpose,
                    symptoms = briefing.symptoms,
                    allergy = briefing.allergy,
                    medication = briefing.medication,
                    memo = briefing.memo
                )
            )
            emit(
                Result.Success(
                    TreatmentBriefingTranslation(
                        visitPurpose = response.visitPurpose,
                        symptoms = response.symptoms,
                        allergy = response.allergy,
                        medication = response.medication,
                        memo = response.memo
                    )
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e))
        }
    }.flowOn(Dispatchers.IO)
}
