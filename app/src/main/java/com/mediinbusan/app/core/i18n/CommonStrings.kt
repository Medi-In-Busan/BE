package com.mediinbusan.app.core.i18n

/** 여러 화면에서 동일하게 반복되는 문구(로고 설명, 뒤로가기, 검색 등). */
data class CommonStrings(
    val logoContentDescription: String,
    val backContentDescription: String,
    val settingsMenuContentDescription: String,
    val languageSelectorContentDescription: String,
    val searchContentDescription: String,
    val searchPlaceholder: String,
    val retryButtonLabel: String,
    // 긴 본문을 접어두고 펼치는 토글. 공공 API가 주는 값(주차 안내·이용요금 등)은 길이가 들쭉날쭉해
    // 한 줄짜리와 여러 문단짜리가 같은 자리에 오므로, 화면 여러 곳에서 같은 문구가 필요하다.
    val expandLabel: String,
    val collapseLabel: String,
    // 즐겨찾기·최근 본 항목이 비었을 때 빈 상태에서 내보내는 유일한 출구. 두 화면이 같은 문구를
    // 쓰므로 여기 둔다.
    val browseHospitalsLabel: String,
    // 즐겨찾기 하트는 core/ui/FavoriteHeartButton.kt(공용 컴포넌트)와 병원 상세가 함께 쓴다 —
    // 한 화면 것이 아니므로 여기 둔다. 주변/관광 화면은 각자 같은 뜻의 필드를 이미 갖고 있어
    // 그대로 두지만, 나중에 정리한다면 이 필드로 모으는 게 맞다.
    val favoriteAddContentDescription: String,
    val favoriteRemoveContentDescription: String,
    // 상세 화면 하단 바에서 앱 안 지도(S-08)로 넘어가는 버튼.
    val mapDetailsContentDescription: String,
    // 공용 하단 내비게이션 바(core/ui/BottomNavBar.kt) 5개 탭 라벨.
    val bottomNavHomeLabel: String,
    val bottomNavHospitalLabel: String,
    val bottomNavGuideLabel: String,
    val bottomNavMapLabel: String,
    val bottomNavDocumentScanLabel: String
) {
    companion object {
        val Ko = CommonStrings(
            logoContentDescription = "메디인부산 로고",
            backContentDescription = "뒤로가기",
            settingsMenuContentDescription = "설정 메뉴",
            languageSelectorContentDescription = "언어 선택",
            searchContentDescription = "검색",
            searchPlaceholder = "병원 이름, 진료과목으로 검색",
            retryButtonLabel = "다시 시도",
            expandLabel = "더보기",
            collapseLabel = "접기",
            browseHospitalsLabel = "병원 둘러보기",
            favoriteAddContentDescription = "즐겨찾기 추가",
            favoriteRemoveContentDescription = "즐겨찾기 해제",
            mapDetailsContentDescription = "앱 지도에서 보기",
            bottomNavHomeLabel = "홈",
            bottomNavHospitalLabel = "의료기관",
            bottomNavGuideLabel = "가이드",
            bottomNavMapLabel = "지도",
            bottomNavDocumentScanLabel = "문서 스캔"
        )
        val En = CommonStrings(
            logoContentDescription = "MediIn Busan logo",
            backContentDescription = "Back",
            settingsMenuContentDescription = "Settings menu",
            languageSelectorContentDescription = "Language selector",
            searchContentDescription = "Search",
            searchPlaceholder = "Search by hospital name or specialty",
            retryButtonLabel = "Retry",
            expandLabel = "Show more",
            collapseLabel = "Show less",
            browseHospitalsLabel = "Browse hospitals",
            favoriteAddContentDescription = "Add to favorites",
            favoriteRemoveContentDescription = "Remove from favorites",
            mapDetailsContentDescription = "View on the in-app map",
            bottomNavHomeLabel = "Home",
            bottomNavHospitalLabel = "Hospitals",
            bottomNavGuideLabel = "Guide",
            bottomNavMapLabel = "Map",
            bottomNavDocumentScanLabel = "Scan"
        )
        val Zh = CommonStrings(
            logoContentDescription = "MediIn Busan 标志",
            backContentDescription = "返回",
            settingsMenuContentDescription = "设置菜单",
            languageSelectorContentDescription = "选择语言",
            searchContentDescription = "搜索",
            searchPlaceholder = "按医院名称或诊疗科目搜索",
            retryButtonLabel = "重试",
            expandLabel = "展开",
            collapseLabel = "收起",
            browseHospitalsLabel = "浏览医院",
            favoriteAddContentDescription = "添加收藏",
            favoriteRemoveContentDescription = "取消收藏",
            mapDetailsContentDescription = "在应用地图中查看",
            bottomNavHomeLabel = "首页",
            bottomNavHospitalLabel = "医疗机构",
            bottomNavGuideLabel = "指南",
            bottomNavMapLabel = "地图",
            bottomNavDocumentScanLabel = "文档扫描"
        )
        val Ja = CommonStrings(
            logoContentDescription = "メディインブサンのロゴ",
            backContentDescription = "戻る",
            settingsMenuContentDescription = "設定メニュー",
            languageSelectorContentDescription = "言語選択",
            searchContentDescription = "検索",
            searchPlaceholder = "病院名・診療科目で検索",
            retryButtonLabel = "再試行",
            expandLabel = "もっと見る",
            collapseLabel = "閉じる",
            browseHospitalsLabel = "病院を見る",
            favoriteAddContentDescription = "お気に入りに追加",
            favoriteRemoveContentDescription = "お気に入りから削除",
            mapDetailsContentDescription = "アプリの地図で見る",
            bottomNavHomeLabel = "ホーム",
            bottomNavHospitalLabel = "医療機関",
            bottomNavGuideLabel = "ガイド",
            bottomNavMapLabel = "地図",
            bottomNavDocumentScanLabel = "文書スキャン"
        )
    }
}
