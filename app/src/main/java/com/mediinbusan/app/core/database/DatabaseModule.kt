package com.mediinbusan.app.core.database

import android.content.Context
import androidx.room.Room
import com.mediinbusan.app.data.favorite.FavoriteDao
import com.mediinbusan.app.data.recent.RecentlyViewedDao
import com.mediinbusan.app.data.searchhistory.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "mediinbusan.db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        // v3->v4(즐겨찾기/최근 본 항목에 subtitle/address/좌표 컬럼 추가)는 순수 컬럼 추가라
        // MIGRATION_3_4로 로컬 데이터를 보존한다. 그 외 아직 안 챙긴 스키마 변경은 출시 전이라
        // 정식 Migration 없이 destructive fallback으로 초기화되게 둔다.
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .addMigrations(MIGRATION_3_4)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideRecentlyViewedDao(database: AppDatabase): RecentlyViewedDao = database.recentlyViewedDao()

    @Provides
    fun provideSearchHistoryDao(database: AppDatabase): SearchHistoryDao = database.searchHistoryDao()
}
