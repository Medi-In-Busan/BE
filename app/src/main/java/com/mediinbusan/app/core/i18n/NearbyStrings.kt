package com.mediinbusan.app.core.i18n

/**
 * S-07(주변 관광·웰니스) 전용 정적 UI 문구.
 * NearbyScreen.kt는 원래 전체가 하드코딩 한국어였다 — F-014 지도 연동(웰니스 코스 동선)에서
 * 새로 추가하는 "이 코스 동선 보기" 버튼 문구만 CLAUDE.md §5 규칙대로 이 시스템을 거친다.
 * 나머지 기존 문구의 소급 i18n 정비는 별도 스코프로 남겨둔다.
 *
 * 이후 PlaceDetailScreen.kt(같은 S-07 화면 그룹의 상세 화면)를 정비하며 그쪽 문구도 여기 추가했다 —
 * 다른 화면과 뜻이 겹치는 것(뒤로가기/공유/전화/위치/정보 없음/길찾기 등)은 CLAUDE.md §5 규칙대로
 * common/hospitalDetail의 기존 필드를 그대로 재사용하고, 새로 만든 건 이 화면 그룹 고유 문구뿐이다.
 * PlaceType.label/recoveryHint의 다국어 버전은 PlaceTypeStrings.kt(MedicalCategoryStrings.kt와
 * 같은 패턴) 참고.
 */
data class NearbyStrings(
    val viewCourseRouteButtonLabel: String,
    val placeNotFoundMessage: String,
    val introSectionTitle: String,
    val distanceLabel: String,
    val distanceFromHospitalFormat: String,
    val lastUpdatedLabel: String,
    val favoriteAddContentDescription: String,
    val favoriteRemoveContentDescription: String,
    val recoveryCheckTitle: String,
    val recoveryDisclaimer: String
) {
    companion object {
        val Ko = NearbyStrings(
            viewCourseRouteButtonLabel = "이 코스 동선 보기",
            placeNotFoundMessage = "장소 정보를 찾을 수 없습니다.",
            introSectionTitle = "소개",
            distanceLabel = "거리",
            distanceFromHospitalFormat = "병원에서 %s",
            lastUpdatedLabel = "갱신일",
            favoriteAddContentDescription = "즐겨찾기 추가",
            favoriteRemoveContentDescription = "즐겨찾기 해제",
            recoveryCheckTitle = "진료 전후 체크",
            recoveryDisclaimer = "개인 상태에 따라 적합한 활동이 달라질 수 있으니, 진료 직후 일정은 병원 안내를 우선하세요."
        )
        val En = NearbyStrings(
            viewCourseRouteButtonLabel = "View route on map",
            placeNotFoundMessage = "Place information not found.",
            introSectionTitle = "About",
            distanceLabel = "Distance",
            distanceFromHospitalFormat = "%s from hospital",
            lastUpdatedLabel = "Updated",
            favoriteAddContentDescription = "Add to favorites",
            favoriteRemoveContentDescription = "Remove from favorites",
            recoveryCheckTitle = "Before & after your visit",
            recoveryDisclaimer = "Suitable activities can vary by individual condition — for the schedule right after treatment, follow your hospital's guidance first."
        )
        val Zh = NearbyStrings(
            viewCourseRouteButtonLabel = "在地图上查看路线",
            placeNotFoundMessage = "未找到场所信息。",
            introSectionTitle = "介绍",
            distanceLabel = "距离",
            distanceFromHospitalFormat = "距医院 %s",
            lastUpdatedLabel = "更新日期",
            favoriteAddContentDescription = "添加收藏",
            favoriteRemoveContentDescription = "取消收藏",
            recoveryCheckTitle = "诊疗前后须知",
            recoveryDisclaimer = "适合的活动可能因个人状况而异，诊疗结束后的日程请优先遵循医院的指引。"
        )
        val Ja = NearbyStrings(
            viewCourseRouteButtonLabel = "このコースの経路を見る",
            placeNotFoundMessage = "スポット情報が見つかりません。",
            introSectionTitle = "紹介",
            distanceLabel = "距離",
            distanceFromHospitalFormat = "病院から%s",
            lastUpdatedLabel = "更新日",
            favoriteAddContentDescription = "お気に入りに追加",
            favoriteRemoveContentDescription = "お気に入りを解除",
            recoveryCheckTitle = "診療前後のチェック",
            recoveryDisclaimer = "体調によって適した活動は異なるため、診療直後の予定は病院の案内を優先してください。"
        )
    }
}
