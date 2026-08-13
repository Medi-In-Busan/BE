package com.mediinbusan.app.core.i18n

/**
 * S-06 의료 이용 절차 가이드(F-008) 정적 UI 문구. STEP 01~06 목록/상세, 하위 항목별 리프 화면,
 * STEP04 전용 화면(TreatmentExaminationDetailScreen)까지 포함한다. 아이콘 리소스·URL·내부 id 등
 * 비문자열/식별자 값은 각 화면·매퍼 코드에 그대로 남기고, 여기서는 표시 문자열만 다룬다.
 */
data class GuideStrings(
    val screenTitle: String,
    val screenSubtitle: String,
    val loadErrorFallback: String,
    val stepEntryPreparationTitle: String,
    val stepEntryPreparationSummary: String,
    val stepReservationInquiryTitle: String,
    val stepReservationInquirySummary: String,
    val stepHospitalCheckinTitle: String,
    val stepHospitalCheckinSummary: String,
    val stepTreatmentExaminationTitle: String,
    val stepTreatmentExaminationSummary: String,
    val stepPaymentReceiptTitle: String,
    val stepPaymentReceiptSummary: String,
    val stepAftercareReturnCheckTitle: String,
    val stepAftercareReturnCheckSummary: String,
    val comingSoonNoticeText: String,
    val entryPreparation: EntryPreparationStrings,
    val reservationInquiry: ReservationInquiryStrings,
    val hospitalCheckin: HospitalCheckinStrings,
    val paymentReceipt: PaymentReceiptStrings,
    val aftercareReturnCheck: AftercareReturnCheckStrings,
    val visaEntryCheck: VisaEntryCheckStrings,
    val insuranceDocuments: InsuranceDocumentsStrings,
    val hospitalInquiry: HospitalInquiryStrings,
    val preInquiryInformation: PreInquiryInformationStrings,
    val passportReservationInfo: PassportReservationInfoStrings,
    val medicalRecordsTestResults: MedicalRecordsTestResultsStrings,
    val totalCostCoverageCheck: TotalCostCoverageCheckStrings,
    val paymentMethodCheck: PaymentMethodCheckStrings,
    val receiptInsuranceDocuments: ReceiptInsuranceDocumentsStrings,
    val medicationSchedule: MedicationScheduleStrings,
    val postTreatmentPrecautions: PostTreatmentPrecautionsStrings,
    val englishDocumentsResults: EnglishDocumentsResultsStrings,
    val airportDeparturePreparation: AirportDeparturePreparationStrings,
    val hospitalLocationCheckin: HospitalLocationCheckinStrings,
    val treatmentExamination: TreatmentExaminationStrings,
    val treatmentBriefingDefaults: TreatmentBriefingDefaultsStrings
) {
    companion object {
        val Ko = GuideStrings(
            screenTitle = "이용 가이드",
            screenSubtitle = "입국 전부터 진료 후 귀국까지 단계별 안내",
            loadErrorFallback = "오류가 발생했습니다.",
            stepEntryPreparationTitle = "입국 전 준비",
            stepEntryPreparationSummary = "예약, 서류, 비자, 보험 등 준비사항",
            stepReservationInquiryTitle = "예약 및 문의",
            stepReservationInquirySummary = "병원 문의 전 준비할 정보와 팁",
            stepHospitalCheckinTitle = "병원 방문 및 접수",
            stepHospitalCheckinSummary = "접수 시 필요한 정보와 절차",
            stepTreatmentExaminationTitle = "진료 및 검사",
            stepTreatmentExaminationSummary = "진료 과정 및 주의사항",
            stepPaymentReceiptTitle = "결제 및 수납",
            stepPaymentReceiptSummary = "결제 방법과 영수증 확인",
            stepAftercareReturnCheckTitle = "진료 후 관리 · 귀국 전 체크",
            stepAftercareReturnCheckSummary = "약 복용, 서류, 영수증, 환급, 주의사항",
            comingSoonNoticeText = "이 단계의 상세 안내는 준비 중입니다.",
            entryPreparation = EntryPreparationStrings.Ko,
            reservationInquiry = ReservationInquiryStrings.Ko,
            hospitalCheckin = HospitalCheckinStrings.Ko,
            paymentReceipt = PaymentReceiptStrings.Ko,
            aftercareReturnCheck = AftercareReturnCheckStrings.Ko,
            visaEntryCheck = VisaEntryCheckStrings.Ko,
            insuranceDocuments = InsuranceDocumentsStrings.Ko,
            hospitalInquiry = HospitalInquiryStrings.Ko,
            preInquiryInformation = PreInquiryInformationStrings.Ko,
            passportReservationInfo = PassportReservationInfoStrings.Ko,
            medicalRecordsTestResults = MedicalRecordsTestResultsStrings.Ko,
            totalCostCoverageCheck = TotalCostCoverageCheckStrings.Ko,
            paymentMethodCheck = PaymentMethodCheckStrings.Ko,
            receiptInsuranceDocuments = ReceiptInsuranceDocumentsStrings.Ko,
            medicationSchedule = MedicationScheduleStrings.Ko,
            postTreatmentPrecautions = PostTreatmentPrecautionsStrings.Ko,
            englishDocumentsResults = EnglishDocumentsResultsStrings.Ko,
            airportDeparturePreparation = AirportDeparturePreparationStrings.Ko,
            hospitalLocationCheckin = HospitalLocationCheckinStrings.Ko,
            treatmentExamination = TreatmentExaminationStrings.Ko,
            treatmentBriefingDefaults = TreatmentBriefingDefaultsStrings.Ko
        )
        val En = GuideStrings(
            screenTitle = "Guide",
            screenSubtitle = "Step-by-step guidance from before you arrive to after treatment and departure",
            loadErrorFallback = "An error occurred.",
            stepEntryPreparationTitle = "Before You Arrive",
            stepEntryPreparationSummary = "Reservations, documents, visa, insurance, and more",
            stepReservationInquiryTitle = "Reservation & Inquiry",
            stepReservationInquirySummary = "Information and tips to prepare before contacting a hospital",
            stepHospitalCheckinTitle = "Hospital Visit & Check-in",
            stepHospitalCheckinSummary = "Information and steps needed at check-in",
            stepTreatmentExaminationTitle = "Treatment & Examination",
            stepTreatmentExaminationSummary = "The treatment process and precautions",
            stepPaymentReceiptTitle = "Payment & Receipt",
            stepPaymentReceiptSummary = "Payment methods and checking your receipt",
            stepAftercareReturnCheckTitle = "Aftercare & Pre-departure Check",
            stepAftercareReturnCheckSummary = "Medication, documents, receipts, refunds, and precautions",
            comingSoonNoticeText = "Detailed guidance for this step is coming soon.",
            entryPreparation = EntryPreparationStrings.En,
            reservationInquiry = ReservationInquiryStrings.En,
            hospitalCheckin = HospitalCheckinStrings.En,
            paymentReceipt = PaymentReceiptStrings.En,
            aftercareReturnCheck = AftercareReturnCheckStrings.En,
            visaEntryCheck = VisaEntryCheckStrings.En,
            insuranceDocuments = InsuranceDocumentsStrings.En,
            hospitalInquiry = HospitalInquiryStrings.En,
            preInquiryInformation = PreInquiryInformationStrings.En,
            passportReservationInfo = PassportReservationInfoStrings.En,
            medicalRecordsTestResults = MedicalRecordsTestResultsStrings.En,
            totalCostCoverageCheck = TotalCostCoverageCheckStrings.En,
            paymentMethodCheck = PaymentMethodCheckStrings.En,
            receiptInsuranceDocuments = ReceiptInsuranceDocumentsStrings.En,
            medicationSchedule = MedicationScheduleStrings.En,
            postTreatmentPrecautions = PostTreatmentPrecautionsStrings.En,
            englishDocumentsResults = EnglishDocumentsResultsStrings.En,
            airportDeparturePreparation = AirportDeparturePreparationStrings.En,
            hospitalLocationCheckin = HospitalLocationCheckinStrings.En,
            treatmentExamination = TreatmentExaminationStrings.En,
            treatmentBriefingDefaults = TreatmentBriefingDefaultsStrings.En
        )
        val Zh = GuideStrings(
            screenTitle = "使用指南",
            screenSubtitle = "从入境前到诊疗后回国，分阶段为您提供指南",
            loadErrorFallback = "发生错误。",
            stepEntryPreparationTitle = "入境前准备",
            stepEntryPreparationSummary = "预约、材料、签证、保险等准备事项",
            stepReservationInquiryTitle = "预约与咨询",
            stepReservationInquirySummary = "联系医院前需准备的信息与小贴士",
            stepHospitalCheckinTitle = "医院就诊与挂号",
            stepHospitalCheckinSummary = "挂号时所需的信息与流程",
            stepTreatmentExaminationTitle = "诊疗与检查",
            stepTreatmentExaminationSummary = "诊疗流程与注意事项",
            stepPaymentReceiptTitle = "结算与收费",
            stepPaymentReceiptSummary = "付款方式与收据确认",
            stepAftercareReturnCheckTitle = "诊后管理与回国前确认",
            stepAftercareReturnCheckSummary = "服药、文件、收据、退税与注意事项",
            comingSoonNoticeText = "该阶段的详细说明正在准备中。",
            entryPreparation = EntryPreparationStrings.Zh,
            reservationInquiry = ReservationInquiryStrings.Zh,
            hospitalCheckin = HospitalCheckinStrings.Zh,
            paymentReceipt = PaymentReceiptStrings.Zh,
            aftercareReturnCheck = AftercareReturnCheckStrings.Zh,
            visaEntryCheck = VisaEntryCheckStrings.Zh,
            insuranceDocuments = InsuranceDocumentsStrings.Zh,
            hospitalInquiry = HospitalInquiryStrings.Zh,
            preInquiryInformation = PreInquiryInformationStrings.Zh,
            passportReservationInfo = PassportReservationInfoStrings.Zh,
            medicalRecordsTestResults = MedicalRecordsTestResultsStrings.Zh,
            totalCostCoverageCheck = TotalCostCoverageCheckStrings.Zh,
            paymentMethodCheck = PaymentMethodCheckStrings.Zh,
            receiptInsuranceDocuments = ReceiptInsuranceDocumentsStrings.Zh,
            medicationSchedule = MedicationScheduleStrings.Zh,
            postTreatmentPrecautions = PostTreatmentPrecautionsStrings.Zh,
            englishDocumentsResults = EnglishDocumentsResultsStrings.Zh,
            airportDeparturePreparation = AirportDeparturePreparationStrings.Zh,
            hospitalLocationCheckin = HospitalLocationCheckinStrings.Zh,
            treatmentExamination = TreatmentExaminationStrings.Zh,
            treatmentBriefingDefaults = TreatmentBriefingDefaultsStrings.Zh
        )
        val Ja = GuideStrings(
            screenTitle = "利用ガイド",
            screenSubtitle = "入国前から診療後の帰国まで、段階的にご案内します",
            loadErrorFallback = "エラーが発生しました。",
            stepEntryPreparationTitle = "入国前の準備",
            stepEntryPreparationSummary = "予約・書類・ビザ・保険などの準備事項",
            stepReservationInquiryTitle = "予約・問い合わせ",
            stepReservationInquirySummary = "病院に問い合わせる前に準備しておきたい情報とコツ",
            stepHospitalCheckinTitle = "病院訪問・受付",
            stepHospitalCheckinSummary = "受付時に必要な情報と手続き",
            stepTreatmentExaminationTitle = "診療・検査",
            stepTreatmentExaminationSummary = "診療の流れと注意事項",
            stepPaymentReceiptTitle = "会計・支払い",
            stepPaymentReceiptSummary = "支払い方法と領収書の確認",
            stepAftercareReturnCheckTitle = "診療後のケア・帰国前チェック",
            stepAftercareReturnCheckSummary = "服薬・書類・領収書・還付・注意事項",
            comingSoonNoticeText = "このステップの詳細案内は準備中です。",
            entryPreparation = EntryPreparationStrings.Ja,
            reservationInquiry = ReservationInquiryStrings.Ja,
            hospitalCheckin = HospitalCheckinStrings.Ja,
            paymentReceipt = PaymentReceiptStrings.Ja,
            aftercareReturnCheck = AftercareReturnCheckStrings.Ja,
            visaEntryCheck = VisaEntryCheckStrings.Ja,
            insuranceDocuments = InsuranceDocumentsStrings.Ja,
            hospitalInquiry = HospitalInquiryStrings.Ja,
            preInquiryInformation = PreInquiryInformationStrings.Ja,
            passportReservationInfo = PassportReservationInfoStrings.Ja,
            medicalRecordsTestResults = MedicalRecordsTestResultsStrings.Ja,
            totalCostCoverageCheck = TotalCostCoverageCheckStrings.Ja,
            paymentMethodCheck = PaymentMethodCheckStrings.Ja,
            receiptInsuranceDocuments = ReceiptInsuranceDocumentsStrings.Ja,
            medicationSchedule = MedicationScheduleStrings.Ja,
            postTreatmentPrecautions = PostTreatmentPrecautionsStrings.Ja,
            englishDocumentsResults = EnglishDocumentsResultsStrings.Ja,
            airportDeparturePreparation = AirportDeparturePreparationStrings.Ja,
            hospitalLocationCheckin = HospitalLocationCheckinStrings.Ja,
            treatmentExamination = TreatmentExaminationStrings.Ja,
            treatmentBriefingDefaults = TreatmentBriefingDefaultsStrings.Ja
        )
    }
}

