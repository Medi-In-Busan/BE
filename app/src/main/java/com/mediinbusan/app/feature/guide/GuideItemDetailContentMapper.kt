package com.mediinbusan.app.feature.guide

import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.GuideCardLavenderBackground
import com.mediinbusan.app.core.designsystem.GuideCardPeachBackground

// GuideStepDetailScreen이 쓰는 것과 동일한 GuideStepDetailContent 콘텐츠 모델을, STEP 하위 항목(leaf)
// 상세 화면에도 그대로 재사용한다. 화면 파일들은 이 콘텐츠를 GuideDetailTemplateScreen에 주입만 한다.

// S-06 STEP01 하위 "비자·입국 조건 확인" — 항목 3개 전부 공식 사이트 외부 링크(url)로 구성.
val visaEntryCheckContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_visa_entry_check_banner,
    bannerAspectRatio = 1672f / 941f,
    bannerStepLabel = "STEP 01",
    bannerTitle = "비자와 입국 조건을 먼저 확인하세요.",
    bannerSubtitle = "국적과 체류기간, 방문 목적에 따라 비자 또는 K-ETA가 필요할 수 있어요.",
    checklistItems = listOf(
        GuideDetailItem(
            id = "k_eta_official_site",
            iconResId = R.drawable.ic_k_eta_official_site,
            title = "K-ETA 공식 사이트",
            description = "무비자 입국 대상국이라면 K-ETA 사전 승인이 필요해요.",
            url = "https://www.k-eta.go.kr"
        ),
        GuideDetailItem(
            id = "korea_visa_portal",
            iconResId = R.drawable.ic_korea_visa_portal,
            title = "대한민국 비자포털",
            description = "체류 목적별 비자 종류와 신청 절차를 확인할 수 있어요.",
            url = "https://www.visa.go.kr"
        ),
        GuideDetailItem(
            id = "hikorea_residence_guide",
            iconResId = R.drawable.ic_hikorea_residence_guide,
            title = "HiKorea 체류 안내",
            description = "입국 후 체류 자격, 외국인등록 등 안내를 확인할 수 있어요.",
            url = "https://www.hikorea.go.kr"
        )
    ),
    noticeIconResId = R.drawable.ic_visa_guide_information,
    noticeText = "본 안내는 비자 발급을 보장하지 않습니다. 정확한 자격 요건은 각 공식 사이트에서 반드시 확인해 주세요."
)

// S-06 STEP01 하위 "보험·서류 준비"
val insuranceDocumentsContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_insurance_documents_banner,
    bannerAspectRatio = 1672f / 941f,
    bannerStepLabel = "STEP 01",
    bannerTitle = "보험과 서류를 미리 준비하면 진료와 보상이 더 쉬워져요.",
    bannerSubtitle = "치료비 보장 범위, 필요 서류, 청구 절차를 미리 확인하고 준비해 주세요.",
    checklistTitle = "준비 서류 체크리스트",
    checklistItems = listOf(
        GuideDetailItem(
            id = "passport_copy",
            iconResId = R.drawable.ic_passport_copy,
            title = "여권 사본",
            description = "출입국 심사와 병원 접수 시 필요할 수 있어요."
        ),
        GuideDetailItem(
            id = "appointment_confirmation",
            iconResId = R.drawable.ic_appointment_confirmation,
            title = "진료 예약 확인서",
            description = "예약한 병원의 예약 확인서나 문자를 준비해 두세요."
        ),
        GuideDetailItem(
            id = "medical_report_document",
            iconResId = R.drawable.ic_medical_report_document,
            title = "진단서 또는 소견서",
            description = "기존에 받은 진단서나 소견서가 있다면 함께 준비하세요."
        ),
        GuideDetailItem(
            id = "medical_receipt",
            iconResId = R.drawable.ic_medical_receipt,
            title = "진료비 영수증",
            description = "보험 청구나 세금 환급에 필요할 수 있어 보관해 두세요."
        )
    ),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "본 안내는 보험 보장 범위를 보장하지 않습니다. 정확한 보장 내용은 가입한 보험사에 확인해 주세요."
)

