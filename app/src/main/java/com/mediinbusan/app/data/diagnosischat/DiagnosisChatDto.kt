package com.mediinbusan.app.data.diagnosischat

import com.mediinbusan.app.feature.selfdiagnosis.DiagnosisAnswerOption
import com.mediinbusan.app.feature.selfdiagnosis.DiagnosisResultType
import kotlinx.serialization.Serializable

/** POST api/v1/diagnosis-chat 요청 바디. 서버는 세션을 저장하지 않으므로(stateless) 매 턴 [slots]를
 *  그대로 실어 보낸다 — 대화 히스토리나 직전 assistant 발화는 보내지 않는다. 다음 질문 대상 슬롯은
 *  서버가 [slots] 상태만으로 스스로 계산한다(클라이언트가 준 대화 텍스트를 신뢰하지 않기 위함). */
@Serializable
data class DiagnosisChatRequestDto(
    val language: String,
    val userMessage: String,
    val slots: DiagnosisSlotsDto
)

/** POST api/v1/diagnosis-chat 응답. [resultType]은 4개 단일 슬롯(entryStayConditions 제외)이
 *  모두 채워지기 전까지는 null이다. */
@Serializable
data class DiagnosisChatResponseDto(
    val reply: String,
    val slots: DiagnosisSlotsDto,
    val resultType: DiagnosisResultType? = null
)

/** 5개 진단 슬롯의 현재 상태. 필드 이름/enum 상수명은 백엔드(Java)와 그대로 공유되는 계약이다. */
@Serializable
data class DiagnosisSlotsDto(
    val visitPurpose: DiagnosisAnswerOption.VisitPurpose? = null,
    val stayDuration: DiagnosisAnswerOption.StayDuration? = null,
    val reservationStatus: DiagnosisAnswerOption.ReservationStatus? = null,
    val interpretationNeed: DiagnosisAnswerOption.InterpretationNeed? = null,
    val entryStayConditions: List<DiagnosisAnswerOption.EntryStayCondition> = emptyList()
)
