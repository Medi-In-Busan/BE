package com.mediinbusan.app.data.recent

import com.mediinbusan.app.data.favorite.FavoriteItemType
import kotlinx.coroutines.flow.Flow

interface RecentRepository {
    fun observeRecentlyViewed(): Flow<List<RecentlyViewed>>
    suspend fun recordView(itemId: String, itemName: String, itemType: FavoriteItemType, imageUrl: String?)
}