// S-06 STEP01 하위 "병원 문의 전 정보 정리"
val hospitalInquiryContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_entry_preparation_banner,
    bannerAspectRatio = 1672f / 941f,
    bannerStepLabel = "STEP 01",
    bannerTitle = "입국 전 필요한 준비를 미리 챙겨두세요.",
    bannerSubtitle = "여권, 체류기간, 방문 목적, 연락처 등 기본 정보를 미리 정리하면 입국과 병원 방문이 편리해요.",
    checklistTitle = "정보 정리 체크리스트",
    checklistItems = listOf(
        GuideDetailItem(
            id = "passport_validity_check",
            iconResId = R.drawable.ic_passport_validity_check,
            title = "여권 유효기간 확인",
            description = "입국일 기준 여권 유효기간이 충분한지 확인하세요."
        ),
        GuideDetailItem(
            id = "stay_duration_visit_purpose",
            iconResId = R.drawable.ic_stay_duration_visit_purpose,
            title = "체류기간 및 방문 목적 정리",
            description = "예상 체류기간과 방문 목적을 정리해두면 문의가 수월해요."
        ),
        GuideDetailItem(
            id = "contact_accommodation_info",
            iconResId = R.drawable.ic_contact_accommodation_info,
            title = "연락처 및 숙소 정보 준비",
            description = "현지 연락처와 숙소 정보를 미리 준비해두세요."
        ),
        GuideDetailItem(
            id = "hospital_info_schedule",
            iconResId = R.drawable.ic_hospital_info_schedule,
            title = "병원 정보 및 일정 정리",
            description = "문의할 병원명과 희망 일정을 정리해두세요."
        )
    ),
    noticeIconResId = R.drawable.ic_entry_preparation_tip,
    noticeText = "문의 전 정보를 미리 정리해두면 병원과의 소통이 더 원활해져요."
)

// S-06 STEP02 하위 "문의 전 전달할 정보 정리"
val preInquiryInformationContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_pre_inquiry_information_banner,
    bannerAspectRatio = 1672f / 941f,
    bannerStepLabel = "STEP 02",
    bannerTitle = "문의 전 전달할 정보 정리",
    bannerSubtitle = "희망 진료와 증상, 방문 시기 등 핵심 정보만 정리해 두면 병원이 더 정확하게 안내할 수 있어요.",
    checklistTitle = "전달 정보 체크리스트",
    checklistItems = listOf(
        GuideDetailItem(
            id = "desired_treatment_checklist",
            iconResId = R.drawable.ic_desired_treatment_checklist,
            title = "희망 진료·검사 내용",
            description = "받고 싶은 진료나 검사 항목을 구체적으로 정리하세요."
        ),
        GuideDetailItem(
            id = "symptoms_medical_records",
            iconResId = R.drawable.ic_symptoms_medical_records,
            title = "현재 증상과 기존 자료",
            description = "현재 증상과 기존 진단서·검사 결과가 있다면 함께 정리하세요."
        ),
        GuideDetailItem(
            id = "preferred_visit_schedule",
            iconResId = R.drawable.ic_preferred_visit_schedule,
            title = "방문 희망 시기",
            description = "방문을 원하는 대략적인 날짜나 기간을 정리하세요."
        ),
        GuideDetailItem(
            id = "basic_personal_information",
            iconResId = R.drawable.ic_basic_personal_information,
            title = "기본 인적 정보",
            description = "이름, 생년월일, 국적 등 기본 정보를 준비하세요."
        ),
        GuideDetailItem(
            id = "estimated_cost_inquiry",
            iconResId = R.drawable.ic_estimated_cost_inquiry,
            title = "예상 비용 문의 여부",
            description = "예상 비용을 함께 문의할지 미리 정리해두세요."
        ),
        GuideDetailItem(
            id = "english_document_requirement",
            iconResId = R.drawable.ic_english_document_requirement,
            title = "영문 서류 필요 여부",
            description = "귀국 후 필요한 영문 서류가 있는지 미리 확인하세요."
        )
    ),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "정리한 정보는 병원 문의 시 그대로 전달하면 상담이 더 빠르고 정확해져요."
)

