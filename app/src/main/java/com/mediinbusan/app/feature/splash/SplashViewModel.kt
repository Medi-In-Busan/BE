package com.mediinbusan.app.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * F-001: 최소 노출 시간만큼 대기한 뒤 다음 화면으로 보낸다.
 *
 * 앱 접근권한 사전 고지를 아직 확인하지 않았다면(최초 실행) Home 대신 고지 화면을 먼저 거친다 —
 * 카메라 권한을 요구하면서 사전 고지가 없다는 원스토어 검증 의견(OA01008717)에 대한 대응이다.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState

    init {
        viewModelScope.launch {
            // DataStore 첫 읽기는 대기 시간 안에 끝나므로 스플래시가 더 길어지지 않는다.
            val acknowledged = userPreferencesRepository.userPreferences.first().permissionNoticeAcknowledged
            delay(MINIMUM_SPLASH_DURATION_MS)
            _uiState.value = if (acknowledged) {
                SplashUiState.NavigateToHome
            } else {
                SplashUiState.NavigateToPermissionNotice
            }
        }
    }

    private companion object {
        const val MINIMUM_SPLASH_DURATION_MS = 3500L
    }
}
