package com.mediinbusan.app.core.i18n

data class NotificationSettingsStrings(
    val cardTitle: String,
    val cardDescription: String
) {
    companion object {
        val Ko = NotificationSettingsStrings(
            cardTitle = "이벤트 및 소식 알림",
            cardDescription = "새로운 소식과 이벤트 알림을 받아보세요"
        )
        val En = NotificationSettingsStrings(
            cardTitle = "News & event notifications",
            cardDescription = "Get notified about news and events"
        )
        val Zh = NotificationSettingsStrings(
            cardTitle = "活动及消息通知",
            cardDescription = "接收最新消息和活动通知"
        )
        val Ja = NotificationSettingsStrings(
            cardTitle = "お知らせ・イベント通知",
            cardDescription = "新しいお知らせやイベント通知を受け取れます"
        )
    }
}
