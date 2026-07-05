package com.mediinbusan.app.feature.splash

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data object NavigateToOnboarding : SplashUiState
    data object NavigateToHome : SplashUiState
}
