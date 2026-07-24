package com.mediinbusan.app.core.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation Compose 2.8+ 타입세이프 라우트. 화면 ID(S-01~S-10)와 1:1로 대응한다.
 */
@Serializable
sealed interface Route {
    @Serializable
    data object Splash : Route // S-01

    @Serializable
    data object Onboarding : Route // S-02

    @Serializable
    data object Home : Route // S-03

    @Serializable
    data class HospitalList(val medicalPurpose: String? = null) : Route // S-04

    @Serializable
    data class HospitalDetail(val hospitalId: String) : Route // S-05

    @Serializable
    data object Search : Route // 검색 (UI 스켈레톤 — 병원+장소 통합 검색은 다음 이슈)

    @Serializable
    data object Guide : Route // S-06

    @Serializable
    data class Nearby(val hospitalId: String) : Route // S-07

    @Serializable
    data class PlaceDetail(val placeId: String) : Route // S-07 상세

    @Serializable
    data class MapView(val hospitalId: String? = null) : Route // S-08, hospitalId=null이면 전체 병원 지도 모드

    @Serializable
    data object Favorite : Route // S-09

    @Serializable
    data object Settings : Route // S-10

    @Serializable
    data class SettingsInfoDetail(val infoId: String) : Route // S-10 하위 정적 정보 페이지 (이용안내/개인정보처리방침/이용약관)

    @Serializable
    data object NotificationSettings : Route // S-10 하위 알림 설정 (로컬 토글, 실제 푸시 인프라 없음)

    @Serializable
    data object RecentlyViewed : Route // S-10 하위 최근 본 항목 (F-016)

    @Serializable
    data object SelfDiagnosis : Route // TODO: 화면 미구현, 진입점만 예약 (자가진단 플로우는 별도 이슈)
}