data class EntryPreparationStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val situationalTitle: String,
    val situational1Title: String,
    val situational1Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = EntryPreparationStrings(
            bannerTitle = "입국 전 준비",
            bannerSubtitle = "입국 전에는 비자, 보험, 병원 문의 준비만 먼저 확인해도 충분해요.",
            checklistTitle = "준비사항 확인",
            item1Title = "비자·입국 조건 확인",
            item1Description = "체류 목적에 맞는 비자 종류와 입국 조건을 미리 확인하세요.",
            item2Title = "보험·서류 준비",
            item2Description = "여행자보험 가입 여부와 진단서 등 필요 서류를 준비하세요.",
            item3Title = "병원 문의 전 정보 정리",
            item3Description = "방문 목적과 증상 등 병원에 전달할 정보를 미리 정리해두세요.",
            situationalTitle = "상황별 확인",
            situational1Title = "91일 이상 치료·요양 가능성",
            situational1Description = "91일 이상 장기 체류가 예상된다면 체류 자격과 보호자 동반 여부를 미리 확인하세요.",
            noticeText = "본 안내는 의료 또는 비자 판단이 아닙니다. 병원 또는 공식 기관의 안내를 우선 확인해 주세요."
        )
        val En = EntryPreparationStrings(
            bannerTitle = "Before You Arrive",
            bannerSubtitle = "Before you arrive, it's enough to check your visa, insurance, and hospital inquiry basics.",
            checklistTitle = "Checklist",
            item1Title = "Check Visa & Entry Requirements",
            item1Description = "Check the visa type and entry requirements that match your purpose of stay in advance.",
            item2Title = "Prepare Insurance & Documents",
            item2Description = "Prepare travel insurance and required documents such as medical certificates.",
            item3Title = "Organize Info Before Contacting a Hospital",
            item3Description = "Organize the information you'll share with the hospital, such as your visit purpose and symptoms.",
            situationalTitle = "Situational Checks",
            situational1Title = "Treatment/Recovery Over 91 Days",
            situational1Description = "If you expect to stay longer than 91 days, check your residence status and whether a guardian needs to accompany you.",
            noticeText = "This guidance is not medical or visa advice. Please check with the hospital or an official agency first."
        )
        val Zh = EntryPreparationStrings(
            bannerTitle = "入境前准备",
            bannerSubtitle = "入境前，先确认签证、保险和医院咨询相关准备即可。",
            checklistTitle = "准备事项确认",
            item1Title = "确认签证与入境条件",
            item1Description = "请提前确认符合您逗留目的的签证种类与入境条件。",
            item2Title = "准备保险与文件",
            item2Description = "请准备旅行保险及诊断书等所需文件。",
            item3Title = "整理病院咨询前的信息",
            item3Description = "请提前整理好访问目的、症状等要告知医院的信息。",
            situationalTitle = "情况确认",
            situational1Title = "治疗·疗养可能超过91天",
            situational1Description = "如预计长期停留超过91天，请提前确认停留资格及是否需要监护人陪同。",
            noticeText = "本指南并非医疗或签证判断依据，请优先确认医院或官方机构的说明。"
        )
        val Ja = EntryPreparationStrings(
            bannerTitle = "入国前の準備",
            bannerSubtitle = "入国前は、ビザ・保険・病院への問い合わせ準備だけ確認しておけば十分です。",
            checklistTitle = "準備事項の確認",
            item1Title = "ビザ・入国条件の確認",
            item1Description = "滞在目的に合ったビザの種類と入国条件を事前に確認してください。",
            item2Title = "保険・書類の準備",
            item2Description = "海外旅行保険の加入状況や診断書など必要書類を準備してください。",
            item3Title = "病院への問い合わせ前の情報整理",
            item3Description = "受診目的や症状など、病院に伝える情報を事前に整理しておきましょう。",
            situationalTitle = "状況別確認",
            situational1Title = "91日以上の治療・療養の可能性",
            situational1Description = "91日以上の長期滞在が見込まれる場合は、在留資格と保護者同伴の要否を事前に確認してください。",
            noticeText = "本案内は医療または査証の判断ではありません。病院または公式機関の案内を優先して確認してください。"
        )
    }
}

data class ReservationInquiryStrings(
    val bannerSubtitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val situationalTitle: String,
    val situational1Title: String,
    val situational1Description: String,
    val situational2Title: String,
    val situational2Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = ReservationInquiryStrings(
            bannerSubtitle = "병원에 문의하기 전 필요한 정보를 정리하고 언어 지원과 문의 채널을 확인해요.",
            item1Title = "병원 공식 문의 채널 확인",
            item1Description = "메디투어부산에서 부산 의료기관의 공식 문의 채널을 확인하세요.",
            item2Title = "상담 가능한 언어로 병원 찾기",
            item2Description = "Medical Korea 등록병원 목록에서 상담 가능한 언어를 확인하세요.",
            item3Title = "문의 전 전달할 정보 정리",
            item3Description = "증상, 희망 진료, 방문 시기 등 전달할 정보를 미리 정리하세요.",
            situationalTitle = "상황별 확인",
            situational1Title = "등록 유치기관 이용 여부 확인",
            situational1Description = "등록된 해외환자 유치기관을 통한 진행인지 확인하세요.",
            situational2Title = "영문 서류 발급 가능 여부",
            situational2Description = "진단서, 소견서 등 영문 서류 발급이 가능한지 확인하세요.",
            noticeText = "본 안내는 특정 병원·기관 이용을 권장하지 않습니다. 실제 진행 여부는 병원에 직접 확인해 주세요."
        )
        val En = ReservationInquiryStrings(
            bannerSubtitle = "Organize the information you need before contacting a hospital, and check language support and inquiry channels.",
            item1Title = "Check Official Hospital Inquiry Channels",
            item1Description = "Check official inquiry channels for Busan medical institutions on Meditour Busan.",
            item2Title = "Find a Hospital by Supported Language",
            item2Description = "Check available consultation languages in the Medical Korea registered hospital list.",
            item3Title = "Organize Info to Share Before Inquiring",
            item3Description = "Organize the information you'll share, such as symptoms, desired treatment, and visit timing.",
            situationalTitle = "Situational Checks",
            situational1Title = "Check Use of a Registered Agency",
            situational1Description = "Check whether you're proceeding through a registered international patient agency.",
            situational2Title = "Check English Document Availability",
            situational2Description = "Check whether English-language documents such as medical certificates are available.",
            noticeText = "This guidance does not recommend any specific hospital or agency. Please confirm directly with the hospital before proceeding."
        )
        val Zh = ReservationInquiryStrings(
            bannerSubtitle = "在联系医院前整理所需信息，并确认语言支持与咨询渠道。",
            item1Title = "确认医院官方咨询渠道",
            item1Description = "请在Meditour Busan确认釜山医疗机构的官方咨询渠道。",
            item2Title = "按可咨询语言查找医院",
            item2Description = "请在Medical Korea注册医院名单中确认可提供咨询的语言。",
            item3Title = "整理咨询前要传达的信息",
            item3Description = "请提前整理症状、期望诊疗、访问时间等要传达的信息。",
            situationalTitle = "情况确认",
            situational1Title = "确认是否使用注册的招揽机构",
            situational1Description = "请确认是否通过注册的海外患者招揽机构进行安排。",
            situational2Title = "确认能否开具英文文件",
            situational2Description = "请确认能否开具诊断书、意见书等英文文件。",
            noticeText = "本指南不推荐特定医院或机构，实际是否办理请直接向医院确认。"
        )
        val Ja = ReservationInquiryStrings(
            bannerSubtitle = "病院に問い合わせる前に必要な情報を整理し、対応言語と問い合わせチャネルを確認しましょう。",
            item1Title = "病院公式問い合わせチャネルの確認",
            item1Description = "メディツアー釜山で釜山の医療機関の公式問い合わせチャネルを確認してください。",
            item2Title = "対応言語で病院を探す",
            item2Description = "Medical Korea登録病院リストで相談可能な言語を確認してください。",
            item3Title = "問い合わせ前に伝える情報の整理",
            item3Description = "症状、希望する診療、訪問時期など伝える情報を事前に整理してください。",
            situationalTitle = "状況別確認",
            situational1Title = "登録誘致機関の利用有無の確認",
            situational1Description = "登録された海外患者誘致機関を通じて進めているか確認してください。",
            situational2Title = "英文書類の発行可否の確認",
            situational2Description = "診断書・所見書などの英文書類が発行可能か確認してください。",
            noticeText = "本案内は特定の病院・機関の利用を推奨するものではありません。実際の手続きは病院に直接ご確認ください。"
        )
    }
}

data class HospitalCheckinStrings(
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item1BadgeLabel: String,
    val item2Title: String,
    val item2Description: String,
    val item2BadgeLabel: String,
    val item3Title: String,
    val item3Description: String,
    val item3BadgeLabel: String,
    val situationalTitle: String,
    val situational1Title: String,
    val situational1Description: String,
    val situational2Title: String,
    val situational2Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = HospitalCheckinStrings(
            bannerSubtitle = "병원 방문 전에는 여권, 예약 정보, 접수 준비만 먼저 확인해도 충분해요.",
            checklistTitle = "이번 단계에서 꼭 확인할 3가지",
            item1Title = "여권·예약정보 준비",
            item1Description = "여권 또는 신분 확인 자료와 예약 시간, 진료과를 미리 확인하세요.",
            item1BadgeLabel = "신분 확인",
            item2Title = "기존 진단서·검사결과 준비",
            item2Description = "기존 진단서, 검사결과, 복용약 정보를 준비하면 접수가 쉬워져요.",
            item2BadgeLabel = "기존 자료",
            item3Title = "병원 위치와 접수 절차 확인",
            item3Description = "병원 위치, 도착 시간, 어디서 접수하는지 먼저 확인하세요.",
            item3BadgeLabel = "접수 안내",
            situationalTitle = "상황별 확인",
            situational1Title = "통역·지원 언어 확인",
            situational1Description = "진료 당일 통역이나 지원 언어가 필요한 경우만 확인하세요.",
            situational2Title = "결제 수단 확인",
            situational2Description = "해외 카드, 현금, 송금 가능 여부가 필요한 경우만 확인하세요.",
            noticeText = "병원마다 접수 절차와 준비 서류가 다를 수 있으니 예약 안내를 다시 확인해 주세요."
        )
        val En = HospitalCheckinStrings(
            bannerSubtitle = "Before your hospital visit, it's enough to check your passport, reservation details, and check-in prep.",
            checklistTitle = "3 Things to Check in This Step",
            item1Title = "Prepare Passport & Reservation Info",
            item1Description = "Check your passport or ID, along with your appointment time and department, in advance.",
            item1BadgeLabel = "ID Check",
            item2Title = "Prepare Existing Records & Test Results",
            item2Description = "Bringing existing medical certificates, test results, and medication info makes check-in easier.",
            item2BadgeLabel = "Existing Records",
            item3Title = "Check Hospital Location & Check-in Process",
            item3Description = "Check the hospital location, arrival time, and where to check in beforehand.",
            item3BadgeLabel = "Check-in Info",
            situationalTitle = "Situational Checks",
            situational1Title = "Check Interpretation & Language Support",
            situational1Description = "Check this only if you'll need interpretation or language support on the day of your visit.",
            situational2Title = "Check Payment Methods",
            situational2Description = "Check this only if you need to know whether overseas cards, cash, or transfers are accepted.",
            noticeText = "Check-in procedures and required documents vary by hospital, so please review your reservation guidance again."
        )
        val Zh = HospitalCheckinStrings(
            bannerSubtitle = "医院就诊前，先确认护照、预约信息及挂号准备即可。",
            checklistTitle = "本阶段务必确认的3件事",
            item1Title = "准备护照与预约信息",
            item1Description = "请提前确认护照或身份证明材料、预约时间及诊疗科室。",
            item1BadgeLabel = "身份确认",
            item2Title = "准备既往诊断书·检查结果",
            item2Description = "准备好既往诊断书、检查结果及服药信息，可让挂号更顺利。",
            item2BadgeLabel = "既往资料",
            item3Title = "确认医院位置与挂号流程",
            item3Description = "请提前确认医院位置、到达时间及挂号地点。",
            item3BadgeLabel = "挂号指南",
            situationalTitle = "情况确认",
            situational1Title = "确认口译与支持语言",
            situational1Description = "仅在就诊当天需要口译或语言支持时确认即可。",
            situational2Title = "确认付款方式",
            situational2Description = "仅在需要确认能否使用海外卡、现金或汇款时确认即可。",
            noticeText = "各医院的挂号流程与所需文件可能不同，请再次确认预约相关说明。"
        )
        val Ja = HospitalCheckinStrings(
            bannerSubtitle = "病院訪問前は、パスポート・予約情報・受付準備だけ確認しておけば十分です。",
            checklistTitle = "このステップで必ず確認する3つのこと",
            item1Title = "パスポート・予約情報の準備",
            item1Description = "パスポートまたは本人確認資料、予約時間、診療科を事前に確認してください。",
            item1BadgeLabel = "本人確認",
            item2Title = "既往の診断書・検査結果の準備",
            item2Description = "既往の診断書、検査結果、服用薬情報を準備しておくと受付がスムーズになります。",
            item2BadgeLabel = "既往資料",
            item3Title = "病院の場所と受付手続きの確認",
            item3Description = "病院の場所、到着時間、どこで受付するかを事前に確認してください。",
            item3BadgeLabel = "受付案内",
            situationalTitle = "状況別確認",
            situational1Title = "通訳・対応言語の確認",
            situational1Description = "受診当日に通訳や対応言語のサポートが必要な場合のみ確認してください。",
            situational2Title = "決済手段の確認",
            situational2Description = "海外カード・現金・送金の可否確認が必要な場合のみご確認ください。",
            noticeText = "受付手続きや必要書類は病院により異なる場合があるため、予約案内を再度ご確認ください。"
        )
    }
}

