package com.mediinbusan.app.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mediinbusan.app.BuildConfig
import com.mediinbusan.app.data.hospital.HospitalApi
import com.mediinbusan.app.data.place.TourismApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

// TODO: 한국관광공사 의료관광정보/관광정보 서비스의 정확한 base URL·오퍼레이션명은
// 실제 API 문서 확인 후 확정한다 (data/hospital/HospitalApi.kt, data/place/TourismApi.kt 참고).
private const val TOUR_API_BASE_URL = "https://apis.data.go.kr/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(TOUR_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideHospitalApi(retrofit: Retrofit): HospitalApi = retrofit.create(HospitalApi::class.java)

    @Provides
    @Singleton
    fun provideTourismApi(retrofit: Retrofit): TourismApi = retrofit.create(TourismApi::class.java)
}
