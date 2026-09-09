package com.mediinbusan.app.feature.permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 앱 접근권한 사전 고지 화면의 유일한 상태 변경 — "확인했습니다"를 눌렀다는 사실을 저장한다.
 * 이 값이 true면 다음 실행부터 스플래시가 곧바로 Home으로 보낸다(SplashViewModel).
 */
@HiltViewModel
class AppPermissionNoticeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    fun onAcknowledged() {
        viewModelScope.launch {
            userPreferencesRepository.setPermissionNoticeAcknowledged(true)
        }
    }
}
