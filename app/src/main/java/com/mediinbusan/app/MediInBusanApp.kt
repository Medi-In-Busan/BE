package com.mediinbusan.app

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import com.kakao.vectormap.KakaoMapSdk
import com.mediinbusan.app.core.ui.KakaoMapAvailability
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MediInBusanApp : Application(), SingletonImageLoader.Factory {
    // 웰니스/부산관광/무장애관광처럼 스크롤로 카드가 계속 재구성되는 화면에서, 기본 메모리
    // 캐시 비율(20%)로는 이미 본 이미지가 다른 이미지에 밀려나 스크롤로 다시 볼 때마다 로딩
    // 스피너가 다시 뜨는 일이 잦았다 — 비율을 넉넉하게 올려 같은 세션에서 재요청 자체를 줄인다.
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, percent = 0.4)
                    .build()
            }
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        // Kakao Maps SDK 네이티브 라이브러리(libK3fAndroid.so)는 arm64-v8a/armeabi-v7a로만 배포되어
        // x86_64 에뮬레이터에서는 KakaoMapSdk.init() 자체가 UnsatisfiedLinkError를 던진다(Error이므로
        // Exception이 아니라 Throwable로 잡아야 한다). 실기기/ARM 에뮬레이터에서는 정상 동작한다.
        // Android Vector Map SDK v2 공개 API에는 초기화 여부를 조회하는 메서드가 없어,
        // init() 성공 여부를 직접 플래그로 기록해 KakaoMapView가 참조하게 한다.
        val kakaoNativeAppKey = BuildConfig.KAKAO_NATIVE_APP_KEY
        if (kakaoNativeAppKey.isBlank()) {
            KakaoMapAvailability.unavailableReason =
                "KAKAO_NATIVE_APP_KEY가 설정되지 않아 지도를 표시할 수 없습니다"
            android.util.Log.e("MediInBusanApp", KakaoMapAvailability.unavailableReason)
            return
        }
        try {
            KakaoMapSdk.init(this, kakaoNativeAppKey)
            KakaoMapAvailability.isAvailable = true
        } catch (t: Throwable) {
            KakaoMapAvailability.unavailableReason = "이 기기에서 Kakao 지도를 초기화하지 못했습니다"
            android.util.Log.e("MediInBusanApp", "KakaoMapSdk.init 실패 — 이 기기/에뮬레이터에서는 지도를 사용할 수 없습니다.", t)
        }
    }
}