data class PaymentReceiptStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item1BadgeLabel: String,
    val item2Title: String,
    val item2Description: String,
    val item2BadgeLabel: String,
    val item3Title: String,
    val item3Description: String,
    val item3BadgeLabel: String,
    val situationalTitle: String,
    val situational1Title: String,
    val situational1Description: String,
    val situational2Title: String,
    val situational2Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = PaymentReceiptStrings(
            bannerTitle = "결제 전 비용·수단·서류 확인",
            bannerSubtitle = "총 비용, 결제 수단, 발급 서류를 미리 확인하세요.",
            checklistTitle = "이번 단계에서 꼭 확인할 3가지",
            item1Title = "총 비용과 포함 항목 확인",
            item1Description = "진료비, 검사비, 시술비, 약제비와 포함·불포함 항목을 먼저 확인하세요.",
            item1BadgeLabel = "비용 확인",
            item2Title = "결제 가능 수단 확인",
            item2Description = "해외 카드, 현금, 송금 등 실제 결제 가능한 수단을 확인하세요.",
            item2BadgeLabel = "결제 수단",
            item3Title = "영수증·보험 청구 서류 확인",
            item3Description = "영수증, 진료비 세부내역서, 보험 청구용 서류 발급 여부를 확인하세요.",
            item3BadgeLabel = "서류 확인",
            situationalTitle = "상황별 확인",
            situational1Title = "보증금·선납금 필요 여부",
            situational1Description = "입원, 시술, 예약 조건에 따라 선납금이 필요한 경우만 확인하세요.",
            situational2Title = "미용시술 세금 환급 가능 여부",
            situational2Description = "피부·미용 시술을 받는 경우에만 세금 환급 가능 여부를 확인하세요.",
            noticeText = "실제 비용과 발급 서류는 병원 및 보험사 기준이 다를 수 있으니 반드시 직접 확인해 주세요."
        )
        val En = PaymentReceiptStrings(
            bannerTitle = "Check Cost, Payment & Documents Before Paying",
            bannerSubtitle = "Check the total cost, payment methods, and documents to be issued in advance.",
            checklistTitle = "3 Things to Check in This Step",
            item1Title = "Check Total Cost & Coverage",
            item1Description = "Check treatment, exam, procedure, and medication fees, along with what's included or not.",
            item1BadgeLabel = "Cost Check",
            item2Title = "Check Available Payment Methods",
            item2Description = "Check which payment methods are actually accepted, such as overseas cards, cash, or transfer.",
            item2BadgeLabel = "Payment Method",
            item3Title = "Check Receipt & Insurance Claim Documents",
            item3Description = "Check whether receipts, itemized statements, and insurance claim documents can be issued.",
            item3BadgeLabel = "Document Check",
            situationalTitle = "Situational Checks",
            situational1Title = "Check Deposit/Prepayment Requirements",
            situational1Description = "Check this only if a deposit is required based on admission, procedure, or reservation terms.",
            situational2Title = "Check Tax Refund for Cosmetic Procedures",
            situational2Description = "Check tax refund eligibility only if you're receiving a skin or cosmetic procedure.",
            noticeText = "Actual costs and issued documents may vary by hospital and insurer, so please confirm directly."
        )
        val Zh = PaymentReceiptStrings(
            bannerTitle = "付款前确认费用·方式·文件",
            bannerSubtitle = "请提前确认总费用、付款方式及可开具的文件。",
            checklistTitle = "本阶段务必确认的3件事",
            item1Title = "确认总费用与包含项目",
            item1Description = "请先确认诊疗费、检查费、手术费、药费及包含·不包含的项目。",
            item1BadgeLabel = "费用确认",
            item2Title = "确认可用的付款方式",
            item2Description = "请确认实际可用的付款方式，如海外卡、现金、汇款等。",
            item2BadgeLabel = "付款方式",
            item3Title = "确认收据与保险理赔文件",
            item3Description = "请确认能否开具收据、诊疗费明细单及保险理赔用文件。",
            item3BadgeLabel = "文件确认",
            situationalTitle = "情况确认",
            situational1Title = "确认是否需要押金·预付款",
            situational1Description = "仅在因住院、手术或预约条件需要预付款时确认即可。",
            situational2Title = "确认美容项目退税资格",
            situational2Description = "仅在接受皮肤·美容项目时确认是否可退税。",
            noticeText = "实际费用及可开具文件因医院和保险公司而异，请务必自行确认。"
        )
        val Ja = PaymentReceiptStrings(
            bannerTitle = "支払い前の費用・手段・書類の確認",
            bannerSubtitle = "総費用、支払い手段、発行される書類を事前に確認してください。",
            checklistTitle = "このステップで必ず確認する3つのこと",
            item1Title = "総費用と含まれる項目の確認",
            item1Description = "診療費・検査費・施術費・薬剤費と、含まれる／含まれない項目を先に確認してください。",
            item1BadgeLabel = "費用確認",
            item2Title = "利用可能な決済手段の確認",
            item2Description = "海外カード、現金、送金など実際に利用できる決済手段を確認してください。",
            item2BadgeLabel = "支払い方法",
            item3Title = "領収書・保険請求書類の確認",
            item3Description = "領収書、診療費明細書、保険請求用書類の発行可否を確認してください。",
            item3BadgeLabel = "書類確認",
            situationalTitle = "状況別確認",
            situational1Title = "保証金・前払金の要否確認",
            situational1Description = "入院・施術・予約条件により前払金が必要な場合のみ確認してください。",
            situational2Title = "美容施術の税還付可否の確認",
            situational2Description = "皮膚・美容施術を受ける場合のみ、税還付の可否を確認してください。",
            noticeText = "実際の費用や発行書類は病院・保険会社により異なる場合があるため、必ず直接ご確認ください。"
        )
    }
}

data class AftercareReturnCheckStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item1BadgeLabel: String,
    val item2Title: String,
    val item2Description: String,
    val item2BadgeLabel: String,
    val item3Title: String,
    val item3Description: String,
    val item3BadgeLabel: String,
    val item4Title: String,
    val item4Description: String,
    val item4BadgeLabel: String,
    val situationalTitle: String,
    val situational1Title: String,
    val situational1Description: String,
    val situational2Title: String,
    val situational2Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = AftercareReturnCheckStrings(
            bannerTitle = "진료 후 관리와\n귀국 전 체크",
            bannerSubtitle = "약 복용, 주의사항, 서류 수령, 귀국 전 준비만 먼저 확인해도 충분해요.",
            checklistTitle = "이번 단계에서 꼭 확인할 4가지",
            item1Title = "약 복용 방법 확인",
            item1Description = "약 이름, 복용 시간, 식전·식후 여부와 복용 기간을 확인하세요.",
            item1BadgeLabel = "약 확인",
            item2Title = "진료 후 주의사항 확인",
            item2Description = "식사, 샤워, 운동 가능 시점과 이상 증상 기준을 확인하세요.",
            item2BadgeLabel = "주의사항",
            item3Title = "영문 서류·검사결과 수령 확인",
            item3Description = "귀국 후 제출이 필요한 서류와 검사결과 수령 여부를 확인하세요.",
            item3BadgeLabel = "서류 확인",
            item4Title = "귀국 전 반입·공항 준비",
            item4Description = "약 반입 제한과 귀국 전 필요한 준비 항목을 확인하세요.",
            item4BadgeLabel = "귀국 준비",
            situationalTitle = "상황별 확인",
            situational1Title = "본국 반입 제한 물품 확인",
            situational1Description = "처방약, 한약, 액체류 등 본국 반입 기준이 필요한 경우만 확인하세요.",
            situational2Title = "장거리 비행 주의",
            situational2Description = "시술·검사 후 장거리 이동 제한이 있는 경우에만 확인하세요.",
            noticeText = "약 복용과 귀국 준비 기준은 진료 내용과 국가별 규정에 따라 다를 수 있으니 병원 안내와 항공·세관 정보를 함께 확인해 주세요."
        )
        val En = AftercareReturnCheckStrings(
            bannerTitle = "Aftercare &\nPre-departure Check",
            bannerSubtitle = "It's enough to check medication, precautions, document pickup, and pre-departure prep first.",
            checklistTitle = "4 Things to Check in This Step",
            item1Title = "Check How to Take Your Medication",
            item1Description = "Check the medication name, timing, before/after meals, and duration.",
            item1BadgeLabel = "Medication Check",
            item2Title = "Check Post-treatment Precautions",
            item2Description = "Check when you can eat, shower, or exercise, and the warning signs to watch for.",
            item2BadgeLabel = "Precautions",
            item3Title = "Check English Documents & Test Results",
            item3Description = "Check whether you've received the documents and test results you'll need to submit after returning home.",
            item3BadgeLabel = "Document Check",
            item4Title = "Prepare for Departure & Customs",
            item4Description = "Check medication import restrictions and other items to prepare before departure.",
            item4BadgeLabel = "Departure Prep",
            situationalTitle = "Situational Checks",
            situational1Title = "Check Import Restrictions in Your Home Country",
            situational1Description = "Check this only if you need to know import rules for prescription drugs, herbal medicine, or liquids.",
            situational2Title = "Long-flight Precautions",
            situational2Description = "Check this only if there are travel restrictions after your procedure or exam.",
            noticeText = "Medication and departure guidelines vary by treatment and country regulations, so please check both hospital guidance and airline/customs information."
        )
        val Zh = AftercareReturnCheckStrings(
            bannerTitle = "诊后管理与\n回国前确认",
            bannerSubtitle = "先确认服药、注意事项、领取文件及回国前准备即可。",
            checklistTitle = "本阶段务必确认的4件事",
            item1Title = "确认服药方法",
            item1Description = "请确认药品名称、服用时间、饭前饭后及服药期限。",
            item1BadgeLabel = "药物确认",
            item2Title = "确认诊疗后注意事项",
            item2Description = "请确认可以进食、洗澡、运动的时间点及异常症状标准。",
            item2BadgeLabel = "注意事项",
            item3Title = "确认英文文件与检查结果领取",
            item3Description = "请确认是否已领取回国后需提交的文件及检查结果。",
            item3BadgeLabel = "文件确认",
            item4Title = "回国前的携带与机场准备",
            item4Description = "请确认药品携带限制及回国前需要准备的事项。",
            item4BadgeLabel = "回国准备",
            situationalTitle = "情况确认",
            situational1Title = "确认本国携带限制物品",
            situational1Description = "仅在需要了解处方药、中草药、液体等本国携带标准时确认即可。",
            situational2Title = "长途飞行注意事项",
            situational2Description = "仅在手术·检查后有长途移动限制时确认即可。",
            noticeText = "服药及回国准备标准会因诊疗内容和各国规定而异，请同时确认医院说明及航空、海关信息。"
        )
        val Ja = AftercareReturnCheckStrings(
            bannerTitle = "診療後のケアと\n帰国前チェック",
            bannerSubtitle = "服薬、注意事項、書類の受け取り、帰国前の準備だけ確認しておけば十分です。",
            checklistTitle = "このステップで必ず確認する4つのこと",
            item1Title = "服薬方法の確認",
            item1Description = "薬の名前、服用時間、食前食後の別、服用期間を確認してください。",
            item1BadgeLabel = "薬の確認",
            item2Title = "診療後の注意事項の確認",
            item2Description = "食事・シャワー・運動が可能な時期と異常症状の基準を確認してください。",
            item2BadgeLabel = "注意事項",
            item3Title = "英文書類・検査結果の受領確認",
            item3Description = "帰国後に提出が必要な書類と検査結果を受け取ったか確認してください。",
            item3BadgeLabel = "書類確認",
            item4Title = "帰国前の持ち込み・空港準備",
            item4Description = "薬の持ち込み制限と帰国前に必要な準備項目を確認してください。",
            item4BadgeLabel = "帰国準備",
            situationalTitle = "状況別確認",
            situational1Title = "自国への持ち込み制限品の確認",
            situational1Description = "処方薬、漢方薬、液体類などの自国持ち込み基準が必要な場合のみ確認してください。",
            situational2Title = "長距離フライトの注意",
            situational2Description = "施術・検査後に長距離移動の制限がある場合のみ確認してください。",
            noticeText = "服薬や帰国準備の基準は診療内容や国ごとの規定により異なるため、病院の案内と航空会社・税関情報もあわせてご確認ください。"
        )
    }
}

data class VisaEntryCheckStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = VisaEntryCheckStrings(
            bannerTitle = "비자·입국 조건 확인",
            bannerSubtitle = "국적과 체류기간, 방문 목적에 따라 비자 또는 K-ETA가 필요할 수 있어요.",
            item1Title = "K-ETA 공식 사이트",
            item1Description = "무비자 입국 대상국이라면 K-ETA 사전 승인이 필요해요.",
            item2Title = "대한민국 비자포털",
            item2Description = "체류 목적별 비자 종류와 신청 절차를 확인할 수 있어요.",
            item3Title = "HiKorea 체류 안내",
            item3Description = "입국 후 체류 자격, 외국인등록 등 안내를 확인할 수 있어요.",
            noticeText = "본 안내는 비자 발급을 보장하지 않습니다. 정확한 자격 요건은 각 공식 사이트에서 반드시 확인해 주세요."
        )
        val En = VisaEntryCheckStrings(
            bannerTitle = "Check Visa & Entry Requirements",
            bannerSubtitle = "Depending on your nationality, length of stay, and purpose of visit, you may need a visa or K-ETA.",
            item1Title = "K-ETA Official Site",
            item1Description = "If you're from a visa-waiver country, you'll need K-ETA pre-approval.",
            item2Title = "Korea Visa Portal",
            item2Description = "Check visa types and application procedures by purpose of stay.",
            item3Title = "HiKorea Residence Guide",
            item3Description = "Check guidance on residence status and foreigner registration after entry.",
            noticeText = "This guidance does not guarantee visa issuance. Please verify exact requirements on each official site."
        )
        val Zh = VisaEntryCheckStrings(
            bannerTitle = "确认签证与入境条件",
            bannerSubtitle = "根据国籍、停留时间和访问目的，您可能需要签证或K-ETA。",
            item1Title = "K-ETA官方网站",
            item1Description = "如属于免签入境国家，需事先获得K-ETA批准。",
            item2Title = "大韩民国签证门户",
            item2Description = "可查询各停留目的对应的签证种类及申请流程。",
            item3Title = "HiKorea停留指南",
            item3Description = "可查询入境后的停留资格、外国人登记等相关说明。",
            noticeText = "本指南不保证签证发放，请务必在各官方网站确认准确的资格要求。"
        )
        val Ja = VisaEntryCheckStrings(
            bannerTitle = "ビザ・入国条件の確認",
            bannerSubtitle = "国籍、滞在期間、訪問目的によってビザまたはK-ETAが必要になる場合があります。",
            item1Title = "K-ETA公式サイト",
            item1Description = "査証免除対象国の場合、K-ETAの事前承認が必要です。",
            item2Title = "大韓民国ビザポータル",
            item2Description = "滞在目的別のビザの種類と申請手続きを確認できます。",
            item3Title = "HiKorea在留案内",
            item3Description = "入国後の在留資格、外国人登録などの案内を確認できます。",
            noticeText = "本案内はビザ発給を保証するものではありません。正確な要件は必ず各公式サイトでご確認ください。"
        )
    }
}

