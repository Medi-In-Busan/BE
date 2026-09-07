package com.mediinbusan.app.feature.nearby

import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.WellnessWalkingCourse
import com.mediinbusan.app.domain.course.HospitalWellnessRoute
import com.mediinbusan.app.domain.tourism.TourismHotPlace
import com.mediinbusan.app.domain.tourism.TourismCatalogItem

data class NearbyUiState(
    val selectedLanguage: String = "ko",
    val isLoading: Boolean = true,
    val places: List<Place> = emptyList(),
    val recommendedRoutes: List<HospitalWellnessRoute> = emptyList(),
    val hotPlaces: List<TourismHotPlace> = emptyList(),
    val isHotPlacesLoading: Boolean = true,
    val hotPlacesError: String? = null,
    val walkingCourses: List<WellnessWalkingCourse> = emptyList(),
    val tourismPreviews: List<TourismCatalogItem> = emptyList(),
    val accessiblePreviews: List<TourismCatalogItem> = emptyList(),
    val errorMessage: String? = null
)

data class PlaceDetailUiState(
    val isLoading: Boolean = true,
    val place: Place? = null,
    val isFavorite: Boolean = false,
    val errorMessage: String? = null,
    // 이 장소 좌표를 기준으로 조회한, 같은 종류(PlaceType)의 주변 장소들(자기 자신 제외).
    // 곁들이는 추천이라 실패해도 화면 전체를 오류로 만들지 않는다 — 빈 목록이면 섹션이 사라진다.
    val nearbySamePlaces: List<Place> = emptyList()
)
