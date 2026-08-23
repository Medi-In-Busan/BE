package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import com.mediinbusan.app.core.common.PendingTourismCatalogItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * 목록(TourismCatalogScreen)에서 PendingTourismCatalogItem에 심어둔 선택 항목을 진입 시 1회
 * consume()한다 — 프로세스 재생성이나 상세 라우트 직접 진입 등으로 비어 있으면 화면이 즉시 뒤로가기
 * 처리한다(PendingTourismCatalogItem 주석 참고).
 */
@HiltViewModel
class TourismCatalogItemDetailViewModel @Inject constructor(
    pendingTourismCatalogItem: PendingTourismCatalogItem
) : ViewModel() {
    private val _uiState = MutableStateFlow(TourismCatalogItemDetailUiState())
    val uiState: StateFlow<TourismCatalogItemDetailUiState> = _uiState

    init {
        val consumed = pendingTourismCatalogItem.consume()
        _uiState.value = TourismCatalogItemDetailUiState(
            category = consumed?.first,
            item = consumed?.second,
            consumed = true
        )
    }
}