data class InsuranceDocumentsStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val item4Title: String,
    val item4Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = InsuranceDocumentsStrings(
            bannerTitle = "보험·서류 준비",
            bannerSubtitle = "치료비 보장 범위, 필요 서류, 청구 절차를 미리 확인하고 준비해 주세요.",
            checklistTitle = "준비 서류 체크리스트",
            item1Title = "여권 사본",
            item1Description = "출입국 심사와 병원 접수 시 필요할 수 있어요.",
            item2Title = "진료 예약 확인서",
            item2Description = "예약한 병원의 예약 확인서나 문자를 준비해 두세요.",
            item3Title = "진단서 또는 소견서",
            item3Description = "기존에 받은 진단서나 소견서가 있다면 함께 준비하세요.",
            item4Title = "진료비 영수증",
            item4Description = "보험 청구나 세금 환급에 필요할 수 있어 보관해 두세요.",
            noticeText = "본 안내는 보험 보장 범위를 보장하지 않습니다. 정확한 보장 내용은 가입한 보험사에 확인해 주세요."
        )
        val En = InsuranceDocumentsStrings(
            bannerTitle = "Prepare Insurance & Documents",
            bannerSubtitle = "Check your coverage, required documents, and claim process in advance.",
            checklistTitle = "Document Checklist",
            item1Title = "Passport Copy",
            item1Description = "May be needed for immigration checks and hospital check-in.",
            item2Title = "Appointment Confirmation",
            item2Description = "Prepare the confirmation or text message from your hospital reservation.",
            item3Title = "Medical Certificate or Opinion",
            item3Description = "If you have an existing medical certificate or opinion, bring it along.",
            item4Title = "Medical Receipt",
            item4Description = "Keep this, as it may be needed for insurance claims or tax refunds.",
            noticeText = "This guidance does not guarantee your insurance coverage. Please confirm exact coverage with your insurer."
        )
        val Zh = InsuranceDocumentsStrings(
            bannerTitle = "准备保险与文件",
            bannerSubtitle = "请提前确认治疗费用保障范围、所需文件及理赔流程。",
            checklistTitle = "准备文件清单",
            item1Title = "护照复印件",
            item1Description = "出入境审查及医院挂号时可能需要。",
            item2Title = "诊疗预约确认单",
            item2Description = "请准备好预约医院的确认单或短信。",
            item3Title = "诊断书或意见书",
            item3Description = "如有既往诊断书或意见书，请一并准备。",
            item4Title = "诊疗费收据",
            item4Description = "可能用于保险理赔或退税，请妥善保管。",
            noticeText = "本指南不保证保险保障范围，请向所投保的保险公司确认准确的保障内容。"
        )
        val Ja = InsuranceDocumentsStrings(
            bannerTitle = "保険・書類の準備",
            bannerSubtitle = "治療費の補償範囲、必要書類、請求手続きを事前に確認し準備してください。",
            checklistTitle = "準備書類チェックリスト",
            item1Title = "パスポートのコピー",
            item1Description = "出入国審査や病院受付の際に必要になる場合があります。",
            item2Title = "診療予約確認書",
            item2Description = "予約した病院の予約確認書やメッセージを準備してください。",
            item3Title = "診断書または所見書",
            item3Description = "既に受け取った診断書や所見書があれば一緒に準備してください。",
            item4Title = "診療費領収書",
            item4Description = "保険請求や税還付に必要になる場合があるため保管しておいてください。",
            noticeText = "本案内は保険の補償範囲を保証するものではありません。正確な補償内容は加入している保険会社にご確認ください。"
        )
    }
}

data class HospitalInquiryStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val item4Title: String,
    val item4Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = HospitalInquiryStrings(
            bannerTitle = "병원 문의 전 정보 정리",
            bannerSubtitle = "여권, 체류기간, 방문 목적, 연락처 등 기본 정보를 미리 정리하면 입국과 병원 방문이 편리해요.",
            checklistTitle = "정보 정리 체크리스트",
            item1Title = "여권 유효기간 확인",
            item1Description = "입국일 기준 여권 유효기간이 충분한지 확인하세요.",
            item2Title = "체류기간 및 방문 목적 정리",
            item2Description = "예상 체류기간과 방문 목적을 정리해두면 문의가 수월해요.",
            item3Title = "연락처 및 숙소 정보 준비",
            item3Description = "현지 연락처와 숙소 정보를 미리 준비해두세요.",
            item4Title = "병원 정보 및 일정 정리",
            item4Description = "문의할 병원명과 희망 일정을 정리해두세요.",
            noticeText = "문의 전 정보를 미리 정리해두면 병원과의 소통이 더 원활해져요."
        )
        val En = HospitalInquiryStrings(
            bannerTitle = "Organize Info Before Contacting a Hospital",
            bannerSubtitle = "Organizing basics like your passport, stay duration, visit purpose, and contact info in advance makes entry and hospital visits easier.",
            checklistTitle = "Info Checklist",
            item1Title = "Check Passport Validity",
            item1Description = "Check that your passport is valid long enough as of your entry date.",
            item2Title = "Organize Stay Duration & Purpose",
            item2Description = "Organizing your expected stay and visit purpose makes inquiries easier.",
            item3Title = "Prepare Contact & Accommodation Info",
            item3Description = "Prepare your local contact number and accommodation details in advance.",
            item4Title = "Organize Hospital Info & Schedule",
            item4Description = "Organize the hospital name you'll contact and your preferred schedule.",
            noticeText = "Organizing information beforehand makes communication with the hospital smoother."
        )
        val Zh = HospitalInquiryStrings(
            bannerTitle = "整理病院咨询前的信息",
            bannerSubtitle = "提前整理护照、停留时间、访问目的、联系方式等基本信息，可让入境和就诊更加顺利。",
            checklistTitle = "信息整理清单",
            item1Title = "确认护照有效期",
            item1Description = "请确认以入境日为准，护照有效期是否充足。",
            item2Title = "整理停留期限及访问目的",
            item2Description = "提前整理预计停留时间及访问目的，可让咨询更顺利。",
            item3Title = "准备联系方式及住宿信息",
            item3Description = "请提前准备好当地联系方式及住宿信息。",
            item4Title = "整理医院信息及日程",
            item4Description = "请整理好要咨询的医院名称及期望日程。",
            noticeText = "提前整理好信息，可让与医院的沟通更加顺畅。"
        )
        val Ja = HospitalInquiryStrings(
            bannerTitle = "病院への問い合わせ前の情報整理",
            bannerSubtitle = "パスポート、滞在期間、訪問目的、連絡先など基本情報を事前に整理しておくと、入国や病院訪問がスムーズになります。",
            checklistTitle = "情報整理チェックリスト",
            item1Title = "パスポート有効期限の確認",
            item1Description = "入国日を基準にパスポートの有効期限が十分か確認してください。",
            item2Title = "滞在期間・訪問目的の整理",
            item2Description = "予想される滞在期間と訪問目的を整理しておくと問い合わせがスムーズになります。",
            item3Title = "連絡先・宿泊先情報の準備",
            item3Description = "現地の連絡先と宿泊先情報を事前に準備してください。",
            item4Title = "病院情報・日程の整理",
            item4Description = "問い合わせる病院名と希望日程を整理しておいてください。",
            noticeText = "問い合わせ前に情報を整理しておくと、病院とのやり取りがよりスムーズになります。"
        )
    }
}

data class PreInquiryInformationStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val item4Title: String,
    val item4Description: String,
    val item5Title: String,
    val item5Description: String,
    val item6Title: String,
    val item6Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = PreInquiryInformationStrings(
            bannerTitle = "문의 전 전달할 정보 정리",
            bannerSubtitle = "희망 진료와 증상, 방문 시기 등 핵심 정보만 정리해 두면 병원이 더 정확하게 안내할 수 있어요.",
            checklistTitle = "전달 정보 체크리스트",
            item1Title = "희망 진료·검사 내용",
            item1Description = "받고 싶은 진료나 검사 항목을 구체적으로 정리하세요.",
            item2Title = "현재 증상과 기존 자료",
            item2Description = "현재 증상과 기존 진단서·검사 결과가 있다면 함께 정리하세요.",
            item3Title = "방문 희망 시기",
            item3Description = "방문을 원하는 대략적인 날짜나 기간을 정리하세요.",
            item4Title = "기본 인적 정보",
            item4Description = "이름, 생년월일, 국적 등 기본 정보를 준비하세요.",
            item5Title = "예상 비용 문의 여부",
            item5Description = "예상 비용을 함께 문의할지 미리 정리해두세요.",
            item6Title = "영문 서류 필요 여부",
            item6Description = "귀국 후 필요한 영문 서류가 있는지 미리 확인하세요.",
            noticeText = "정리한 정보는 병원 문의 시 그대로 전달하면 상담이 더 빠르고 정확해져요."
        )
        val En = PreInquiryInformationStrings(
            bannerTitle = "Organize Info to Share Before Inquiring",
            bannerSubtitle = "Organizing key details like desired treatment, symptoms, and visit timing helps the hospital guide you more accurately.",
            checklistTitle = "Info to Share Checklist",
            item1Title = "Desired Treatment/Exam",
            item1Description = "Specify the treatment or exam you'd like to receive.",
            item2Title = "Current Symptoms & Existing Records",
            item2Description = "Organize your current symptoms along with any existing medical certificates or test results.",
            item3Title = "Preferred Visit Timing",
            item3Description = "Organize the approximate date or period you'd like to visit.",
            item4Title = "Basic Personal Info",
            item4Description = "Prepare basics such as your name, date of birth, and nationality.",
            item5Title = "Whether to Ask About Estimated Cost",
            item5Description = "Decide in advance whether you'll also ask about estimated costs.",
            item6Title = "Whether English Documents Are Needed",
            item6Description = "Check in advance whether you'll need English-language documents after returning home.",
            noticeText = "Sharing this organized information directly with the hospital makes consultations faster and more accurate."
        )
        val Zh = PreInquiryInformationStrings(
            bannerTitle = "整理咨询前要传达的信息",
            bannerSubtitle = "只要整理好期望诊疗、症状、访问时间等核心信息，医院就能提供更准确的指导。",
            checklistTitle = "传达信息清单",
            item1Title = "期望诊疗·检查内容",
            item1Description = "请具体整理希望接受的诊疗或检查项目。",
            item2Title = "现有症状与既往资料",
            item2Description = "请一并整理现有症状及既往诊断书、检查结果（如有）。",
            item3Title = "期望访问时间",
            item3Description = "请整理希望访问的大致日期或期间。",
            item4Title = "基本个人信息",
            item4Description = "请准备姓名、出生日期、国籍等基本信息。",
            item5Title = "是否咨询预估费用",
            item5Description = "请提前想好是否要一并询问预估费用。",
            item6Title = "是否需要英文文件",
            item6Description = "请提前确认回国后是否需要英文文件。",
            noticeText = "将整理好的信息直接告知医院，可让咨询更快速、更准确。"
        )
        val Ja = PreInquiryInformationStrings(
            bannerTitle = "問い合わせ前に伝える情報の整理",
            bannerSubtitle = "希望する診療、症状、訪問時期など重要な情報を整理しておくと、病院がより的確に案内できます。",
            checklistTitle = "伝達情報チェックリスト",
            item1Title = "希望する診療・検査内容",
            item1Description = "受けたい診療や検査項目を具体的に整理してください。",
            item2Title = "現在の症状と既往資料",
            item2Description = "現在の症状と、既往の診断書・検査結果があれば一緒に整理してください。",
            item3Title = "訪問希望時期",
            item3Description = "訪問を希望するおおよその日付や期間を整理してください。",
            item4Title = "基本個人情報",
            item4Description = "氏名、生年月日、国籍などの基本情報を準備してください。",
            item5Title = "概算費用の問い合わせ有無",
            item5Description = "概算費用も一緒に問い合わせるか事前に整理しておいてください。",
            item6Title = "英文書類の要否確認",
            item6Description = "帰国後に必要な英文書類があるか事前に確認してください。",
            noticeText = "整理した情報をそのまま病院に伝えると、相談がより早く正確になります。"
        )
    }
}

