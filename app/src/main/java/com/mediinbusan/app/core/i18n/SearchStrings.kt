package com.mediinbusan.app.core.i18n

data class SearchStrings(
    val recentSearchesEmpty: String,
    val recentSearchesTitle: String,
    val noMatchingHospitals: String,
    val deleteSearchTermContentDescription: String,
    val sortLabel: String,
    val emptyResultsTitle: String,
    val emptyResultsSubtitle: String,
    val resetFiltersButton: String,
    // 두 span(검정 라벨 + 코랄 건수)으로 나눠 그리는 결과 카운트 문구용 조각.
    val resultCountWithQueryFormat: String,
    val resultCountGenericLabel: String,
    val resultCountSuffixFormat: String,
    val tourismFilterLabel: String,
    val sortName: String,
    val sortDistance: String,
    val loadErrorFallback: String
) {
    companion object {
        val Ko = SearchStrings(
            recentSearchesEmpty = "최근 검색어가 없어요",
            recentSearchesTitle = "최근 검색어",
            noMatchingHospitals = "일치하는 병원이 없어요",
            deleteSearchTermContentDescription = "검색어 삭제",
            sortLabel = "정렬",
            emptyResultsTitle = "원하는 결과가 없으신가요?",
            emptyResultsSubtitle = "검색어를 변경하거나 필터 조건을 확인해보세요",
            resetFiltersButton = "검색조건 초기화",
            resultCountWithQueryFormat = "'%s' 검색결과 ",
            resultCountGenericLabel = "검색결과 ",
            resultCountSuffixFormat = "%d건",
            tourismFilterLabel = "관광지",
            sortName = "이름순",
            sortDistance = "가까운순",
            loadErrorFallback = "검색 결과를 불러오지 못했습니다."
        )
        val En = SearchStrings(
            recentSearchesEmpty = "No recent searches",
            recentSearchesTitle = "Recent searches",
            noMatchingHospitals = "No matching hospitals",
            deleteSearchTermContentDescription = "Delete search term",
            sortLabel = "Sort",
            emptyResultsTitle = "Not finding what you're looking for?",
            emptyResultsSubtitle = "Try a different search term or check your filters",
            resetFiltersButton = "Reset filters",
            resultCountWithQueryFormat = "Results for '%s' ",
            resultCountGenericLabel = "Results ",
            resultCountSuffixFormat = "%d",
            tourismFilterLabel = "Tourist attractions",
            sortName = "Name",
            sortDistance = "Nearest",
            loadErrorFallback = "Couldn't load search results."
        )
        val Zh = SearchStrings(
            recentSearchesEmpty = "暂无最近搜索",
            recentSearchesTitle = "最近搜索",
            noMatchingHospitals = "没有匹配的医院",
            deleteSearchTermContentDescription = "删除搜索词",
            sortLabel = "排序",
            emptyResultsTitle = "没有找到您想要的结果吗？",
            emptyResultsSubtitle = "请更改搜索词或检查筛选条件",
            resetFiltersButton = "重置搜索条件",
            resultCountWithQueryFormat = "“%s” 的搜索结果 ",
            resultCountGenericLabel = "搜索结果 ",
            resultCountSuffixFormat = "%d 条",
            tourismFilterLabel = "旅游景点",
            sortName = "按名称排序",
            sortDistance = "按距离排序",
            loadErrorFallback = "无法加载搜索结果。"
        )
        val Ja = SearchStrings(
            recentSearchesEmpty = "最近の検索履歴がありません",
            recentSearchesTitle = "最近の検索",
            noMatchingHospitals = "一致する病院がありません",
            deleteSearchTermContentDescription = "検索語を削除",
            sortLabel = "並び替え",
            emptyResultsTitle = "お探しの結果が見つかりませんか？",
            emptyResultsSubtitle = "検索語を変更するか、フィルター条件をご確認ください",
            resetFiltersButton = "検索条件をリセット",
            resultCountWithQueryFormat = "「%s」の検索結果 ",
            resultCountGenericLabel = "検索結果 ",
            resultCountSuffixFormat = "%d件",
            tourismFilterLabel = "観光スポット",
            sortName = "名前順",
            sortDistance = "近い順",
            loadErrorFallback = "検索結果を読み込めませんでした。"
        )
    }
}
