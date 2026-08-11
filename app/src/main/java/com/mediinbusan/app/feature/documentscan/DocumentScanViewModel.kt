package com.mediinbusan.app.feature.documentscan

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 진단서·처방전 OCR 번역(문서 스캔) 화면. 백엔드 OCR 연동 전 단계라 이미지 촬영/선택까지만
 * 담당하고, 실제 분석 요청은 이후 이슈에서 붙인다.
 */
@HiltViewModel
class DocumentScanViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentScanUiState())
    val uiState: StateFlow<DocumentScanUiState> = _uiState

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { preferences ->
                _uiState.update { it.copy(languageCode = preferences.languageCode) }
            }
        }
    }

    fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun onImageCleared() {
        _uiState.update { it.copy(selectedImageUri = null) }
    }

    fun onLanguageSelected(languageCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguageCode(languageCode)
        }
    }
}
