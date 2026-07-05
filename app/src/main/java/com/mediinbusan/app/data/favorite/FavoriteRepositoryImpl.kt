package com.mediinbusan.app.data.favorite

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** F-015: 이미 저장된 항목을 다시 누르면 저장을 해제한다. */
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun observeFavorites(): Flow<List<Favorite>> =
        favoriteDao.observeFavorites().map { entities -> entities.map { it.toDomain() } }

    override fun observeIsFavorite(itemId: String): Flow<Boolean> =
        favoriteDao.observeIsFavorite(itemId)

    override suspend fun toggleFavorite(favorite: Favorite) {
        val isFavorite = favoriteDao.observeIsFavorite(favorite.itemId).first()
        if (isFavorite) {
            favoriteDao.delete(favorite.toEntity())
        } else {
            favoriteDao.upsert(favorite.toEntity())
        }
    }
}
