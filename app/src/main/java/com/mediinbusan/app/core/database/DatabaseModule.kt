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
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideRecentlyViewedDao(database: AppDatabase): RecentlyViewedDao = database.recentlyViewedDao()
}
