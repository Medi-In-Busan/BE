package com.mediinbusan.app.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.core.navigation.BottomBarVisibilityController
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.hospital.HospitalRepository
import com.mediinbusan.app.data.place.PlaceRepository
import com.mediinbusan.app.domain.course.AssembleWellnessCourseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * F-010,F-013: 병원 및 주변 장소의 공개 좌표만 지도에 표시한다.
 * 사용자 현재 위치는 조회하거나 외부로 전송하지 않는다(구조적 제약) — "내 위치로 이동" 류의
 * 버튼이 있더라도 고정된 부산 기본 좌표로만 이동한다(core/ui/KakaoMapView.kt의 BusanDefaultCenter 참고).
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val hospitalRepository: HospitalRepository,
    private val placeRepository: PlaceRepository,
    private val favoriteRepository: FavoriteRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val assembleWellnessCourse: AssembleWellnessCourseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    init {
        viewModelScope.launch {
            favoriteRepository.observeFavorites().collect { favorites ->
                val hospitalIds = favorites
                    .filter { it.itemType == FavoriteItemType.HOSPITAL }
                    .map { it.itemId }
                    .toSet()
                val placeIds = favorites
                    .filter { it.itemType == FavoriteItemType.PLACE }
                    .map { it.itemId }
                    .toSet()
                _uiState.update { it.copy(favoriteHospitalIds = hospitalIds, favoritePlaceIds = placeIds) }
            }
        }
        // selectedMarkerId를 건드리는 지점(onMarkerSelected/onCategorySelected/searchThisArea/
        // loadHospitalFocused)마다 컨트롤러를 따로 호출하는 대신, 상태 하나를 계속 관찰해서 신호를
        // 보낸다 — 어느 경로로 선택이 바뀌든(카테고리 전환으로 선택이 풀리는 경우 포함) 놓치지 않는다.
        viewModelScope.launch {
            _uiState.map { it.selectedMarkerId != null }.distinctUntilChanged().collect { active ->
                BottomBarVisibilityController.setMapSelectionActive(active)
            }
        }
    }

    // 이 화면을 완전히 떠날 때(뒤로가기 등으로 NavBackStackEntry가 정리될 때) 선택 상태가 하단
    // 탭바를 계속 숨긴 채로 남아있지 않게 확실히 되돌린다.
    override fun onCleared() {
        super.onCleared()
        BottomBarVisibilityController.setMapSelectionActive(false)
    }

    /**
     * 이미 같은 대상·같은 언어로 불러와 둔 상태를 기억한다. 화면에 다시 들어올 때마다 처음부터
     * 다시 받지 않기 위한 것 — 아래 [load] 주석 참고. 조회에 실패하면 null로 되돌려 재시도가
     * 막히지 않게 한다.
     */
    private var loadedKey: LoadKey? = null

    private data class LoadKey(val hospitalId: String?, val courseId: String?, val languageCode: String)

    /**
     * 화면 진입 시 호출된다. **같은 대상을 같은 언어로 이미 불러왔으면 아무것도 하지 않는다.**
     *
     * 예전엔 재진입마다 무조건 다시 받았는데, 그 사이 isLoading=true가 되면서 MapScreen이 잠깐
     * LoadingState로 바뀌고 BrowseMap이 통째로 사라졌다 — 목록에서 항목을 골라 상세로 갔다가
     * 뒤로 돌아올 때마다 스피너가 한 번 번쩍이고, 그때 화면 상태(펼침 여부·스크롤 위치)가 전부
     * 초기화돼 지도만 남았다. 즐겨찾기는 init의 observeFavorites가 계속 흘려주므로 이 조회에
     * 기대지 않고, 언어가 바뀌면 키가 달라져 정상적으로 다시 받는다.
     */
    fun load(hospitalId: String?, courseId: String? = null) {
        viewModelScope.launch {
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            val key = LoadKey(hospitalId, courseId, languageCode)
            if (loadedKey == key) return@launch
            loadedKey = key
            when {
                hospitalId != null && courseId != null -> loadCourseRoute(hospitalId, courseId, languageCode)
                hospitalId != null -> loadHospitalFocused(hospitalId, languageCode)
                else -> loadAllHospitals(languageCode)
            }
            // 실패한 조회는 캐시하지 않는다 — ErrorState의 "다시 시도"가 그대로 다시 돌아야 한다.
            if (_uiState.value.errorMessage != null) {
                loadedKey = null
            }
        }
    }

    /** 하단 시트의 펼침/접힘. 화면이 아니라 여기 두는 이유는 MapUiState.isListExpanded 주석 참고. */
    fun onListExpandedChange(expanded: Boolean) {
        _uiState.update { it.copy(isListExpanded = expanded) }
    }

    // F-014 웰니스 코스 동선: 코스를 구성하는 병원+장소들을 방문 순서 그대로 지도에 그리기 위한 로드 경로.
    // AssembleWellnessCourseUseCase는 feature/nearby의 WellnessCourseCard가 이미 쓰는 것과 동일한
    // 유스케이스 — 여기서는 그 결과 중 courseId가 일치하는 코스 하나만 골라 activeCourse에 담는다.
    private suspend fun loadCourseRoute(hospitalId: String, courseId: String, languageCode: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val hospitalResult = hospitalRepository.getHospitalDetail(hospitalId, languageCode)
            .first { it !is Result.Loading }
        val coursesResult = assembleWellnessCourse(hospitalId, languageCode)
            .first { it !is Result.Loading }

        val hospital = (hospitalResult as? Result.Success)?.data
        val course = (coursesResult as? Result.Success)?.data?.firstOrNull { it.id == courseId }

        _uiState.update {
            it.copy(
                isLoading = false,
                focusedHospital = hospital,
                activeCourse = course,
                selectedMarkerId = hospital?.id,
                errorMessage = if (hospital == null || course == null) "코스 정보를 불러올 수 없습니다." else null
            )
        }
    }

    private suspend fun loadHospitalFocused(hospitalId: String, languageCode: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val hospitalResult = hospitalRepository.getHospitalDetail(hospitalId, languageCode)
            .first { it !is Result.Loading }
        val placesResult = placeRepository.getNearbyPlaces(hospitalId, languageCode)
            .first { it !is Result.Loading }

        val hospital = (hospitalResult as? Result.Success)?.data
        val places = (placesResult as? Result.Success)?.data.orEmpty()

        _uiState.update {
            it.copy(
                isLoading = false,
                focusedHospital = hospital,
                nearbyPlaces = places,
                selectedMarkerId = hospital?.id,
                errorMessage = if (hospital == null) "병원 위치 정보를 불러올 수 없습니다." else null
            )
        }
    }

    private suspend fun loadAllHospitals(languageCode: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val hospitalsResult = hospitalRepository.getHospitals(languageCode = languageCode)
            .first { it !is Result.Loading }
        val hospitals = (hospitalsResult as? Result.Success)?.data.orEmpty()

        // 예전엔 병원 목록 맨 앞 병원 하나의 반경 3km(getNearbyPlaces)만 빌려써서, 그 반경 밖
        // 장소(다른 구에 upsert된 부산맛집 데이터 등)가 지도에 아예 안 떴다 — 병원 비종속 전체
        // 조회(getAllPlaces, GET /api/wellness/places)로 바꿔 부산 전역 장소가 다 보이게 한다.
        val placesResult = placeRepository.getAllPlaces(languageCode).first { it !is Result.Loading }
        val places = (placesResult as? Result.Success)?.data.orEmpty()

        _uiState.update {
            it.copy(
                isLoading = false,
                allHospitals = hospitals,
                allPlaces = places,
                errorMessage = if (hospitalsResult is Result.Error) "병원 목록을 불러올 수 없습니다." else null
            )
        }
    }

    /**
     * "이 위치에서 검색" 버튼용. latitude/longitude는 지도 카메라 중심(core/ui/KakaoMapView.kt의
     * searchAreaRequestId 트리거) — 사용자가 지도를 움직여서 만든 좌표이지 기기 GPS가 아니다.
     */
    fun searchThisArea(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            // 장소(관광/음식)는 좌표 기반 서버 조회가 없어 이 좌표를 필터 기준으로만 쓴다
            // (MapUiState.areaCenter 참고) — 병원은 아래에서 실제로 서버를 다시 조회한다.
            _uiState.update { it.copy(isSearchingArea = true, errorMessage = null, areaCenter = MapPoint(latitude, longitude)) }
            val result = hospitalRepository.getNearbyHospitals(latitude = latitude, longitude = longitude)
                .first { it !is Result.Loading }
            val hospitals = (result as? Result.Success)?.data.orEmpty()

            _uiState.update {
                it.copy(
                    isSearchingArea = false,
                    allHospitals = hospitals,
                    selectedMarkerId = null,
                    markersActivated = true,
                    errorMessage = if (result is Result.Error) "이 위치 주변 병원을 불러오지 못했습니다." else null
                )
            }
        }
    }

    /**
     * 카테고리 탭은 토글이다 — 이미 켜져 있는 탭을 다시 누르면 마커를 전부 감추고(markersActivated
     * = false) 선택도 푼다. 화면에서는 하단 목록 시트도 같이 내려가 지도만 남는다.
     */
    fun onCategorySelected(category: MapCategory) {
        // areaCenter는 장소 목록만 걸러낸다 — 병원은 searchThisArea가 allHospitals 자체를 그 지점
        // 주변 결과로 갈아끼운다. 그래서 areaCenter만 풀면 장소는 부산 전역으로 넓어지는데 병원은
        // 이전 영역 결과에 묶인 채로 남아, 두 목록이 서로 다른 범위를 보여주면서 그 사실이 화면
        // 어디에도 표시되지 않았다. 좁혀져 있었다면 병원 전체 목록도 같이 다시 받는다.
        val wasAreaNarrowed = _uiState.value.areaCenter != null
        _uiState.update {
            if (it.markersActivated && it.selectedCategory == category) {
                it.copy(markersActivated = false, selectedMarkerId = null, areaCenter = null)
            } else {
                // 탭을 바꾸면 "이 위치에서 검색"으로 좁혀둔 범위는 푼다 — 새 카테고리를 고른 건
                // 다시 전체에서 보겠다는 뜻에 가깝다.
                it.copy(selectedCategory = category, markersActivated = true, selectedMarkerId = null, areaCenter = null)
            }
        }
        if (wasAreaNarrowed) reloadAllHospitals()
    }

    /**
     * "이 위치에서 검색"으로 좁혀둔 병원 목록을 부산 전역으로 되돌린다.
     *
     * [loadAllHospitals]와 달리 장소는 다시 받지 않고 isLoading도 건드리지 않는다 — 이미 화면에
     * 떠 있는 목록을 조용히 넓히는 자리라, 스피너로 화면을 한 번 비우면 그게 더 어색하다.
     */
    private fun reloadAllHospitals() {
        viewModelScope.launch {
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            val result = hospitalRepository.getHospitals(languageCode = languageCode)
                .first { it !is Result.Loading }
            val hospitals = (result as? Result.Success)?.data ?: return@launch
            _uiState.update { it.copy(allHospitals = hospitals) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    // HospitalSearchListViewModel의 onFilterToggled와 달리 서버에 다시 요청하지 않는다 — 지도는
    // 이미 받아둔 allHospitals를 MapUiState.visibleHospitals에서 클라이언트 필터링만 한다.
    fun onSpecialtyFilterToggled(specialty: String) {
        _uiState.update { state ->
            val updated = if (specialty in state.selectedSpecialties) {
                state.selectedSpecialties - specialty
            } else {
                state.selectedSpecialties + specialty
            }
            state.copy(selectedSpecialties = updated)
        }
    }

    fun onSpecialtyFiltersCleared() {
        _uiState.update { it.copy(selectedSpecialties = emptySet()) }
    }

    // 이미 받아둔 allPlaces에서 MapUiState.visiblePlaces가 Place.isTranslated로 클라이언트 필터링만
    // 한다 — 진료과목 필터와 같은 패턴, 서버 재요청 없음.
    fun onLanguageFilterToggled() {
        _uiState.update { it.copy(languageFilterEnabled = !it.languageFilterEnabled) }
    }

    fun onMarkerSelected(hospitalId: String?) {
        _uiState.update { it.copy(selectedMarkerId = hospitalId) }
    }

    fun onToggleFavorite(hospitalId: String) {
        val hospital: Hospital = _uiState.value.allHospitals.firstOrNull { it.id == hospitalId }
            ?: _uiState.value.focusedHospital?.takeIf { it.id == hospitalId }
            ?: return
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(
                Favorite(
                    itemId = hospital.id,
                    itemType = FavoriteItemType.HOSPITAL,
                    name = hospital.name,
                    imageUrl = hospital.imageUrl,
                    savedAt = System.currentTimeMillis(),
                    subtitle = hospital.specialties.joinToString(", "),
                    address = hospital.address,
                    latitude = hospital.latitude,
                    longitude = hospital.longitude
                )
            )
        }
    }

    /** 장소도 상세화면과 즐겨찾기 화면에서 이미 즐겨찾기 대상이다 — 지도 선택 카드에서도 같이 쓴다. */
    fun onTogglePlaceFavorite(placeId: String) {
        val place = _uiState.value.allPlaces.firstOrNull { it.id == placeId }
            ?: _uiState.value.nearbyPlaces.firstOrNull { it.id == placeId }
            ?: return
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(
                Favorite(
                    itemId = place.id,
                    itemType = FavoriteItemType.PLACE,
                    name = place.name,
                    imageUrl = place.imageUrl,
                    savedAt = System.currentTimeMillis(),
                    subtitle = place.type.name,
                    address = place.address,
                    latitude = place.latitude,
                    longitude = place.longitude
                )
            )
        }
    }
}
