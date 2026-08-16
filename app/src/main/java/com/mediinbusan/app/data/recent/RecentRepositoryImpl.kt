package com.mediinbusan.app.data.recent

import com.mediinbusan.app.data.favorite.FavoriteItemType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** F-016: 최근 본 항목이 없으면 화면에서 해당 영역을 숨긴다(빈 리스트로 표현). */
class RecentRepositoryImpl @Inject constructor(
    private val recentlyViewedDao: RecentlyViewedDao
) : RecentRepository {

    override fun observeRecentlyViewed(): Flow<List<RecentlyViewed>> =
        recentlyViewedDao.observeRecentlyViewed().map { entities -> entities.map { it.toDomain() } }

    override suspend fun recordView(
        itemId: String,
        itemName: String,
        itemType: FavoriteItemType,
        imageUrl: String?,
        subtitle: String,
        address: String,
        latitude: Double?,
        longitude: Double?
    ) {
        recentlyViewedDao.upsert(
            RecentlyViewedEntity(
                itemId = itemId,
                itemName = itemName,
                itemType = itemType.name,
                imageUrl = imageUrl,
                viewedAt = System.currentTimeMillis(),
                subtitle = subtitle,
                address = address,
                latitude = latitude,
                longitude = longitude
            )
        )
    }

    override suspend fun removeItem(itemId: String) {
        recentlyViewedDao.deleteByItemId(itemId)
    }

    override suspend fun removeAll() {
        recentlyViewedDao.clearAll()
    }
}
