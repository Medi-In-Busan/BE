package com.mediinbusan.app.feature.guide

import com.mediinbusan.app.R
import com.mediinbusan.app.core.i18n.GuideStrings

// GuideStepPageContentMapper(합본 페이지)가 섹션을 구성할 때 필요한 조각(checklistItems·bannerTitle·
// orderSteps·questions)만 가져다 쓴다. STEP 하위 항목별 leaf 단독 화면은 전부 클릭 경로가 사라져
// 삭제했으므로, 여기서는 배너 이미지·안내 배너 등 leaf 전용 필드를 더 이상 만들지 않는다.
// strings는 호출부에서 LocalAppStrings.current.guide를 그대로 넘겨받는다.

// S-06 STEP01 하위 "비자·입국 조건 확인" — 항목 3개 전부 공식 사이트 외부 링크(url)로 구성되는
// LINK 카드라 iconResId가 실제로 그려진다.
fun visaEntryCheckContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.visaEntryCheck
    return GuideStepDetailContent(
        bannerTitle = s.bannerTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "k_eta_official_site",
                iconResId = R.drawable.guide_global_guide,
                title = s.item1Title,
                description = s.item1Description,
                url = "https://www.k-eta.go.kr"
            ),
            GuideDetailItem(
                id = "korea_visa_portal",
                iconResId = R.drawable.guide_visa_document,
                title = s.item2Title,
                description = s.item2Description,
                url = "https://www.visa.go.kr"
            ),
            GuideDetailItem(
                id = "hikorea_residence_guide",
                iconResId = R.drawable.guide_passport_document,
                title = s.item3Title,
                description = s.item3Description,
                url = "https://www.hikorea.go.kr"
            )
        ),
        orderStepsTitle = s.orderStepsTitle,
        orderSteps = listOf(
            GuideOrderStep(title = s.order1Title, description = s.order1Description),
            GuideOrderStep(title = s.order2Title, description = s.order2Description),
            GuideOrderStep(title = s.order3Title, description = s.order3Description),
            GuideOrderStep(title = s.order4Title, description = s.order4Description)
        )
    )
}

// S-06 STEP01 하위 "보험·서류 준비" — 합본 페이지에서 MEMO 카드로만 렌더링되므로 iconResId는 안 그려진다.
fun insuranceDocumentsContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.insuranceDocuments
    return GuideStepDetailContent(
        bannerTitle = s.bannerTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "passport_copy",
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_passport_copy
            ),
            GuideDetailItem(
                id = "appointment_confirmation",
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_appointment_calendar
            ),
            GuideDetailItem(
                id = "medical_report_document",
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_medical_document
            ),
            GuideDetailItem(
                id = "medical_receipt",
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_medical_receipt,
                memoBackgroundResId = R.drawable.guide_memo5
            )
        )
    )
}

// S-06 STEP01 하위 "병원 문의 전 정보 정리" — MEMO 카드 전용.
fun hospitalInquiryContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.hospitalInquiry
    return GuideStepDetailContent(
        bannerTitle = s.bannerTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "passport_validity_check",
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_passport_copy
            ),
            GuideDetailItem(
                id = "stay_duration_visit_purpose",
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_visa_document
            ),
            GuideDetailItem(
                id = "contact_accommodation_info",
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_contact_accommodation_preparation
            ),
            GuideDetailItem(
                id = "hospital_info_schedule",
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_hospital_schedule_preparation
            )
        )
    )
}

// S-06 STEP02 하위 "문의 전 전달할 정보 정리" — MEMO 카드 전용.
fun preInquiryInformationContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.preInquiryInformation
    return GuideStepDetailContent(
        bannerTitle = s.bannerTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "desired_treatment_checklist",
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_preferred_treatment_exam,
                memoBackgroundResId = R.drawable.guide_memo7
            ),
            GuideDetailItem(
                id = "symptoms_medical_records",
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_symptom_diagnosis_info,
                memoBackgroundResId = R.drawable.guide_memo8
            ),
            GuideDetailItem(
                id = "preferred_visit_schedule",
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_preferred_medical_schedule,
                memoBackgroundResId = R.drawable.guide_memo6
            ),
            GuideDetailItem(
                id = "interpreter_language_support",
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_interpreter_language_support,
                memoBackgroundResId = R.drawable.guide_memo4
            )
        )
    )
}

