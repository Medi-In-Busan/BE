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

fun GuidePhase.toStepNumberLabel(): String = when (this) {
    GuidePhase.ENTRY_PREPARATION -> "01"
    GuidePhase.RESERVATION_INQUIRY -> "02"
    GuidePhase.HOSPITAL_CHECKIN -> "03"
    GuidePhase.TREATMENT_EXAMINATION -> "04"
    GuidePhase.PAYMENT_RECEIPT -> "05"
    GuidePhase.AFTERCARE_RETURN_CHECK -> "06"
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