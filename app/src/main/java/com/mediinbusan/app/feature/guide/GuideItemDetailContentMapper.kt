package com.mediinbusan.app.feature.guide

import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.GuideCardLavenderBackground
import com.mediinbusan.app.core.designsystem.GuideCardPeachBackground
import com.mediinbusan.app.core.i18n.GuideStrings

// GuideStepDetailScreen이 쓰는 것과 동일한 GuideStepDetailContent 콘텐츠 모델을, STEP 하위 항목(leaf)
// 상세 화면에도 그대로 재사용한다. 화면 파일들은 이 콘텐츠를 GuideDetailTemplateScreen에 주입만 한다.
// strings는 호출부(Composable)에서 LocalAppStrings.current.guide를 그대로 넘겨받는다.

// S-06 STEP01 하위 "비자·입국 조건 확인" — 항목 3개 전부 공식 사이트 외부 링크(url)로 구성.
fun visaEntryCheckContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.visaEntryCheck
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_visa_entry_check_banner,
        bannerAspectRatio = 1672f / 941f,
        bannerStepLabel = "STEP 01",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
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
        ),
        noticeIconResId = R.drawable.ic_visa_guide_information,
        noticeText = s.noticeText
    )
}

// S-06 STEP01 하위 "보험·서류 준비"
fun insuranceDocumentsContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.insuranceDocuments
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_insurance_documents_banner,
        bannerAspectRatio = 1672f / 941f,
        bannerStepLabel = "STEP 01",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "passport_copy",
                iconResId = R.drawable.ic_passport_copy,
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_passport_copy
            ),
            GuideDetailItem(
                id = "appointment_confirmation",
                iconResId = R.drawable.ic_appointment_confirmation,
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_appointment_calendar
            ),
            GuideDetailItem(
                id = "medical_report_document",
                iconResId = R.drawable.ic_medical_report_document,
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_medical_document
            ),
            GuideDetailItem(
                id = "medical_receipt",
                iconResId = R.drawable.ic_medical_receipt,
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_medical_receipt,
                memoBackgroundResId = R.drawable.guide_memo5
            )
        ),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

// S-06 STEP01 하위 "병원 문의 전 정보 정리"
fun hospitalInquiryContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.hospitalInquiry
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_entry_preparation_banner,
        bannerAspectRatio = 1672f / 941f,
        bannerStepLabel = "STEP 01",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "passport_validity_check",
                iconResId = R.drawable.ic_passport_validity_check,
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_passport_copy
            ),
            GuideDetailItem(
                id = "stay_duration_visit_purpose",
                iconResId = R.drawable.ic_stay_duration_visit_purpose,
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_visa_document
            ),
            GuideDetailItem(
                id = "contact_accommodation_info",
                iconResId = R.drawable.ic_contact_accommodation_info,
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_contact_accommodation_preparation
            ),
            GuideDetailItem(
                id = "hospital_info_schedule",
                iconResId = R.drawable.ic_hospital_info_schedule,
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_hospital_schedule_preparation
            )
        ),
        noticeIconResId = R.drawable.ic_entry_preparation_tip,
        noticeText = s.noticeText
    )
}

// S-06 STEP02 하위 "문의 전 전달할 정보 정리"
fun preInquiryInformationContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.preInquiryInformation
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_pre_inquiry_information_banner,
        bannerAspectRatio = 1672f / 941f,
        bannerStepLabel = "STEP 02",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "desired_treatment_checklist",
                iconResId = R.drawable.ic_desired_treatment_checklist,
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_preferred_treatment_exam,
                memoBackgroundResId = R.drawable.guide_memo7
            ),
            GuideDetailItem(
                id = "symptoms_medical_records",
                iconResId = R.drawable.ic_symptoms_medical_records,
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_symptom_diagnosis_info,
                memoBackgroundResId = R.drawable.guide_memo8
            ),
            GuideDetailItem(
                id = "preferred_visit_schedule",
                iconResId = R.drawable.ic_preferred_visit_schedule,
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_preferred_medical_schedule,
                memoBackgroundResId = R.drawable.guide_memo6
            ),
            GuideDetailItem(
                id = "interpreter_language_support",
                iconResId = R.drawable.ic_interpretation_language_support,
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_interpreter_language_support,
                memoBackgroundResId = R.drawable.guide_memo4
            )
        ),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

