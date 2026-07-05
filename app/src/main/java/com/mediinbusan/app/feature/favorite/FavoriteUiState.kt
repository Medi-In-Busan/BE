package com.mediinbusan.app.feature.favorite

import com.mediinbusan.app.data.favorite.Favorite

data class FavoriteUiState(
    val favorites: List<Favorite> = emptyList()
)
