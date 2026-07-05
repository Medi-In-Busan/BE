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
    data object Guide : Route // S-06

    @Serializable
    data class Nearby(val hospitalId: String) : Route // S-07

    @Serializable
    data class PlaceDetail(val placeId: String) : Route // S-07 상세

    @Serializable
    data class MapView(val hospitalId: String) : Route // S-08

    @Serializable
    data object Favorite : Route // S-09

    @Serializable
    data object Settings : Route // S-10
}
