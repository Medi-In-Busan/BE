package com.mediinbusan.app.feature.documentscan

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.document.DocumentOcrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 진단서·처방전 OCR 번역(문서 스캔) 화면. 이미지 촬영/선택 + CLOVA OCR 프록시(backend/document) 호출을 담당한다. */
@HiltViewModel
class DocumentScanViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val documentOcrRepository: DocumentOcrRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 촬영/선택한 이미지는 프로세스가 죽었다 복원돼도(카메라 앱 실행 중 메모리 회수 등)
    // 유지되도록 SavedStateHandle에 문자열로 함께 저장한다.
    private val _uiState = MutableStateFlow(
        DocumentScanUiState(
            selectedImageUri = savedStateHandle.get<String>(KEY_SELECTED_IMAGE_URI)?.let(Uri::parse)
        )
    )
    val uiState: StateFlow<DocumentScanUiState> = _uiState

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { preferences ->
                _uiState.update { it.copy(languageCode = preferences.languageCode) }
            }
        }
    }

    fun onImageSelected(uri: Uri) {
        savedStateHandle[KEY_SELECTED_IMAGE_URI] = uri.toString()
        _uiState.update {
            it.copy(selectedImageUri = uri, extractedText = null, translatedText = null, isAnalysisError = false, analysisError = null)
        }
    }

    fun onImageCleared() {
        savedStateHandle[KEY_SELECTED_IMAGE_URI] = null
        _uiState.update {
            it.copy(selectedImageUri = null, extractedText = null, translatedText = null, isAnalysisError = false, analysisError = null)
        }
    }

    fun onAnalyzeClick() {
        // isAnalyzing을 여기서 동기적으로 먼저 세팅해야 한다 — Repository의 flow가
        // flowOn(Dispatchers.IO)를 쓰기 때문에 Result.Loading emit도 IO 디스패치를 거쳐 약간
        // 늦게 도착한다. 그 틈에 버튼이 눌리는 걸 이 가드가 막는다.
        if (_uiState.value.isAnalyzing) return
        val imageUri = _uiState.value.selectedImageUri ?: return
        val targetLanguage = _uiState.value.languageCode
        _uiState.update { it.copy(isAnalyzing = true, isAnalysisError = false, analysisError = null) }
        viewModelScope.launch {
            documentOcrRepository.extractText(imageUri, targetLanguage).collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Result.Loading -> state
                        is Result.Success -> state.copy(
                            isAnalyzing = false,
                            extractedText = result.data.text,
                            translatedText = result.data.translatedText,
                            isAnalysisError = false,
                            analysisError = null
                        )
                        // 폴백 문구는 여기서 언어를 고정하지 않고 화면이 LocalAppStrings로 매번 새로 읽는다.
                        is Result.Error -> state.copy(isAnalyzing = false, isAnalysisError = true, analysisError = result.message)
                    }
                }
            }
        }
    }

    fun onLanguageSelected(languageCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguageCode(languageCode)
        }
    }

    companion object {
        private const val KEY_SELECTED_IMAGE_URI = "selectedImageUri"
    }
}
