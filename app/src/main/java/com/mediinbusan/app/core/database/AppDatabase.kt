package com.mediinbusan.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mediinbusan.app.data.favorite.FavoriteDao
import com.mediinbusan.app.data.favorite.FavoriteEntity
import com.mediinbusan.app.data.recent.RecentlyViewedDao
import com.mediinbusan.app.data.recent.RecentlyViewedEntity

/**
 * 즐겨찾기(F-015)·최근 본 항목(F-016)만 로컬 영속화한다.
 * Hospital/Place/GuideStep은 OpenAPI/정적 데이터에서 매 세션 조회하므로 Room 엔티티로 두지 않는다
 * (이유는 docs/ARCHITECTURE.md 참고).
 */
@Database(
    entities = [FavoriteEntity::class, RecentlyViewedEntity::class],
    version = 2, // v2: RecentlyViewedEntity에 itemType/imageUrl 추가 (출시 전이라 destructive migration으로 처리)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentlyViewedDao(): RecentlyViewedDao
}
