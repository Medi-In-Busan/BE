package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.PendingTourismCatalogItem
import com.mediinbusan.app.core.common.Result
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
    private val recentRepository: RecentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TourismCatalogItemDetailUiState())
    val uiState: StateFlow<TourismCatalogItemDetailUiState> = _uiState
    private val selection = pendingTourismCatalogItem.consume()
    private val hotPlaceDistrict = selection?.item?.details?.get("hotPlaceDistrict")
        ?.let { name -> BusanDistrict.entries.find { it.name == name } }
    private var loadJob: Job? = null

    init {
        if (selection != null) {
            _uiState.value = TourismCatalogItemDetailUiState(
                category = selection.category,
                item = if (hotPlaceDistrict == null) selection.item else null,
                selectedTitle = selection.item.title,
                consumed = true,
                isLoading = hotPlaceDistrict != null
            )
            if (hotPlaceDistrict != null) {
                retry()
            } else {
                recordView(selection.item, selection.category, selection.district)
            }
        }
    }

    fun retry() {
        val original = selection?.item ?: return
        val district = hotPlaceDistrict ?: return
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = true, matchNotFound = false, loadFailed = false) }
        loadJob = viewModelScope.launch {
            when (val result = repository.findMatchingPlace(original.title, district)) {
                is Result.Success -> {
                    val matched = result.data
                    val mergedItem = matched?.copy(details = matched.details + original.details.filterKeys { key ->
                        key in setOf("congestionRate", "signguNm", "baseYmd", "baseYm")
                    })
                    val category = if (matched == null) null else TourismCatalogCategory.PLACES_KO
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            matchNotFound = matched == null,
                            category = category,
                            item = mergedItem
                        )
                    }
                    if (mergedItem != null) recordView(mergedItem, category, district)
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
            if (district == null) return@launch

            when (val result = repository.findMatchingPlace(snapshot.title, district)) {
                is Result.Success -> {
                    val matched = result.data
                    if (matched != null) {
                        _uiState.update {
                            it.copy(isLoading = false, item = matched, category = TourismCatalogCategory.PLACES_KO, isSnapshot = false)
                        }
                        recordView(matched, TourismCatalogCategory.PLACES_KO, district)
                    } else {
                        _uiState.update { it.copy(isLoading = false) } // 스냅샷 유지, isSnapshot = true
                    }
                }
                is Result.Error, Result.Loading -> _uiState.update { it.copy(isLoading = false) } // 스냅샷 유지
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
}