// S-06 STEP03 하위 "여권·예약정보 준비"
fun passportReservationInfoContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.passportReservationInfo
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_passport_reservation_preparation_banner,
        bannerAspectRatio = 1672f / 941f,
        bannerStepLabel = "STEP 03",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "passport_identity_verification",
                iconResId = R.drawable.ic_passport_identity_verification,
                title = s.item1Title,
                description = s.item1Description
            ),
            GuideDetailItem(
                id = "reservation_appointment_confirmation",
                iconResId = R.drawable.ic_appointment_confirmation,
                title = s.item2Title,
                description = s.item2Description
            ),
            GuideDetailItem(
                id = "patient_name_verification",
                iconResId = R.drawable.ic_patient_name_verification,
                title = s.item3Title,
                description = s.item3Description
            ),
            GuideDetailItem(
                id = "contact_or_messenger",
                iconResId = R.drawable.ic_contact_or_messenger,
                title = s.item4Title,
                description = s.item4Description
            ),
            GuideDetailItem(
                id = "companion_check",
                iconResId = R.drawable.ic_companion_check,
                title = s.item5Title,
                description = s.item5Description
            ),
            GuideDetailItem(
                id = "expected_arrival_time",
                iconResId = R.drawable.ic_expected_arrival_time,
                title = s.item6Title,
                description = s.item6Description
            )
        ),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

// S-06 STEP03 하위 "기존 진단서·검사결과 준비" — 상황별 섹션은 2열 GRID 레이아웃 사용.
fun medicalRecordsTestResultsContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.medicalRecordsTestResults
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_medical_records_test_results_banner,
        bannerAspectRatio = 1536f / 1024f,
        bannerStepLabel = "STEP 03",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "medical_report_or_opinion",
                iconResId = R.drawable.ic_medical_report_or_opinion,
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_diagnosis_report,
                memoBackgroundResId = R.drawable.guide_memo5
            ),
            GuideDetailItem(
                id = "xray_test_result",
                iconResId = R.drawable.ic_xray_test_result,
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_test_imaging_results,
                memoBackgroundResId = R.drawable.guide_memo4
            ),
            GuideDetailItem(
                id = "current_medication_info",
                iconResId = R.drawable.ic_current_medication_info,
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_current_medications,
                memoBackgroundResId = R.drawable.guide_memo1
            ),
            GuideDetailItem(
                id = "allergy_or_underlying_condition",
                iconResId = R.drawable.ic_allergy_or_underlying_condition,
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_allergy_medical_history,
                memoBackgroundResId = R.drawable.guide_memo4
            )
        ),
        situationalTitle = s.situationalTitle,
        situationalItems = listOf(
            GuideDetailItem(
                id = "english_or_translation_material",
                iconResId = R.drawable.ic_english_or_translation_material,
                title = s.situational1Title,
                description = s.situational1Description,
                cardBackgroundColor = GuideCardPeachBackground
            ),
            GuideDetailItem(
                id = "digital_file_preparation",
                iconResId = R.drawable.ic_digital_file_preparation,
                title = s.situational2Title,
                description = s.situational2Description,
                cardBackgroundColor = GuideCardLavenderBackground
            )
        ),
        situationalLayout = GuideSituationalLayout.GRID,
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

