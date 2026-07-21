package com.mediinbusan.app.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/** F-001: 최초 실행 여부/저장된 언어 설정을 확인해 온보딩 또는 홈으로 보낸다. */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState

    init {
        viewModelScope.launch {
            val preferences = async { userPreferencesRepository.userPreferences.first() }
            delay(MINIMUM_SPLASH_DURATION_MS)
            _uiState.value = if (preferences.await().onboardingComplete) {
                SplashUiState.NavigateToHome
            } else {
                SplashUiState.NavigateToOnboarding
            }
        }
    }

    private companion object {
        const val MINIMUM_SPLASH_DURATION_MS = 3500L
    }
}
