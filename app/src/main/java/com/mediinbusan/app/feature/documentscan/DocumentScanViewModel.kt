package com.mediinbusan.app.feature.documentscan

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.document.DocumentOcrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val savedStateHandle: SavedStateHandle,
    @param:ApplicationContext private val context: Context
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
        // 지난 세션에 남은 촬영본 정리. 프로세스가 복원된 경우엔 위에서 되살린 선택만 남긴다.
        cleanUpCapturedImages(keep = _uiState.value.selectedImageUri)
    }

    fun onImageSelected(uri: Uri) {
        savedStateHandle[KEY_SELECTED_IMAGE_URI] = uri.toString()
        _uiState.update {
            it.copy(selectedImageUri = uri, extractedText = null, translatedText = null, isAnalysisError = false, analysisError = null)
        }
        cleanUpCapturedImages(keep = uri)
    }

    fun onImageCleared() {
        savedStateHandle[KEY_SELECTED_IMAGE_URI] = null
        _uiState.update {
            it.copy(selectedImageUri = null, extractedText = null, translatedText = null, isAnalysisError = false, analysisError = null)
        }
        cleanUpCapturedImages(keep = null)
    }

    /**
     * 캐시에 쌓인 촬영본 중 [keep]만 남기고 지운다.
     *
     * 예전에는 화면에서 `LaunchedEffect(uiState.selectedImageUri)`로 돌렸는데, 촬영 화면
     * (Route.DocumentCapture)이 별도 목적지가 되면서 깨졌다 — 촬영을 끝내고 pop해 돌아오면 이 화면이
     * 새로 컴포즈되는데, 그 첫 프레임의 selectedImageUri는 아직 **촬영 전 값(null)** 이다(촬영 결과는
     * NavBackStackEntry의 savedStateHandle에만 있고 ViewModel엔 아직 안 들어왔다). 그래서 정리가
     * keep=null로 돌아 방금 찍은 파일을 지웠고, 그 뒤 상태가 갱신돼 미리보기는 이미 없는 파일을
     * 가리켰다 — 촬영 직후 사진이 안 보이던 원인이다.
     *
     * 정리 시점을 컴포지션이 아니라 **선택 상태가 실제로 바뀌는 순간**으로 옮겨 같은 일이 다시
     * 생기지 않게 한다. 여기서는 지울 대상이 무엇인지가 항상 확정돼 있다.
     */
    private fun cleanUpCapturedImages(keep: Uri?) {
        viewModelScope.launch(Dispatchers.IO) { clearCapturedImages(context, keep = keep) }
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

    /**
     * 이 ViewModel이 사라진다는 건 선택 상태도 같이 사라진다는 뜻이라, 캐시에 남은 촬영본은
     * 주인 없는 진단서 사진이 된다. 다음에 문서 스캔 탭을 여는 시점(화면의 clearCapturedImages)까지
     * 기다리지 말고 여기서 지운다.
     *
     * 탭 전환은 여기로 오지 않는다 — navigateToTab이 상태를 보관해서 ViewModel이 그대로 살아 있고,
     * 돌아왔을 때 미리보기가 그대로 떠 있어야 하므로 파일도 남아 있어야 맞다. 프로세스가 강제
     * 종료될 때도 이 콜백은 안 불리는데, 그때는 SavedStateHandle로 선택이 복원되므로 역시 남는 게 맞다.
     *
     * viewModelScope는 이 시점에 이미 취소돼 있어 쓸 수 없고, 삭제는 화면 수명과 무관하게 끝나야
     * 하므로 이 작업만 하고 끝나는 스코프를 따로 만든다(파일 몇 개 지우는 짧은 IO다).
     */
    override fun onCleared() {
        super.onCleared()
        CoroutineScope(Dispatchers.IO).launch { clearCapturedImages(context, keep = null) }
    }

    companion object {
        private const val KEY_SELECTED_IMAGE_URI = "selectedImageUri"
    }
}