// S-06 STEP03 하위 "여권·예약정보 준비"
val passportReservationInfoContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_passport_reservation_preparation_banner,
    bannerAspectRatio = 1672f / 941f,
    bannerStepLabel = "STEP 03",
    bannerTitle = "여권·예약정보 준비",
    bannerSubtitle = "여권과 예약 정보를 미리 준비하면 접수가 더 빨라져요.",
    checklistTitle = "준비물 체크리스트",
    checklistItems = listOf(
        GuideDetailItem(
            id = "passport_identity_verification",
            iconResId = R.drawable.ic_passport_identity_verification,
            title = "여권 신원 확인",
            description = "여권 또는 외국인등록증으로 신원을 확인할 수 있도록 준비하세요."
        ),
        GuideDetailItem(
            id = "reservation_appointment_confirmation",
            iconResId = R.drawable.ic_appointment_confirmation,
            title = "예약 확인",
            description = "예약 일시와 진료과를 다시 한번 확인하세요."
        ),
        GuideDetailItem(
            id = "patient_name_verification",
            iconResId = R.drawable.ic_patient_name_verification,
            title = "환자 성명 확인",
            description = "여권상 영문 성명과 예약자 정보가 일치하는지 확인하세요."
        ),
        GuideDetailItem(
            id = "contact_or_messenger",
            iconResId = R.drawable.ic_contact_or_messenger,
            title = "연락처 확인",
            description = "병원에서 연락 가능한 전화번호나 메신저를 준비하세요."
        ),
        GuideDetailItem(
            id = "companion_check",
            iconResId = R.drawable.ic_companion_check,
            title = "동반자 확인",
            description = "동반자가 있다면 인원과 관계를 미리 정리하세요."
        ),
        GuideDetailItem(
            id = "expected_arrival_time",
            iconResId = R.drawable.ic_expected_arrival_time,
            title = "도착 예정 시간",
            description = "병원 도착 예정 시간을 미리 확인해두면 접수가 수월해요."
        )
    ),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "정리한 정보는 접수 시 안내 직원에게 전달하면 더 빠르게 도와드려요."
)

// S-06 STEP03 하위 "기존 진단서·검사결과 준비" — 상황별 섹션은 2열 GRID 레이아웃 사용.
val medicalRecordsTestResultsContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_medical_records_test_results_banner,
    bannerAspectRatio = 1536f / 1024f,
    bannerStepLabel = "STEP 03",
    bannerTitle = "기존 진단서·검사결과 준비",
    bannerSubtitle = "기존 자료를 미리 준비하면 의료진이 상태를 더 빠르게 이해할 수 있어요.",
    checklistTitle = "준비하면 좋은 자료",
    checklistItems = listOf(
        GuideDetailItem(
            id = "medical_report_or_opinion",
            iconResId = R.drawable.ic_medical_report_or_opinion,
            title = "기존 진단서 또는 소견서",
            description = "받았던 진단 내용이나 소견서를 준비하세요."
        ),
        GuideDetailItem(
            id = "xray_test_result",
            iconResId = R.drawable.ic_xray_test_result,
            title = "검사결과 또는 영상자료",
            description = "혈액검사, 영상검사 결과가 있다면 함께 준비하세요."
        ),
        GuideDetailItem(
            id = "current_medication_info",
            iconResId = R.drawable.ic_current_medication_info,
            title = "복용 중인 약 정보",
            description = "현재 복용 중인 약 이름이나 처방 내용을 정리하세요."
        ),
        GuideDetailItem(
            id = "allergy_or_underlying_condition",
            iconResId = R.drawable.ic_allergy_or_underlying_condition,
            title = "알레르기·기저질환 정보",
            description = "알레르기나 중요한 건강정보가 있다면 함께 전달하세요."
        )
    ),
    situationalTitle = "상황에 따라 준비하세요",
    situationalItems = listOf(
        GuideDetailItem(
            id = "english_or_translation_material",
            iconResId = R.drawable.ic_english_or_translation_material,
            title = "영문 또는 번역 자료",
            description = "있는 경우 함께 가져가면 설명이 더 쉬워져요.",
            cardBackgroundColor = GuideCardPeachBackground
        ),
        GuideDetailItem(
            id = "digital_file_preparation",
            iconResId = R.drawable.ic_digital_file_preparation,
            title = "디지털 파일 준비",
            description = "사진이나 PDF 파일도 함께 준비해 두세요.",
            cardBackgroundColor = GuideCardLavenderBackground
        )
    ),
    situationalLayout = GuideSituationalLayout.GRID,
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "모든 자료가 꼭 필요한 것은 아니며 병원이 요청한 자료를 우선 준비해 주세요."
)

