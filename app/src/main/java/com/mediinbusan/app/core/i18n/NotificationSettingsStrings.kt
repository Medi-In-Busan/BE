package com.mediinbusan.app.core.i18n

data class NotificationSettingsStrings(
    val cardTitle: String,
    val cardDescription: String,
    val statusOnLabel: String,
    val statusOffLabel: String,
    val noticeText: String
) {
    companion object {
        val Ko = NotificationSettingsStrings(
            cardTitle = "이벤트 및 소식 알림",
            cardDescription = "새로운 소식과 이벤트 알림을 받아보세요",
            statusOnLabel = "현재 알림이 켜져 있어요.",
            statusOffLabel = "현재 알림이 꺼져 있어요.",
            noticeText = "실제 알림 발송 기능은 준비 중이에요. 여기서 설정한 값은 추후 알림 기능이 제공될 때 그대로 반영됩니다."
        )
        val En = NotificationSettingsStrings(
            cardTitle = "News & event notifications",
            cardDescription = "Get notified about news and events",
            statusOnLabel = "Notifications are currently on.",
            statusOffLabel = "Notifications are currently off.",
            noticeText = "Actual push notifications are still in development. The value you set here will be applied as-is once notifications are available."
        )
        val Zh = NotificationSettingsStrings(
            cardTitle = "活动及消息通知",
            cardDescription = "接收最新消息和活动通知",
            statusOnLabel = "当前通知已开启。",
            statusOffLabel = "当前通知已关闭。",
            noticeText = "实际的通知推送功能仍在开发中。您在此处设置的值将在通知功能上线后原样应用。"
        )
        val Ja = NotificationSettingsStrings(
            cardTitle = "お知らせ・イベント通知",
            cardDescription = "新しいお知らせやイベント通知を受け取れます",
            statusOnLabel = "現在、通知はオンになっています。",
            statusOffLabel = "現在、通知はオフになっています。",
            noticeText = "実際の通知配信機能は準備中です。ここで設定した値は、今後通知機能が提供される際にそのまま反映されます。"
        )
    }
}
