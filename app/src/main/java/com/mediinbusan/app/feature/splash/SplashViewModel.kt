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
 * F-001: 최초 실행 여부를 확인해 언어 선택/준비 유형 진단/홈 중 하나로 보낸다.
 * 순서는 항상 언어 선택 -> 진단 -> 홈이라, 언어 선택만 끝내고 앱을 종료했던 세션이면
 * 진단부터 다시 시작한다(언어 선택은 반복하지 않음).
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
            val (onboardingComplete, diagnosisComplete) = preferences.await()
                .let { it.onboardingComplete to it.diagnosisComplete }
            _uiState.value = when {
                !onboardingComplete -> SplashUiState.NavigateToOnboarding
                !diagnosisComplete -> SplashUiState.NavigateToSelfDiagnosis
                else -> SplashUiState.NavigateToHome
            }
        }
    }

    private companion object {
        const val MINIMUM_SPLASH_DURATION_MS = 3500L
    }
}