// S-06 STEP05 하위 "총 비용과 포함 항목 확인" — situational 없이 checklist + questions 조합.
val totalCostCoverageCheckContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_payment_billing_banner,
    bannerAspectRatio = 1448f / 1086f,
    bannerStepLabel = "STEP 05",
    bannerTitle = "총 비용과 포함 항목 확인",
    bannerSubtitle = "결제 전, 상담료·검사비·시술비·약제비 등 총 비용과 포함·불포함 항목을 꼼꼼히 확인하세요.",
    checklistTitle = "먼저 확인하세요",
    checklistItems = listOf(
        GuideDetailItem(
            id = "cost_examination_clipboard",
            iconResId = R.drawable.ic_examination_name_clipboard,
            title = "진료·검사·시술비",
            description = "상담료, 검사비, 시술비 등 기본 비용 항목을 확인하세요."
        ),
        GuideDetailItem(
            id = "cost_current_medication_info",
            iconResId = R.drawable.ic_current_medication_info,
            title = "약제비·재료비 포함 여부",
            description = "약값, 소모품, 재료비가 총 비용에 포함되는지 확인하세요."
        ),
        GuideDetailItem(
            id = "cost_caution_warning",
            iconResId = R.drawable.ic_caution_warning,
            title = "추가 비용 발생 가능 항목",
            description = "추가 검사, 마취, 입원, 병실 이용 시 비용이 달라질 수 있어요."
        ),
        GuideDetailItem(
            id = "cost_medical_documents_folder",
            iconResId = R.drawable.ic_medical_documents_folder,
            title = "보증금·선납금 필요 여부",
            description = "입원이나 예약 조건에 따라 선납금이 필요한 경우만 확인하세요."
        )
    ),
    questionsTitle = "이렇게 물어보세요",
    questions = listOf(
        "총 비용에 포함되지 않는 항목이 있나요?",
        "추가 검사나 입원이 생기면 비용이 얼마나 달라지나요?",
        "보증금 또는 선납금이 필요한가요?"
    ),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "최종 비용은 진료 내용에 따라 달라질 수 있으니 병원 안내를 기준으로 확인해 주세요."
)

// S-06 STEP05 하위 "결제 가능 수단 확인"
val paymentMethodCheckContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_available_payment_methods_banner,
    bannerAspectRatio = 1672f / 941f,
    bannerStepLabel = "STEP 05",
    bannerTitle = "결제 가능 수단 확인",
    bannerSubtitle = "치료 전이나 퇴원 전에 실제로 사용 가능한 결제 수단을 미리 확인해 보세요.",
    checklistTitle = "결제 전에 체크하세요",
    checklistItems = listOf(
        GuideDetailItem(
            id = "payment_krw_basis",
            iconResId = R.drawable.ic_estimated_cost_inquiry,
            title = "원화 기준 결제 여부",
            description = "많은 병원이 원화 기준으로 결제하니 실제 청구 통화를 확인하세요."
        ),
        GuideDetailItem(
            id = "payment_overseas_card",
            iconResId = R.drawable.ic_payment_method_check,
            title = "해외 카드 사용 가능 여부",
            description = "Visa, Mastercard, Amex 등 사용 가능한 해외 카드 브랜드를 확인하세요."
        ),
        GuideDetailItem(
            id = "payment_cash_transfer",
            iconResId = R.drawable.ic_medical_documents_folder,
            title = "현금·계좌이체·송금 가능 여부",
            description = "카드 외에 현금, 계좌이체, 해외송금 결제가 가능한지 확인하세요."
        ),
        GuideDetailItem(
            id = "payment_deposit_method",
            iconResId = R.drawable.ic_medical_receipt,
            title = "보증금·선납금 결제 방식",
            description = "선납금이 필요한 경우 어떤 수단으로 결제해야 하는지 확인하세요."
        )
    ),
    situationalTitle = "함께 확인하면 좋아요",
    situationalItems = listOf(
        GuideDetailItem(
            id = "payment_card_limit_block",
            iconResId = R.drawable.ic_insurance_document_check,
            title = "카드 한도·해외결제 차단",
            description = "결제 전에 카드 한도와 해외 사용 차단 여부를 미리 확인하세요."
        ),
        GuideDetailItem(
            id = "payment_onsite_location",
            iconResId = R.drawable.ic_hospital_location_map,
            title = "현장 결제 위치",
            description = "응급실, 외래, 입원 등 어디에서 수납하는지 확인하면 더 편리해요."
        )
    ),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "병원마다 가능한 결제 수단이 다르니 실제 수납 전 다시 확인해 주세요."
)