data class PassportReservationInfoStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val item4Title: String,
    val item4Description: String,
    val item5Title: String,
    val item5Description: String,
    val item6Title: String,
    val item6Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = PassportReservationInfoStrings(
            bannerTitle = "여권·예약정보 준비",
            bannerSubtitle = "여권과 예약 정보를 미리 준비하면 접수가 더 빨라져요.",
            checklistTitle = "준비물 체크리스트",
            item1Title = "여권 신원 확인",
            item1Description = "여권 또는 외국인등록증으로 신원을 확인할 수 있도록 준비하세요.",
            item2Title = "예약 확인",
            item2Description = "예약 일시와 진료과를 다시 한번 확인하세요.",
            item3Title = "환자 성명 확인",
            item3Description = "여권상 영문 성명과 예약자 정보가 일치하는지 확인하세요.",
            item4Title = "연락처 확인",
            item4Description = "병원에서 연락 가능한 전화번호나 메신저를 준비하세요.",
            item5Title = "동반자 확인",
            item5Description = "동반자가 있다면 인원과 관계를 미리 정리하세요.",
            item6Title = "도착 예정 시간",
            item6Description = "병원 도착 예정 시간을 미리 확인해두면 접수가 수월해요.",
            noticeText = "정리한 정보는 접수 시 안내 직원에게 전달하면 더 빠르게 도와드려요."
        )
        val En = PassportReservationInfoStrings(
            bannerTitle = "Prepare Passport & Reservation Info",
            bannerSubtitle = "Preparing your passport and reservation details in advance speeds up check-in.",
            checklistTitle = "Checklist",
            item1Title = "Passport ID Verification",
            item1Description = "Have your passport or foreigner registration card ready for identity verification.",
            item2Title = "Reservation Confirmation",
            item2Description = "Double-check your appointment date, time, and department.",
            item3Title = "Patient Name Verification",
            item3Description = "Check that your passport's English name matches the reservation details.",
            item4Title = "Contact Info",
            item4Description = "Prepare a phone number or messenger contact the hospital can reach you at.",
            item5Title = "Companion Info",
            item5Description = "If you have a companion, organize the number of people and their relationship to you.",
            item6Title = "Expected Arrival Time",
            item6Description = "Knowing your expected arrival time in advance makes check-in easier.",
            noticeText = "Sharing this organized information with front-desk staff at check-in helps them assist you faster."
        )
        val Zh = PassportReservationInfoStrings(
            bannerTitle = "准备护照与预约信息",
            bannerSubtitle = "提前准备好护照及预约信息，可让挂号更快速。",
            checklistTitle = "准备物品清单",
            item1Title = "护照身份确认",
            item1Description = "请准备好护照或外国人登记证以供身份核实。",
            item2Title = "预约确认",
            item2Description = "请再次确认预约日期时间及诊疗科室。",
            item3Title = "患者姓名确认",
            item3Description = "请确认护照上的英文姓名与预约人信息是否一致。",
            item4Title = "联系方式确认",
            item4Description = "请准备医院可联系到的电话号码或即时通讯方式。",
            item5Title = "同行人员确认",
            item5Description = "如有同行人员，请提前整理人数及关系。",
            item6Title = "预计到达时间",
            item6Description = "提前确认预计到达医院的时间，可让挂号更顺利。",
            noticeText = "挂号时将整理好的信息告知工作人员，能获得更快速的协助。"
        )
        val Ja = PassportReservationInfoStrings(
            bannerTitle = "パスポート・予約情報の準備",
            bannerSubtitle = "パスポートと予約情報を事前に準備しておくと、受付がより早くなります。",
            checklistTitle = "持ち物チェックリスト",
            item1Title = "パスポートによる本人確認",
            item1Description = "パスポートまたは外国人登録証で本人確認ができるよう準備してください。",
            item2Title = "予約の確認",
            item2Description = "予約日時と診療科をもう一度確認してください。",
            item3Title = "患者氏名の確認",
            item3Description = "パスポートの英字氏名と予約者情報が一致しているか確認してください。",
            item4Title = "連絡先の確認",
            item4Description = "病院が連絡できる電話番号やメッセンジャーを準備してください。",
            item5Title = "同伴者の確認",
            item5Description = "同伴者がいる場合、人数と続柄を事前に整理してください。",
            item6Title = "到着予定時刻",
            item6Description = "病院への到着予定時刻を事前に確認しておくと受付がスムーズです。",
            noticeText = "整理した情報を受付スタッフに伝えると、より迅速に対応してもらえます。"
        )
    }
}

data class MedicalRecordsTestResultsStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val item4Title: String,
    val item4Description: String,
    val situationalTitle: String,
    val situational1Title: String,
    val situational1Description: String,
    val situational2Title: String,
    val situational2Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = MedicalRecordsTestResultsStrings(
            bannerTitle = "기존 진단서·검사결과 준비",
            bannerSubtitle = "기존 자료를 미리 준비하면 의료진이 상태를 더 빠르게 이해할 수 있어요.",
            checklistTitle = "준비하면 좋은 자료",
            item1Title = "기존 진단서 또는 소견서",
            item1Description = "받았던 진단 내용이나 소견서를 준비하세요.",
            item2Title = "검사결과 또는 영상자료",
            item2Description = "혈액검사, 영상검사 결과가 있다면 함께 준비하세요.",
            item3Title = "복용 중인 약 정보",
            item3Description = "현재 복용 중인 약 이름이나 처방 내용을 정리하세요.",
            item4Title = "알레르기·기저질환 정보",
            item4Description = "알레르기나 중요한 건강정보가 있다면 함께 전달하세요.",
            situationalTitle = "상황에 따라 준비하세요",
            situational1Title = "영문 또는 번역 자료",
            situational1Description = "있는 경우 함께 가져가면 설명이 더 쉬워져요.",
            situational2Title = "디지털 파일 준비",
            situational2Description = "사진이나 PDF 파일도 함께 준비해 두세요.",
            noticeText = "모든 자료가 꼭 필요한 것은 아니며 병원이 요청한 자료를 우선 준비해 주세요."
        )
        val En = MedicalRecordsTestResultsStrings(
            bannerTitle = "Prepare Existing Records & Test Results",
            bannerSubtitle = "Preparing your existing records in advance helps medical staff understand your condition faster.",
            checklistTitle = "Recommended Materials",
            item1Title = "Existing Medical Certificate or Opinion",
            item1Description = "Prepare any prior diagnosis details or medical opinion you've received.",
            item2Title = "Test Results or Imaging",
            item2Description = "If you have blood test or imaging results, bring them along too.",
            item3Title = "Current Medication Info",
            item3Description = "Organize the names or prescription details of medications you're currently taking.",
            item4Title = "Allergy & Underlying Condition Info",
            item4Description = "If you have allergies or important health conditions, share them too.",
            situationalTitle = "Prepare If Applicable",
            situational1Title = "English or Translated Materials",
            situational1Description = "If available, bringing these along makes explanations easier.",
            situational2Title = "Digital File Prep",
            situational2Description = "Prepare photos or PDF files as well.",
            noticeText = "Not all materials are strictly required — prioritize what the hospital has requested."
        )
        val Zh = MedicalRecordsTestResultsStrings(
            bannerTitle = "准备既往诊断书·检查结果",
            bannerSubtitle = "提前准备既往资料，可让医护人员更快了解您的状况。",
            checklistTitle = "建议准备的资料",
            item1Title = "既往诊断书或意见书",
            item1Description = "请准备既往接受过的诊断内容或意见书。",
            item2Title = "检查结果或影像资料",
            item2Description = "如有血液检查、影像检查结果，请一并准备。",
            item3Title = "现服用药物信息",
            item3Description = "请整理目前服用药物的名称或处方内容。",
            item4Title = "过敏·基础疾病信息",
            item4Description = "如有过敏或重要健康信息，请一并告知。",
            situationalTitle = "视情况准备",
            situational1Title = "英文或翻译资料",
            situational1Description = "如有的话一并携带，可让说明更容易。",
            situational2Title = "准备电子文件",
            situational2Description = "请一并准备好照片或PDF文件。",
            noticeText = "并非所有资料都是必需的，请优先准备医院所要求的资料。"
        )
        val Ja = MedicalRecordsTestResultsStrings(
            bannerTitle = "既往の診断書・検査結果の準備",
            bannerSubtitle = "既往の資料を事前に準備しておくと、医療スタッフが状態をより早く把握できます。",
            checklistTitle = "準備しておくとよい資料",
            item1Title = "既往の診断書または所見書",
            item1Description = "これまでに受けた診断内容や所見書を準備してください。",
            item2Title = "検査結果または画像資料",
            item2Description = "血液検査、画像検査の結果があれば一緒に準備してください。",
            item3Title = "服用中の薬の情報",
            item3Description = "現在服用している薬の名前や処方内容を整理してください。",
            item4Title = "アレルギー・既往症の情報",
            item4Description = "アレルギーや重要な健康情報があれば一緒に伝えてください。",
            situationalTitle = "状況に応じて準備してください",
            situational1Title = "英文または翻訳資料",
            situational1Description = "お持ちの場合、一緒に持参すると説明がしやすくなります。",
            situational2Title = "デジタルファイルの準備",
            situational2Description = "写真やPDFファイルも一緒に準備しておいてください。",
            noticeText = "すべての資料が必ず必要というわけではありません。病院から求められた資料を優先して準備してください。"
        )
    }
}

data class TotalCostCoverageCheckStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val item4Title: String,
    val item4Description: String,
    val questionsTitle: String,
    val question1: String,
    val question2: String,
    val question3: String,
    val noticeText: String
) {
    companion object {
        val Ko = TotalCostCoverageCheckStrings(
            bannerTitle = "총 비용과 포함 항목 확인",
            bannerSubtitle = "총 비용과 포함·불포함 항목을 결제 전 확인하세요.",
            checklistTitle = "먼저 확인하세요",
            item1Title = "진료·검사·시술비",
            item1Description = "상담료, 검사비, 시술비 등 기본 비용 항목을 확인하세요.",
            item2Title = "약제비·재료비 포함 여부",
            item2Description = "약값, 소모품, 재료비가 총 비용에 포함되는지 확인하세요.",
            item3Title = "추가 비용 발생 가능 항목",
            item3Description = "추가 검사, 마취, 입원, 병실 이용 시 비용이 달라질 수 있어요.",
            item4Title = "보증금·선납금 필요 여부",
            item4Description = "입원이나 예약 조건에 따라 선납금이 필요한 경우만 확인하세요.",
            questionsTitle = "이렇게 물어보세요",
            question1 = "총 비용에 포함되지 않는 항목이 있나요?",
            question2 = "추가 검사나 입원이 생기면 비용이 얼마나 달라지나요?",
            question3 = "보증금 또는 선납금이 필요한가요?",
            noticeText = "최종 비용은 진료 내용에 따라 달라질 수 있으니 병원 안내를 기준으로 확인해 주세요."
        )
        val En = TotalCostCoverageCheckStrings(
            bannerTitle = "Check Total Cost & Coverage",
            bannerSubtitle = "Check the total cost and what's included or excluded before paying.",
            checklistTitle = "Check First",
            item1Title = "Treatment/Exam/Procedure Fees",
            item1Description = "Check basic cost items such as consultation, exam, and procedure fees.",
            item2Title = "Medication/Material Fee Inclusion",
            item2Description = "Check whether medication, supplies, and material costs are included in the total.",
            item3Title = "Possible Additional Costs",
            item3Description = "Costs may vary with additional exams, anesthesia, admission, or room use.",
            item4Title = "Deposit/Prepayment Requirement",
            item4Description = "Check this only if a deposit is required based on admission or reservation terms.",
            questionsTitle = "Try Asking",
            question1 = "Are there any items not included in the total cost?",
            question2 = "How much would the cost change if additional exams or admission are needed?",
            question3 = "Is a deposit or prepayment required?",
            noticeText = "Final costs may vary depending on treatment details, so please confirm based on hospital guidance."
        )
        val Zh = TotalCostCoverageCheckStrings(
            bannerTitle = "确认总费用与包含项目",
            bannerSubtitle = "请在付款前确认总费用及包含·不包含的项目。",
            checklistTitle = "请先确认",
            item1Title = "诊疗·检查·手术费",
            item1Description = "请确认咨询费、检查费、手术费等基本费用项目。",
            item2Title = "确认是否包含药费·材料费",
            item2Description = "请确认药费、耗材、材料费是否包含在总费用中。",
            item3Title = "可能产生的额外费用项目",
            item3Description = "如有额外检查、麻醉、住院、病房使用，费用可能会有所不同。",
            item4Title = "确认是否需要押金·预付款",
            item4Description = "仅在因住院或预约条件需要预付款时确认即可。",
            questionsTitle = "不妨这样询问",
            question1 = "有哪些项目不包含在总费用中？",
            question2 = "如果需要额外检查或住院，费用会有多大变化？",
            question3 = "是否需要押金或预付款？",
            noticeText = "最终费用会因诊疗内容而异，请以医院的说明为准进行确认。"
        )
        val Ja = TotalCostCoverageCheckStrings(
            bannerTitle = "総費用と含まれる項目の確認",
            bannerSubtitle = "支払い前に総費用と含まれる／含まれない項目を確認してください。",
            checklistTitle = "まず確認してください",
            item1Title = "診療・検査・施術費",
            item1Description = "相談料、検査費、施術費など基本費用項目を確認してください。",
            item2Title = "薬剤費・材料費の含有確認",
            item2Description = "薬代、消耗品、材料費が総費用に含まれるか確認してください。",
            item3Title = "追加費用が発生し得る項目",
            item3Description = "追加検査、麻酔、入院、病室利用などで費用が変わる場合があります。",
            item4Title = "保証金・前払金の要否",
            item4Description = "入院や予約条件により前払金が必要な場合のみ確認してください。",
            questionsTitle = "こう聞いてみましょう",
            question1 = "総費用に含まれない項目はありますか？",
            question2 = "追加検査や入院が発生した場合、費用はどれくらい変わりますか？",
            question3 = "保証金または前払金は必要ですか？",
            noticeText = "最終費用は診療内容によって異なる場合があるため、病院の案内を基準にご確認ください。"
        )
    }
}

