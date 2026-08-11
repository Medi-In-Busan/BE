package com.mediinbusan.app.feature.map

import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceType

enum class MapCategory { ALL, HOSPITAL, TOURIST, FOOD }

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
    val favoriteHospitalIds: Set<String> = emptySet(),
    // "이 위치에서 검색" 버튼 눌러서 allHospitals를 갱신하는 중일 때만 true. isLoading과 달리 지도/마커를 가리지 않는다.
    val isSearchingArea: Boolean = false
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
                MapCategory.ALL -> allPlaces
                MapCategory.TOURIST -> allPlaces.filter { it.type == PlaceType.TOURIST_ATTRACTION }
                MapCategory.FOOD -> allPlaces.filter { it.type == PlaceType.RESTAURANT }
                MapCategory.HOSPITAL -> emptyList()
            }
            return if (searchQuery.isBlank()) byCategory else byCategory.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

    // visibleHospitals와 달리 "관광"/"음식" 탭에서는 병원을 숨긴다 — visiblePlaces가 이미 카테고리별로
    // 걸러주는 것과 대칭을 맞춰, 지도 핀·하단 카드 목록이 항상 같은 기준으로 표시되게 한다.
    val categoryHospitals: List<Hospital>
        get() = if (selectedCategory == MapCategory.TOURIST || selectedCategory == MapCategory.FOOD) {
            emptyList()
        } else {
            visibleHospitals
        }
}
