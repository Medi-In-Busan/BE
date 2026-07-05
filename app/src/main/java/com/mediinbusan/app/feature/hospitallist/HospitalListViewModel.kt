package com.mediinbusan.app.feature.hospitallist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.hospital.HospitalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** F-004 목록 조회, F-005 검색/필터. */
@HiltViewModel
class HospitalListViewModel @Inject constructor(
    private val hospitalRepository: HospitalRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HospitalListUiState())
    val uiState: StateFlow<HospitalListUiState> = _uiState

    fun load(medicalPurpose: String?) {
        viewModelScope.launch {
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            hospitalRepository.getHospitals(medicalPurpose, languageCode).collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Result.Loading -> state.copy(isLoading = true, errorMessage = null)
                        is Result.Success -> state.copy(isLoading = false, hospitals = result.data, errorMessage = null)
                        is Result.Error -> state.copy(isLoading = false, errorMessage = result.message ?: "오류가 발생했습니다.")
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
