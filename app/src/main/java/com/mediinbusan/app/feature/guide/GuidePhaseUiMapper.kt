package com.mediinbusan.app.feature.guide

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.GuideStepBlue
import com.mediinbusan.app.core.designsystem.GuideStepGreen
import com.mediinbusan.app.core.designsystem.GuideStepOrange
import com.mediinbusan.app.core.designsystem.GuideStepPurple
import com.mediinbusan.app.core.designsystem.GuideStepRed
import com.mediinbusan.app.core.designsystem.GuideStepTeal
import com.mediinbusan.app.core.i18n.GuideStrings
import com.mediinbusan.app.data.guide.GuidePhase

// 콘텐츠(GuideStep)-UI(아이콘·라벨) 분리 매퍼.
@DrawableRes
fun GuidePhase.toIconResId(): Int = when (this) {
    GuidePhase.ENTRY_PREPARATION -> R.drawable.ic_guide_entry_preparation
    GuidePhase.RESERVATION_INQUIRY -> R.drawable.ic_guide_reservation_inquiry
    GuidePhase.HOSPITAL_CHECKIN -> R.drawable.ic_guide_hospital_checkin
    GuidePhase.TREATMENT_EXAMINATION -> R.drawable.ic_guide_treatment_examination
    GuidePhase.PAYMENT_RECEIPT -> R.drawable.ic_guide_payment_receipt
    GuidePhase.AFTERCARE_RETURN_CHECK -> R.drawable.ic_guide_aftercare_return_check
}

// STEP 목록(S-06) 캐러셀 카드 안에 들어가는 3D 일러스트 아이콘 6종(투명 배경) — 상세 화면
// 아이콘(toIconResId)과는 별개 리소스다. 카드 전면 사진이 아니라 카드 내부에 얹는 일러스트다.
@DrawableRes
fun GuidePhase.toCardPhotoResId(): Int = when (this) {
    GuidePhase.ENTRY_PREPARATION -> R.drawable.guide_pre_arrival
    GuidePhase.RESERVATION_INQUIRY -> R.drawable.guide_reservation
    GuidePhase.HOSPITAL_CHECKIN -> R.drawable.guide_hospital_visit
    GuidePhase.TREATMENT_EXAMINATION -> R.drawable.guide_examination
    GuidePhase.PAYMENT_RECEIPT -> R.drawable.guide_payment
    GuidePhase.AFTERCARE_RETURN_CHECK -> R.drawable.guide_recovery_return
}

fun GuidePhase.toStepNumberLabel(): String = when (this) {
    GuidePhase.ENTRY_PREPARATION -> "01"
    GuidePhase.RESERVATION_INQUIRY -> "02"
    GuidePhase.HOSPITAL_CHECKIN -> "03"
    GuidePhase.TREATMENT_EXAMINATION -> "04"
    GuidePhase.PAYMENT_RECEIPT -> "05"
    GuidePhase.AFTERCARE_RETURN_CHECK -> "06"
}

// STEP 목록 카드·상세 화면 상단 타이틀에 쓰는 짧은 제목. STEP 상세 배너의 긴 안내문(bannerTitle)과는 별개다.
fun GuidePhase.toStepTitle(strings: GuideStrings): String = when (this) {
    GuidePhase.ENTRY_PREPARATION -> strings.stepEntryPreparationTitle
    GuidePhase.RESERVATION_INQUIRY -> strings.stepReservationInquiryTitle
    GuidePhase.HOSPITAL_CHECKIN -> strings.stepHospitalCheckinTitle
    GuidePhase.TREATMENT_EXAMINATION -> strings.stepTreatmentExaminationTitle
    GuidePhase.PAYMENT_RECEIPT -> strings.stepPaymentReceiptTitle
    GuidePhase.AFTERCARE_RETURN_CHECK -> strings.stepAftercareReturnCheckTitle
}

fun GuidePhase.toStepSummary(strings: GuideStrings): String = when (this) {
    GuidePhase.ENTRY_PREPARATION -> strings.stepEntryPreparationSummary
    GuidePhase.RESERVATION_INQUIRY -> strings.stepReservationInquirySummary
    GuidePhase.HOSPITAL_CHECKIN -> strings.stepHospitalCheckinSummary
    GuidePhase.TREATMENT_EXAMINATION -> strings.stepTreatmentExaminationSummary
    GuidePhase.PAYMENT_RECEIPT -> strings.stepPaymentReceiptSummary
    GuidePhase.AFTERCARE_RETURN_CHECK -> strings.stepAftercareReturnCheckSummary
}

