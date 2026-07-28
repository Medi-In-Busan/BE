package com.mediinbusan.app.feature.selfdiagnosis

/** 5개 질문에 대한 답변을 바탕으로 안내하는 준비 유형. 매핑 로직은 DiagnosisTypeMapper 참고. */
enum class DiagnosisResultType(
    val title: String,
    val description: String,
    val checklist: List<String>,
    val noticeText: String?,
    val ctas: List<DiagnosisCta>
) {
    TYPE_A(
        title = "개인 직접 문의형에 가까워요",
        description = "피부·미용, 치과 상담, 건강검진, 웰니스처럼 방문 목적이 비교적 명확한 경우 " +
            "병원 공식 홈페이지, 이메일, 전화 등을 통해 먼저 문의할 수 있어요.",
        checklist = listOf(
            "희망 진료 분야와 방문 목적 정리",
            "예상 방문일과 체류 일정 확인",
            "병원 지원 언어와 공식 문의 채널 확인",
            "진료 후 이동·휴식 계획 확인",
            "비자 또는 K-ETA 필요 여부 공식 확인"
        ),
        noticeText = null,
        ctas = listOf(
            DiagnosisCta("병원 문의 전 준비사항 보기", DiagnosisCtaTarget.HOSPITAL_INQUIRY_CHECKLIST),
            DiagnosisCta("부산 의료기관 찾아보기", DiagnosisCtaTarget.HOSPITAL_BROWSE)
        )
    ),
    TYPE_B(
        title = "병원 국제진료센터 문의형에 가까워요",
        description = "외국어 접수, 지원 언어, 진료 절차, 준비서류 확인이 필요한 경우 " +
            "병원 공식 채널 또는 국제진료센터를 통해 안내를 확인해보세요.",
        checklist = listOf(
            "병원 지원 언어와 문의 채널 확인",
            "통역 제공 여부와 지원 범위 확인",
            "진료 전 준비서류 확인",
            "예상 방문일과 방문 목적 전달",
            "진료 후 주의사항과 문서 수령 방식 확인"
        ),
        noticeText = null,
        ctas = listOf(
            DiagnosisCta("통역·지원 언어 확인 보기", DiagnosisCtaTarget.INTERPRETATION_SUPPORT),
            DiagnosisCta("병원 문의 전 준비사항 보기", DiagnosisCtaTarget.HOSPITAL_INQUIRY_CHECKLIST),
            DiagnosisCta("부산 의료기관 찾아보기", DiagnosisCtaTarget.HOSPITAL_BROWSE)
        )
    ),
    TYPE_C(
        title = "등록 유치기관 확인이 필요할 수 있어요",
        description = "입원, 장기 치료, 여러 일정 조율, 숙박·차량·통역 등 부가 지원이 필요한 경우 " +
            "등록된 외국인환자 유치기관 또는 병원 공식 채널의 안내를 확인해 주세요.",
        checklist = listOf(
            "외국인환자 유치기관 등록 여부 확인",
            "포함·불포함 서비스 범위 확인",
            "통역·숙박·차량 등 부가 지원 범위 확인",
            "수수료·예상 비용·취소 조건 확인",
            "병원 공식 정보와 진료 내용 대조"
        ),
        noticeText = "메디인부산은 유치기관을 중개하거나 패키지를 판매하지 않습니다. " +
            "등록 여부, 수수료, 포함 서비스 범위는 사용자가 직접 확인해 주세요.",
        ctas = listOf(
            DiagnosisCta("확인 항목 보기", DiagnosisCtaTarget.REGISTERED_AGENCY_CHECKLIST),
            DiagnosisCta("비자·입국 확인 가이드 보기", DiagnosisCtaTarget.VISA_ENTRY_GUIDE),
            DiagnosisCta("의료 이용 절차 보기", DiagnosisCtaTarget.MEDICAL_PROCEDURE_GUIDE)
        )
    ),
    TYPE_D(
        title = "장기치료·비자 확인형에 가까워요",
        description = "치료·요양 기간이 길어질 수 있거나 동반 가족, 초청 서류, 체류자격 확인이 필요한 경우 " +
            "병원 또는 공식 기관 안내를 우선 확인해 주세요.",
        checklist = listOf(
            "예상 치료·체류기간 확인",
            "병원 또는 등록 유치기관 초청 서류 필요 여부 확인",
            "동반 가족·보호자 여부 확인",
            "의료관광 비자 또는 장기 치료 체류자격 공식 확인",
            "귀국 전 수령 문서 확인"
        ),
        noticeText = "본 결과는 비자 판단이 아닙니다. 비자와 체류자격은 반드시 공식 기관에서 확인해 주세요.",
        ctas = listOf(
            DiagnosisCta("비자·입국 확인 가이드 보기", DiagnosisCtaTarget.VISA_ENTRY_GUIDE),
            DiagnosisCta("병원 문의 전 체크리스트 보기", DiagnosisCtaTarget.HOSPITAL_INQUIRY_CHECKLIST),
            DiagnosisCta("귀국 전 체크리스트 보기", DiagnosisCtaTarget.DEPARTURE_CHECKLIST)
        )
    ),
    TYPE_E(
        title = "관광 중심 웰니스 체험형에 가까워요",
        description = "의료 시술보다 스파, 명상, 휴식, 자연 치유, 전통·한방 테마 체험에 가까운 목적이라면 " +
            "부산의 웰니스 장소와 짧은 이동 코스를 중심으로 확인해볼 수 있어요.",
        checklist = listOf(
            "원하는 웰니스 유형 확인",
            "실내·저강도 장소 확인",
            "의료기관 이용 여부 확인",
            "비자 또는 K-ETA 필요 여부 공식 확인",
            "부산 권역별 웰니스 코스 확인"
        ),
        noticeText = null,
        ctas = listOf(
            DiagnosisCta("부산 웰니스 장소 보기", DiagnosisCtaTarget.WELLNESS_PLACES),
            DiagnosisCta("부산 의료·웰니스 정보 보기", DiagnosisCtaTarget.MEDICAL_WELLNESS_INFO),
            DiagnosisCta("비자·입국 확인 가이드 보기", DiagnosisCtaTarget.VISA_ENTRY_GUIDE)
        )
    );

    companion object {
        /** 결과 화면 하단에 항상 표시하는 공통 안전 문구. */
        const val COMMON_SAFETY_NOTICE = "본 결과는 의료 또는 비자 판단이 아닌 일반 준비 안내입니다."
    }
}