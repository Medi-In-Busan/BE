package com.mediinbusan.backend.diagnosischat.service;

import java.util.Map;

/**
 * 칩 탭(정확한 enum 값)으로 슬롯이 채워졌을 때 쓰는 정적 후속 질문 문구. DiagnosisChatService가 Gemini를
 * 호출하지 않은 턴에서 이 클래스가 반환하는 문장을 그대로 reply로 내려준다 — 4개 언어(ko/en/zh/ja)는
 * Android core/i18n의 SelfDiagnosisStrings·ChatStrings에서 쓰는 용어(체류 기간/예약 상태/통역 등)와
 * 톤을 맞췄다. computeTargetSlot이 "entryStayConditions"를 가리키는 경우는 4개 필수 슬롯이 이미 다
 * 찬 뒤라 resultType이 함께 내려가고, 현재 UI(SelfDiagnosisScreen)는 그 순간 결과 화면으로 즉시
 * 전환돼 이 reply 문장 자체는 화면에 노출되지 않는다 — 그래도 API 계약상 빈 문자열을 주지 않도록
 * 짧은 마무리 인사를 채워둔다.
 */
final class DiagnosisFollowUpTemplates {

    private static final Map<String, Map<String, String>> TEMPLATES = Map.of(
        "stayDuration", Map.of(
            "ko", "체류 기간은 어느 정도로 예상하시나요?",
            "en", "About how long do you plan to stay?",
            "zh", "您预计停留多长时间呢？",
            "ja", "滞在期間はどのくらいを予定していますか？"
        ),
        "reservationStatus", Map.of(
            "ko", "병원 예약은 어떻게 진행되고 있나요?",
            "en", "How's your hospital reservation going so far?",
            "zh", "医院预约目前进行到哪一步了？",
            "ja", "病院の予約状況について教えてください。"
        ),
        "interpretationNeed", Map.of(
            "ko", "통역 지원이 필요하신가요?",
            "en", "Would you need interpretation support?",
            "zh", "您需要口译支持吗？",
            "ja", "通訳のサポートは必要ですか？"
        ),
        "entryStayConditions", Map.of(
            "ko", "확인 감사합니다! 준비 유형을 안내해드릴게요.",
            "en", "Thanks! Let me show you your preparation type.",
            "zh", "谢谢确认！马上为您显示准备类型。",
            "ja", "ご確認ありがとうございます！準備タイプをご案内します。"
        )
    );

    private DiagnosisFollowUpTemplates() {
    }

    /** nextTargetSlot은 항상 DiagnosisChatService#computeTargetSlot의 반환값이다(위 4개 키 중 하나만 옴 —
     *  visitPurpose는 슬롯 진행상 다음 타겟이 될 수 없으므로 이 맵에 없어도 된다). 알 수 없는 language는
     *  영어로 대체한다(Android SupportedLanguage가 ko/en/zh/ja 4종으로 고정돼 있어 실질적으로는 발생하지 않음). */
    static String text(String language, String nextTargetSlot) {
        Map<String, String> byLanguage = TEMPLATES.get(nextTargetSlot);
        if (byLanguage == null) {
            return TEMPLATES.get("entryStayConditions").get("en");
        }
        return byLanguage.getOrDefault(language, byLanguage.get("en"));
    }
}
