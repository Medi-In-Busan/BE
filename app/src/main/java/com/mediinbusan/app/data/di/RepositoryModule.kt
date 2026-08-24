package com.mediinbusan.app.data.di

import com.mediinbusan.app.data.diagnosischat.DiagnosisChatRepository
import com.mediinbusan.app.data.diagnosischat.DiagnosisChatRepositoryImpl
import com.mediinbusan.app.data.document.DocumentOcrRepository
import com.mediinbusan.app.data.document.DocumentOcrRepositoryImpl
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.favorite.FavoriteRepositoryImpl
import com.mediinbusan.app.data.guide.GuideRepository
import com.mediinbusan.app.data.guide.GuideRepositoryImpl
import com.mediinbusan.app.data.hospital.HospitalRepository
import com.mediinbusan.app.data.hospital.HospitalRepositoryImpl
import com.mediinbusan.app.data.place.PlaceRepository
import com.mediinbusan.app.data.place.PlaceRepositoryImpl
import com.mediinbusan.app.data.place.WellnessTourismRepository
import com.mediinbusan.app.data.place.WellnessTourismRepositoryImpl
import com.mediinbusan.app.data.recent.RecentRepository
import com.mediinbusan.app.data.recent.RecentRepositoryImpl
import com.mediinbusan.app.data.searchhistory.SearchHistoryRepository
import com.mediinbusan.app.data.searchhistory.SearchHistoryRepositoryImpl
import com.mediinbusan.app.data.tourism.TourismCatalogRepository
import com.mediinbusan.app.data.tourism.TourismCatalogRepositoryImpl
import com.mediinbusan.app.data.tourism.TourismInteractionRepository
import com.mediinbusan.app.data.tourism.TourismInteractionRepositoryImpl
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
    fun bindWellnessTourismRepository(impl: WellnessTourismRepositoryImpl): WellnessTourismRepository

    @Binds
    fun bindTourismCatalogRepository(impl: TourismCatalogRepositoryImpl): TourismCatalogRepository

    @Binds
    fun bindTourismInteractionRepository(impl: TourismInteractionRepositoryImpl): TourismInteractionRepository

    @Binds
    fun bindGuideRepository(impl: GuideRepositoryImpl): GuideRepository

    @Binds
    fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    fun bindRecentRepository(impl: RecentRepositoryImpl): RecentRepository

    @Binds
    fun bindSearchHistoryRepository(impl: SearchHistoryRepositoryImpl): SearchHistoryRepository

    @Binds
    fun bindDocumentOcrRepository(impl: DocumentOcrRepositoryImpl): DocumentOcrRepository

    @Binds
    fun bindDiagnosisChatRepository(impl: DiagnosisChatRepositoryImpl): DiagnosisChatRepository
}
