package com.mediinbusan.app.feature.selfdiagnosis

/**
 * 결과 화면 CTA가 가리키는 목적지. 실제 라우팅은 MediInBusanNavHost.kt의
 * Route.SelfDiagnosis composable에서 onNavigateToCtaTarget 콜백으로 처리한다.
 *
 * GUIDE_STEP*는 이용 가이드(S-06) STEP 상세로 그대로 연결한다 — 진단 결과 전용 화면을 새로
 * 만드는 대신 기존 콘텐츠를 재사용한다(사용자 피드백으로 신규 화면은 걷어냄).
 */
enum class DiagnosisCtaTarget {
    HOSPITAL_BROWSE,
    WELLNESS_PLACES,
    GUIDE_STEP01_ENTRY_PREPARATION,
    GUIDE_STEP02_RESERVATION_INQUIRY,
    GUIDE_STEP03_HOSPITAL_CHECKIN,
    GUIDE_STEP06_AFTERCARE_RETURN_CHECK
}

data class DiagnosisCta(
    val label: String,
    val target: DiagnosisCtaTarget,
    val iconRes: Int
)
