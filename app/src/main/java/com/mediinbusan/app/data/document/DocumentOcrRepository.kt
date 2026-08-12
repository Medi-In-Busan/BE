package com.mediinbusan.app.data.document

import android.net.Uri
import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow

interface DocumentOcrRepository {
    // imageUri는 카메라(FileProvider)/갤러리(Photo Picker) 어느 쪽이든 content:// Uri.
    fun extractText(imageUri: Uri): Flow<Result<String>>
}
