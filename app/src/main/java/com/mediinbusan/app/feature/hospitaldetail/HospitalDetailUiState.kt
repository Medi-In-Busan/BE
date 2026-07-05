package com.mediinbusan.app.feature.hospitaldetail

import com.mediinbusan.app.data.hospital.Hospital

data class HospitalDetailUiState(
    val isLoading: Boolean = true,
    val hospital: Hospital? = null,
    val isFavorite: Boolean = false,
    val errorMessage: String? = null
)
