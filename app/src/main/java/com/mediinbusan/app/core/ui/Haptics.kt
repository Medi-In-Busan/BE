package com.mediinbusan.app.core.ui

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/*
 * 앱 공용 촉각 피드백.
 *
 * Compose의 LocalHapticFeedback/HapticFeedbackType 대신 플랫폼 View API를 쓴다. 이 프로젝트의
 * 의존성 그래프에서 androidx.compose.ui는 Compose BOM이 지정한 1.7.8이 아니라 다른 라이브러리가
 * 끌어온 1.10.0으로 해석된다 — CLAUDE.md §6-6의 FlowRow 크래시(컴파일은 새 API로 링크되는데 실제
 * dex엔 없어서 실기기에서 NoSuchMethodError)와 정확히 같은 상황이다. HapticFeedbackType의 풍부한
 * 상수들(Confirm/ToggleOn/SegmentTick 등)은 Compose UI 1.8부터 생긴 것이라 딱 그 함정에 걸린다.
 *
 * 반면 HapticFeedbackConstants는 int 상수라 컴파일 시점에 값이 인라인되고, View.performHapticFeedback는
 * API 1부터 있다 — 런타임에 심볼을 찾는 일 자체가 없어서 버전 혼재의 영향을 받지 않는다.
 *
 * 시스템 설정 존중: performHapticFeedback를 FLAG_IGNORE_GLOBAL_SETTING 없이 부르면 사용자가 기기
 * 설정에서 터치 피드백을 꺼둔 경우 아무 일도 일어나지 않는다. 우리가 따로 확인할 필요가 없다.
 */

/**
 * 확정 신호로 쓸 상수. CONFIRM은 API 30부터라, 그 아래에서는 가장 가까운 감촉인 KEYBOARD_TAP으로
 * 떨어뜨린다(모르는 상수를 그냥 넘기면 진동이 아예 안 울려 minSdk 24~29에서 피드백이 사라진다).
 */
private val ConfirmConstant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    HapticFeedbackConstants.CONFIRM
} else {
    HapticFeedbackConstants.KEYBOARD_TAP
}

/** 현재 컴포지션의 View에 묶인 촉각 피드백 핸들. */
@Composable
fun rememberHaptics(): Haptics {
    val view = LocalView.current
    return remember(view) { Haptics(view) }
}

@Immutable
class Haptics internal constructor(private val view: View) {

    /**
     * 값이 바뀌는 순간의 가벼운 틱 — 필터 칩, 선택지, 즐겨찾기 해제처럼 "바뀌었다"만 알리면 되는 곳.
     */
    fun tick() {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }

    /**
     * "됐다"를 알리는 확정 신호 — 즐겨찾기 추가, 촬영 셔터처럼 결과가 남는 동작.
     * [tick]보다 분명해서 되돌리는 동작과 만드는 동작이 손끝에서 구분된다.
     */
    fun confirm() {
        view.performHapticFeedback(ConfirmConstant)
    }
}
