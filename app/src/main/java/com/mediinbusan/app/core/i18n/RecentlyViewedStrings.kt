package com.mediinbusan.app.core.i18n

data class RecentlyViewedStrings(
    // "총 N건"처럼 두 톤으로 나눠 강조하는 카운트 라벨 — HospitalSearchListScreen의
    // 검색결과 N건 라벨과 같은 스타일(접두어는 진한 텍스트, 건수는 코랄핑크).
    val totalCountPrefix: String,
    val totalCountSuffixFormat: String,
    val deleteAllLabel: String,
    val removeItemContentDescription: String,
    val emptyMessage: String,
    val relativeJustNow: String,
    val relativeMinutesFormat: String,
    val relativeHoursFormat: String,
    val relativeDaysFormat: String
) {
    companion object {
        val Ko = RecentlyViewedStrings(
            totalCountPrefix = "총 ",
            totalCountSuffixFormat = "%d건",
            deleteAllLabel = "전체 삭제",
            removeItemContentDescription = "최근 본 항목 삭제",
            emptyMessage = "최근 확인한 병원·장소가 없습니다.",
            relativeJustNow = "방금 전",
            relativeMinutesFormat = "%d분 전",
            relativeHoursFormat = "%d시간 전",
            relativeDaysFormat = "%d일 전"
        )
        val En = RecentlyViewedStrings(
            totalCountPrefix = "Total ",
            totalCountSuffixFormat = "%d",
            deleteAllLabel = "Delete all",
            removeItemContentDescription = "Remove from recently viewed",
            emptyMessage = "No recently viewed hospitals or places.",
            relativeJustNow = "Just now",
            relativeMinutesFormat = "%dm ago",
            relativeHoursFormat = "%dh ago",
            relativeDaysFormat = "%dd ago"
        )
        val Zh = RecentlyViewedStrings(
            totalCountPrefix = "共 ",
            totalCountSuffixFormat = "%d 项",
            deleteAllLabel = "全部删除",
            removeItemContentDescription = "从最近浏览中删除",
            emptyMessage = "暂无最近浏览的医院·场所。",
            relativeJustNow = "刚刚",
            relativeMinutesFormat = "%d分钟前",
            relativeHoursFormat = "%d小时前",
            relativeDaysFormat = "%d天前"
        )
        val Ja = RecentlyViewedStrings(
            totalCountPrefix = "合計",
            totalCountSuffixFormat = "%d件",
            deleteAllLabel = "すべて削除",
            removeItemContentDescription = "最近見た項目から削除",
            emptyMessage = "最近確認した病院・スポットがありません。",
            relativeJustNow = "たった今",
            relativeMinutesFormat = "%d分前",
            relativeHoursFormat = "%d時間前",
            relativeDaysFormat = "%d日前"
        )
    }
}
