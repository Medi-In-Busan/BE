package com.mediinbusan.app.feature.splash

sealed interface SplashUiState {
    data object Loading : SplashUiState

    /** 최초 실행 — 앱 접근권한 사전 고지를 먼저 보여준다(feature/permission). */
    data object NavigateToPermissionNotice : SplashUiState

    data object NavigateToHome : SplashUiState
}
