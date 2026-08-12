package com.mediinbusan.app.feature.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.core.i18n.appStringsFor
import com.mediinbusan.app.data.guide.GuideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** F-008 의료 이용 절차 가이드. */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GuideViewModel @Inject constructor(
    private val guideRepository: GuideRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuideUiState())
    val uiState: StateFlow<GuideUiState> = _uiState

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences
                .map { it.languageCode }
                .distinctUntilChanged()
                // 언어가 바뀔 때마다 STEP 목록을 다시 불러와야 카드 제목/요약이 즉시 갱신된다.
                .flatMapLatest { languageCode -> guideRepository.getGuideSteps(languageCode) }
                .collect { result ->
                    _uiState.update { state ->
                        when (result) {
                            is Result.Loading -> state.copy(isLoading = true, errorMessage = null)
                            is Result.Success -> state.copy(isLoading = false, steps = result.data, errorMessage = null)
                            is Result.Error -> state.copy(
                                isLoading = false,
                                errorMessage = result.message ?: appStringsFor(state.languageCode).guide.loadErrorFallback
                            )
                        }
                    }
                }
        }
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { preferences ->
                _uiState.update { it.copy(languageCode = preferences.languageCode) }
            }
        }
    }

    fun onLanguageSelected(languageCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguageCode(languageCode)
        }
    }
}
