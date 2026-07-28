package com.mediinbusan.app.feature.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.imageLoader
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** F-018 데이터 출처 및 이용 유의사항, 언어 변경, 캐시 삭제. */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { preferences ->
                _uiState.update { it.copy(selectedLanguage = preferences.languageCode) }
            }
        }
    }

    fun onLanguageSelected(languageCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguageCode(languageCode)
        }
    }

    // Coil3 기본 싱글톤 ImageLoader의 메모리·디스크 캐시만 지운다. 즐겨찾기(Room)·언어 설정
    // (DataStore)은 cacheDir이 아닌 filesDir 쪽에 저장돼 있어 영향받지 않는다.
    fun onClearCacheConfirmed() {
        viewModelScope.launch(Dispatchers.IO) {
            val loader = context.imageLoader
            loader.memoryCache?.clear()
            loader.diskCache?.clear()
            _uiState.update { it.copy(cacheClearedEventId = it.cacheClearedEventId + 1) }
        }
    }
}
