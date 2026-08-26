package com.mediinbusan.app.feature.selfdiagnosis

import androidx.compose.ui.graphics.Color
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.GuideStepBlue
import com.mediinbusan.app.core.designsystem.GuideStepGreen
import com.mediinbusan.app.core.designsystem.GuideStepOrange
import com.mediinbusan.app.core.designsystem.GuideStepRed
import com.mediinbusan.app.core.designsystem.GuideStepTeal
import com.mediinbusan.app.core.i18n.DiagnosisResultStrings

/** [DiagnosisResultType] 하나를 화면에 표시할 때 필요한 언어별 문구 + 지표 막대 + CTA 묶음. */
data class DiagnosisResultDisplay(
    val title: String,
    val subtitle: String,
    val accentColor: Color,
    /** 직접 문의 / 서류 복잡도 / 지원 필요도 / 관광·웰니스 순, 각 0~4단계. */
    val metricLevels: List<Int>,
    val checklist: List<String>,
    val ctas: List<DiagnosisCta>
)

fun DiagnosisResultType.toDisplay(strings: DiagnosisResultStrings): DiagnosisResultDisplay = when (this) {
    DiagnosisResultType.TYPE_A -> DiagnosisResultDisplay(
        title = strings.typeATitle,
        subtitle = strings.typeASubtitle,
        accentColor = GuideStepBlue,
        metricLevels = listOf(4, 2, 1, 2),
        checklist = listOf(
            strings.typeAChecklist1,
            strings.typeAChecklist2,
            strings.typeAChecklist3,
            strings.typeAChecklist4
        ),
        ctas = listOf(
            DiagnosisCta(strings.typeACta1Label, DiagnosisCtaTarget.GUIDE_STEP03_HOSPITAL_CHECKIN, R.drawable.self_diagnosis_hospital_inquiry_preparation),
            DiagnosisCta(strings.typeACta2Label, DiagnosisCtaTarget.HOSPITAL_BROWSE, R.drawable.self_diagnosis_busan_medical_institution_search)
        )
    )
    DiagnosisResultType.TYPE_B -> DiagnosisResultDisplay(
        title = strings.typeBTitle,
        subtitle = strings.typeBSubtitle,
        accentColor = GuideStepTeal,
        metricLevels = listOf(3, 3, 4, 1),
        checklist = listOf(
            strings.typeBChecklist1,
            strings.typeBChecklist2,
            strings.typeBChecklist3,
            strings.typeBChecklist4
        ),
        ctas = listOf(
            DiagnosisCta(strings.typeBCta1Label, DiagnosisCtaTarget.GUIDE_STEP02_RESERVATION_INQUIRY, R.drawable.self_diagnosis_interpreter_language_support),
            DiagnosisCta(strings.typeBCta2Label, DiagnosisCtaTarget.GUIDE_STEP03_HOSPITAL_CHECKIN, R.drawable.self_diagnosis_hospital_inquiry_preparation),
            DiagnosisCta(strings.typeBCta3Label, DiagnosisCtaTarget.HOSPITAL_BROWSE, R.drawable.self_diagnosis_busan_medical_institution_search)
        )
    )
    DiagnosisResultType.TYPE_C -> DiagnosisResultDisplay(
        title = strings.typeCTitle,
        subtitle = strings.typeCSubtitle,
        accentColor = GuideStepOrange,
        metricLevels = listOf(2, 4, 4, 1),
        checklist = listOf(
            strings.typeCChecklist1,
            strings.typeCChecklist2,
            strings.typeCChecklist3,
            strings.typeCChecklist4
        ),
        ctas = listOf(
            DiagnosisCta(strings.typeCCta1Label, DiagnosisCtaTarget.GUIDE_STEP01_ENTRY_PREPARATION, R.drawable.self_diagnosis_visa_entry_guide),
            DiagnosisCta(strings.typeCCta2Label, DiagnosisCtaTarget.GUIDE_STEP03_HOSPITAL_CHECKIN, R.drawable.self_diagnosis_medical_use_process)
        )
    )
    DiagnosisResultType.TYPE_D -> DiagnosisResultDisplay(
        title = strings.typeDTitle,
        subtitle = strings.typeDSubtitle,
        accentColor = GuideStepRed,
        metricLevels = listOf(2, 4, 3, 1),
        checklist = listOf(
            strings.typeDChecklist1,
            strings.typeDChecklist2,
            strings.typeDChecklist3,
            strings.typeDChecklist4
        ),
        ctas = listOf(
            DiagnosisCta(strings.typeDCta1Label, DiagnosisCtaTarget.GUIDE_STEP01_ENTRY_PREPARATION, R.drawable.self_diagnosis_visa_entry_guide),
            DiagnosisCta(strings.typeDCta2Label, DiagnosisCtaTarget.GUIDE_STEP02_RESERVATION_INQUIRY, R.drawable.self_diagnosis_hospital_inquiry_checklist),
            DiagnosisCta(strings.typeDCta3Label, DiagnosisCtaTarget.GUIDE_STEP06_AFTERCARE_RETURN_CHECK, R.drawable.self_diagnosis_return_home_checklist)
        )
    )
    DiagnosisResultType.TYPE_E -> DiagnosisResultDisplay(
        title = strings.typeETitle,
        subtitle = strings.typeESubtitle,
        accentColor = GuideStepGreen,
        metricLevels = listOf(2, 1, 1, 4),
        checklist = listOf(
            strings.typeEChecklist1,
            strings.typeEChecklist2,
            strings.typeEChecklist3,
            strings.typeEChecklist4
        ),
        ctas = listOf(
            DiagnosisCta(strings.typeECta1Label, DiagnosisCtaTarget.WELLNESS_PLACES, R.drawable.self_diagnosis_busan_wellness_places),
            DiagnosisCta(strings.typeECta2Label, DiagnosisCtaTarget.GUIDE_STEP01_ENTRY_PREPARATION, R.drawable.self_diagnosis_visa_entry_guide_e)
        )
    )
}