// S-06 STEP03 하위 "기존 진단서·검사결과 준비" — MEMO 카드 전용. situational 섹션(2열 GRID)은
// 합본 페이지가 아예 안 가져다 쓰므로 그 항목들도 함께 제거했다.
fun medicalRecordsTestResultsContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.medicalRecordsTestResults
    return GuideStepDetailContent(
        bannerTitle = s.bannerTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "medical_report_or_opinion",
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_diagnosis_report,
                memoBackgroundResId = R.drawable.guide_memo5
            ),
            GuideDetailItem(
                id = "xray_test_result",
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_test_imaging_results,
                memoBackgroundResId = R.drawable.guide_memo4
            ),
            GuideDetailItem(
                id = "current_medication_info",
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_current_medications,
                memoBackgroundResId = R.drawable.guide_memo1
            ),
            GuideDetailItem(
                id = "allergy_or_underlying_condition",
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_allergy_medical_history,
                memoBackgroundResId = R.drawable.guide_memo4
            )
        )
    )
}

// S-06 STEP05 하위 "총 비용과 포함 항목 확인" — MEMO 카드 + questions 조합.
fun totalCostCoverageCheckContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.totalCostCoverageCheck
    return GuideStepDetailContent(
        bannerTitle = s.bannerTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "cost_examination_clipboard",
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_medical_treatment_fee,
                memoBackgroundResId = R.drawable.guide_memo1
            ),
            GuideDetailItem(
                id = "cost_current_medication_info",
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_medication_material_included,
                memoBackgroundResId = R.drawable.guide_memo4
            ),
            GuideDetailItem(
                id = "cost_caution_warning",
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_additional_cost_items,
                memoBackgroundResId = R.drawable.guide_memo6
            ),
            GuideDetailItem(
                id = "cost_medical_documents_folder",
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_deposit_prepayment,
                memoBackgroundResId = R.drawable.guide_memo7
            )
        ),
        questionsTitle = s.questionsTitle,
        questions = listOf(s.question1, s.question2, s.question3)
    )
}

// S-06 STEP05 하위 "결제 가능 수단 확인" — MEMO 카드 전용.
fun paymentMethodCheckContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.paymentMethodCheck
    return GuideStepDetailContent(
        bannerTitle = s.bannerTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "payment_currency",
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_payment_currency,
                memoBackgroundResId = R.drawable.guide_memo5
            ),
            GuideDetailItem(
                id = "payment_available_methods",
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_available_payment_methods,
                memoBackgroundResId = R.drawable.guide_memo8
            ),
            GuideDetailItem(
                id = "payment_card_limit_overseas_setting",
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_card_limit_overseas_payment,
                memoBackgroundResId = R.drawable.guide_memo4
            ),
            GuideDetailItem(
                id = "payment_insurance_direct_billing_gop",
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_insurance_direct_billing_gop,
                memoBackgroundResId = R.drawable.guide_memo3
            ),
            GuideDetailItem(
                id = "payment_location",
                title = s.item5Title,
                description = s.item5Description,
                memoIllustrationResId = R.drawable.guide_payment_location,
                memoBackgroundResId = R.drawable.guide_memo2
            )
        )
    )
}

// S-06 STEP05 하위 "영수증·보험 청구 서류 확인" — MEMO 카드 + questions 조합. situational 섹션은
// 합본 페이지가 안 가져다 쓰므로 제거했다.
fun receiptInsuranceDocumentsContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.receiptInsuranceDocuments
    return GuideStepDetailContent(
        bannerTitle = s.bannerTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "receipt_basic",
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_medical_receipt,
                memoBackgroundResId = R.drawable.guide_memo5
            ),
            GuideDetailItem(
                id = "receipt_detailed_statement",
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_medical_bill_details,
                memoBackgroundResId = R.drawable.guide_memo1
            ),
            GuideDetailItem(
                id = "receipt_insurance_claim_document",
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_insurance_claim_documents,
                memoBackgroundResId = R.drawable.guide_memo4
            )
        ),
        questionsTitle = s.questionsTitle,
        questions = listOf(s.question1, s.question2, s.question3)
    )
}

