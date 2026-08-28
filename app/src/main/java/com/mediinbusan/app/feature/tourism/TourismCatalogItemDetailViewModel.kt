package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.PendingTourismCatalogItem
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.data.tourism.TourismCatalogRepository
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
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
 * 처리한다(PendingTourismCatalogItem 주석 참고).
 */
@HiltViewModel
class TourismCatalogItemDetailViewModel @Inject constructor(
    pendingTourismCatalogItem: PendingTourismCatalogItem,
    private val repository: TourismCatalogRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TourismCatalogItemDetailUiState())
    val uiState: StateFlow<TourismCatalogItemDetailUiState> = _uiState
    private val selection = pendingTourismCatalogItem.consume()
    private val hotPlaceDistrict = selection?.second?.details?.get("hotPlaceDistrict")
        ?.let { name -> BusanDistrict.entries.find { it.name == name } }
    private var loadJob: Job? = null

    init {
        _uiState.value = TourismCatalogItemDetailUiState(
            category = selection?.first,
            item = if (hotPlaceDistrict == null) selection?.second else null,
            selectedTitle = selection?.second?.title,
            consumed = true,
            isLoading = hotPlaceDistrict != null
        )
        if (hotPlaceDistrict != null) retry()
    }

    fun retry() {
        val original = selection?.second ?: return
        val district = hotPlaceDistrict ?: return
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = true, matchNotFound = false, loadFailed = false) }
        loadJob = viewModelScope.launch {
            when (val result = repository.findMatchingPlace(original.title, district)) {
                is Result.Success -> {
                    val matched = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            matchNotFound = matched == null,
                            category = if (matched == null) null else TourismCatalogCategory.PLACES_KO,
                            item = matched?.copy(details = matched.details + original.details.filterKeys { key ->
                                key in setOf("congestionRate", "signguNm", "baseYmd", "baseYm")
                            })
                        )
                    }
                }
                is Result.Error, Result.Loading -> _uiState.update { it.copy(isLoading = false, loadFailed = true) }
            }
        }
    }
}
