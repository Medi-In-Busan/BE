package com.mediinbusan.app.core.i18n

/**
 * S-07(주변 관광·웰니스) 전용 정적 UI 문구.
 * NearbyScreen.kt는 원래 전체가 하드코딩 한국어였다 — F-014 지도 연동(웰니스 코스 동선)에서
 * 새로 추가하는 "이 코스 동선 보기" 버튼 문구만 CLAUDE.md §5 규칙대로 이 시스템을 거친다.
 * 나머지 기존 문구의 소급 i18n 정비는 별도 스코프로 남겨둔다.
 */
data class NearbyStrings(
    val viewCourseRouteButtonLabel: String
) {
    companion object {
        val Ko = NearbyStrings(viewCourseRouteButtonLabel = "이 코스 동선 보기")
        val En = NearbyStrings(viewCourseRouteButtonLabel = "View route on map")
        val Zh = NearbyStrings(viewCourseRouteButtonLabel = "在地图上查看路线")
        val Ja = NearbyStrings(viewCourseRouteButtonLabel = "このコースの経路を見る")
    }
}
