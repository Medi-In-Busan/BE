package com.mediinbusan.app

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MediInBusanApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Kakao Maps SDK 네이티브 라이브러리(libK3fAndroid.so)는 arm64-v8a/armeabi-v7a로만 배포되어
        // x86_64 에뮬레이터에서는 KakaoMapSdk.init() 자체가 UnsatisfiedLinkError를 던진다(Error이므로
        // Exception이 아니라 Throwable로 잡아야 한다). 실기기/ARM 에뮬레이터에서는 정상 동작한다.
        try {
            KakaoMapSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        } catch (t: Throwable) {
            android.util.Log.e("MediInBusanApp", "KakaoMapSdk.init 실패 — 이 기기/에뮬레이터에서는 지도를 사용할 수 없습니다.", t)
        }
    }
}
