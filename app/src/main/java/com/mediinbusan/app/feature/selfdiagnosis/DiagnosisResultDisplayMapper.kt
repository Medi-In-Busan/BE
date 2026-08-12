package com.mediinbusan.app.feature.selfdiagnosis

import com.mediinbusan.app.core.i18n.DiagnosisResultStrings

/** [DiagnosisResultType] 하나를 화면에 표시할 때 필요한 언어별 문구 묶음. */
data class DiagnosisResultDisplay(
    val typeLabel: String,
    val title: String,
    val description: String,
    val checklist: List<String>,
    val noticeText: String?,
    val ctas: List<DiagnosisCta>
)

fun DiagnosisResultType.toDisplay(strings: DiagnosisResultStrings): DiagnosisResultDisplay = when (this) {
    DiagnosisResultType.TYPE_A -> DiagnosisResultDisplay(
        typeLabel = strings.typeATypeLabel,
        title = strings.typeATitle,
        description = strings.typeADescription,
        checklist = listOf(
            strings.typeAChecklist1,
            strings.typeAChecklist2,
            strings.typeAChecklist3,
            strings.typeAChecklist4,
            strings.typeAChecklist5
        ),
        noticeText = null,
        ctas = listOf(
            DiagnosisCta(strings.typeACta1Label, DiagnosisCtaTarget.HOSPITAL_INQUIRY_CHECKLIST),
            DiagnosisCta(strings.typeACta2Label, DiagnosisCtaTarget.HOSPITAL_BROWSE)
        )
    )
    DiagnosisResultType.TYPE_B -> DiagnosisResultDisplay(
        typeLabel = strings.typeBTypeLabel,
        title = strings.typeBTitle,
        description = strings.typeBDescription,
        checklist = listOf(
            strings.typeBChecklist1,
            strings.typeBChecklist2,
            strings.typeBChecklist3,
            strings.typeBChecklist4,
            strings.typeBChecklist5
        ),
        noticeText = null,
        ctas = listOf(
            DiagnosisCta(strings.typeBCta1Label, DiagnosisCtaTarget.INTERPRETATION_SUPPORT),
            DiagnosisCta(strings.typeBCta2Label, DiagnosisCtaTarget.HOSPITAL_INQUIRY_CHECKLIST),
            DiagnosisCta(strings.typeBCta3Label, DiagnosisCtaTarget.HOSPITAL_BROWSE)
        )
    )
    DiagnosisResultType.TYPE_C -> DiagnosisResultDisplay(
        typeLabel = strings.typeCTypeLabel,
        title = strings.typeCTitle,
        description = strings.typeCDescription,
        checklist = listOf(
            strings.typeCChecklist1,
            strings.typeCChecklist2,
            strings.typeCChecklist3,
            strings.typeCChecklist4,
            strings.typeCChecklist5
        ),
        noticeText = strings.typeCNoticeText,
        ctas = listOf(
            DiagnosisCta(strings.typeCCta1Label, DiagnosisCtaTarget.REGISTERED_AGENCY_CHECKLIST),
            DiagnosisCta(strings.typeCCta2Label, DiagnosisCtaTarget.VISA_ENTRY_GUIDE),
            DiagnosisCta(strings.typeCCta3Label, DiagnosisCtaTarget.TOTAL_COST_COVERAGE_CHECK)
        )
    )
    DiagnosisResultType.TYPE_D -> DiagnosisResultDisplay(
        typeLabel = strings.typeDTypeLabel,
        title = strings.typeDTitle,
        description = strings.typeDDescription,
        checklist = listOf(
            strings.typeDChecklist1,
            strings.typeDChecklist2,
            strings.typeDChecklist3,
            strings.typeDChecklist4,
            strings.typeDChecklist5
        ),
        noticeText = strings.typeDNoticeText,
        ctas = listOf(
            DiagnosisCta(strings.typeDCta1Label, DiagnosisCtaTarget.VISA_ENTRY_GUIDE),
            DiagnosisCta(strings.typeDCta2Label, DiagnosisCtaTarget.HOSPITAL_INQUIRY_CHECKLIST),
            DiagnosisCta(strings.typeDCta3Label, DiagnosisCtaTarget.DEPARTURE_CHECKLIST)
        )
    )
    DiagnosisResultType.TYPE_E -> DiagnosisResultDisplay(
        typeLabel = strings.typeETypeLabel,
        title = strings.typeETitle,
        description = strings.typeEDescription,
        checklist = listOf(
            strings.typeEChecklist1,
            strings.typeEChecklist2,
            strings.typeEChecklist3,
            strings.typeEChecklist4,
            strings.typeEChecklist5
        ),
        noticeText = null,
        ctas = listOf(
            DiagnosisCta(strings.typeECta1Label, DiagnosisCtaTarget.WELLNESS_PLACES),
            DiagnosisCta(strings.typeECta2Label, DiagnosisCtaTarget.VISA_ENTRY_GUIDE)
        )
    )
}