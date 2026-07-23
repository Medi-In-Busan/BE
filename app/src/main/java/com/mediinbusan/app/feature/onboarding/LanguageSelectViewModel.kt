package com.mediinbusan.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * F-002 언어 선택. F-003 의료 목적 선택은 별도 진단(SelfDiagnosis) 플로우로 분리될 예정이라
 * 이 화면은 언어 선택만 다루고, 완료 시 바로 onboardingComplete를 true로 저장한다.
 */
@HiltViewModel
class LanguageSelectViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageSelectUiState())
    val uiState: StateFlow<LanguageSelectUiState> = _uiState

    init {
        viewModelScope.launch {
            val currentCode = userPreferencesRepository.userPreferences.first().languageCode
            _uiState.update { it.copy(selectedCode = currentCode) }
        }
    }

    fun onLanguageSelected(code: String) {
        _uiState.update { it.copy(selectedCode = code) }
    }

    fun onNextClicked(onFinished: () -> Unit) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguageCode(_uiState.value.selectedCode)
            userPreferencesRepository.setOnboardingComplete(true)
            onFinished()
        }
    }
}
