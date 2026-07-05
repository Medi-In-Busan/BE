package com.mediinbusan.app.feature.guide

import com.mediinbusan.app.data.guide.GuideStep

data class GuideUiState(
    val isLoading: Boolean = true,
    val steps: List<GuideStep> = emptyList(),
    val errorMessage: String? = null
)
