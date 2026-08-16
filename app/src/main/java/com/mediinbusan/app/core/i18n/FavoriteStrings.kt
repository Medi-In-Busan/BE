package com.mediinbusan.app.core.i18n

data class FavoriteStrings(
    val screenTitle: String,
    // "총 N건"처럼 두 톤으로 나눠 강조하는 카운트 라벨 — HospitalSearchListScreen의
    // 검색결과 N건 라벨과 같은 스타일(접두어는 진한 텍스트, 건수는 코랄핑크).
    val totalCountPrefix: String,
    val totalCountSuffixFormat: String,
    val deleteAllLabel: String,
    val removeItemContentDescription: String,
    val emptyMessage: String
) {
    companion object {
        val Ko = FavoriteStrings(
            screenTitle = "즐겨찾기 관리",
            totalCountPrefix = "총 ",
            totalCountSuffixFormat = "%d건",
            deleteAllLabel = "전체 삭제",
            removeItemContentDescription = "즐겨찾기 삭제",
            emptyMessage = "저장한 병원·장소가 없습니다."
        )
        val En = FavoriteStrings(
            screenTitle = "Manage favorites",
            totalCountPrefix = "Total ",
            totalCountSuffixFormat = "%d",
            deleteAllLabel = "Delete all",
            removeItemContentDescription = "Remove favorite",
            emptyMessage = "No saved hospitals or places."
        )
        val Zh = FavoriteStrings(
            screenTitle = "收藏管理",
            totalCountPrefix = "共 ",
            totalCountSuffixFormat = "%d 项",
            deleteAllLabel = "全部删除",
            removeItemContentDescription = "删除收藏",
            emptyMessage = "暂无已保存的医院·场所。"
        )
        val Ja = FavoriteStrings(
            screenTitle = "お気に入り管理",
            totalCountPrefix = "合計",
            totalCountSuffixFormat = "%d件",
            deleteAllLabel = "すべて削除",
            removeItemContentDescription = "お気に入りを削除",
            emptyMessage = "保存した病院・スポットがありません。"
        )
    }
}
