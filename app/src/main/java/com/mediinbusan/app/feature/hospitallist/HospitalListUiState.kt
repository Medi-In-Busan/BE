package com.mediinbusan.app.feature.hospitallist

import com.mediinbusan.app.data.hospital.Hospital

data class HospitalListUiState(
    val isLoading: Boolean = true,
    val hospitals: List<Hospital> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
) {
    val filteredHospitals: List<Hospital>
        get() = if (searchQuery.isBlank()) {
            hospitals
        } else {
            hospitals.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
}
