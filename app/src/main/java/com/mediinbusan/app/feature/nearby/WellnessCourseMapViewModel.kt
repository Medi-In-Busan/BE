package com.mediinbusan.app.feature.nearby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.core.i18n.appStringsFor
import com.mediinbusan.app.data.route.DrivingRoute
import com.mediinbusan.app.data.route.DrivingRoutePoint
import com.mediinbusan.app.data.route.DrivingRouteRepository
import com.mediinbusan.app.data.route.TravelMode
import com.mediinbusan.app.domain.course.GetRecommendedHospitalWellnessRouteUseCase
import com.mediinbusan.app.domain.course.HospitalWellnessRoute
import com.mediinbusan.app.domain.course.HospitalWellnessRoutePoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class WellnessCourseMapViewModel @Inject constructor(
    private val getRecommendedRoute: GetRecommendedHospitalWellnessRouteUseCase,
    private val drivingRouteRepository: DrivingRouteRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WellnessCourseMapUiState())
    val uiState: StateFlow<WellnessCourseMapUiState> = _uiState
    private var recommendedRoute: HospitalWellnessRoute? = null

    fun load(hospitalId: String, courseIndex: Int = 0) {
        viewModelScope.launch {
            val strings = appStringsFor(userPreferencesRepository.userPreferences.first().languageCode).nearby
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val recommendationResult = getRecommendedRoute(hospitalId, courseIndex)
            val recommendation = (recommendationResult as? Result.Success)?.data
            if (recommendation == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = (recommendationResult as? Result.Error)?.message
                            ?: strings.courseLoadError
                    )
                }
                return@launch
            }
            recommendedRoute = recommendation
            val routeResult = getRoute(recommendation, TravelMode.DRIVING)
            val route = (routeResult as? Result.Success)?.data?.let { recommendation.withActualRoute(it) }
            _uiState.value = WellnessCourseMapUiState(
                isLoading = false,
                route = route,
                selectedId = recommendation.stops.firstOrNull()?.place?.id,
                travelMode = TravelMode.DRIVING,
                errorMessage = if (route == null) {
                    (routeResult as? Result.Error)?.message ?: strings.routeLoadError
                } else {
                    null
                }
            )
        }
    }

    fun select(id: String) {
        _uiState.update { it.copy(selectedId = id) }
    }

    fun selectTravelMode(mode: TravelMode) {
        val recommendation = recommendedRoute ?: return
        if (_uiState.value.travelMode == mode || _uiState.value.isRouteRefreshing) return
        viewModelScope.launch {
            val strings = appStringsFor(userPreferencesRepository.userPreferences.first().languageCode).nearby
            _uiState.update { it.copy(isRouteRefreshing = true, routeErrorMessage = null) }
            val result = getRoute(recommendation, mode)
            val route = (result as? Result.Success)?.data?.let { recommendation.withActualRoute(it) }
            if (route == null) {
                _uiState.update {
                    it.copy(
                        isRouteRefreshing = false,
                        routeErrorMessage = (result as? Result.Error)?.message ?: strings.routeChangeError
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        route = route,
                        travelMode = mode,
                        isRouteRefreshing = false,
                        routeErrorMessage = null
                    )
                }
            }
        }
    }

    private suspend fun getRoute(
        recommendation: HospitalWellnessRoute,
        mode: TravelMode
    ): Result<DrivingRoute> = drivingRouteRepository.getRoute(
        origin = DrivingRoutePoint(
            name = recommendation.hospital.name,
            latitude = requireNotNull(recommendation.hospital.latitude),
            longitude = requireNotNull(recommendation.hospital.longitude)
        ),
        stops = recommendation.stops.map { stop ->
            DrivingRoutePoint(
                name = stop.place.name,
                latitude = requireNotNull(stop.place.latitude),
                longitude = requireNotNull(stop.place.longitude)
            )
        },
        mode = mode
    )

    private fun HospitalWellnessRoute.withActualRoute(actualRoute: DrivingRoute): HospitalWellnessRoute? {
        if (actualRoute.path.size < 2 || actualRoute.sections.size != stops.size) return null
        return copy(
            stops = stops.mapIndexed { index, stop ->
                val section = actualRoute.sections[index]
                stop.copy(
                    distanceFromPreviousKm = section.distanceMeters / 1_000.0,
                    transferMinutes = ceil(section.durationSeconds / 60.0).toInt().coerceAtLeast(1)
                )
            },
            totalDistanceKm = actualRoute.distanceMeters / 1_000.0,
            estimatedDurationMinutes = ceil(actualRoute.durationSeconds / 60.0).toInt(),
            roadPath = actualRoute.path.map { HospitalWellnessRoutePoint(it.latitude, it.longitude) },
            hasActualDrivingRoute = true
        )
    }
}
