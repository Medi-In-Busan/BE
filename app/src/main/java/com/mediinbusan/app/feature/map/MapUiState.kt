package com.mediinbusan.app.feature.map

import com.mediinbusan.app.core.common.haversineDistanceMeters
import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceCategory
import com.mediinbusan.app.data.place.PlaceType
import com.mediinbusan.app.domain.course.WellnessCourse

enum class MapCategory { ALL, HOSPITAL, TOURIST, FOOD }

/** "이 위치에서 검색"의 기준 좌표. 지도 카메라 중심이지 기기 GPS가 아니다(CLAUDE.md §1). */
data class MapPoint(val latitude: Double, val longitude: Double)

/** "이 위치에서 검색"으로 장소 목록을 좁힐 반경. 병원 쪽 서버 조회 반경과 비슷한 감각으로 맞췄다. */
private const val AREA_SEARCH_RADIUS_METERS = 5_000.0

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
    // 카테고리 탭(전체/병원/관광/음식)이나 "이 위치에서 검색"을 직접 누르기 전까지는 마커를 하나도
    // 그리지 않는다(첫 진입에 마커가 지도를 다 덮는 걸 막는다). 켜져 있는 탭을 한 번 더 누르면 다시
    // false가 되어 마커와 하단 목록 시트가 함께 사라진다. 화면 상태(remember)가 아니라 여기 두는
    // 이유는, 마커→상세화면→뒤로가기로 돌아왔을 때 골라둔 카테고리가 그대로 남아있어야 하기 때문이다.
    val markersActivated: Boolean = false,
    // 하단 시트가 "펼친 목록"(목적별로 찾는 장소) 상태인지. markersActivated와 같은 이유로 화면
    // 상태(rememberSaveable)가 아니라 여기 둔다 — 목록에서 항목을 골라 상세로 갔다 뒤로 돌아오면
    // MapScreen이 잠깐 LoadingState로 바뀌면서 BrowseMap이 통째로 사라지는데, 그때 화면 로컬
    // 상태는 같이 날아가 목록이 접힌 채(=지도만 보이는 채) 돌아왔다.
    val isListExpanded: Boolean = false,
    val searchQuery: String = "",
    val selectedMarkerId: String? = null,
    val favoriteHospitalIds: Set<String> = emptySet(),
    val favoritePlaceIds: Set<String> = emptySet(),
    // "이 위치에서 검색"으로 좁힌 기준점. 병원은 이 좌표로 서버를 다시 조회하고(searchThisArea),
    // 장소는 서버에 좌표 기반 조회 API가 없어(PlaceRepository 참고) 이미 받아둔 전체 목록을
    // 이 좌표 반경으로 거른다 — 어느 탭에서 눌러도 "이 주변만 보기"라는 같은 뜻이 되게 한다.
    // 카테고리를 바꾸면 해제된다.
    val areaCenter: MapPoint? = null,
    // "이 위치에서 검색" 버튼 눌러서 allHospitals를 갱신하는 중일 때만 true. isLoading과 달리 지도/마커를 가리지 않는다.
    val isSearchingArea: Boolean = false,
    // MedicalCategory.label(한국어) 값의 집합 — HospitalSearchListUiState의 SearchFilterChip과 같은
    // 식별자 규칙을 따른다. 비어있으면 진료과목으로 거르지 않는다(필터 미적용).
    val selectedSpecialties: Set<String> = emptySet(),
    // "관광" 탭의 종류 필터(관광지/쇼핑/숙소/스파/산책). 진료과목 필터가 병원 탭에서 하는 일을
    // 장소 탭에서 그대로 한다 — 비어있으면 미적용. 탭을 바꾸면 ViewModel이 비운다.
    val selectedPlaceTypes: Set<PlaceType> = emptySet(),
    // "음식" 탭의 요리 종류 필터(한식/일식/중식/양식/이색/카페). 음식 탭은 전부 같은
    // PlaceType.RESTAURANT라 종류로는 못 가르고, 한 단계 아래인 PlaceCategory로만 갈린다.
    val selectedPlaceCategories: Set<PlaceCategory> = emptySet(),
    // "번역된 장소만" 필터 — 켜져 있으면 visiblePlaces에서 Place.isTranslated == false인 장소를
    // 숨긴다(병원은 이름·주소가 번역 대상이 아니라 영향 없음). 한국어일 땐 의미가 없어(전부
    // isTranslated=true) MapScreen에서 토글 자체를 숨긴다.
    val languageFilterEnabled: Boolean = false
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
            // 탭별 세부 필터(관광=종류, 음식=요리 종류). 병원 탭의 진료과목 필터와 같은 자리·같은
            // 규칙이다 — 아무것도 안 고르면 미적용.
            val byKind = when (selectedCategory) {
                MapCategory.TOURIST ->
                    if (selectedPlaceTypes.isEmpty()) byCategory else byCategory.filter { it.type in selectedPlaceTypes }
                MapCategory.FOOD ->
                    if (selectedPlaceCategories.isEmpty()) byCategory else byCategory.filter { it.category in selectedPlaceCategories }
                MapCategory.ALL, MapCategory.HOSPITAL -> byCategory
            }
            val byQuery = if (searchQuery.isBlank()) byKind else byKind.filter { it.name.contains(searchQuery, ignoreCase = true) }
            val byLanguage = if (languageFilterEnabled) byQuery.filter { it.isTranslated } else byQuery
            // "이 위치에서 검색"을 누른 뒤에는 그 지점 반경 안의 장소만 남긴다(병원은 서버 조회
            // 자체가 그 지점 기준이라 이 필터가 필요 없다).
            val center = areaCenter ?: return byLanguage
            return byLanguage.filter { place ->
                val lat = place.latitude ?: return@filter false
                val lng = place.longitude ?: return@filter false
                haversineDistanceMeters(center.latitude, center.longitude, lat, lng) <= AREA_SEARCH_RADIUS_METERS
            }
        }

    /**
     * "관광" 탭 필터 칩으로 내놓을 종류 — 지금 지도에 올라와 있는 장소에 실제로 있는 것만 고른다.
     * 있지도 않은 종류를 눌러 "검색 결과 없음"을 보게 만들지 않으려는 것이다.
     */
    val tourPlaceTypeOptions: List<PlaceType>
        get() = PlaceType.entries.filter { type ->
            type != PlaceType.RESTAURANT && allPlaces.any { it.type == type }
        }

    /**
     * "음식" 탭 필터 칩으로 내놓을 요리 종류. [PlaceCategory.OTHER]는 "분류를 모른다"는 뜻이라
     * 칩으로 내지 않는다(부산맛집정보에서 온 음식점은 분류 코드 자체가 없다) — 대신 아무 칩도
     * 고르지 않은 기본 상태에서는 그 장소들도 전부 보인다.
     */
    val foodPlaceCategoryOptions: List<PlaceCategory>
        get() = PlaceCategory.entries.filter { category ->
            category != PlaceCategory.OTHER &&
                allPlaces.any { it.type == PlaceType.RESTAURANT && it.category == category }
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
