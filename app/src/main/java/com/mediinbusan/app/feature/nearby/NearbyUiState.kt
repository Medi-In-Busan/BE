package com.mediinbusan.app.feature.nearby

import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.WellnessWalkingCourse
import com.mediinbusan.app.domain.course.WellnessCourse

data class NearbyUiState(
    val isLoading: Boolean = true,
    val places: List<Place> = emptyList(),
    val courses: List<WellnessCourse> = emptyList(),
    val walkingCourses: List<WellnessWalkingCourse> = emptyList(),
    val errorMessage: String? = null
)

data class PlaceDetailUiState(
    val isLoading: Boolean = true,
    val place: Place? = null,
    val isFavorite: Boolean = false,
    val errorMessage: String? = null
)
