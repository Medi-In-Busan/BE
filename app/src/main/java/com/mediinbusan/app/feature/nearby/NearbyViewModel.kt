package com.mediinbusan.app.feature.nearby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.domain.nearby.GetNearbyPlacesSortedByDistanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** F-011 병원 주변 관광·웰니스 추천. */
@HiltViewModel
class NearbyViewModel @Inject constructor(
    private val getNearbyPlacesSortedByDistance: GetNearbyPlacesSortedByDistanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NearbyUiState())
    val uiState: StateFlow<NearbyUiState> = _uiState

    fun load(hospitalId: String) {
        viewModelScope.launch {
            getNearbyPlacesSortedByDistance(hospitalId).collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Result.Loading -> state.copy(isLoading = true, errorMessage = null)
                        is Result.Success -> state.copy(isLoading = false, places = result.data, errorMessage = null)
                        is Result.Error -> state.copy(isLoading = false, errorMessage = result.message ?: "오류가 발생했습니다.")
                    }
                }
            }
        }
    }
}
