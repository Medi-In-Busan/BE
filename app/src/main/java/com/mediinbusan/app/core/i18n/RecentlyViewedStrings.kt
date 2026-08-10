package com.mediinbusan.app.core.i18n

data class RecentlyViewedStrings(
    val emptyMessage: String,
    val relativeJustNow: String,
    val relativeMinutesFormat: String,
    val relativeHoursFormat: String,
    val relativeDaysFormat: String
) {
    companion object {
        val Ko = RecentlyViewedStrings(
            emptyMessage = "최근 확인한 병원·장소가 없습니다.",
            relativeJustNow = "방금 전",
            relativeMinutesFormat = "%d분 전",
            relativeHoursFormat = "%d시간 전",
            relativeDaysFormat = "%d일 전"
        )
        val En = RecentlyViewedStrings(
            emptyMessage = "No recently viewed hospitals or places.",
            relativeJustNow = "Just now",
            relativeMinutesFormat = "%dm ago",
            relativeHoursFormat = "%dh ago",
            relativeDaysFormat = "%dd ago"
        )
        val Zh = RecentlyViewedStrings(
            emptyMessage = "暂无最近浏览的医院·场所。",
            relativeJustNow = "刚刚",
            relativeMinutesFormat = "%d分钟前",
            relativeHoursFormat = "%d小时前",
            relativeDaysFormat = "%d天前"
        )
        val Ja = RecentlyViewedStrings(
            emptyMessage = "最近確認した病院・スポットがありません。",
            relativeJustNow = "たった今",
            relativeMinutesFormat = "%d分前",
            relativeHoursFormat = "%d時間前",
            relativeDaysFormat = "%d日前"
        )
    }
}
