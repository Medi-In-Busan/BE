package com.mediinbusan.app.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mediinbusan.app.BuildConfig
import com.mediinbusan.app.data.diagnosischat.DiagnosisChatApi
import com.mediinbusan.app.data.document.DocumentOcrApi
import com.mediinbusan.app.data.hospital.HospitalApi
import com.mediinbusan.app.data.place.TourismApi
import com.mediinbusan.app.data.tourism.TourismCatalogApi
import com.mediinbusan.app.data.route.DrivingRouteApi
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

    // 응답/요청 본문에 의료 문서 원문·건강 상태가 실리는 엔드포인트. 이 경로들만 BODY 로깅에서
    // 제외한다(SensitivePathLoggingInterceptor 참고).
    private val SENSITIVE_LOG_PATHS = listOf("/documents/ocr", "/diagnosis-chat")

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val bodyLevel = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        val basicLevel = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        // 호출 단위 로그(메서드·URL·응답 코드·소요 시간)는 애플리케이션 인터셉터로 단다 — 연결 자체가
        // 실패한 요청(오프라인, base URL 오타)도 남기려면 이 자리여야 한다. 네트워크 인터셉터는
        // 연결이 성립해야 돌기 때문에 그런 호출은 흔적조차 남지 않는다.
        val callLogging = HttpLoggingInterceptor().apply { level = basicLevel }
        // 본문 로그는 네트워크 인터셉터로 단다. 애플리케이션 인터셉터는 최초 요청 경로로 한 번만
        // 민감도를 판정해서, 비민감 경로가 /documents/ocr 같은 민감 경로로 리디렉션되면 최종 본문이
        // 그대로 찍힌다. 네트워크 인터셉터는 리디렉션·재시도로 실제 오간 홉마다 다시 돌아 경로를
        // 매번 새로 판정한다. 민감 홉은 여기서 NONE으로 완전히 빼고, 메서드·URL·소요 시간은 위
        // callLogging이 이미 남기므로 디버깅에 필요한 정보는 그대로 남는다.
        val bodyLogging = SensitivePathLoggingInterceptor(
            defaultLogger = HttpLoggingInterceptor().apply { level = bodyLevel },
            sensitiveLogger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.NONE },
            sensitivePaths = SENSITIVE_LOG_PATHS
        )
        // 기본 10초 타임아웃은 문서 스캔 이미지 업로드(멀티파트, CLOVA OCR 왕복 포함)에는
        // 빠듯할 수 있어 전체 클라이언트 기준으로 여유를 둔다.
        return OkHttpClient.Builder()
            .addInterceptor(callLogging)
            .addNetworkInterceptor(bodyLogging)
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

    @Provides
    @Singleton
    fun provideTourismCatalogApi(okHttpClient: OkHttpClient, json: Json): TourismCatalogApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.MEDIINBUSAN_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TourismCatalogApi::class.java)

    @Provides
    @Singleton
    fun provideDrivingRouteApi(okHttpClient: OkHttpClient, json: Json): DrivingRouteApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.MEDIINBUSAN_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(DrivingRouteApi::class.java)

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

    // DiagnosisChatApi도 자체 백엔드(backend/diagnosischat, Gemini 프록시)를 바라본다.
    @Provides
    @Singleton
    fun provideDiagnosisChatApi(okHttpClient: OkHttpClient, json: Json): DiagnosisChatApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.MEDIINBUSAN_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(DiagnosisChatApi::class.java)
}