// STEP 상세 배너(GuideStepHero)의 부제 문구.
fun GuidePhase.toHeroSubtitle(strings: GuideStrings): String = when (this) {
    GuidePhase.ENTRY_PREPARATION -> strings.stepEntryPreparationHeroSubtitle
    GuidePhase.RESERVATION_INQUIRY -> strings.stepReservationInquiryHeroSubtitle
    GuidePhase.HOSPITAL_CHECKIN -> strings.stepHospitalCheckinHeroSubtitle
    GuidePhase.TREATMENT_EXAMINATION -> strings.stepTreatmentExaminationHeroSubtitle
    GuidePhase.PAYMENT_RECEIPT -> strings.stepPaymentReceiptHeroSubtitle
    GuidePhase.AFTERCARE_RETURN_CHECK -> strings.stepAftercareReturnCheckHeroSubtitle
}

// 캐러셀 카드의 마스코트 말풍선(2줄: 일반 텍스트 + 강조 텍스트) 문구.
fun GuidePhase.toTipLead(strings: GuideStrings): String = when (this) {
    GuidePhase.ENTRY_PREPARATION -> strings.stepEntryPreparationTipLead
    GuidePhase.RESERVATION_INQUIRY -> strings.stepReservationInquiryTipLead
    GuidePhase.HOSPITAL_CHECKIN -> strings.stepHospitalCheckinTipLead
    GuidePhase.TREATMENT_EXAMINATION -> strings.stepTreatmentExaminationTipLead
    GuidePhase.PAYMENT_RECEIPT -> strings.stepPaymentReceiptTipLead
    GuidePhase.AFTERCARE_RETURN_CHECK -> strings.stepAftercareReturnCheckTipLead
}

fun GuidePhase.toTipHighlight(strings: GuideStrings): String = when (this) {
    GuidePhase.ENTRY_PREPARATION -> strings.stepEntryPreparationTipHighlight
    GuidePhase.RESERVATION_INQUIRY -> strings.stepReservationInquiryTipHighlight
    GuidePhase.HOSPITAL_CHECKIN -> strings.stepHospitalCheckinTipHighlight
    GuidePhase.TREATMENT_EXAMINATION -> strings.stepTreatmentExaminationTipHighlight
    GuidePhase.PAYMENT_RECEIPT -> strings.stepPaymentReceiptTipHighlight
    GuidePhase.AFTERCARE_RETURN_CHECK -> strings.stepAftercareReturnCheckTipHighlight
}

// STEP 목록 캐러셀 상단에 뜨는 마스코트 캐릭터. STEP별로 다른 포즈/의상을 보여준다.
// STEP01은 전용 이미지가 아직 없어 기본 마스코트를 그대로 쓴다.
@DrawableRes
fun GuidePhase.toMascotResId(): Int = when (this) {
    GuidePhase.ENTRY_PREPARATION -> R.drawable.common_medin_busan_mascot
    GuidePhase.RESERVATION_INQUIRY -> R.drawable.guide_step02_reservation_inquiry
    GuidePhase.HOSPITAL_CHECKIN -> R.drawable.guide_step03_hospital_visit_registration
    GuidePhase.TREATMENT_EXAMINATION -> R.drawable.guide_step04_treatment_examination
    GuidePhase.PAYMENT_RECEIPT -> R.drawable.guide_step05_payment_billing
    GuidePhase.AFTERCARE_RETURN_CHECK -> R.drawable.guide_step06_recovery_return
}

// 카드 번호 강조색.
fun GuidePhase.toAccentColor(): Color = when (this) {
    GuidePhase.ENTRY_PREPARATION -> GuideStepBlue
    GuidePhase.RESERVATION_INQUIRY -> GuideStepRed
    GuidePhase.HOSPITAL_CHECKIN -> GuideStepPurple
    GuidePhase.TREATMENT_EXAMINATION -> GuideStepTeal
    GuidePhase.PAYMENT_RECEIPT -> GuideStepOrange
    GuidePhase.AFTERCARE_RETURN_CHECK -> GuideStepGreen
}