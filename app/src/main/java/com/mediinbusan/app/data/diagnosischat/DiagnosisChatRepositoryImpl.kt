package com.mediinbusan.app.data.diagnosischat

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/** MediInBusan 자체 백엔드(backend/, com.mediinbusan.backend.diagnosischat)의 Gemini 진단 챗봇 프록시를 호출한다. */
class DiagnosisChatRepositoryImpl @Inject constructor(
    private val diagnosisChatApi: DiagnosisChatApi
) : DiagnosisChatRepository {

    override fun sendMessage(
        language: String,
        userMessage: String,
        slots: DiagnosisSlotsDto
    ): Flow<Result<DiagnosisChatResponseDto>> = flow {
        emit(Result.Loading)
        try {
            val request = DiagnosisChatRequestDto(language = language, userMessage = userMessage, slots = slots)
            emit(Result.Success(diagnosisChatApi.sendMessage(request)))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e))
        }
    }.flowOn(Dispatchers.IO)
}