// S-06 STEP06 하위 "약 복용 방법 확인" — MEMO 카드 전용.
fun medicationScheduleContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.medicationSchedule
    return GuideStepDetailContent(
        bannerTitle = s.bannerTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "medication_name_purpose",
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_medication_name_purpose,
                memoBackgroundResId = R.drawable.guide_memo1
            ),
            GuideDetailItem(
                id = "medication_dosage_frequency",
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_medication_dosage_frequency,
                memoBackgroundResId = R.drawable.guide_memo4
            ),
            GuideDetailItem(
                id = "medication_timing_check",
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_medication_timing,
                memoBackgroundResId = R.drawable.guide_memo3
            ),
            GuideDetailItem(
                id = "medication_duration_storage",
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_medication_duration_storage,
                memoBackgroundResId = R.drawable.guide_memo2
            ),
            GuideDetailItem(
                id = "medication_missed_side_effects",
                title = s.item5Title,
                description = s.item5Description,
                memoIllustrationResId = R.drawable.guide_medication_missed_side_effects,
                memoBackgroundResId = R.drawable.guide_memo5
            )
        )
    )
}

// S-06 STEP06 하위 "진료 후 주의사항 확인" — MEMO 카드 전용.
fun postTreatmentPrecautionsContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.postTreatmentPrecautions
    return GuideStepDetailContent(
        bannerTitle = s.bannerTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "precaution_daily_activity",
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_recovery_daily_activity,
                memoBackgroundResId = R.drawable.guide_memo8
            ),
            GuideDetailItem(
                id = "precaution_restricted_activities",
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_activities_to_avoid,
                memoBackgroundResId = R.drawable.guide_memo4
            ),
            GuideDetailItem(
                id = "precaution_warning_symptoms",
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_emergency_warning_signs,
                memoBackgroundResId = R.drawable.guide_memo3
            ),
            GuideDetailItem(
                id = "precaution_followup_schedule",
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_followup_schedule,
                memoBackgroundResId = R.drawable.guide_memo7
            ),
            GuideDetailItem(
                id = "precaution_flight_travel",
                title = s.item5Title,
                description = s.item5Description,
                memoIllustrationResId = R.drawable.guide_flight_travel_precautions,
                memoBackgroundResId = R.drawable.guide_memo5
            )
        )
    )
}

// S-06 STEP06 하위 "영문 서류·검사결과 수령 확인" — MEMO 카드 전용.
fun englishDocumentsResultsContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.englishDocumentsResults
    return GuideStepDetailContent(
        bannerTitle = s.bannerTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "doc_english_medical_certificate",
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_english_medical_certificate,
                memoBackgroundResId = R.drawable.guide_memo1
            ),
            GuideDetailItem(
                id = "doc_test_results_imaging_files",
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_test_imaging_results,
                memoBackgroundResId = R.drawable.guide_memo4
            ),
            GuideDetailItem(
                id = "doc_prescription_discharge_summary",
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_prescription_discharge_summary,
                memoBackgroundResId = R.drawable.guide_memo3
            ),
            GuideDetailItem(
                id = "doc_issuance_time_fee",
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_document_issue_time_cost,
                memoBackgroundResId = R.drawable.guide_memo7
            ),
            GuideDetailItem(
                id = "doc_email_digital_receipt",
                title = s.item5Title,
                description = s.item5Description,
                memoIllustrationResId = R.drawable.guide_digital_document_delivery,
                memoBackgroundResId = R.drawable.guide_memo5
            )
        )
    )
}

// S-06 STEP06 하위 "귀국·공항 준비" — MEMO 카드 전용.
fun airportDeparturePreparationContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.airportDeparturePreparation
    return GuideStepDetailContent(
        bannerTitle = s.bannerTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "departure_medication_entry_rules",
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_medication_entry_rules,
                memoBackgroundResId = R.drawable.guide_memo8
            ),
            GuideDetailItem(
                id = "departure_carry_medical_documents",
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_carry_medical_documents,
                memoBackgroundResId = R.drawable.guide_memo4
            ),
            GuideDetailItem(
                id = "departure_carryon_medical_supplies",
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_carryon_medical_supplies,
                memoBackgroundResId = R.drawable.guide_memo3
            ),
            GuideDetailItem(
                id = "departure_storage_transport",
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_medication_storage_transport,
                memoBackgroundResId = R.drawable.guide_memo2
            ),
            GuideDetailItem(
                id = "departure_timezone_medication_schedule",
                title = s.item5Title,
                description = s.item5Description,
                memoIllustrationResId = R.drawable.guide_timezone_medication_schedule,
                memoBackgroundResId = R.drawable.guide_memo5
            )
        )
    )
}
