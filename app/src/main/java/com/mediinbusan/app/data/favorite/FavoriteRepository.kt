package com.mediinbusan.app.data.favorite

import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<List<Favorite>>
    fun observeIsFavorite(itemId: String): Flow<Boolean>
    suspend fun toggleFavorite(favorite: Favorite)
}