data class PaymentMethodCheckStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val item4Title: String,
    val item4Description: String,
    val situationalTitle: String,
    val situational1Title: String,
    val situational1Description: String,
    val situational2Title: String,
    val situational2Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = PaymentMethodCheckStrings(
            bannerTitle = "결제 가능 수단 확인",
            bannerSubtitle = "치료 전이나 퇴원 전에 실제로 사용 가능한 결제 수단을 미리 확인해 보세요.",
            checklistTitle = "결제 전에 체크하세요",
            item1Title = "원화 기준 결제 여부",
            item1Description = "많은 병원이 원화 기준으로 결제하니 실제 청구 통화를 확인하세요.",
            item2Title = "해외 카드 사용 가능 여부",
            item2Description = "Visa, Mastercard, Amex 등 사용 가능한 해외 카드 브랜드를 확인하세요.",
            item3Title = "현금·계좌이체·송금 가능 여부",
            item3Description = "카드 외에 현금, 계좌이체, 해외송금 결제가 가능한지 확인하세요.",
            item4Title = "보증금·선납금 결제 방식",
            item4Description = "선납금이 필요한 경우 어떤 수단으로 결제해야 하는지 확인하세요.",
            situationalTitle = "함께 확인하면 좋아요",
            situational1Title = "카드 한도·해외결제 차단",
            situational1Description = "결제 전에 카드 한도와 해외 사용 차단 여부를 미리 확인하세요.",
            situational2Title = "현장 결제 위치",
            situational2Description = "응급실, 외래, 입원 등 어디에서 수납하는지 확인하면 더 편리해요.",
            noticeText = "병원마다 가능한 결제 수단이 다르니 실제 수납 전 다시 확인해 주세요."
        )
        val En = PaymentMethodCheckStrings(
            bannerTitle = "Check Available Payment Methods",
            bannerSubtitle = "Before treatment or discharge, check which payment methods are actually accepted.",
            checklistTitle = "Check Before Paying",
            item1Title = "Payment in Korean Won",
            item1Description = "Many hospitals charge in Korean won, so check the actual billing currency.",
            item2Title = "Overseas Card Availability",
            item2Description = "Check which international card brands (Visa, Mastercard, Amex, etc.) are accepted.",
            item3Title = "Cash/Transfer Availability",
            item3Description = "Besides cards, check whether cash, bank transfer, or overseas remittance is accepted.",
            item4Title = "Deposit Payment Method",
            item4Description = "If a deposit is required, check which payment method to use.",
            situationalTitle = "Also Worth Checking",
            situational1Title = "Card Limit & Overseas Block",
            situational1Description = "Before paying, check your card limit and whether overseas use is blocked.",
            situational2Title = "On-site Payment Location",
            situational2Description = "Knowing where to pay — ER, outpatient, or inpatient — makes things easier.",
            noticeText = "Accepted payment methods vary by hospital, so please double-check before actual payment."
        )
        val Zh = PaymentMethodCheckStrings(
            bannerTitle = "确认可用付款方式",
            bannerSubtitle = "请在治疗前或出院前提前确认实际可用的付款方式。",
            checklistTitle = "付款前请确认",
            item1Title = "确认是否以韩元结算",
            item1Description = "许多医院以韩元结算，请确认实际结算货币。",
            item2Title = "确认能否使用海外卡",
            item2Description = "请确认可使用的海外卡品牌，如Visa、Mastercard、Amex等。",
            item3Title = "确认能否现金·转账·汇款",
            item3Description = "除信用卡外，请确认能否使用现金、转账或海外汇款支付。",
            item4Title = "确认押金·预付款支付方式",
            item4Description = "如需预付款，请确认应使用哪种支付方式。",
            situationalTitle = "建议一并确认",
            situational1Title = "确认卡片额度·海外支付限制",
            situational1Description = "付款前请确认卡片额度及是否设置了海外使用限制。",
            situational2Title = "确认现场收费地点",
            situational2Description = "确认在急诊、门诊、住院等哪个地方收费会更方便。",
            noticeText = "各医院可用的付款方式不同，请在实际付款前再次确认。"
        )
        val Ja = PaymentMethodCheckStrings(
            bannerTitle = "利用可能な決済手段の確認",
            bannerSubtitle = "治療前や退院前に、実際に利用できる決済手段を事前に確認しておきましょう。",
            checklistTitle = "支払い前にチェックしてください",
            item1Title = "ウォン建て決済かの確認",
            item1Description = "多くの病院はウォン建てで決済するため、実際の請求通貨を確認してください。",
            item2Title = "海外カード利用可否の確認",
            item2Description = "Visa、Mastercard、Amexなど利用可能な海外カードブランドを確認してください。",
            item3Title = "現金・振込・送金の可否",
            item3Description = "カード以外に現金、口座振込、海外送金での支払いが可能か確認してください。",
            item4Title = "保証金・前払金の支払い方法",
            item4Description = "前払金が必要な場合、どの手段で支払うべきか確認してください。",
            situationalTitle = "あわせて確認しておくとよいこと",
            situational1Title = "カード限度額・海外利用制限",
            situational1Description = "支払い前にカードの利用限度額と海外利用制限の有無を確認してください。",
            situational2Title = "現地の支払い場所",
            situational2Description = "救急、外来、入院など、どこで支払うか確認しておくとより便利です。",
            noticeText = "利用可能な決済手段は病院によって異なるため、実際の支払い前に再度ご確認ください。"
        )
    }
}

data class ReceiptInsuranceDocumentsStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val item4Title: String,
    val item4Description: String,
    val situationalTitle: String,
    val situational1Title: String,
    val situational1Description: String,
    val situational2Title: String,
    val situational2Description: String,
    val questionsTitle: String,
    val question1: String,
    val question2: String,
    val question3: String,
    val noticeText: String
) {
    companion object {
        val Ko = ReceiptInsuranceDocumentsStrings(
            bannerTitle = "영수증·보험 청구 서류 확인",
            bannerSubtitle = "결제 후 필요한 서류의 발급 가능 여부를 확인하세요.",
            checklistTitle = "받아야 할 서류",
            item1Title = "영수증",
            item1Description = "결제 금액을 확인할 수 있는 기본 영수증을 받아두세요.",
            item2Title = "진료비 세부내역서",
            item2Description = "비용 항목이 자세히 적힌 서류가 필요한지 확인하세요.",
            item3Title = "보험 청구용 서류",
            item3Description = "보험사에서 요구하는 진단서, 소견서, 확인서 여부를 확인하세요.",
            item4Title = "영문 서류 발급 가능 여부",
            item4Description = "본국 제출이 필요한 경우 영문 발급 가능 여부를 함께 확인하세요.",
            situationalTitle = "상황별 확인",
            situational1Title = "보험사 요구 양식 여부",
            situational1Description = "보험사 전용 양식이나 추가 증빙 서류가 필요한 경우만 확인하세요.",
            situational2Title = "미용시술 세금 환급 가능 여부",
            situational2Description = "피부·미용 시술을 받은 경우에만 세금 환급 가능 여부를 확인하세요.",
            questionsTitle = "이렇게 요청해 보세요",
            question1 = "영수증과 진료비 세부내역서를 받을 수 있을까요?",
            question2 = "보험 청구에 필요한 서류를 함께 발급받을 수 있나요?",
            question3 = "영문 서류 발급이 가능하면 언제 받을 수 있나요?",
            noticeText = "필요 서류는 보험사와 병원 기준이 다를 수 있으니 둘 다 확인해 주세요."
        )
        val En = ReceiptInsuranceDocumentsStrings(
            bannerTitle = "Check Receipt & Insurance Documents",
            bannerSubtitle = "Check whether the documents you'll need after payment can be issued.",
            checklistTitle = "Documents to Receive",
            item1Title = "Receipt",
            item1Description = "Get a basic receipt showing the amount paid.",
            item2Title = "Itemized Statement",
            item2Description = "Check whether you need a document with a detailed cost breakdown.",
            item3Title = "Insurance Claim Documents",
            item3Description = "Check whether your insurer requires a medical certificate, opinion, or confirmation.",
            item4Title = "English Document Availability",
            item4Description = "If you need to submit documents at home, also check whether English versions can be issued.",
            situationalTitle = "Situational Checks",
            situational1Title = "Insurer-required Forms",
            situational1Description = "Check this only if your insurer requires a specific form or extra supporting documents.",
            situational2Title = "Tax Refund for Cosmetic Procedures",
            situational2Description = "Check tax refund eligibility only if you received a skin or cosmetic procedure.",
            questionsTitle = "Try Asking For",
            question1 = "Could I get a receipt and an itemized statement?",
            question2 = "Can I also get the documents needed for my insurance claim?",
            question3 = "If English documents are available, when can I receive them?",
            noticeText = "Required documents may differ between your insurer and the hospital, so please check both."
        )
        val Zh = ReceiptInsuranceDocumentsStrings(
            bannerTitle = "确认收据·保险理赔文件",
            bannerSubtitle = "请确认付款后所需文件是否可以开具。",
            checklistTitle = "需要领取的文件",
            item1Title = "收据",
            item1Description = "请领取可确认付款金额的基本收据。",
            item2Title = "诊疗费明细单",
            item2Description = "请确认是否需要详细列明费用项目的文件。",
            item3Title = "保险理赔用文件",
            item3Description = "请确认保险公司要求的诊断书、意见书、确认书等文件。",
            item4Title = "确认能否开具英文文件",
            item4Description = "如需在本国提交，请一并确认能否开具英文版文件。",
            situationalTitle = "情况确认",
            situational1Title = "确认保险公司要求的表格",
            situational1Description = "仅在需要保险公司专用表格或额外证明文件时确认即可。",
            situational2Title = "确认美容项目退税资格",
            situational2Description = "仅在接受皮肤·美容项目后确认是否可退税。",
            questionsTitle = "不妨这样提出请求",
            question1 = "可以给我收据和诊疗费明细单吗？",
            question2 = "可以一并开具保险理赔所需的文件吗？",
            question3 = "如果可以开具英文文件，什么时候能拿到？",
            noticeText = "所需文件因保险公司和医院标准而异，请两方都进行确认。"
        )
        val Ja = ReceiptInsuranceDocumentsStrings(
            bannerTitle = "領収書・保険請求書類の確認",
            bannerSubtitle = "支払い後に必要な書類が発行可能か確認してください。",
            checklistTitle = "受け取るべき書類",
            item1Title = "領収書",
            item1Description = "支払い金額を確認できる基本の領収書を受け取っておいてください。",
            item2Title = "診療費明細書",
            item2Description = "費用項目が詳しく記載された書類が必要か確認してください。",
            item3Title = "保険請求用書類",
            item3Description = "保険会社が求める診断書、所見書、確認書の有無を確認してください。",
            item4Title = "英文書類の発行可否",
            item4Description = "本国での提出が必要な場合、英文発行の可否もあわせて確認してください。",
            situationalTitle = "状況別確認",
            situational1Title = "保険会社指定様式の要否",
            situational1Description = "保険会社専用の様式や追加の証憑書類が必要な場合のみ確認してください。",
            situational2Title = "美容施術の税還付可否",
            situational2Description = "皮膚・美容施術を受けた場合のみ、税還付の可否を確認してください。",
            questionsTitle = "こうお願いしてみましょう",
            question1 = "領収書と診療費明細書をいただけますか？",
            question2 = "保険請求に必要な書類も一緒に発行していただけますか？",
            question3 = "英文書類の発行が可能な場合、いつ受け取れますか？",
            noticeText = "必要書類は保険会社と病院の基準により異なる場合があるため、両方をご確認ください。"
        )
    }
}

data class MedicationScheduleStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val item4Title: String,
    val item4Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = MedicationScheduleStrings(
            bannerTitle = "약 복용 방법 확인",
            bannerSubtitle = "약 이름과 복용 시간, 복용 기간을 미리 확인하면 안전하게 복용할 수 있어요.",
            checklistTitle = "복용 전 체크리스트",
            item1Title = "약 이름 확인",
            item1Description = "처방받은 약 이름과 성분을 확인해두세요.",
            item2Title = "복용 시간·식전후 여부 확인",
            item2Description = "정해진 복용 시간과 식전·식후 여부를 놓치지 않도록 확인하세요.",
            item3Title = "복용 기간 확인",
            item3Description = "며칠간 복용해야 하는지, 언제까지 복용하는지 확인하세요.",
            item4Title = "보관 및 주의사항 확인",
            item4Description = "보관 방법과 함께 복용하면 안 되는 약이 있는지 확인하세요.",
            noticeText = "정확한 복용법은 처방전과 약사 안내를 기준으로 다시 확인해 주세요."
        )
        val En = MedicationScheduleStrings(
            bannerTitle = "Check How to Take Your Medication",
            bannerSubtitle = "Checking the medication name, timing, and duration in advance helps you take it safely.",
            checklistTitle = "Before-taking Checklist",
            item1Title = "Check Medication Name",
            item1Description = "Check the name and ingredients of your prescribed medication.",
            item2Title = "Check Timing & Before/After Meals",
            item2Description = "Make sure not to miss the set timing and whether it's before or after meals.",
            item3Title = "Check Duration",
            item3Description = "Check how many days you need to take it and until when.",
            item4Title = "Check Storage & Precautions",
            item4Description = "Check storage instructions and any medications that shouldn't be combined.",
            noticeText = "Please verify exact dosage instructions based on your prescription and pharmacist's guidance."
        )
        val Zh = MedicationScheduleStrings(
            bannerTitle = "确认服药方法",
            bannerSubtitle = "提前确认药品名称、服用时间及服药期限，可安全服用。",
            checklistTitle = "服药前检查清单",
            item1Title = "确认药品名称",
            item1Description = "请确认所处方药品的名称及成分。",
            item2Title = "确认服用时间及饭前饭后",
            item2Description = "请务必确认规定的服用时间及饭前饭后要求。",
            item3Title = "确认服药期限",
            item3Description = "请确认需服用几天、服用至何时。",
            item4Title = "确认保管方式及注意事项",
            item4Description = "请确认保管方法及是否有不可同服的药物。",
            noticeText = "准确的服用方法请以处方及药剂师说明为准再次确认。"
        )
        val Ja = MedicationScheduleStrings(
            bannerTitle = "服薬方法の確認",
            bannerSubtitle = "薬の名前、服用時間、服用期間を事前に確認しておくと安全に服用できます。",
            checklistTitle = "服用前チェックリスト",
            item1Title = "薬の名前の確認",
            item1Description = "処方された薬の名前と成分を確認しておいてください。",
            item2Title = "服用時間・食前食後の確認",
            item2Description = "決められた服用時間と食前・食後の別を忘れずに確認してください。",
            item3Title = "服用期間の確認",
            item3Description = "何日間服用する必要があるか、いつまで服用するか確認してください。",
            item4Title = "保管・注意事項の確認",
            item4Description = "保管方法と、一緒に服用してはいけない薬がないか確認してください。",
            noticeText = "正確な服用方法は処方箋と薬剤師の案内を基準に再度ご確認ください。"
        )
    }
}

data class PostTreatmentPrecautionsStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val item4Title: String,
    val item4Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = PostTreatmentPrecautionsStrings(
            bannerTitle = "진료 후 주의사항 확인",
            bannerSubtitle = "식사, 샤워, 운동 가능 시점과 이상 증상 기준을 미리 확인하세요.",
            checklistTitle = "회복 중 체크리스트",
            item1Title = "식사 가능 시점 확인",
            item1Description = "금식 해제 시점과 먹어도 되는 음식을 확인하세요.",
            item2Title = "샤워·운동 제한 확인",
            item2Description = "샤워, 사우나, 운동이 가능한 시점을 확인하세요.",
            item3Title = "이상 증상 기준 확인",
            item3Description = "발열, 출혈 등 병원에 연락해야 하는 증상 기준을 확인하세요.",
            item4Title = "재방문 필요 여부 확인",
            item4Description = "추가 진료나 재방문이 필요한지 확인하세요.",
            noticeText = "회복 속도는 개인차가 있으니 의료진의 안내를 우선해 주세요."
        )
        val En = PostTreatmentPrecautionsStrings(
            bannerTitle = "Check Post-treatment Precautions",
            bannerSubtitle = "Check in advance when you can eat, shower, or exercise, and the warning signs to watch for.",
            checklistTitle = "Recovery Checklist",
            item1Title = "Check When You Can Eat",
            item1Description = "Check when fasting ends and what foods are okay to eat.",
            item2Title = "Check Shower/Exercise Restrictions",
            item2Description = "Check when showering, sauna, and exercise become allowed.",
            item3Title = "Check Warning Signs",
            item3Description = "Check symptoms like fever or bleeding that require you to contact the hospital.",
            item4Title = "Check Follow-up Need",
            item4Description = "Check whether additional treatment or a follow-up visit is needed.",
            noticeText = "Recovery speed varies by individual, so please follow your medical team's guidance first."
        )
        val Zh = PostTreatmentPrecautionsStrings(
            bannerTitle = "确认诊疗后注意事项",
            bannerSubtitle = "请提前确认可以进食、洗澡、运动的时间点及异常症状标准。",
            checklistTitle = "恢复期检查清单",
            item1Title = "确认可进食时间",
            item1Description = "请确认解除禁食的时间及可食用的食物。",
            item2Title = "确认洗澡·运动限制",
            item2Description = "请确认可以洗澡、桑拿及运动的时间点。",
            item3Title = "确认异常症状标准",
            item3Description = "请确认发热、出血等需要联系医院的症状标准。",
            item4Title = "确认是否需要复诊",
            item4Description = "请确认是否需要额外诊疗或复诊。",
            noticeText = "恢复速度因人而异，请以医护人员的指导为优先。"
        )
        val Ja = PostTreatmentPrecautionsStrings(
            bannerTitle = "診療後の注意事項の確認",
            bannerSubtitle = "食事・シャワー・運動が可能な時期と異常症状の基準を事前に確認してください。",
            checklistTitle = "回復中チェックリスト",
            item1Title = "食事可能な時期の確認",
            item1Description = "絶食解除の時期と食べてよい食品を確認してください。",
            item2Title = "シャワー・運動制限の確認",
            item2Description = "シャワー、サウナ、運動が可能になる時期を確認してください。",
            item3Title = "異常症状の基準確認",
            item3Description = "発熱、出血など病院へ連絡すべき症状の基準を確認してください。",
            item4Title = "再受診の要否確認",
            item4Description = "追加の診療や再受診が必要か確認してください。",
            noticeText = "回復スピードには個人差があるため、医療スタッフの案内を優先してください。"
        )
    }
}

data class EnglishDocumentsResultsStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val item4Title: String,
    val item4Description: String,
    val item5Title: String,
    val item5Description: String,
    val item6Title: String,
    val item6Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = EnglishDocumentsResultsStrings(
            bannerTitle = "영문 서류·검사결과 수령 확인",
            bannerSubtitle = "귀국 후 제출이 필요한 서류와 검사결과 수령 여부를 미리 확인하세요.",
            checklistTitle = "수령 전 체크리스트",
            item1Title = "영문 진단서·소견서 수령",
            item1Description = "영문으로 발급되는 진단서나 소견서를 받았는지 확인하세요.",
            item2Title = "검사결과·영상자료 수령",
            item2Description = "검사결과와 영상자료를 파일이나 CD로 받을 수 있는지 확인하세요.",
            item3Title = "발급 소요시간·비용 확인",
            item3Description = "서류 발급에 걸리는 시간과 추가 비용이 있는지 확인하세요.",
            item4Title = "원본·사본 서류 확인",
            item4Description = "원본과 사본 중 어떤 서류가 필요한지 확인하세요.",
            item5Title = "공항 출국 전 수령 완료 확인",
            item5Description = "출국 전에 모든 서류를 수령했는지 다시 한번 확인하세요.",
            item6Title = "이메일·파일 수령 가능 여부",
            item6Description = "귀국 후에도 이메일이나 파일로 받을 수 있는지 확인하세요.",
            noticeText = "필요한 서류 종류는 본국 제출 기관 기준에 따라 다를 수 있어요."
        )
        val En = EnglishDocumentsResultsStrings(
            bannerTitle = "Check English Documents & Test Results",
            bannerSubtitle = "Check in advance whether you've received the documents and test results you'll need after returning home.",
            checklistTitle = "Before-pickup Checklist",
            item1Title = "Receive English Certificate/Opinion",
            item1Description = "Check whether you've received an English-language medical certificate or opinion.",
            item2Title = "Receive Test Results & Imaging",
            item2Description = "Check whether test results and imaging can be received as files or on CD.",
            item3Title = "Check Issuance Time & Fee",
            item3Description = "Check how long document issuance takes and whether there's an additional fee.",
            item4Title = "Check Original vs Copy",
            item4Description = "Check whether you need the original document or a copy.",
            item5Title = "Confirm Pickup Before Departure",
            item5Description = "Double-check that you've received all documents before leaving the airport.",
            item6Title = "Check Email/File Delivery",
            item6Description = "Check whether you can still receive documents by email or file after returning home.",
            noticeText = "The types of documents you need may vary depending on the submitting institution in your home country."
        )
        val Zh = EnglishDocumentsResultsStrings(
            bannerTitle = "确认英文文件·检查结果领取",
            bannerSubtitle = "请提前确认是否已领取回国后需提交的文件及检查结果。",
            checklistTitle = "领取前检查清单",
            item1Title = "领取英文诊断书·意见书",
            item1Description = "请确认是否已领取以英文开具的诊断书或意见书。",
            item2Title = "领取检查结果·影像资料",
            item2Description = "请确认能否以文件或光盘形式领取检查结果及影像资料。",
            item3Title = "确认发行所需时间·费用",
            item3Description = "请确认文件发行所需的时间及是否产生额外费用。",
            item4Title = "确认原件·复印件",
            item4Description = "请确认需要的是原件还是复印件。",
            item5Title = "确认出境前已领取完毕",
            item5Description = "请在出境前再次确认是否已领取所有文件。",
            item6Title = "确认能否通过邮件·文件领取",
            item6Description = "请确认回国后是否仍可通过邮件或文件形式领取。",
            noticeText = "所需文件种类可能因本国提交机构的标准而异。"
        )
        val Ja = EnglishDocumentsResultsStrings(
            bannerTitle = "英文書類・検査結果の受領確認",
            bannerSubtitle = "帰国後に提出が必要な書類と検査結果を受け取ったか事前に確認してください。",
            checklistTitle = "受け取り前チェックリスト",
            item1Title = "英文診断書・所見書の受領",
            item1Description = "英文で発行される診断書や所見書を受け取ったか確認してください。",
            item2Title = "検査結果・画像資料の受領",
            item2Description = "検査結果や画像資料をファイルまたはCDで受け取れるか確認してください。",
            item3Title = "発行所要時間・費用の確認",
            item3Description = "書類発行にかかる時間と追加費用の有無を確認してください。",
            item4Title = "原本・写しの確認",
            item4Description = "原本と写しのどちらが必要か確認してください。",
            item5Title = "出国前の受領完了確認",
            item5Description = "出国前にすべての書類を受け取ったか再度確認してください。",
            item6Title = "メール・ファイル受領の可否",
            item6Description = "帰国後もメールやファイルで受け取れるか確認してください。",
            noticeText = "必要な書類の種類は本国の提出先機関の基準により異なる場合があります。"
        )
    }
}

data class AirportDeparturePreparationStrings(
    val bannerTitle: String,
    val bannerSubtitle: String,
    val checklistTitle: String,
    val item1Title: String,
    val item1Description: String,
    val item2Title: String,
    val item2Description: String,
    val item3Title: String,
    val item3Description: String,
    val item4Title: String,
    val item4Description: String,
    val item5Title: String,
    val item5Description: String,
    val item6Title: String,
    val item6Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = AirportDeparturePreparationStrings(
            bannerTitle = "귀국 전 반입·공항 준비",
            bannerSubtitle = "약 반입 제한과 귀국 전 필요한 준비 항목을 미리 확인하세요.",
            checklistTitle = "출국 전 체크리스트",
            item1Title = "기내 반입 의료서류 확인",
            item1Description = "여권, 진단서 등 기내에 직접 소지할 의료서류를 확인하세요.",
            item2Title = "액체 의약품·의료용품 확인",
            item2Description = "물약, 안약 등 액체류 의약품의 반입 용량 기준을 확인하세요.",
            item3Title = "공항 보안검색 준비",
            item3Description = "보안검색 시 의약품과 의료용품을 미리 꺼내둘 수 있도록 준비하세요.",
            item4Title = "의약품 반입 제한 확인",
            item4Description = "처방약, 한약 등 반입 제한 품목인지 확인하세요.",
            item5Title = "여권 유효기간 재확인",
            item5Description = "귀국편 탑승에 필요한 여권 유효기간을 다시 확인하세요.",
            item6Title = "항공권·수하물 확인",
            item6Description = "항공권 일정과 수하물 규정을 확인하세요.",
            noticeText = "반입 제한 품목과 세관 규정은 국가마다 다르니 항공사·세관 안내를 확인해 주세요."
        )
        val En = AirportDeparturePreparationStrings(
            bannerTitle = "Prepare for Departure & Customs",
            bannerSubtitle = "Check medication import restrictions and other pre-departure items in advance.",
            checklistTitle = "Before-departure Checklist",
            item1Title = "Carry-on Medical Documents",
            item1Description = "Check medical documents like your passport and certificate to carry onboard.",
            item2Title = "Liquid Medication/Supplies",
            item2Description = "Check the volume limits for liquid medications such as syrups or eye drops.",
            item3Title = "Prepare for Security Screening",
            item3Description = "Have your medications and medical supplies ready to take out during security screening.",
            item4Title = "Check Medication Import Restrictions",
            item4Description = "Check whether prescription drugs, herbal medicine, etc. are restricted items.",
            item5Title = "Recheck Passport Validity",
            item5Description = "Double-check that your passport is valid for your return flight.",
            item6Title = "Check Flight & Baggage",
            item6Description = "Check your flight schedule and baggage rules.",
            noticeText = "Restricted items and customs rules vary by country, so please check with your airline and customs."
        )
        val Zh = AirportDeparturePreparationStrings(
            bannerTitle = "回国前的携带与机场准备",
            bannerSubtitle = "请提前确认药品携带限制及回国前需要准备的事项。",
            checklistTitle = "出境前检查清单",
            item1Title = "确认随身携带的医疗文件",
            item1Description = "请确认护照、诊断书等需随身携带上机的医疗文件。",
            item2Title = "确认液体药品·医疗用品",
            item2Description = "请确认水剂、眼药水等液体药品的携带容量标准。",
            item3Title = "准备机场安检",
            item3Description = "请提前准备好，以便在安检时能取出药品和医疗用品。",
            item4Title = "确认药品携带限制",
            item4Description = "请确认处方药、中草药等是否属于携带限制物品。",
            item5Title = "再次确认护照有效期",
            item5Description = "请再次确认搭乘回国航班所需的护照有效期。",
            item6Title = "确认机票·行李",
            item6Description = "请确认机票日程及行李规定。",
            noticeText = "携带限制物品及海关规定因国家而异，请确认航空公司及海关的说明。"
        )
        val Ja = AirportDeparturePreparationStrings(
            bannerTitle = "帰国前の持ち込み・空港準備",
            bannerSubtitle = "薬の持ち込み制限と帰国前に必要な準備項目を事前に確認してください。",
            checklistTitle = "出国前チェックリスト",
            item1Title = "機内持ち込み医療書類の確認",
            item1Description = "パスポート、診断書など機内に直接携行する医療書類を確認してください。",
            item2Title = "液体医薬品・医療用品の確認",
            item2Description = "水薬、点眼薬など液体医薬品の持ち込み容量基準を確認してください。",
            item3Title = "空港保安検査の準備",
            item3Description = "保安検査の際、医薬品や医療用品をすぐ取り出せるよう準備してください。",
            item4Title = "医薬品持ち込み制限の確認",
            item4Description = "処方薬、漢方薬などが持ち込み制限品に該当するか確認してください。",
            item5Title = "パスポート有効期限の再確認",
            item5Description = "帰国便搭乗に必要なパスポートの有効期限を再度確認してください。",
            item6Title = "航空券・手荷物の確認",
            item6Description = "航空券の日程と手荷物規定を確認してください。",
            noticeText = "持ち込み制限品や税関規定は国によって異なるため、航空会社・税関の案内をご確認ください。"
        )
    }
}

