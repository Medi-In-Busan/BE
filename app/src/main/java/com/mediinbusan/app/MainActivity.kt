package com.mediinbusan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.navigation.MediInBusanApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
