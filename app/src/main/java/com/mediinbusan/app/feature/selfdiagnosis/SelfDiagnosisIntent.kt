package com.mediinbusan.app.feature.selfdiagnosis

/** Screen -> ViewModel 단방향 사용자 액션. */
sealed interface SelfDiagnosisIntent {
    /** 인트로(시작 페이지)의 "진단 시작하기" 버튼. */
    data object StartDiagnosis : SelfDiagnosisIntent
    data class SelectAnswer(val questionId: DiagnosisQuestionId, val option: DiagnosisAnswerOption) : SelfDiagnosisIntent
    data object GoNext : SelfDiagnosisIntent
    data object Restart : SelfDiagnosisIntent
    data class ClickCta(val target: DiagnosisCtaTarget) : SelfDiagnosisIntent

    /** 상단바 뒤로가기/시스템 백. 결과→질문, 질문 1번째→인트로, 인트로→화면 밖(NavigateBack 이벤트) 순으로 한 단계씩 되돌아간다. */
    data object ClickBack : SelfDiagnosisIntent
}