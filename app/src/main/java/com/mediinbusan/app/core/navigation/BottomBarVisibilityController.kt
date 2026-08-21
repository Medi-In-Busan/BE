package com.mediinbusan.app.core.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 지도(S-08)에서 마커/카드를 선택하면 하단 탭바 자리를 선택 카드가 대신 차지하는 UX를 위한
 * 공유 신호. feature 패키지끼리는 서로를 직접 import하지 않는다는 규칙(CLAUDE.md §4) 때문에
 * feature/map의 MapViewModel과 core/navigation의 MediInBusanApp이 직접 참조를 주고받을 수
 * 없어, 이 core/navigation 싱글턴을 통해서만 신호를 주고받는다.
 *
 * 의존성이 전혀 없는 신호 하나만 들고 있어 Hilt로 관리할 이유가 없다 — 순수 Kotlin object로
 * 앱 전체에서 인스턴스가 하나뿐임을 보장한다.
 */
object BottomBarVisibilityController {
    private val _mapSelectionActive = MutableStateFlow(false)

    /** true면 지도에서 마커/카드가 선택된 상태 — 하단 탭바를 숨기고 선택 카드가 그 자리를 채운다. */
    val mapSelectionActive: StateFlow<Boolean> = _mapSelectionActive.asStateFlow()

    fun setMapSelectionActive(active: Boolean) {
        _mapSelectionActive.value = active
    }
}
