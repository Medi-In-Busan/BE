package com.mediinbusan.app.core.i18n

/**
 * S-08 지도 화면(하단 탭 '지도'로 진입하는 전체 병원 브라우징 모드) 전용 정적 UI 문구.
 * "뒤로가기"/"길찾기"처럼 다른 화면과 뜻이 겹치는 문구는 새로 만들지 않고
 * [CommonStrings]/[HospitalDetailStrings]를 그대로 재사용한다(MapScreen.kt 참고).
 */
data class MapStrings(
    val searchPlaceholder: String,
    val filterLabel: String,
    val searchThisAreaLabel: String,
    val searchingThisAreaLabel: String,
    val recenterContentDescription: String,
    val categoryAllLabel: String,
    val categoryHospitalLabel: String,
    val categoryTouristLabel: String,
    val categoryFoodLabel: String,
    val emptyHospitalMessage: String,
    val emptyPlaceMessage: String,
    val detailButtonLabel: String,
    val hidePanelContentDescription: String,
    val showPanelContentDescription: String,
    val specialtyFilterSectionLabel: String,
    // F-014 웰니스 코스 동선 모드(Route.MapView.courseId) 전용. "예상 소요 " 뒤에 "${hours}h"를
    // 그대로 이어붙이는 접두어 필드다 — 이 파일의 다른 필드처럼 함수가 아닌 flat field 컨벤션을 따른다.
    val courseDurationPrefix: String,
    val courseNotFoundMessage: String,
    // 코스 동선 지도 위 화살표 경로선(외부 카카오맵 앱 연동 없이 우리 지도 안에서 방향을 표시,
    // core/ui/KakaoMapView.kt의 renderRoute 참고)이 뭘 뜻하는지 짚어주는 짧은 안내 문구.
    val routeArrowHintLabel: String,
    // "번역된 장소만" 토글(MapUiState.languageFilterEnabled) — 한국어 UI에서는 이 토글 자체를
    // 숨기므로(MapScreen.kt BrowseMap 참고) 항상 안 쓰이지만, 다른 필드와 같은 규칙으로 Ko도 채운다.
    val languageFilterLabel: String
) {
    companion object {
        val Ko = MapStrings(
            searchPlaceholder = "지도에서 검색...",
            filterLabel = "필터",
            searchThisAreaLabel = "이 위치에서 검색",
            searchingThisAreaLabel = "검색 중…",
            recenterContentDescription = "기본 위치로 이동",
            categoryAllLabel = "전체",
            categoryHospitalLabel = "병원",
            categoryTouristLabel = "관광",
            categoryFoodLabel = "음식",
            emptyHospitalMessage = "표시할 병원이 없습니다.",
            emptyPlaceMessage = "표시할 장소가 없습니다.",
            detailButtonLabel = "상세보기",
            hidePanelContentDescription = "카드 목록 숨기기",
            showPanelContentDescription = "카드 목록 보기",
            specialtyFilterSectionLabel = "진료과목",
            courseDurationPrefix = "예상 소요 ",
            courseNotFoundMessage = "코스 정보를 불러올 수 없습니다.",
            routeArrowHintLabel = "지도의 화살표를 따라 이동 순서를 확인하세요",
            languageFilterLabel = "번역된 곳만"
        )
        val En = MapStrings(
            searchPlaceholder = "Search on map...",
            filterLabel = "Filter",
            searchThisAreaLabel = "Search this area",
            searchingThisAreaLabel = "Searching…",
            recenterContentDescription = "Move to default location",
            categoryAllLabel = "All",
            categoryHospitalLabel = "Hospital",
            categoryTouristLabel = "Sightseeing",
            categoryFoodLabel = "Food",
            emptyHospitalMessage = "No hospitals to show.",
            emptyPlaceMessage = "No places to show.",
            detailButtonLabel = "View details",
            hidePanelContentDescription = "Hide card list",
            showPanelContentDescription = "Show card list",
            specialtyFilterSectionLabel = "Specialty",
            courseDurationPrefix = "Est. ",
            courseNotFoundMessage = "Couldn't load course details.",
            routeArrowHintLabel = "Follow the arrows on the map to see the visiting order",
            languageFilterLabel = "Translated only"
        )
        val Zh = MapStrings(
            searchPlaceholder = "在地图上搜索...",
            filterLabel = "筛选",
            searchThisAreaLabel = "搜索此区域",
            searchingThisAreaLabel = "搜索中…",
            recenterContentDescription = "移动到默认位置",
            categoryAllLabel = "全部",
            categoryHospitalLabel = "医院",
            categoryTouristLabel = "观光",
            categoryFoodLabel = "美食",
            emptyHospitalMessage = "没有可显示的医院。",
            emptyPlaceMessage = "没有可显示的场所。",
            detailButtonLabel = "查看详情",
            hidePanelContentDescription = "隐藏卡片列表",
            showPanelContentDescription = "显示卡片列表",
            specialtyFilterSectionLabel = "诊疗科目",
            courseDurationPrefix = "预计所需 ",
            courseNotFoundMessage = "无法加载课程信息。",
            routeArrowHintLabel = "请沿地图上的箭头查看移动顺序",
            languageFilterLabel = "仅显示已翻译"
        )
        val Ja = MapStrings(
            searchPlaceholder = "地図で検索...",
            filterLabel = "フィルター",
            searchThisAreaLabel = "この付近で検索",
            searchingThisAreaLabel = "検索中…",
            recenterContentDescription = "初期位置に移動",
            categoryAllLabel = "すべて",
            categoryHospitalLabel = "病院",
            categoryTouristLabel = "観光",
            categoryFoodLabel = "グルメ",
            emptyHospitalMessage = "表示できる病院がありません。",
            emptyPlaceMessage = "表示できるスポットがありません。",
            detailButtonLabel = "詳細を見る",
            hidePanelContentDescription = "カードリストを隠す",
            showPanelContentDescription = "カードリストを表示",
            specialtyFilterSectionLabel = "診療科目",
            courseDurationPrefix = "所要時間目安 ",
            courseNotFoundMessage = "コース情報を読み込めません。",
            routeArrowHintLabel = "地図の矢印に沿って移動順序をご確認ください",
            languageFilterLabel = "翻訳済みのみ"
        )
    }
}
