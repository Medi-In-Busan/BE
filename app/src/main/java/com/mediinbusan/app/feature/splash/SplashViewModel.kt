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

/**
 * F-001: 최초 실행 여부를 확인해 언어 선택/홈 중 하나로 보낸다. 준비 유형 진단(챗봇)은 더 이상
 * 이 최초 실행 흐름에 강제로 끼지 않고, Home의 "AI 진단하기" 진입점을 통해서만 접근한다.
 */
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
            val onboardingComplete = preferences.await().onboardingComplete
            _uiState.value = if (!onboardingComplete) {
                SplashUiState.NavigateToOnboarding
            } else {
                SplashUiState.NavigateToHome
            }
        }
    }

    private companion object {
        const val MINIMUM_SPLASH_DURATION_MS = 3500L
    }
}
