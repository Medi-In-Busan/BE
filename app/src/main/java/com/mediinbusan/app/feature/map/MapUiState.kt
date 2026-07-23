package com.mediinbusan.app.feature.map

import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceType

enum class MapCategory { HOSPITAL, TOURIST, FOOD }

data class MapUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    // hospitalId != null인 "특정 병원 지도" 모드(상세페이지 '지도에서 보기'로 진입)에서만 채워진다.
    val focusedHospital: Hospital? = null,
    val nearbyPlaces: List<Place> = emptyList(),
    // hospitalId == null인 "전체 병원 브라우징" 모드(하단 탭 '지도'로 진입)에서만 채워진다.
    val allHospitals: List<Hospital> = emptyList(),
    val allPlaces: List<Place> = emptyList(),
    val selectedCategory: MapCategory = MapCategory.HOSPITAL,
    val searchQuery: String = "",
    val selectedMarkerId: String? = null,
    val favoriteHospitalIds: Set<String> = emptySet()
) {
    val visibleHospitals: List<Hospital>
        get() = if (searchQuery.isBlank()) {
            allHospitals
        } else {
            allHospitals.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

    val visiblePlaces: List<Place>
        get() {
            val byCategory = when (selectedCategory) {
                MapCategory.TOURIST -> allPlaces.filter { it.type == PlaceType.TOURIST_ATTRACTION }
                MapCategory.FOOD -> allPlaces.filter { it.type == PlaceType.RESTAURANT }
                MapCategory.HOSPITAL -> emptyList()
            }
            return if (searchQuery.isBlank()) byCategory else byCategory.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
}
