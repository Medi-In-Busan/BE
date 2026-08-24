package com.mediinbusan.app.feature.nearby

import com.mediinbusan.app.domain.course.HospitalWellnessRoute
import com.mediinbusan.app.data.route.TravelMode

data class WellnessCourseMapUiState(
    val isLoading: Boolean = true,
    val route: HospitalWellnessRoute? = null,
    val selectedId: String? = null,
    val travelMode: TravelMode = TravelMode.DRIVING,
    val isRouteRefreshing: Boolean = false,
    val routeErrorMessage: String? = null,
    val errorMessage: String? = null
)
