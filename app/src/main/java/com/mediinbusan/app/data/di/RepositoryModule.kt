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
import com.mediinbusan.app.data.route.DrivingRouteRepository
import com.mediinbusan.app.data.route.DrivingRouteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    // HospitalRepositoryImpl/PlaceRepositoryImpl은 인메모리 TTL 캐시(TtlCache)를 인스턴스 필드로
    // 들고 있다 — @Singleton이 없으면 화면을 오갈 때(ViewModel 재생성)마다 새 인스턴스가 만들어져
    // 캐시가 매번 비워진다. @Binds로 노출하는 이 메서드에도 스코프를 똑같이 선언해야 한다.
    @Singleton
    @Binds
    fun bindHospitalRepository(impl: HospitalRepositoryImpl): HospitalRepository

    @Singleton
    @Binds
    fun bindPlaceRepository(impl: PlaceRepositoryImpl): PlaceRepository

    @Binds
    fun bindWellnessTourismRepository(impl: WellnessTourismRepositoryImpl): WellnessTourismRepository

    @Binds
    fun bindTourismCatalogRepository(impl: TourismCatalogRepositoryImpl): TourismCatalogRepository

    @Binds
    fun bindTourismInteractionRepository(impl: TourismInteractionRepositoryImpl): TourismInteractionRepository

    @Binds
    fun bindDrivingRouteRepository(impl: DrivingRouteRepositoryImpl): DrivingRouteRepository

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
