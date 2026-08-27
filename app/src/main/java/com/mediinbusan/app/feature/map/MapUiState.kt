package com.mediinbusan.app.feature.map

import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceType
import com.mediinbusan.app.domain.course.WellnessCourse

enum class MapCategory { ALL, HOSPITAL, TOURIST, FOOD }

data class MapUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    // hospitalId != null인 "특정 병원 지도" 모드(상세페이지 '지도에서 보기'로 진입)에서만 채워진다.
    val focusedHospital: Hospital? = null,
    val nearbyPlaces: List<Place> = emptyList(),
    // hospitalId + courseId 둘 다 있는 "코스 동선" 모드에서만 채워진다(Route.MapView.courseId 참고).
    val activeCourse: WellnessCourse? = null,
    // hospitalId == null인 "전체 병원 브라우징" 모드(하단 탭 '지도'로 진입)에서만 채워진다.
    val allHospitals: List<Hospital> = emptyList(),
    val allPlaces: List<Place> = emptyList(),
    val selectedCategory: MapCategory = MapCategory.HOSPITAL,
    val searchQuery: String = "",
    val selectedMarkerId: String? = null,
    val favoriteHospitalIds: Set<String> = emptySet(),
    // "이 위치에서 검색" 버튼 눌러서 allHospitals를 갱신하는 중일 때만 true. isLoading과 달리 지도/마커를 가리지 않는다.
    val isSearchingArea: Boolean = false,
    // MedicalCategory.label(한국어) 값의 집합 — HospitalSearchListUiState의 SearchFilterChip과 같은
    // 식별자 규칙을 따른다. 비어있으면 진료과목으로 거르지 않는다(필터 미적용).
    val selectedSpecialties: Set<String> = emptySet()
) {
    val visibleHospitals: List<Hospital>
        get() {
            val byQuery = if (searchQuery.isBlank()) {
                allHospitals
            } else {
                allHospitals.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }
            return if (selectedSpecialties.isEmpty()) {
                byQuery
            } else {
                byQuery.filter { hospital -> hospital.specialties.any { it in selectedSpecialties } }
            }
        }

    val visiblePlaces: List<Place>
        get() {
            // "관광" 탭은 PlaceType.TOURIST_ATTRACTION만 세던 게 버그였다 — MapScreen.kt의
            // Place.toMapPin()(그리고 PlaceDetailScreen 등 다른 화면)은 RESTAURANT만 FOOD로 두고
            // 나머지(SHOPPING/LODGING/SPA/WALK/OTHER 포함)는 전부 "관광" 마커로 묶어서 그린다.
            // "전체" 탭에서는 그 규칙대로 다 보이는데, "관광" 탭만 TOURIST_ATTRACTION으로 좁게 걸러
            // 같은 장소인데도 전체일 때 개수와 관광 탭일 때 개수가 달라 보였다 — 마커 색 분류와
            // 동일한 기준(RESTAURANT가 아니면 전부 관광)으로 맞춘다.
            val byCategory = when (selectedCategory) {
                MapCategory.ALL -> allPlaces
                MapCategory.TOURIST -> allPlaces.filter { it.type != PlaceType.RESTAURANT }
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
