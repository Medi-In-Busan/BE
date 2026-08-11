package com.mediinbusan.app.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mediinbusan.app.BuildConfig
import com.mediinbusan.app.data.document.DocumentOcrApi
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
import java.util.concurrent.TimeUnit
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
        // 기본 10초 타임아웃은 문서 스캔 이미지 업로드(멀티파트, CLOVA OCR 왕복 포함)에는
        // 빠듯할 수 있어 전체 클라이언트 기준으로 여유를 둔다.
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
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

    // HospitalApi는 한국관광공사 API가 아니라 자체 백엔드(backend/)를 바라봐서, TOUR_API_BASE_URL을 쓰는
    // 공용 Retrofit 대신 별도 base URL로 직접 빌드한다.
    @Provides
    @Singleton
    fun provideHospitalApi(okHttpClient: OkHttpClient, json: Json): HospitalApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.MEDIINBUSAN_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(HospitalApi::class.java)

    @Provides
    @Singleton
    fun provideTourismApi(okHttpClient: OkHttpClient, json: Json): TourismApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.MEDIINBUSAN_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TourismApi::class.java)

    // DocumentOcrApi도 자체 백엔드(backend/document)를 바라본다.
    @Provides
    @Singleton
    fun provideDocumentOcrApi(okHttpClient: OkHttpClient, json: Json): DocumentOcrApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.MEDIINBUSAN_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(DocumentOcrApi::class.java)
}
