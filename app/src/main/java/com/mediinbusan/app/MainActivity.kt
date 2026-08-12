package com.mediinbusan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.navigation.MediInBusanApp
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.haze.HazeLogger

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // BottomNavBar 인디케이터의 hazeEffect가 실제로 real blur를 그리는지 fallback(블러 없이
        // tint만 칠하는 모드)인지 Logcat 태그 "HazeEffect"로 직접 확인하기 위한 진단용 스위치.
        // 디버그 빌드에서만 켠다.
        if (BuildConfig.DEBUG) {
            HazeLogger.enabled = true
        }
        // 시스템 스플래시 → 우리 Compose Splash로 넘어갈 때 기본 아이콘 페이드아웃
        // 애니메이션이 들어가면 화면이 밀리는 것처럼 보인다. 애니메이션 없이 바로 제거한다.
        splashScreen.setOnExitAnimationListener { splashScreenView -> splashScreenView.remove() }
        setContent {
            MediInBusanTheme {
                MediInBusanApp()
            }
        }
    }
}
