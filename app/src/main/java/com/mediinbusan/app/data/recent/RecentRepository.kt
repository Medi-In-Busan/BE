package com.mediinbusan.app.data.recent

import kotlinx.coroutines.flow.Flow

interface RecentRepository {
    fun observeRecentlyViewed(): Flow<List<RecentlyViewed>>
    suspend fun recordView(itemId: String, itemName: String)
}