// S-06 STEP05 하위 "영수증·보험 청구 서류 확인" — checklist + situational + questions 세 섹션 모두 사용.
val receiptInsuranceDocumentsContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_receipt_insurance_documents_banner,
    bannerAspectRatio = 1448f / 1086f,
    bannerStepLabel = "STEP 05",
    bannerTitle = "영수증·보험 청구 서류 확인",
    bannerSubtitle = "결제 후 필요한 서류를 미리 확인하고 발급 가능 여부를 체크하면 보험 청구와 환급 절차가 더 쉬워집니다.",
    checklistTitle = "받아야 할 서류",
    checklistItems = listOf(
        GuideDetailItem(
            id = "receipt_basic",
            iconResId = R.drawable.ic_medical_receipt,
            title = "영수증",
            description = "결제 금액을 확인할 수 있는 기본 영수증을 받아두세요."
        ),
        GuideDetailItem(
            id = "receipt_detailed_statement",
            iconResId = R.drawable.ic_results_document,
            title = "진료비 세부내역서",
            description = "비용 항목이 자세히 적힌 서류가 필요한지 확인하세요."
        ),
        GuideDetailItem(
            id = "receipt_insurance_claim_document",
            iconResId = R.drawable.ic_examination_name_clipboard,
            title = "보험 청구용 서류",
            description = "보험사에서 요구하는 진단서, 소견서, 확인서 여부를 확인하세요."
        ),
        GuideDetailItem(
            id = "receipt_english_document_availability",
            iconResId = R.drawable.ic_english_documents_availability,
            title = "영문 서류 발급 가능 여부",
            description = "본국 제출이 필요한 경우 영문 발급 가능 여부를 함께 확인하세요."
        )
    ),
    situationalTitle = "상황별 확인",
    situationalItems = listOf(
        GuideDetailItem(
            id = "receipt_insurer_form",
            iconResId = R.drawable.ic_insurance_document_check,
            title = "보험사 요구 양식 여부",
            description = "보험사 전용 양식이나 추가 증빙 서류가 필요한 경우만 확인하세요."
        ),
        GuideDetailItem(
            id = "receipt_cosmetic_tax_refund",
            iconResId = R.drawable.ic_beauty_treatment_tax_refund_eligibility,
            title = "미용시술 세금 환급 가능 여부",
            description = "피부·미용 시술을 받은 경우에만 세금 환급 가능 여부를 확인하세요."
        )
    ),
    questionsTitle = "이렇게 요청해 보세요",
    questions = listOf(
        "영수증과 진료비 세부내역서를 받을 수 있을까요?",
        "보험 청구에 필요한 서류를 함께 발급받을 수 있나요?",
        "영문 서류 발급이 가능하면 언제 받을 수 있나요?"
    ),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "필요 서류는 보험사와 병원 기준이 다를 수 있으니 둘 다 확인해 주세요."
)