/** STEP03 하위 "병원 위치와 접수 절차 확인" — 공용 GuideStepDetailContent 모델 대신 전용 레이아웃을 쓰는 화면. */
data class HospitalLocationCheckinStrings(
    val title: String,
    val bannerTitle: String,
    val bannerSubtitle: String,
    val itemCardTitle: String,
    val itemCardDescription: String,
    val sectionTitle: String,
    val step1Title: String,
    val step1Description: String,
    val step2Title: String,
    val step2Description: String,
    val step3Title: String,
    val step3Description: String,
    val step4Title: String,
    val step4Description: String,
    val noticeText: String
) {
    companion object {
        val Ko = HospitalLocationCheckinStrings(
            title = "병원 위치 및 접수 절차",
            bannerTitle = "병원 정보와 접수 절차 한눈에",
            bannerSubtitle = "병원 위치, 교통편, 주차 정보와 접수 절차를 한눈에 확인하세요.",
            itemCardTitle = "병원 정보 확인하기",
            itemCardDescription = "병원 위치, 교통편, 주차 정보와 경로 안내를 확인할 수 있어요.",
            sectionTitle = "접수 절차",
            step1Title = "접수 위치 확인",
            step1Description = "국제진료센터/외래 접수처 중 어디로 가는지 확인",
            step2Title = "서류 제시",
            step2Description = "여권 또는 예약정보를 제시하고 필요한 서류를 제출",
            step3Title = "대기 및 안내",
            step3Description = "접수 후 대기 장소와 다음 안내를 확인",
            step4Title = "진료 또는 검사 이동",
            step4Description = "진료실/검사실 위치 안내 후 이동",
            noticeText = "병원마다 접수 위치와 절차가 다를 수 있으니 병원 안내를 다시 확인해 주세요."
        )
        val En = HospitalLocationCheckinStrings(
            title = "Hospital Location & Check-in Procedure",
            bannerTitle = "Hospital Info & Check-in at a Glance",
            bannerSubtitle = "See hospital location, transportation, parking, and check-in procedure all at a glance.",
            itemCardTitle = "Check Hospital Info",
            itemCardDescription = "Check the hospital location, transportation, parking, and directions.",
            sectionTitle = "Check-in Process",
            step1Title = "Check Check-in Location",
            step1Description = "Check whether to go to the International Clinic or the outpatient reception desk.",
            step2Title = "Present Documents",
            step2Description = "Present your passport or reservation details and submit required documents.",
            step3Title = "Wait & Guidance",
            step3Description = "After check-in, check the waiting area and next instructions.",
            step4Title = "Move to Treatment/Exam",
            step4Description = "Follow directions to the treatment or exam room.",
            noticeText = "Check-in locations and procedures vary by hospital, so please review hospital guidance again."
        )
        val Zh = HospitalLocationCheckinStrings(
            title = "医院位置及挂号流程",
            bannerTitle = "一览医院信息与挂号流程",
            bannerSubtitle = "一目了然地确认医院位置、交通方式、停车信息及挂号流程。",
            itemCardTitle = "查看医院信息",
            itemCardDescription = "可查看医院位置、交通方式、停车信息及路线指引。",
            sectionTitle = "挂号流程",
            step1Title = "确认挂号地点",
            step1Description = "确认应前往国际诊疗中心还是门诊挂号处。",
            step2Title = "出示文件",
            step2Description = "出示护照或预约信息，并提交所需文件。",
            step3Title = "等待及指引",
            step3Description = "挂号后确认等待地点及后续指引。",
            step4Title = "前往诊疗或检查室",
            step4Description = "在指引诊室/检查室位置后前往。",
            noticeText = "各医院的挂号地点与流程可能不同，请再次确认医院的说明。"
        )
        val Ja = HospitalLocationCheckinStrings(
            title = "病院の場所と受付手続き",
            bannerTitle = "病院情報と受付手続きを一目で",
            bannerSubtitle = "病院の場所、交通手段、駐車場情報、受付手続きを一目で確認できます。",
            itemCardTitle = "病院情報を確認する",
            itemCardDescription = "病院の場所、交通手段、駐車場情報、経路案内を確認できます。",
            sectionTitle = "受付手続き",
            step1Title = "受付場所の確認",
            step1Description = "国際診療センター／外来受付のどちらへ行くか確認",
            step2Title = "書類の提示",
            step2Description = "パスポートまたは予約情報を提示し、必要書類を提出",
            step3Title = "待機・案内",
            step3Description = "受付後、待機場所と次の案内を確認",
            step4Title = "診療・検査室への移動",
            step4Description = "診察室・検査室の場所案内を受けて移動",
            noticeText = "受付場所や手続きは病院により異なる場合があるため、病院の案内を再度ご確認ください。"
        )
    }
}

/** STEP04 전용 화면(TreatmentExaminationDetailScreen). 공용 GuideStepDetailContent 모델을 쓰지 않는다. */
data class TreatmentExaminationStrings(
    val bannerSubtitle: String,
    val briefingSectionTitle: String,
    val briefingLabelVisitPurpose: String,
    val briefingLabelSymptoms: String,
    val briefingLabelAllergyMedication: String,
    val briefingLabelReturnDate: String,
    val briefingLabelMemo: String,
    val editContentDescription: String,
    val saveContentDescription: String,
    val todayChecklistTitle: String,
    val todayItem1Title: String,
    val todayItem1Description: String,
    val todayItem2Title: String,
    val todayItem2Description: String,
    val todayItem3Title: String,
    val todayItem3Description: String,
    val inquirySectionTitle: String,
    val inquiry1Title: String,
    val inquiry1Description: String,
    val inquiry2Title: String,
    val inquiry2Description: String,
    val inquiry2BadgeLabel: String,
    val noticeText: String
) {
    companion object {
        val Ko = TreatmentExaminationStrings(
            bannerSubtitle = "진료 전후에 꼭 필요한 정보를 빠르게 확인할 수 있도록 도와드려요.",
            briefingSectionTitle = "내 진료 브리핑 카드",
            briefingLabelVisitPurpose = "방문 목적",
            briefingLabelSymptoms = "현재 증상",
            briefingLabelAllergyMedication = "알레르기·복용약",
            briefingLabelReturnDate = "귀국 예정일",
            briefingLabelMemo = "전달 메모",
            editContentDescription = "수정",
            saveContentDescription = "저장",
            todayChecklistTitle = "오늘 꼭 확인할 3가지",
            todayItem1Title = "정확한 진료·검사 이름",
            todayItem1Description = "오늘 실제로 받는 진료·검사·시술 이름을 확인하세요.",
            todayItem2Title = "개인별 주의사항",
            todayItem2Description = "금식, 복용약, 일상 복귀, 피해야 할 행동을 확인하세요.",
            todayItem3Title = "결과·서류·재문의 방법",
            todayItem3Description = "결과 수령 시점, 영문 서류, 귀국 후 문의 채널을 확인하세요.",
            inquirySectionTitle = "귀국 후 문의 채널",
            inquiry1Title = "방문한 병원에 먼저 문의",
            inquiry1Description = "진료 결과, 처방, 이상 증상은 방문한 병원 국제진료센터에 먼저 문의하세요.",
            inquiry2Title = "Medical Korea 공식 상담",
            inquiry2Description = "의료 이용 상담이나 불편 사항은 Medical Korea 공식 지원센터에서 확인할 수 있어요.",
            inquiry2BadgeLabel = "공식",
            noticeText = "본 안내는 일반 정보이며, 실제 진료 내용과 주의사항은 의료진 안내를 우선해 주세요."
        )
        val En = TreatmentExaminationStrings(
            bannerSubtitle = "We help you quickly check the information you need before and after treatment.",
            briefingSectionTitle = "My Treatment Briefing Card",
            briefingLabelVisitPurpose = "Visit Purpose",
            briefingLabelSymptoms = "Current Symptoms",
            briefingLabelAllergyMedication = "Allergies & Medications",
            briefingLabelReturnDate = "Return Date",
            briefingLabelMemo = "Notes",
            editContentDescription = "Edit",
            saveContentDescription = "Save",
            todayChecklistTitle = "3 Things to Check Today",
            todayItem1Title = "Exact Treatment/Exam Name",
            todayItem1Description = "Check the exact name of the treatment, exam, or procedure you're receiving today.",
            todayItem2Title = "Personal Precautions",
            todayItem2Description = "Check fasting, medications, return to daily activities, and things to avoid.",
            todayItem3Title = "Results, Documents & Follow-up",
            todayItem3Description = "Check when you'll receive results, English documents, and how to inquire after returning home.",
            inquirySectionTitle = "Inquiry Channels After Return",
            inquiry1Title = "Contact the Hospital You Visited First",
            inquiry1Description = "For results, prescriptions, or abnormal symptoms, contact the International Clinic of the hospital you visited first.",
            inquiry2Title = "Medical Korea Official Support",
            inquiry2Description = "For consultations or complaints about medical services, check the Medical Korea official support center.",
            inquiry2BadgeLabel = "Official",
            noticeText = "This is general information — please prioritize your medical team's guidance for actual treatment details and precautions."
        )
        val Zh = TreatmentExaminationStrings(
            bannerSubtitle = "帮助您快速确认诊疗前后必需的信息。",
            briefingSectionTitle = "我的诊疗简报卡",
            briefingLabelVisitPurpose = "访问目的",
            briefingLabelSymptoms = "现有症状",
            briefingLabelAllergyMedication = "过敏·服用药物",
            briefingLabelReturnDate = "预计回国日期",
            briefingLabelMemo = "备注",
            editContentDescription = "编辑",
            saveContentDescription = "保存",
            todayChecklistTitle = "今天务必确认的3件事",
            todayItem1Title = "准确的诊疗·检查名称",
            todayItem1Description = "请确认今天实际接受的诊疗、检查、施术名称。",
            todayItem2Title = "个人注意事项",
            todayItem2Description = "请确认禁食、服药、恢复日常及应避免的行为。",
            todayItem3Title = "结果·文件·再咨询方式",
            todayItem3Description = "请确认结果领取时间、英文文件及回国后的咨询渠道。",
            inquirySectionTitle = "回国后咨询渠道",
            inquiry1Title = "优先咨询就诊医院",
            inquiry1Description = "诊疗结果、处方、异常症状请先咨询您就诊医院的国际诊疗中心。",
            inquiry2Title = "Medical Korea官方咨询",
            inquiry2Description = "医疗使用咨询或不便事项，可在Medical Korea官方支援中心确认。",
            inquiry2BadgeLabel = "官方",
            noticeText = "本指南为一般信息，实际诊疗内容及注意事项请以医护人员的说明为准。"
        )
        val Ja = TreatmentExaminationStrings(
            bannerSubtitle = "診療の前後に必要な情報をすばやく確認できるようサポートします。",
            briefingSectionTitle = "私の診療ブリーフィングカード",
            briefingLabelVisitPurpose = "訪問目的",
            briefingLabelSymptoms = "現在の症状",
            briefingLabelAllergyMedication = "アレルギー・服用薬",
            briefingLabelReturnDate = "帰国予定日",
            briefingLabelMemo = "伝達メモ",
            editContentDescription = "編集",
            saveContentDescription = "保存",
            todayChecklistTitle = "今日必ず確認する3つのこと",
            todayItem1Title = "正確な診療・検査名の確認",
            todayItem1Description = "本日実際に受ける診療・検査・施術の名前を確認してください。",
            todayItem2Title = "個人別注意事項",
            todayItem2Description = "絶食、服薬、日常復帰、避けるべき行動を確認してください。",
            todayItem3Title = "結果・書類・再問い合わせ方法",
            todayItem3Description = "結果の受け取り時期、英文書類、帰国後の問い合わせ先を確認してください。",
            inquirySectionTitle = "帰国後の問い合わせ先",
            inquiry1Title = "受診した病院にまず問い合わせ",
            inquiry1Description = "診療結果、処方、異常症状については、受診した病院の国際診療センターにまずお問い合わせください。",
            inquiry2Title = "Medical Korea公式相談",
            inquiry2Description = "医療利用に関する相談や不便な点は、Medical Korea公式サポートセンターで確認できます。",
            inquiry2BadgeLabel = "公式",
            noticeText = "本案内は一般的な情報であり、実際の診療内容や注意事項は医療スタッフの案内を優先してください。"
        )
    }
}

/** TreatmentBriefing(data/guide) 필드가 비어있을 때 화면에 보여줄 언어별 예시 placeholder. 사용자가 실제로
 *  입력한 값은 DataStore에 그대로 저장되며 이 기본값으로 덮어쓰지 않는다. */
data class TreatmentBriefingDefaultsStrings(
    val visitPurpose: String,
    val symptoms: String,
    val allergyMedication: String,
    val returnDate: String,
    val memo: String
) {
    companion object {
        val Ko = TreatmentBriefingDefaultsStrings(
            visitPurpose = "상담/검사 문의",
            symptoms = "피부 트러블, 가려움",
            allergyMedication = "페니실린 알레르기 / 복용약 없음",
            returnDate = "7월 28일",
            memo = "빠른 검사 가능 여부 확인 희망"
        )
        val En = TreatmentBriefingDefaultsStrings(
            visitPurpose = "Consultation/exam inquiry",
            symptoms = "Skin trouble, itching",
            allergyMedication = "Penicillin allergy / No medications",
            returnDate = "July 28",
            memo = "Hoping to check if faster exams are available"
        )
        val Zh = TreatmentBriefingDefaultsStrings(
            visitPurpose = "咨询/检查询问",
            symptoms = "皮肤问题、瘙痒",
            allergyMedication = "青霉素过敏 / 无服用药物",
            returnDate = "7月28日",
            memo = "希望确认能否尽快安排检查"
        )
        val Ja = TreatmentBriefingDefaultsStrings(
            visitPurpose = "相談・検査に関する問い合わせ",
            symptoms = "肌トラブル、かゆみ",
            allergyMedication = "ペニシリンアレルギー／服用薬なし",
            returnDate = "7月28日",
            memo = "早めの検査可否を確認したい"
        )
    }
}