package com.mediinbusan.app.feature.nearby

import com.mediinbusan.app.data.place.Place

data class NearbyUiState(
    val isLoading: Boolean = true,
    val places: List<Place> = emptyList(),
    val errorMessage: String? = null
)

data class PlaceDetailUiState(
    val isLoading: Boolean = true,
    val place: Place? = null,
    val isFavorite: Boolean = false,
    val errorMessage: String? = null
)
