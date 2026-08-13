package com.mediinbusan.app.feature.selfdiagnosis

/** 5개 질문에 대한 답변을 바탕으로 안내하는 준비 유형. 매핑 로직은 DiagnosisTypeMapper 참고.
 *  표시 문자열(라벨/제목/설명/체크리스트/안내문/CTA 라벨)은 언어별로 바뀌므로 여기 담지 않고
 *  [toDisplay]에서 core/i18n의 DiagnosisResultStrings와 결합해 조립한다. */
enum class DiagnosisResultType {
    TYPE_A, TYPE_B, TYPE_C, TYPE_D, TYPE_E
}