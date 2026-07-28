package com.mediinbusan.app.feature.selfdiagnosis

/** 결과 화면 CTA가 가리키는 9개 목적지. 하위 페이지는 아직 없고, 실제 화면이 생기면
 *  SelfDiagnosisRoute의 onNavigateToCtaTarget 콜백에서 이 값을 보고 라우팅하면 된다. */
enum class DiagnosisCtaTarget {
    HOSPITAL_INQUIRY_CHECKLIST,
    HOSPITAL_BROWSE,
    INTERPRETATION_SUPPORT,
    REGISTERED_AGENCY_CHECKLIST,
    VISA_ENTRY_GUIDE,
    MEDICAL_PROCEDURE_GUIDE,
    DEPARTURE_CHECKLIST,
    WELLNESS_PLACES,
    MEDICAL_WELLNESS_INFO
}

data class DiagnosisCta(
    val label: String,
    val target: DiagnosisCtaTarget
)