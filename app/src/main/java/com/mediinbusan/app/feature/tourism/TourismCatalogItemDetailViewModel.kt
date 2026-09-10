package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.PendingTourismCatalogItem
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.place.PlaceRepository
import com.mediinbusan.app.data.place.PlaceType
import com.mediinbusan.app.data.place.toPlaceType
import com.mediinbusan.app.data.recent.RecentItemType
import com.mediinbusan.app.data.recent.RecentRepository
import com.mediinbusan.app.data.tourism.TourismCatalogRepository
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 목록(TourismCatalogScreen)에서 PendingTourismCatalogItem에 심어둔 선택 항목을 진입 시 1회
 * consume()한다 — 프로세스 재생성이나 상세 라우트 직접 진입 등으로 비어 있으면 화면이 즉시 뒤로가기
 * 처리한다(PendingTourismCatalogItem 주석 참고). "최근 본 항목"에서 재진입한 경우엔 대신
 * loadFromRecent()가 저장된 스냅샷을 보여주고, 구·군 컨텍스트가 있으면 findMatchingPlace로 최신
 * 데이터 재조회를 시도한다(F-016).
 */
@HiltViewModel
class TourismCatalogItemDetailViewModel @Inject constructor(
    pendingTourismCatalogItem: PendingTourismCatalogItem,
    private val repository: TourismCatalogRepository,
    private val recentRepository: RecentRepository,
    private val placeRepository: PlaceRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TourismCatalogItemDetailUiState())
    val uiState: StateFlow<TourismCatalogItemDetailUiState> = _uiState
    private val selection = pendingTourismCatalogItem.consume()
    // 혼잡도(CROWDING) 항목만 관광공사 상세를 이름으로 다시 찾아 붙인다 — 혼잡도 응답엔 관광지
    // 이름과 지수밖에 없어서 그대로 그리면 사진·주소·소개가 전부 빈 상세가 된다. 재조회에 쓰는
    // 구·군은 어느 진입점에서 왔든(핫플레이스 카드 / 혼잡도 리스트) PendingTourismCatalogItem이
    // 한 규칙으로 확정해 실어 보낸다 — 그래서 여기서는 카테고리만 보면 된다.
    private val crowdingDistrict = selection
        ?.takeIf { it.category == TourismCatalogCategory.CROWDING }
        ?.district
    private var loadJob: Job? = null
    private var nearbyJob: Job? = null

    init {
        if (selection != null) {
            _uiState.value = TourismCatalogItemDetailUiState(
                category = selection.category,
                item = if (crowdingDistrict == null) selection.item else null,
                selectedTitle = selection.item.title,
                consumed = true,
                isLoading = crowdingDistrict != null
            )
            if (crowdingDistrict != null) {
                retry()
            } else {
                recordView(selection.item, selection.category, selection.district)
                loadNearbySameType(selection.item)
            }
        } else {
            // 보여줄 게 아무것도 없는 진입(프로세스 재생성으로 PendingTourismCatalogItem이 비었거나
            // 상세 라우트로 직접 들어온 경우) — 화면의 "consumed && selectedTitle == null" 판정이
            // 뒤로가기를 처리한다. 예전엔 여기서 상태를 그대로 둬서 consumed가 false로 남았고, 그
            // 조건이 성립하지 않아 뒤로가기도 안 되고 아무것도 안 그려진 빈 화면만 남았다.
            //
            // 최근 본 항목으로 들어온 경우엔 화면이 곧바로 loadFromRecent()로 이 상태를 덮어쓰므로
            // 여기서 뒤로가기 신호를 켜도 안전하다 — 다만 화면이 그 덮어쓰기를 놓치지 않도록
            // 판정 시점에 상태를 다시 읽어야 한다(TourismCatalogItemDetailScreen의 해당 주석 참고).
            _uiState.value = TourismCatalogItemDetailUiState(consumed = true)
        }
    }

    fun retry() {
        val selected = selection ?: return
        val original = selected.item
        val district = crowdingDistrict ?: return
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = true, matchNotFound = false, loadFailed = false) }
        loadJob = viewModelScope.launch {
            when (val result = repository.findMatchingPlace(original.title, district)) {
                is Result.Success -> {
                    val matched = result.data
                    val mergedItem = matched?.copy(details = matched.details + original.details.filterKeys { key ->
                        key in setOf("congestionRate", "signguNm", "baseYmd", "baseYm")
                    })
                    // 매칭에 실패해도 화면을 비우지 않는다. 핫플레이스 목록의 1위(감만시장 등)처럼
                    // 한국관광공사 관광지 DB에 아예 없는 곳이 실제로 있는데(전통시장·부두 등 빅데이터
                    // 방문지 통계에만 잡히는 장소), 예전엔 이런 항목을 누르면 "찾지 못했습니다" 한 줄만
                    // 남고 목록에서 이미 본 이름·혼잡도조차 못 봤다. 목록에서 넘어온 원본 항목을 그대로
                    // 그리고, 화면에는 관광공사 상세가 붙지 않았다는 안내(matchNotFound)를 같이 띄운다.
                    //
                    // 소개문 폴백이 subtitle을 먼저 집는데 혼잡도 항목의 subtitle은 지수 숫자("83.5")라,
                    // 그대로 두면 소개 카드에 숫자만 덩그러니 나온다 — 폴백 항목에서는 지운다(지수는
                    // 아래 혼잡도 카드가 congestionRate로 제대로 보여준다).
                    val category = if (matched == null) selected.category else TourismCatalogCategory.PLACES_KO
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            matchNotFound = matched == null,
                            category = category,
                            item = mergedItem ?: original.copy(subtitle = null)
                        )
                    }
                    if (mergedItem != null) {
                        recordView(mergedItem, category, district)
                        loadNearbySameType(mergedItem)
                    }
                }
                is Result.Error, Result.Loading -> _uiState.update { it.copy(isLoading = false, loadFailed = true) }
            }
        }
    }

    /** RecentlyViewedScreen에서 TOURISM_ITEM을 다시 눌렀을 때 진입하는 경로. */
    fun loadFromRecent(itemId: String) {
        loadJob?.cancel()
        // consumed는 아직 true로 두지 않는다 — Screen의 "consumed && selectedTitle == null -> 뒤로가기"
        // 처리가 findById 결과를 받기도 전에(이 비동기 조회 중에) 곧장 발동해서 상세 화면이 열리자마자
        // 닫혀버리는 버그가 있었다. 조회가 끝나 결과가 확정된 뒤에만 consumed = true로 바꾼다.
        _uiState.value = TourismCatalogItemDetailUiState(isLoading = true)
        loadJob = viewModelScope.launch {
            val recent = recentRepository.findById(itemId)
            if (recent == null) {
                _uiState.value = TourismCatalogItemDetailUiState(consumed = true) // selectedTitle == null -> 화면이 뒤로가기 처리
                return@launch
            }
            val category = recent.tourismCategory
                ?.let { name -> TourismCatalogCategory.entries.find { it.name == name } }
            val district = recent.tourismDistrict
                ?.let { name -> BusanDistrict.entries.find { it.name == name } }
            val snapshot = TourismCatalogItem(
                id = recent.itemId,
                title = recent.itemName,
                subtitle = recent.subtitle.ifBlank { null },
                address = recent.address.ifBlank { null },
                imageUrl = recent.imageUrl,
                latitude = recent.latitude,
                longitude = recent.longitude,
                categoryCode = null,
                details = emptyMap()
            )
            // 우선 스냅샷을 바로 보여주고, 구·군 컨텍스트가 있으면 그 위에 최신 데이터로 교체를 시도한다.
            _uiState.value = TourismCatalogItemDetailUiState(
                category = category,
                item = snapshot,
                selectedTitle = snapshot.title,
                consumed = true,
                isLoading = district != null,
                isSnapshot = true
            )
            // 스냅샷 좌표로 먼저 걸어둔다 — 아래 재조회가 성공하면 최신 좌표로 다시 건다.
            loadNearbySameType(snapshot)
            if (district == null) return@launch

            when (val result = repository.findMatchingPlace(snapshot.title, district)) {
                is Result.Success -> {
                    val matched = result.data
                    if (matched != null) {
                        _uiState.update {
                            it.copy(isLoading = false, item = matched, category = TourismCatalogCategory.PLACES_KO, isSnapshot = false)
                        }
                        recordView(matched, TourismCatalogCategory.PLACES_KO, district)
                        loadNearbySameType(matched)
                    } else {
                        _uiState.update { it.copy(isLoading = false) } // 스냅샷 유지, isSnapshot = true
                    }
                }
                is Result.Error, Result.Loading -> _uiState.update { it.copy(isLoading = false) } // 스냅샷 유지
            }
        }
    }

    /**
     * 같은 종류(관광지/맛집 등)의 주변 장소를 이 항목 좌표 기준으로 불러온다. 장소
     * 상세(feature/nearby/PlaceDetailViewModel.loadNearbySameType)와 같은 규칙이다 — 기기 GPS는
     * 쓰지 않고 좌표는 방금 받은 항목의 것이며(CLAUDE.md §1), 백엔드 /api/wellness/places에 종류
     * 파라미터가 없어서 반경 안을 받아온 뒤 앱에서 같은 PlaceType만 남긴다.
     *
     * 카탈로그 항목(TourAPI)과 주변 장소(웰니스 DB)는 출처가 달라 id가 겹치지 않는다 — 지금 보고
     * 있는 곳이 결과에 섞여도 id로는 못 거르므로 이름으로도 한 번 더 뺀다.
     *
     * 실패하면 조용히 빈 목록으로 둔다. 곁들이는 추천이라 섹션만 사라지는 게 맞다.
     */
    private fun loadNearbySameType(item: TourismCatalogItem) {
        val latitude = item.latitude
        val longitude = item.longitude
        // 좌표가 없으면 "주변"을 정의할 수 없다(관광사진·혼잡도처럼 장소가 아닌 항목이 여기 걸린다).
        if (latitude == null || longitude == null) return
        val placeType = item.categoryCode.toPlaceType()
        // "기타"는 종류가 아니라 분류 실패에 가깝다 — 같은 기타끼리 묶어봐야 서로 상관없는 곳들이다.
        if (placeType == PlaceType.OTHER) return

        nearbyJob?.cancel()
        nearbyJob = viewModelScope.launch {
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            placeRepository.getPlacesNear(
                latitude = latitude,
                longitude = longitude,
                radiusMeters = NEARBY_RADIUS_METERS,
                languageCode = languageCode
            ).collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Result.Success -> state.copy(
                            nearbySamePlaces = result.data
                                .filter { it.type == placeType && it.name != item.title }
                                .take(NEARBY_MAX_COUNT)
                        )
                        is Result.Error -> state.copy(nearbySamePlaces = emptyList())
                        is Result.Loading -> state
                    }
                }
            }
        }
    }

    private fun recordView(item: TourismCatalogItem, category: TourismCatalogCategory?, district: BusanDistrict?) {
        viewModelScope.launch {
            recentRepository.recordView(
                itemId = item.id,
                itemName = item.title,
                itemType = RecentItemType.TOURISM_ITEM,
                imageUrl = item.imageUrl,
                subtitle = item.subtitle ?: "",
                address = item.address ?: "",
                latitude = item.latitude,
                longitude = item.longitude,
                tourismCategory = category?.name,
                tourismDistrict = district?.name
            )
        }
    }

    companion object {
        /** 장소 상세(PlaceDetailViewModel)와 같은 반경 — 같은 곳을 어느 화면으로 들어가든 같은 목록을 본다. */
        private const val NEARBY_RADIUS_METERS = 2000.0

        /** 가로 스크롤 한 줄에 담기는 만큼만. 목록 화면이 아니라 곁들이는 추천이다. */
        private const val NEARBY_MAX_COUNT = 10
    }
}
