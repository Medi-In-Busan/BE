package com.mediinbusan.app.data.document

import android.net.Uri
import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow

/** translatedText/targetLanguage는 번역이 요청되지 않았거나 실패했을 때 null이다. */
data class DocumentOcrResult(
    val text: String,
    val translatedText: String?,
    val targetLanguage: String?
)

interface DocumentOcrRepository {
    // imageUri는 카메라(FileProvider)/갤러리(Photo Picker) 어느 쪽이든 content:// Uri.
    // targetLanguage는 앱의 SupportedLanguage.code. null이면 번역을 요청하지 않는다.
    fun extractText(imageUri: Uri, targetLanguage: String?): Flow<Result<DocumentOcrResult>>
}