// S-06 STEP05 하위 "총 비용과 포함 항목 확인" — situational 없이 checklist + questions 조합.
fun totalCostCoverageCheckContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.totalCostCoverageCheck
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_payment_billing_banner,
        // 원본 캔버스(1448x1086)는 위아래에 흰 여백이 커서 그대로 쓰면 그림이 작아 보인다 —
        // 실제 카드 그림 비율(1416x796)로 좁혀 Crop이 여백을 잘라내고 그림이 프레임을 채우게 한다.
        bannerAspectRatio = 1416f / 796f,
        bannerStepLabel = "STEP 05",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "cost_examination_clipboard",
                iconResId = R.drawable.ic_examination_name_clipboard,
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_medical_treatment_fee,
                memoBackgroundResId = R.drawable.guide_memo1
            ),
            GuideDetailItem(
                id = "cost_current_medication_info",
                iconResId = R.drawable.ic_current_medication_info,
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_medication_material_included,
                memoBackgroundResId = R.drawable.guide_memo4
            ),
            GuideDetailItem(
                id = "cost_caution_warning",
                iconResId = R.drawable.ic_caution_warning,
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_additional_cost_items,
                memoBackgroundResId = R.drawable.guide_memo6
            ),
            GuideDetailItem(
                id = "cost_medical_documents_folder",
                iconResId = R.drawable.ic_medical_documents_folder,
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_deposit_prepayment,
                memoBackgroundResId = R.drawable.guide_memo7
            )
        ),
        questionsTitle = s.questionsTitle,
        questions = listOf(s.question1, s.question2, s.question3),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

// S-06 STEP05 하위 "결제 가능 수단 확인"
fun paymentMethodCheckContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.paymentMethodCheck
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_available_payment_methods_banner,
        bannerAspectRatio = 1672f / 941f,
        bannerStepLabel = "STEP 05",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "payment_currency",
                iconResId = R.drawable.ic_estimated_cost_inquiry,
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_payment_currency,
                memoBackgroundResId = R.drawable.guide_memo5
            ),
            GuideDetailItem(
                id = "payment_available_methods",
                iconResId = R.drawable.ic_payment_method_check,
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_available_payment_methods,
                memoBackgroundResId = R.drawable.guide_memo8
            ),
            GuideDetailItem(
                id = "payment_card_limit_overseas_setting",
                iconResId = R.drawable.ic_insurance_document_check,
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_card_limit_overseas_payment,
                memoBackgroundResId = R.drawable.guide_memo4
            ),
            GuideDetailItem(
                id = "payment_insurance_direct_billing_gop",
                iconResId = R.drawable.ic_guide_payment_receipt,
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_insurance_direct_billing_gop,
                memoBackgroundResId = R.drawable.guide_memo3
            ),
            GuideDetailItem(
                id = "payment_location",
                iconResId = R.drawable.ic_hospital_location_map,
                title = s.item5Title,
                description = s.item5Description,
                memoIllustrationResId = R.drawable.guide_payment_location,
                memoBackgroundResId = R.drawable.guide_memo2
            )
        ),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

// S-06 STEP05 하위 "영수증·보험 청구 서류 확인" — checklist + situational + questions 세 섹션 모두 사용.
fun receiptInsuranceDocumentsContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.receiptInsuranceDocuments
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_receipt_insurance_documents_banner,
        // 원본 캔버스(1448x1086)는 위아래 흰 여백이 특히 커서 그대로 쓰면 그림이 작아 보인다 —
        // 실제 카드 그림 비율(1400x672)로 좁혀 Crop이 여백을 잘라내고 그림이 프레임을 채우게 한다.
        bannerAspectRatio = 1400f / 672f,
        bannerStepLabel = "STEP 05",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "receipt_basic",
                iconResId = R.drawable.ic_medical_receipt,
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_medical_receipt,
                memoBackgroundResId = R.drawable.guide_memo5
            ),
            GuideDetailItem(
                id = "receipt_detailed_statement",
                iconResId = R.drawable.ic_results_document,
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_medical_bill_details,
                memoBackgroundResId = R.drawable.guide_memo1
            ),
            GuideDetailItem(
                id = "receipt_insurance_claim_document",
                iconResId = R.drawable.ic_examination_name_clipboard,
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_insurance_claim_documents,
                memoBackgroundResId = R.drawable.guide_memo4
            )
        ),
        situationalTitle = s.situationalTitle,
        situationalItems = listOf(
            GuideDetailItem(
                id = "receipt_insurer_form",
                iconResId = R.drawable.ic_insurance_document_check,
                title = s.situational1Title,
                description = s.situational1Description
            ),
            GuideDetailItem(
                id = "receipt_cosmetic_tax_refund",
                iconResId = R.drawable.ic_beauty_treatment_tax_refund_eligibility,
                title = s.situational2Title,
                description = s.situational2Description
            )
        ),
        questionsTitle = s.questionsTitle,
        questions = listOf(s.question1, s.question2, s.question3),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