// S-06 STEP06 하위 "약 복용 방법 확인"
val medicationScheduleContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_post_treatment_travel_preparation_banner,
    bannerAspectRatio = 1672f / 941f,
    bannerStepLabel = "STEP 06",
    bannerTitle = "약 복용 방법 확인",
    bannerSubtitle = "약 이름과 복용 시간, 복용 기간을 미리 확인하면 안전하게 복용할 수 있어요.",
    checklistTitle = "복용 전 체크리스트",
    checklistItems = listOf(
        GuideDetailItem(
            id = "medication_name_check",
            iconResId = R.drawable.ic_current_medication_info,
            title = "약 이름 확인",
            description = "처방받은 약 이름과 성분을 확인해두세요."
        ),
        GuideDetailItem(
            id = "medication_timing_check",
            iconResId = R.drawable.ic_expected_arrival_time,
            title = "복용 시간·식전후 여부 확인",
            description = "정해진 복용 시간과 식전·식후 여부를 놓치지 않도록 확인하세요."
        ),
        GuideDetailItem(
            id = "medication_duration_check",
            iconResId = R.drawable.ic_return_date_calendar,
            title = "복용 기간 확인",
            description = "며칠간 복용해야 하는지, 언제까지 복용하는지 확인하세요."
        ),
        GuideDetailItem(
            id = "medication_storage_caution",
            iconResId = R.drawable.ic_caution_warning,
            title = "보관 및 주의사항 확인",
            description = "보관 방법과 함께 복용하면 안 되는 약이 있는지 확인하세요."
        )
    ),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "정확한 복용법은 처방전과 약사 안내를 기준으로 다시 확인해 주세요."
)

// S-06 STEP06 하위 "진료 후 주의사항 확인"
val postTreatmentPrecautionsContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_post_treatment_precautions,
    bannerAspectRatio = 1672f / 941f,
    bannerStepLabel = "STEP 06",
    bannerTitle = "진료 후 주의사항 확인",
    bannerSubtitle = "식사, 샤워, 운동 가능 시점과 이상 증상 기준을 미리 확인하세요.",
    checklistTitle = "회복 중 체크리스트",
    checklistItems = listOf(
        GuideDetailItem(
            id = "precaution_meal_time",
            iconResId = R.drawable.ic_meal_shower_exercise_time,
            title = "식사 가능 시점 확인",
            description = "금식 해제 시점과 먹어도 되는 음식을 확인하세요."
        ),
        GuideDetailItem(
            id = "precaution_restricted_activities",
            iconResId = R.drawable.ic_restricted_activities,
            title = "샤워·운동 제한 확인",
            description = "샤워, 사우나, 운동이 가능한 시점을 확인하세요."
        ),
        GuideDetailItem(
            id = "precaution_warning_symptoms",
            iconResId = R.drawable.ic_warning_symptoms,
            title = "이상 증상 기준 확인",
            description = "발열, 출혈 등 병원에 연락해야 하는 증상 기준을 확인하세요."
        ),
        GuideDetailItem(
            id = "precaution_followup_visit",
            iconResId = R.drawable.ic_followup_visit_needed,
            title = "재방문 필요 여부 확인",
            description = "추가 진료나 재방문이 필요한지 확인하세요."
        )
    ),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "회복 속도는 개인차가 있으니 의료진의 안내를 우선해 주세요."
)

