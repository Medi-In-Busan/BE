package com.mediinbusan.app.core.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Home(S-03)에서 아래로 스크롤하면 하단 탭바가 축소되는 연출을 위한 공유 신호(축소 비율은
 * core/ui/BottomBarScrollShrink.kt의 rememberScrollShrinkAnimation 기본값을 따른다).
 * BottomBarVisibilityController와 같은 이유(CLAUDE.md §4, feature 패키지는 서로를 직접
 * import하지 않는다)로 feature/home과 core/navigation의 MediInBusanApp이 이 싱글턴을 통해서만
 * scale 값을 주고받는다.
 */
object BottomBarScaleController {
    private val _scale = MutableStateFlow(1f)

    /** 1f = 원래 크기, 축소되면 그보다 작은 값. BottomNavBar 호출부가 graphicsLayer로 직접 적용한다. */
    val scale: StateFlow<Float> = _scale.asStateFlow()

    fun setScale(value: Float) {
        _scale.value = value
    }
}