// S-06 STEP06 하위 "약 복용 방법 확인"
fun medicationScheduleContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.medicationSchedule
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_post_treatment_travel_preparation_banner,
        bannerAspectRatio = 1672f / 941f,
        bannerStepLabel = "STEP 06",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "medication_name_purpose",
                iconResId = R.drawable.ic_current_medication_info,
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_medication_name_purpose,
                memoBackgroundResId = R.drawable.guide_memo1
            ),
            GuideDetailItem(
                id = "medication_dosage_frequency",
                iconResId = R.drawable.ic_current_medication_info,
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_medication_dosage_frequency,
                memoBackgroundResId = R.drawable.guide_memo4
            ),
            GuideDetailItem(
                id = "medication_timing_check",
                iconResId = R.drawable.ic_expected_arrival_time,
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_medication_timing,
                memoBackgroundResId = R.drawable.guide_memo3
            ),
            GuideDetailItem(
                id = "medication_duration_storage",
                iconResId = R.drawable.ic_return_date_calendar,
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_medication_duration_storage,
                memoBackgroundResId = R.drawable.guide_memo2
            ),
            GuideDetailItem(
                id = "medication_missed_side_effects",
                iconResId = R.drawable.ic_caution_warning,
                title = s.item5Title,
                description = s.item5Description,
                memoIllustrationResId = R.drawable.guide_medication_missed_side_effects,
                memoBackgroundResId = R.drawable.guide_memo5
            )
        ),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

// S-06 STEP06 하위 "진료 후 주의사항 확인"
fun postTreatmentPrecautionsContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.postTreatmentPrecautions
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_post_treatment_precautions,
        bannerAspectRatio = 1672f / 941f,
        bannerStepLabel = "STEP 06",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "precaution_daily_activity",
                iconResId = R.drawable.ic_meal_shower_exercise_time,
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_recovery_daily_activity,
                memoBackgroundResId = R.drawable.guide_memo8
            ),
            GuideDetailItem(
                id = "precaution_restricted_activities",
                iconResId = R.drawable.ic_restricted_activities,
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_activities_to_avoid,
                memoBackgroundResId = R.drawable.guide_memo4
            ),
            GuideDetailItem(
                id = "precaution_warning_symptoms",
                iconResId = R.drawable.ic_warning_symptoms,
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_emergency_warning_signs,
                memoBackgroundResId = R.drawable.guide_memo3
            ),
            GuideDetailItem(
                id = "precaution_followup_schedule",
                iconResId = R.drawable.ic_followup_visit_needed,
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_followup_schedule,
                memoBackgroundResId = R.drawable.guide_memo7
            ),
            GuideDetailItem(
                id = "precaution_flight_travel",
                iconResId = R.drawable.ic_long_flight_seat,
                title = s.item5Title,
                description = s.item5Description,
                memoIllustrationResId = R.drawable.guide_flight_travel_precautions,
                memoBackgroundResId = R.drawable.guide_memo5
            )
        ),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

