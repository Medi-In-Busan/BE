package com.mediinbusan.app.feature.map

import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.place.Place

data class MapUiState(
    val isLoading: Boolean = true,
    val hospital: Hospital? = null,
    val nearbyPlaces: List<Place> = emptyList(),
    val errorMessage: String? = null
)
