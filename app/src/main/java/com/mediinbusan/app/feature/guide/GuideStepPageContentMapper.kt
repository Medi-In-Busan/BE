package com.mediinbusan.app.feature.guide

import com.mediinbusan.app.R
import com.mediinbusan.app.core.i18n.GuideStrings
import com.mediinbusan.app.data.guide.GuidePhase

// STEP 상세 리디자인 합본 페이지 매퍼. GuideStepDetailContentMapper(STEP 개요)와
// GuideItemDetailContentMapper(leaf 항목별 상세)의 기존 함수가 반환하는 실제 데이터(제목/설명/URL/id)는
// 그대로 재사용하고, 여기서는 "어떤 섹션에 어떤 항목을 배치할지"만 재구성한다 — 원본 콘텐츠 함수는
// 건드리지 않는다(leaf 단독 진입 라우트에서도 계속 동일하게 쓰인다).
// 상황별 확인 섹션과 안내 배너는 합본 페이지 디자인에서 제외한다(사용자 피드백 반영).
// STEP04(진료 및 검사)는 브리핑 카드 등 구조 자체가 달라 TreatmentExaminationDetailScreen 전용으로 유지한다.
fun GuidePhase.toStepPageContent(strings: GuideStrings): GuideStepPageContent = when (this) {
    GuidePhase.ENTRY_PREPARATION -> entryPreparationPageContent(strings)
    GuidePhase.RESERVATION_INQUIRY -> reservationInquiryPageContent(strings)
    GuidePhase.HOSPITAL_CHECKIN -> hospitalCheckinPageContent(strings)
    GuidePhase.TREATMENT_EXAMINATION -> error("STEP04는 TreatmentExaminationDetailScreen 전용 화면을 사용한다")
    GuidePhase.PAYMENT_RECEIPT -> paymentReceiptPageContent(strings)
    GuidePhase.AFTERCARE_RETURN_CHECK -> aftercareReturnCheckPageContent(strings)
}

private fun GuidePhase.toBasePageContent(
    strings: GuideStrings,
    heroResId: Int,
    sections: List<GuideStepPageSection>
) = GuideStepPageContent(
    heroResId = heroResId,
    stepNumberLabel = toStepNumberLabel(),
    stepTitle = toStepTitle(strings),
    stepSubtitle = toHeroSubtitle(strings),
    sections = sections,
    tipLead = toTipLead(strings),
    tipHighlight = toTipHighlight(strings)
)

private fun entryPreparationPageContent(strings: GuideStrings): GuideStepPageContent {
    val visaEntryCheck = visaEntryCheckContent(strings)
    val insuranceDocuments = insuranceDocumentsContent(strings)
    val hospitalInquiry = hospitalInquiryContent(strings)
    return GuidePhase.ENTRY_PREPARATION.toBasePageContent(
        strings = strings,
        heroResId = R.drawable.guide_step01_pre_arrival_hero_banner,
        sections = listOf(
            GuideStepPageSection(
                title = visaEntryCheck.bannerTitle,
                cardStyle = GuideStepCardStyle.LINK,
                rowItems = visaEntryCheck.checklistItems,
                orderStepsTitle = visaEntryCheck.orderStepsTitle,
                orderSteps = visaEntryCheck.orderSteps
            ),
            GuideStepPageSection(title = insuranceDocuments.bannerTitle, cardStyle = GuideStepCardStyle.MEMO, rowItems = insuranceDocuments.checklistItems),
            GuideStepPageSection(title = hospitalInquiry.bannerTitle, cardStyle = GuideStepCardStyle.MEMO, rowItems = hospitalInquiry.checklistItems)
        )
    )
}

private fun reservationInquiryPageContent(strings: GuideStrings): GuideStepPageContent {
    val overview = reservationInquiryContent(strings)
    val preInquiryInformation = preInquiryInformationContent(strings)
    return GuidePhase.RESERVATION_INQUIRY.toBasePageContent(
        strings = strings,
        heroResId = R.drawable.guide_step02_reservation_inquiry_banner,
        sections = listOf(
            GuideStepPageSection(
                title = "",
                cardStyle = GuideStepCardStyle.LINK,
                rowItems = overview.checklistItems.filter { it.url != null }
            ),
            GuideStepPageSection(title = preInquiryInformation.bannerTitle, cardStyle = GuideStepCardStyle.MEMO, rowItems = preInquiryInformation.checklistItems)
        )
    )
}

