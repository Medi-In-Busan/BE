package com.mediinbusan.app.core.i18n

data class FavoriteStrings(
    val screenTitle: String,
    val totalCountFormat: String,
    val emptyMessage: String,
    val filterAll: String,
    val filterHospital: String,
    val filterPlace: String,
    val sortRecent: String,
    val sortName: String
) {
    companion object {
        val Ko = FavoriteStrings(
            screenTitle = "즐겨찾기 관리",
            totalCountFormat = "총 %d건",
            emptyMessage = "저장한 병원·장소가 없습니다.",
            filterAll = "전체",
            filterHospital = "병원",
            filterPlace = "장소",
            sortRecent = "최근 저장순",
            sortName = "이름순"
        )
        val En = FavoriteStrings(
            screenTitle = "Manage favorites",
            totalCountFormat = "%d total",
            emptyMessage = "No saved hospitals or places.",
            filterAll = "All",
            filterHospital = "Hospitals",
            filterPlace = "Places",
            sortRecent = "Recently saved",
            sortName = "Name"
        )
        val Zh = FavoriteStrings(
            screenTitle = "收藏管理",
            totalCountFormat = "共 %d 项",
            emptyMessage = "暂无已保存的医院·场所。",
            filterAll = "全部",
            filterHospital = "医院",
            filterPlace = "场所",
            sortRecent = "按最近保存排序",
            sortName = "按名称排序"
        )
        val Ja = FavoriteStrings(
            screenTitle = "お気に入り管理",
            totalCountFormat = "合計%d件",
            emptyMessage = "保存した病院・スポットがありません。",
            filterAll = "すべて",
            filterHospital = "病院",
            filterPlace = "スポット",
            sortRecent = "最近保存した順",
            sortName = "名前順"
        )
    }
}
