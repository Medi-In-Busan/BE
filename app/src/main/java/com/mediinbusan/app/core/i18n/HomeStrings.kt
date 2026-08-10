package com.mediinbusan.app.core.i18n

data class HomeStrings(
    val settingsMenuContentDescription: String,
    val heroTitle: String,
    val heroSubtitle: String,
    val medicalPurposeSectionTitle: String,
    val quickLinksSectionTitle: String,
    val recommendedHospitalsSectionTitle: String,
    val viewAllLabel: String,
    val recommendedHospitalsEmpty: String,
    val loadErrorFallback: String,
    val quickLinkHospitalList: String,
    val quickLinkGuide: String,
    val quickLinkWellness: String,
    val quickLinkMap: String,
    val quickLinkSelfDiagnosis: String,
    val quickLinkFavorite: String
) {
    companion object {
        val Ko = HomeStrings(
            settingsMenuContentDescription = "설정 메뉴",
            heroTitle = "부산에서 만나는\n맞춤 의료 여행",
            heroSubtitle = "의료 목적에 맞는 부산 의료기관을 찾아보세요",
            medicalPurposeSectionTitle = "의료 목적 선택",
            quickLinksSectionTitle = "바로가기",
            recommendedHospitalsSectionTitle = "추천 의료기관",
            viewAllLabel = "전체보기",
            recommendedHospitalsEmpty = "추천 의료기관이 없습니다",
            loadErrorFallback = "추천 의료기관을 불러오지 못했습니다.",
            quickLinkHospitalList = "의료기관 찾기",
            quickLinkGuide = "의료 이용 가이드",
            quickLinkWellness = "추천 웰니스",
            quickLinkMap = "지도에서 보기",
            quickLinkSelfDiagnosis = "진단하기",
            quickLinkFavorite = "즐겨찾기"
        )
        val En = HomeStrings(
            settingsMenuContentDescription = "Settings menu",
            heroTitle = "Personalized medical trips\nin Busan",
            heroSubtitle = "Find Busan hospitals that match your medical purpose",
            medicalPurposeSectionTitle = "Choose a medical purpose",
            quickLinksSectionTitle = "Quick links",
            recommendedHospitalsSectionTitle = "Recommended hospitals",
            viewAllLabel = "View all",
            recommendedHospitalsEmpty = "No recommended hospitals",
            loadErrorFallback = "Couldn't load recommended hospitals.",
            quickLinkHospitalList = "Find hospitals",
            quickLinkGuide = "Medical guide",
            quickLinkWellness = "Recommended wellness",
            quickLinkMap = "View on map",
            quickLinkSelfDiagnosis = "Self diagnosis",
            quickLinkFavorite = "Favorites"
        )
        val Zh = HomeStrings(
            settingsMenuContentDescription = "设置菜单",
            heroTitle = "在釜山开启\n专属医疗之旅",
            heroSubtitle = "寻找符合您医疗目的的釜山医疗机构",
            medicalPurposeSectionTitle = "选择医疗目的",
            quickLinksSectionTitle = "快捷入口",
            recommendedHospitalsSectionTitle = "推荐医疗机构",
            viewAllLabel = "查看全部",
            recommendedHospitalsEmpty = "暂无推荐医疗机构",
            loadErrorFallback = "无法加载推荐医疗机构。",
            quickLinkHospitalList = "寻找医院",
            quickLinkGuide = "医疗指南",
            quickLinkWellness = "推荐养生",
            quickLinkMap = "在地图查看",
            quickLinkSelfDiagnosis = "自我诊断",
            quickLinkFavorite = "收藏"
        )
        val Ja = HomeStrings(
            settingsMenuContentDescription = "設定メニュー",
            heroTitle = "釜山で叶える\nオーダーメイド医療旅行",
            heroSubtitle = "目的に合った釜山の医療機関を探してみましょう",
            medicalPurposeSectionTitle = "医療目的を選択",
            quickLinksSectionTitle = "ショートカット",
            recommendedHospitalsSectionTitle = "おすすめ医療機関",
            viewAllLabel = "すべて見る",
            recommendedHospitalsEmpty = "おすすめの医療機関がありません",
            loadErrorFallback = "おすすめ医療機関を読み込めませんでした。",
            quickLinkHospitalList = "医療機関を探す",
            quickLinkGuide = "医療利用ガイド",
            quickLinkWellness = "おすすめウェルネス",
            quickLinkMap = "地図で見る",
            quickLinkSelfDiagnosis = "セルフ診断",
            quickLinkFavorite = "お気に入り"
        )
    }
}
