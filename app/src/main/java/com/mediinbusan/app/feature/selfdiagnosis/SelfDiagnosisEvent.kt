package com.mediinbusan.app.feature.selfdiagnosis

/** ViewModel -> Screen 일회성 이벤트(SharedFlow로 방출). */
sealed interface SelfDiagnosisEvent {
    /** 첫 질문에서 뒤로가기 시 화면 자체를 벗어난다. */
    data object NavigateBack : SelfDiagnosisEvent

    /** 결과 화면 CTA 클릭. 대상 하위 페이지는 아직 없어 호출부에서 콜백을 no-op으로 둬도 된다. */
    data class NavigateToCtaTarget(val target: DiagnosisCtaTarget) : SelfDiagnosisEvent

    /** FinishSetup 처리 완료(진단완료 상태 저장 후) - 최초 실행 흐름에서만 발생, Home으로 보낸다. */
    data object NavigateToHome : SelfDiagnosisEvent
}