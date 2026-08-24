package com.mediinbusan.app.data.diagnosischat

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow

interface DiagnosisChatRepository {
    // language는 SupportedLanguage.code. slots는 지금까지 누적된 슬롯 상태(첫 턴은 전부 비어있음).
    fun sendMessage(language: String, userMessage: String, slots: DiagnosisSlotsDto): Flow<Result<DiagnosisChatResponseDto>>
}
