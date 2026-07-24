package com.mediinbusan.app.core.database

import android.content.Context
import androidx.room.Room
import com.mediinbusan.app.data.favorite.FavoriteDao
import com.mediinbusan.app.data.recent.RecentlyViewedDao
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
        // 출시 전이라 정식 Migration 대신 destructive fallback을 쓴다. 스키마가 바뀔 때마다
        // 즐겨찾기/최근 본 항목이 초기화되지만, 실사용자 데이터가 없는 단계라 문제 없다.
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideRecentlyViewedDao(database: AppDatabase): RecentlyViewedDao = database.recentlyViewedDao()
}
