package com.mediinbusan.app.feature.selfdiagnosis

/** ViewModel -> Screen 일회성 이벤트(SharedFlow로 방출). */
sealed interface SelfDiagnosisEvent {
    /** 첫 질문에서 뒤로가기 시 화면 자체를 벗어난다. */
    data object NavigateBack : SelfDiagnosisEvent

    /** 결과 화면 CTA 클릭. 대상 하위 페이지는 아직 없어 호출부에서 콜백을 no-op으로 둬도 된다. */
    data class NavigateToCtaTarget(val target: DiagnosisCtaTarget) : SelfDiagnosisEvent
}