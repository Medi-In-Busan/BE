package com.mediinbusan.app.feature.documentscan

import android.net.Uri
import com.mediinbusan.app.core.datastore.SupportedLanguage

data class DocumentScanUiState(
    val languageCode: String = SupportedLanguage.DEFAULT.code,
    val selectedImageUri: Uri? = null,
    val isAnalyzing: Boolean = false,
    val extractedText: String? = null,
    val translatedText: String? = null,
    val isAnalysisError: Boolean = false,
    val analysisError: String? = null,
    /** 카메라 권한을 이미 한 번 요청해 본 적 있는지 — 사전 고지/설정 안내 중 어느 쪽을 띄울지 판단한다. */
    val cameraPermissionRequested: Boolean = false
)
