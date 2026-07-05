package com.mediinbusan.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** F-002 언어 선택, F-003 의료 목적 선택. */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState

    fun onLanguageSelected(languageCode: String) {
        _uiState.update { it.copy(selectedLanguage = languageCode) }
    }

    fun onPurposeSelected(purpose: String) {
        _uiState.update { it.copy(selectedPurpose = purpose) }
    }

    fun onComplete(onFinished: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            userPreferencesRepository.setLanguageCode(state.selectedLanguage)
            userPreferencesRepository.setMedicalPurpose(state.selectedPurpose)
            userPreferencesRepository.setOnboardingComplete(true)
            onFinished()
        }
    }
}