private fun hospitalCheckinPageContent(strings: GuideStrings): GuideStepPageContent {
    val medicalRecordsTestResults = medicalRecordsTestResultsContent(strings)
    val hospitalLocationCheckin = strings.hospitalLocationCheckin
    return GuidePhase.HOSPITAL_CHECKIN.toBasePageContent(
        strings = strings,
        heroResId = R.drawable.guide_step03_hospital_visit_registration_banner,
        sections = listOf(
            // 병원 정보(위치·교통·주차) 확인은 지도로 바로 이동하는 안내 카드 한 장으로 유지하고,
            // 기존 onItemClick(HOSPITAL_LOCATION_CHECKIN_GUIDE) 내비게이션을 그대로 재사용한다.
            GuideStepPageSection(
                title = "",
                cardStyle = GuideStepCardStyle.INFO,
                rowItems = listOf(
                    GuideDetailItem(
                        id = GuideDetailItemId.HOSPITAL_LOCATION_CHECKIN_GUIDE,
                        iconResId = R.drawable.guide_hospital_location_map,
                        title = hospitalLocationCheckin.itemCardTitle,
                        description = hospitalLocationCheckin.itemCardDescription,
                        navigable = true,
                        descriptionHighlightPrefix = hospitalLocationCheckin.itemCardDescriptionHighlight
                    )
                )
            ),
            // 접수 절차(확인 순서)는 예전 03-03 화면의 4단계를 그대로 가져와 메모 카드 아래에 배치한다.
            GuideStepPageSection(
                title = strings.visitReceptionPreparation.sectionTitle,
                cardStyle = GuideStepCardStyle.MEMO,
                rowItems = visitReceptionPreparationItems(strings),
                orderStepsTitle = hospitalLocationCheckin.sectionTitle,
                orderSteps = listOf(
                    GuideOrderStep(title = hospitalLocationCheckin.step1Title, description = hospitalLocationCheckin.step1Description),
                    GuideOrderStep(title = hospitalLocationCheckin.step2Title, description = hospitalLocationCheckin.step2Description),
                    GuideOrderStep(title = hospitalLocationCheckin.step3Title, description = hospitalLocationCheckin.step3Description),
                    GuideOrderStep(title = hospitalLocationCheckin.step4Title, description = hospitalLocationCheckin.step4Description)
                )
            ),
            GuideStepPageSection(title = strings.medicalMaterialsPreparationTitle, cardStyle = GuideStepCardStyle.MEMO, rowItems = medicalRecordsTestResults.checklistItems)
        )
    )
}

private fun paymentReceiptPageContent(strings: GuideStrings): GuideStepPageContent {
    val totalCostCoverageCheck = totalCostCoverageCheckContent(strings)
    val paymentMethodCheck = paymentMethodCheckContent(strings)
    val receiptInsuranceDocuments = receiptInsuranceDocumentsContent(strings)
    return GuidePhase.PAYMENT_RECEIPT.toBasePageContent(
        strings = strings,
        heroResId = R.drawable.guide_step05_payment_billing_banner,
        sections = listOf(
            GuideStepPageSection(
                title = totalCostCoverageCheck.bannerTitle,
                cardStyle = GuideStepCardStyle.MEMO,
                rowItems = totalCostCoverageCheck.checklistItems,
                questionsTitle = totalCostCoverageCheck.questionsTitle,
                questions = totalCostCoverageCheck.questions
            ),
            GuideStepPageSection(title = paymentMethodCheck.bannerTitle, cardStyle = GuideStepCardStyle.MEMO, rowItems = paymentMethodCheck.checklistItems),
            GuideStepPageSection(
                title = receiptInsuranceDocuments.bannerTitle,
                cardStyle = GuideStepCardStyle.MEMO,
                rowItems = receiptInsuranceDocuments.checklistItems,
                questionsTitle = receiptInsuranceDocuments.questionsTitle,
                questions = receiptInsuranceDocuments.questions
            )
        )
    )
}

private fun aftercareReturnCheckPageContent(strings: GuideStrings): GuideStepPageContent {
    val medicationSchedule = medicationScheduleContent(strings)
    val postTreatmentPrecautions = postTreatmentPrecautionsContent(strings)
    val englishDocumentsResults = englishDocumentsResultsContent(strings)
    val airportDeparturePreparation = airportDeparturePreparationContent(strings)
    return GuidePhase.AFTERCARE_RETURN_CHECK.toBasePageContent(
        strings = strings,
        heroResId = R.drawable.guide_step06_recovery_return_preparation_banner,
        sections = listOf(
            GuideStepPageSection(title = medicationSchedule.bannerTitle, cardStyle = GuideStepCardStyle.MEMO, rowItems = medicationSchedule.checklistItems),
            GuideStepPageSection(title = postTreatmentPrecautions.bannerTitle, cardStyle = GuideStepCardStyle.MEMO, rowItems = postTreatmentPrecautions.checklistItems),
            GuideStepPageSection(title = englishDocumentsResults.bannerTitle, cardStyle = GuideStepCardStyle.MEMO, rowItems = englishDocumentsResults.checklistItems),
            GuideStepPageSection(title = airportDeparturePreparation.bannerTitle, cardStyle = GuideStepCardStyle.MEMO, rowItems = airportDeparturePreparation.checklistItems)
        )
    )
}
