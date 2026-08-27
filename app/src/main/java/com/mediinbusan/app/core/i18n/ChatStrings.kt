package com.mediinbusan.app.core.i18n

/**
 * 준비 유형 진단 챗봇(SelfDiagnosis 채팅 화면) 정적 UI 문구. "다시 진단하기" 버튼 라벨 등
 * 결과 화면 쪽 문구는 이미 SelfDiagnosisStrings에 있으므로(DiagnosisResultContent가 그대로 재사용)
 * 여기서는 채팅 입력 UI에만 필요한 문구를 다룬다.
 */
data class ChatStrings(
    val chatBubbleLabel: String,
    val aiDiagnosisLabel: String,
    val greetingMessage: String,
    val inputPlaceholder: String,
    val sendButtonContentDescription: String,
    val errorMessage: String,
    val todayLabel: String,
    /** 슬롯 진행 바 옆 "n/n단계" 표시용. %1$d=현재 단계, %2$d=전체 단계 수. */
    val stepProgressFormat: String
) {
    companion object {
        val Ko = ChatStrings(
            chatBubbleLabel = "챗봇",
            aiDiagnosisLabel = "AI진단",
            greetingMessage = "안녕하세요! 준비 유형 안내까지만 도와드리는 챗봇이에요(그 외 문의는 support@medinbusan.kr로 부탁드려요). " +
                "먼저, 이번엔 어떤 목적으로 방문을 고려하고 계신가요?",
            inputPlaceholder = "방문 목적, 체류 기간 등을 입력해 주세요",
            sendButtonContentDescription = "전송",
            errorMessage = "일시적인 오류가 발생했어요. 다시 시도해 주세요.",
            todayLabel = "오늘",
            stepProgressFormat = "%1\$d/%2\$d단계"
        )
        val En = ChatStrings(
            chatBubbleLabel = "Chat",
            aiDiagnosisLabel = "AI Diagnosis",
            greetingMessage = "Hi! I can only help you find your preparation type (for anything else, please email support@medinbusan.kr). " +
                "First, what's the purpose of your visit this time?",
            inputPlaceholder = "e.g. purpose of visit, length of stay",
            sendButtonContentDescription = "Send",
            errorMessage = "Something went wrong. Please try again.",
            todayLabel = "Today",
            stepProgressFormat = "Step %1\$d/%2\$d"
        )
        val Zh = ChatStrings(
            chatBubbleLabel = "聊天",
            aiDiagnosisLabel = "AI诊断",
            greetingMessage = "您好！本聊天机器人仅能帮您确认准备类型（其他咨询请发送邮件至support@medinbusan.kr）。" +
                "首先，您这次访问的目的是什么？",
            inputPlaceholder = "请输入访问目的、停留时间等",
            sendButtonContentDescription = "发送",
            errorMessage = "发生了临时错误，请重试。",
            todayLabel = "今天",
            stepProgressFormat = "第%1\$d/%2\$d步"
        )
        val Ja = ChatStrings(
            chatBubbleLabel = "チャット",
            aiDiagnosisLabel = "AI診断",
            greetingMessage = "こんにちは！このチャットボットは準備タイプの確認のみお手伝いできます（それ以外のお問い合わせはsupport@medinbusan.krまで）。" +
                "まず、今回はどのような目的で訪問を検討していますか？",
            inputPlaceholder = "訪問目的や滞在期間などを入力してください",
            sendButtonContentDescription = "送信",
            errorMessage = "一時的なエラーが発生しました。もう一度お試しください。",
            todayLabel = "今日",
            stepProgressFormat = "%1\$d/%2\$d ステップ"
        )
    }
}
