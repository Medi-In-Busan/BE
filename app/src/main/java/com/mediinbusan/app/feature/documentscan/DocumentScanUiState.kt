package com.mediinbusan.app.feature.documentscan

import android.net.Uri
import com.mediinbusan.app.core.datastore.SupportedLanguage

data class DocumentScanUiState(
    val languageCode: String = SupportedLanguage.DEFAULT.code,
    val selectedImageUri: Uri? = null,
    val isAnalyzing: Boolean = false,
    val extractedText: String? = null,
    val isAnalysisError: Boolean = false,
    val analysisError: String? = null
)
