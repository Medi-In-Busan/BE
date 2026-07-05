package com.mediinbusan.app.feature.home

data class HomeUiState(
    val medicalPurposes: List<String> = listOf("피부·미용", "건강검진", "치과", "한방", "재활", "웰니스"),
    val selectedPurpose: String? = null
)
