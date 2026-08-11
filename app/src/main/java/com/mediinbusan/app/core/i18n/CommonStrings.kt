package com.mediinbusan.app.core.i18n

/** 여러 화면에서 동일하게 반복되는 문구(로고 설명, 뒤로가기, 검색, 병원/장소 배지 등). */
data class CommonStrings(
    val logoContentDescription: String,
    val backContentDescription: String,
    val searchContentDescription: String,
    val searchPlaceholder: String,
    val retryButtonLabel: String,
    // ItemTypeBadge(즐겨찾기·최근 본 항목 목록)가 공유하는 병원/장소 구분 배지 라벨.
    val itemTypeHospitalBadge: String,
    val itemTypePlaceBadge: String
) {
    companion object {
        val Ko = CommonStrings(
            logoContentDescription = "메디인부산 로고",
            backContentDescription = "뒤로가기",
            searchContentDescription = "검색",
            searchPlaceholder = "병원 이름, 진료과목으로 검색",
            retryButtonLabel = "다시 시도",
            itemTypeHospitalBadge = "병원",
            itemTypePlaceBadge = "장소"
        )
        val En = CommonStrings(
            logoContentDescription = "MediIn Busan logo",
            backContentDescription = "Back",
            searchContentDescription = "Search",
            searchPlaceholder = "Search by hospital name or specialty",
            retryButtonLabel = "Retry",
            itemTypeHospitalBadge = "Hospital",
            itemTypePlaceBadge = "Place"
        )
        val Zh = CommonStrings(
            logoContentDescription = "MediIn Busan 标志",
            backContentDescription = "返回",
            searchContentDescription = "搜索",
            searchPlaceholder = "按医院名称或诊疗科目搜索",
            retryButtonLabel = "重试",
            itemTypeHospitalBadge = "医院",
            itemTypePlaceBadge = "场所"
        )
        val Ja = CommonStrings(
            logoContentDescription = "メディインブサンのロゴ",
            backContentDescription = "戻る",
            searchContentDescription = "検索",
            searchPlaceholder = "病院名・診療科目で検索",
            retryButtonLabel = "再試行",
            itemTypeHospitalBadge = "病院",
            itemTypePlaceBadge = "スポット"
        )
    }
}
