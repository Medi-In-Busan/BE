package com.mediinbusan.app.feature.selfdiagnosis

import kotlinx.serialization.Serializable

/** 5개 질문에 대한 답변을 바탕으로 안내하는 준비 유형. 매핑 로직은 diagnosis-chat 백엔드의
 *  DiagnosisTypeMapper(Java 포팅본)가 담당한다 — 슬롯이 모두 채워지면 서버가 이 값을 판정해
 *  응답으로 내려준다. 표시 문자열(라벨/제목/설명/체크리스트/안내문/CTA 라벨)은 언어별로 바뀌므로
 *  여기 담지 않고 [toDisplay]에서 core/i18n의 DiagnosisResultStrings와 결합해 조립한다.
 *  @Serializable인 이유: 백엔드 응답 JSON의 resultType 필드를 상수명 그대로 역직렬화하기 위함 —
 *  상수명을 바꾸면 서버(Java enum)도 같이 바꿔야 한다. */
@Serializable
enum class DiagnosisResultType {
    TYPE_A, TYPE_B, TYPE_C, TYPE_D, TYPE_E
}