package com.mediinbusan.app.feature.documentscan

import android.net.Uri
import com.mediinbusan.app.core.datastore.SupportedLanguage

/** OCR/번역 백엔드 연동 전 단계라, 지금은 이미지 선택 여부만 상태로 갖는다. */
data class DocumentScanUiState(
    val languageCode: String = SupportedLanguage.DEFAULT.code,
    val selectedImageUri: Uri? = null
)
