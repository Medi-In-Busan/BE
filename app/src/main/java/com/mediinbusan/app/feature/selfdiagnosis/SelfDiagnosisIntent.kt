package com.mediinbusan.app.feature.selfdiagnosis

/** Screen -> ViewModel 단방향 사용자 액션. */
sealed interface SelfDiagnosisIntent {
    data class UpdateInputText(val text: String) : SelfDiagnosisIntent

    /** 자유 텍스트 입력창에서 전송. */
    data class SendMessage(val text: String) : SelfDiagnosisIntent

    /** 추천 답변 칩 탭. 말풍선에는 Screen이 이미 언어별로 변환한 [label]을 그대로 보여주되(ViewModel은
     *  i18n 문자열에 접근하지 않음), 서버로는 [option]의 enum 상수명을 실어 보낸다 — 서버가 정확히 같은
     *  이름과 매칭되면 Gemini 호출 없이 정적 응답으로 처리한다(DiagnosisChatService#tryResolveDirectAnswer
     *  참고). 자유 텍스트 입력(SendMessage)은 이 최적화 대상이 아니라 기존처럼 항상 Gemini로 간다. */
    data class TapSuggestedReply(val option: DiagnosisAnswerOption, val label: String) : SelfDiagnosisIntent

    /** 결과 화면의 "다시 진단하기" — 서버 호출 없이 로컬 채팅 상태만 초기화한다. */
    data object Restart : SelfDiagnosisIntent

    data class ClickCta(val target: DiagnosisCtaTarget) : SelfDiagnosisIntent

    /** 상단바 뒤로가기/시스템 백. 채팅 화면은 더 이상 인트로/질문 단계 구분이 없어 항상 화면 밖으로 나간다. */
    data object ClickBack : SelfDiagnosisIntent

    /** 결과 화면의 "홈으로 돌아가기". */
    data object FinishSetup : SelfDiagnosisIntent
}