// S-06 STEP06 하위 "영문 서류·검사결과 수령 확인"
val englishDocumentsResultsContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_english_documents_test_results_banner,
    bannerAspectRatio = 1491f / 1055f,
    bannerStepLabel = "STEP 06",
    bannerTitle = "영문 서류·검사결과 수령 확인",
    bannerSubtitle = "귀국 후 제출이 필요한 서류와 검사결과 수령 여부를 미리 확인하세요.",
    checklistTitle = "수령 전 체크리스트",
    checklistItems = listOf(
        GuideDetailItem(
            id = "doc_english_medical_certificate",
            iconResId = R.drawable.ic_english_medical_certificate,
            title = "영문 진단서·소견서 수령",
            description = "영문으로 발급되는 진단서나 소견서를 받았는지 확인하세요."
        ),
        GuideDetailItem(
            id = "doc_test_results_imaging_files",
            iconResId = R.drawable.ic_test_results_imaging_files,
            title = "검사결과·영상자료 수령",
            description = "검사결과와 영상자료를 파일이나 CD로 받을 수 있는지 확인하세요."
        ),
        GuideDetailItem(
            id = "doc_issuance_time_fee",
            iconResId = R.drawable.ic_issuance_time_fee,
            title = "발급 소요시간·비용 확인",
            description = "서류 발급에 걸리는 시간과 추가 비용이 있는지 확인하세요."
        ),
        GuideDetailItem(
            id = "doc_original_copy_check",
            iconResId = R.drawable.ic_original_copy_document_check,
            title = "원본·사본 서류 확인",
            description = "원본과 사본 중 어떤 서류가 필요한지 확인하세요."
        ),
        GuideDetailItem(
            id = "doc_airport_pickup_complete",
            iconResId = R.drawable.ic_airport_departure_pickup_complete,
            title = "공항 출국 전 수령 완료 확인",
            description = "출국 전에 모든 서류를 수령했는지 다시 한번 확인하세요."
        ),
        GuideDetailItem(
            id = "doc_email_file_receipt",
            iconResId = R.drawable.ic_email_file_receipt_available,
            title = "이메일·파일 수령 가능 여부",
            description = "귀국 후에도 이메일이나 파일로 받을 수 있는지 확인하세요."
        )
    ),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "필요한 서류 종류는 본국 제출 기관 기준에 따라 다를 수 있어요."
)

// S-06 STEP06 하위 "귀국 전 반입·공항 준비"
val airportDeparturePreparationContent = GuideStepDetailContent(
    bannerResId = R.drawable.img_post_treatment_travel_preparation_banner,
    bannerAspectRatio = 1672f / 941f,
    bannerStepLabel = "STEP 06",
    bannerTitle = "귀국 전 반입·공항 준비",
    bannerSubtitle = "약 반입 제한과 귀국 전 필요한 준비 항목을 미리 확인하세요.",
    checklistTitle = "출국 전 체크리스트",
    checklistItems = listOf(
        GuideDetailItem(
            id = "departure_carry_on_documents",
            iconResId = R.drawable.ic_carry_on_medical_documents,
            title = "기내 반입 의료서류 확인",
            description = "여권, 진단서 등 기내에 직접 소지할 의료서류를 확인하세요."
        ),
        GuideDetailItem(
            id = "departure_liquid_medicine",
            iconResId = R.drawable.ic_liquid_medicine_medical_supplies,
            title = "액체 의약품·의료용품 확인",
            description = "물약, 안약 등 액체류 의약품의 반입 용량 기준을 확인하세요."
        ),
        GuideDetailItem(
            id = "departure_security_screening",
            iconResId = R.drawable.ic_airport_security_screening_preparation,
            title = "공항 보안검색 준비",
            description = "보안검색 시 의약품과 의료용품을 미리 꺼내둘 수 있도록 준비하세요."
        ),
        GuideDetailItem(
            id = "departure_restricted_medicine",
            iconResId = R.drawable.ic_restricted_medicine_check,
            title = "의약품 반입 제한 확인",
            description = "처방약, 한약 등 반입 제한 품목인지 확인하세요."
        ),
        GuideDetailItem(
            id = "departure_passport_validity",
            iconResId = R.drawable.ic_passport_validity_check,
            title = "여권 유효기간 재확인",
            description = "귀국편 탑승에 필요한 여권 유효기간을 다시 확인하세요."
        ),
        GuideDetailItem(
            id = "departure_flight_baggage",
            iconResId = R.drawable.ic_long_flight_seat,
            title = "항공권·수하물 확인",
            description = "항공권 일정과 수하물 규정을 확인하세요."
        )
    ),
    noticeIconResId = R.drawable.ic_guide_information,
    noticeText = "반입 제한 품목과 세관 규정은 국가마다 다르니 항공사·세관 안내를 확인해 주세요."
)
