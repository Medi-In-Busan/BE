package com.mediinbusan.app.domain.course

import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.hospital.HospitalRepository
import com.mediinbusan.app.data.place.PlaceRepository
import com.mediinbusan.app.data.recent.RecentRepository
import com.mediinbusan.app.data.tourism.TourismInteractionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetRecommendedHospitalWellnessRouteUseCase @Inject constructor(
    private val hospitalRepository: HospitalRepository,
    private val placeRepository: PlaceRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val interactionRepository: TourismInteractionRepository,
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository,
    private val buildPersonalization: BuildHospitalWellnessPersonalizationUseCase,
    private val buildRoute: BuildHospitalWellnessRouteUseCase
) {
    suspend operator fun invoke(hospitalId: String, routeIndex: Int = 0): Result<HospitalWellnessRoute> {
        return when (val result = getRoutes(hospitalId)) {
            is Result.Success -> result.data.getOrNull(routeIndex)?.let { Result.Success(it) }
                ?: Result.Error(message = "선택한 추천 코스를 찾을 수 없습니다.")
            is Result.Error -> result
            Result.Loading -> Result.Loading
        }
    }

    suspend fun getRoutes(hospitalId: String): Result<List<HospitalWellnessRoute>> {
        val preferences = userPreferencesRepository.userPreferences.first()
        val hospitalResult = hospitalRepository.getHospitalDetail(hospitalId, preferences.languageCode)
            .first { it !is Result.Loading }
        val placesResult = placeRepository.getNearbyPlaces(hospitalId)
            .first { it !is Result.Loading }
        val hospital = (hospitalResult as? Result.Success)?.data
        val places = (placesResult as? Result.Success)?.data
        if (hospital == null || places == null) {
            return Result.Error(
                message = (hospitalResult as? Result.Error)?.message
                    ?: (placesResult as? Result.Error)?.message
                    ?: "추천 코스를 불러오지 못했습니다."
            )
        }

        val personalization = buildPersonalization(
            medicalPurpose = preferences.medicalPurpose,
            profile = interactionRepository.profile.first(),
            favorites = favoriteRepository.observeFavorites().first(),
            recentItems = recentRepository.observeRecentlyViewed().first()
        )
        val routes = buildRoute.buildAlternatives(
            hospital = hospital,
            places = places,
            personalization = personalization
        )
        if (routes.isEmpty()) {
            return Result.Error(message = "코스를 만들 수 있는 주변 장소가 4곳 이상 필요합니다.")
        }

        return Result.Success(routes)
    }
}
