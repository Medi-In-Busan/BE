package com.mediinbusan.app.data.guide

import kotlinx.serialization.Serializable

data class GuideStep(
    val id: String,
    val phase: GuidePhase,
    val title: String,
    val content: String,
    val languageCode: String,
    val sortOrder: Int
)

// res/drawable ic_guide_* 아이콘 6종 1:1 대응 (STEP 01~06). Navigation 타입세이프 라우트 인자로 쓰여 Serializable 필요.
@Serializable
enum class GuidePhase {
    ENTRY_PREPARATION, // STEP 01 입국 전 준비
    RESERVATION_INQUIRY, // STEP 02 예약 및 문의
    HOSPITAL_CHECKIN, // STEP 03 병원 방문 및 접수
    TREATMENT_EXAMINATION, // STEP 04 진료 및 검사
    PAYMENT_RECEIPT, // STEP 05 결제 및 수납
    AFTERCARE_RETURN_CHECK // STEP 06 진료 후 관리·귀국 전 체크
}
