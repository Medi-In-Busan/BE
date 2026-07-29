package com.mediinbusan.app.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.mediinbusan.app.data.guide.TreatmentBriefingRepository
import com.mediinbusan.app.data.guide.TreatmentBriefingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

// STEP04 진료 브리핑 카드 전용 DataStore 파일. UserPreferences(언어·온보딩 등 앱 설정)와
// 관심사가 달라(사용자가 매번 새로 적는 세션성 정보) 별도 파일로 분리한다.
private val Context.treatmentBriefingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "treatment_briefing"
)

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TreatmentBriefingDataStore

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.userPreferencesDataStore

    @Provides
    @Singleton
    @TreatmentBriefingDataStore
    fun provideTreatmentBriefingDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.treatmentBriefingDataStore
}

@Module
@InstallIn(SingletonComponent::class)
interface DataStoreBindsModule {
    @Binds
    fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository

    @Binds
    fun bindTreatmentBriefingRepository(impl: TreatmentBriefingRepositoryImpl): TreatmentBriefingRepository
}
