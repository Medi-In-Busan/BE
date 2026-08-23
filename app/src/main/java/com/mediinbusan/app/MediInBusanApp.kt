package com.mediinbusan.app

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk
import com.mediinbusan.app.core.ui.KakaoMapAvailability
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MediInBusanApp : Application() {
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