// S-06 STEP06 하위 "영문 서류·검사결과 수령 확인"
fun englishDocumentsResultsContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.englishDocumentsResults
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_english_documents_test_results_banner,
        bannerAspectRatio = 1491f / 1055f,
        bannerStepLabel = "STEP 06",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "doc_english_medical_certificate",
                iconResId = R.drawable.ic_english_medical_certificate,
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_english_medical_certificate,
                memoBackgroundResId = R.drawable.guide_memo1
            ),
            GuideDetailItem(
                id = "doc_test_results_imaging_files",
                iconResId = R.drawable.ic_test_results_imaging_files,
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_test_imaging_results,
                memoBackgroundResId = R.drawable.guide_memo4
            ),
            GuideDetailItem(
                id = "doc_prescription_discharge_summary",
                iconResId = R.drawable.ic_current_medication_info,
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_prescription_discharge_summary,
                memoBackgroundResId = R.drawable.guide_memo3
            ),
            GuideDetailItem(
                id = "doc_issuance_time_fee",
                iconResId = R.drawable.ic_issuance_time_fee,
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_document_issue_time_cost,
                memoBackgroundResId = R.drawable.guide_memo7
            ),
            GuideDetailItem(
                id = "doc_email_digital_receipt",
                iconResId = R.drawable.ic_email_file_receipt_available,
                title = s.item5Title,
                description = s.item5Description,
                memoIllustrationResId = R.drawable.guide_digital_document_delivery,
                memoBackgroundResId = R.drawable.guide_memo5
            )
        ),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}

// S-06 STEP06 하위 "귀국·공항 준비"
fun airportDeparturePreparationContent(strings: GuideStrings): GuideStepDetailContent {
    val s = strings.airportDeparturePreparation
    return GuideStepDetailContent(
        bannerResId = R.drawable.img_post_treatment_travel_preparation_banner,
        bannerAspectRatio = 1672f / 941f,
        bannerStepLabel = "STEP 06",
        bannerTitle = s.bannerTitle,
        bannerSubtitle = s.bannerSubtitle,
        checklistTitle = s.checklistTitle,
        checklistItems = listOf(
            GuideDetailItem(
                id = "departure_medication_entry_rules",
                iconResId = R.drawable.ic_restricted_medicine_check,
                title = s.item1Title,
                description = s.item1Description,
                memoIllustrationResId = R.drawable.guide_medication_entry_rules,
                memoBackgroundResId = R.drawable.guide_memo8
            ),
            GuideDetailItem(
                id = "departure_carry_medical_documents",
                iconResId = R.drawable.ic_carry_on_medical_documents,
                title = s.item2Title,
                description = s.item2Description,
                memoIllustrationResId = R.drawable.guide_carry_medical_documents,
                memoBackgroundResId = R.drawable.guide_memo4
            ),
            GuideDetailItem(
                id = "departure_carryon_medical_supplies",
                iconResId = R.drawable.ic_liquid_medicine_medical_supplies,
                title = s.item3Title,
                description = s.item3Description,
                memoIllustrationResId = R.drawable.guide_carryon_medical_supplies,
                memoBackgroundResId = R.drawable.guide_memo3
            ),
            GuideDetailItem(
                id = "departure_storage_transport",
                iconResId = R.drawable.ic_caution_warning,
                title = s.item4Title,
                description = s.item4Description,
                memoIllustrationResId = R.drawable.guide_medication_storage_transport,
                memoBackgroundResId = R.drawable.guide_memo2
            ),
            GuideDetailItem(
                id = "departure_timezone_medication_schedule",
                iconResId = R.drawable.ic_medication_schedule,
                title = s.item5Title,
                description = s.item5Description,
                memoIllustrationResId = R.drawable.guide_timezone_medication_schedule,
                memoBackgroundResId = R.drawable.guide_memo5
            )
        ),
        noticeIconResId = R.drawable.ic_guide_information,
        noticeText = s.noticeText
    )
}