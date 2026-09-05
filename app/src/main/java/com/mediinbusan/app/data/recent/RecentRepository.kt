package com.mediinbusan.app.data.recent

import kotlinx.coroutines.flow.Flow

interface RecentRepository {
    fun observeRecentlyViewed(): Flow<List<RecentlyViewed>>
    suspend fun findById(itemId: String): RecentlyViewed?
    suspend fun recordView(
        itemId: String,
        itemName: String,
        itemType: RecentItemType,
        imageUrl: String?,
        subtitle: String = "",
        address: String = "",
        latitude: Double? = null,
        longitude: Double? = null,
        tourismCategory: String? = null,
        tourismDistrict: String? = null
    )
    suspend fun removeItem(itemId: String)
    suspend fun removeAll()
}
