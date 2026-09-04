package com.mediinbusan.app.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** F-001: 최소 노출 시간만큼 대기한 뒤 Home으로 보낸다. */
@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState

    init {
        viewModelScope.launch {
            delay(MINIMUM_SPLASH_DURATION_MS)
            _uiState.value = SplashUiState.NavigateToHome
        }
    }

    private companion object {
        const val MINIMUM_SPLASH_DURATION_MS = 3500L
    }
}
