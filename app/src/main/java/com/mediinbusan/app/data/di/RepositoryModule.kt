package com.mediinbusan.app.data.di

import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.favorite.FavoriteRepositoryImpl
import com.mediinbusan.app.data.guide.GuideRepository
import com.mediinbusan.app.data.guide.GuideRepositoryImpl
import com.mediinbusan.app.data.hospital.HospitalRepository
import com.mediinbusan.app.data.hospital.HospitalRepositoryImpl
import com.mediinbusan.app.data.place.PlaceRepository
import com.mediinbusan.app.data.place.PlaceRepositoryImpl
import com.mediinbusan.app.data.recent.RecentRepository
import com.mediinbusan.app.data.recent.RecentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindHospitalRepository(impl: HospitalRepositoryImpl): HospitalRepository

    @Binds
    fun bindPlaceRepository(impl: PlaceRepositoryImpl): PlaceRepository

    @Binds
    fun bindGuideRepository(impl: GuideRepositoryImpl): GuideRepository

    @Binds
    fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    fun bindRecentRepository(impl: RecentRepositoryImpl): RecentRepository
}
