package com.mediinbusan.app.feature.nearby

import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.WellnessWalkingCourse
import com.mediinbusan.app.domain.course.HospitalWellnessRoute
import com.mediinbusan.app.domain.tourism.TourismHotPlace
import com.mediinbusan.app.domain.tourism.TourismCatalogItem

data class NearbyUiState(
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
    val errorMessage: String? = null
)
