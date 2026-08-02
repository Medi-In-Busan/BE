package com.mediinbusan.app.feature.selfdiagnosis

/** 결과 화면 CTA가 가리키는 목적지. 실제 라우팅은 MediInBusanNavHost.kt의
 *  Route.SelfDiagnosis composable에서 onNavigateToCtaTarget 콜백으로 처리한다.
 *  WELLNESS_PLACES는 전용 화면이 아직 없어 연결 전까지 no-op으로 둔다. */
enum class DiagnosisCtaTarget {
    HOSPITAL_INQUIRY_CHECKLIST,
    HOSPITAL_BROWSE,
    INTERPRETATION_SUPPORT,
    REGISTERED_AGENCY_CHECKLIST,
    VISA_ENTRY_GUIDE,
    TOTAL_COST_COVERAGE_CHECK,
    DEPARTURE_CHECKLIST,
    WELLNESS_PLACES
}

data class DiagnosisCta(
    val label: String,
    val target: DiagnosisCtaTarget
)