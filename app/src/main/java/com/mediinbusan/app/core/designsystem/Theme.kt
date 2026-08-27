package com.mediinbusan.app.core.designsystem

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private val LightColors = lightColorScheme(
    primary = MediBlue40,
    secondary = MediTeal40,
    tertiary = MediSand40,
    error = MediError40
)

private val DarkColors = darkColorScheme(
    primary = MediBlue80,
    secondary = MediTeal80,
    tertiary = MediSand80,
    error = MediError80
)

@Composable
fun MediInBusanTheme(
    // 이 앱의 모든 화면은 MaterialTheme.colorScheme 토큰이 아니라 core/designsystem의 고정
    // 라이트 팔레트(TextPrimary, CoralPrimary, Color.White 등)를 직접 쓴다 — 실질적인 다크
    // 테마 지원이 없다. 그런데도 기본값이 isSystemInDarkTheme()를 따라가던 탓에, 시스템이
    // 다크 모드인 기기에서는 바로 아래 최상위 Surface(color = colorScheme.background)가
    // DarkColors의 거의 검은 배경을 잠깐 칠했다가 각 화면 자신의(항상 라이트인) Scaffold가
    // 그 위를 덮는 구조였다 — 화면 전환마다(특히 KakaoMapView처럼 첫 프레임이 늦게 뜨는 화면)
    // 그 검은 배경이 한 프레임 스쳐 지나가며 "화면이 흑백으로 깜빡인다"는 리포트로 이어졌다.
    // 다크 테마를 실제로 지원하기 전까지는 시스템 설정과 무관하게 라이트로 고정한다.
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MediInBusanTypography,
        shapes = MediInBusanShapes
    ) {
        // 최상위에 불투명 배경을 깔아둔다. 이게 없으면 배경을 직접 안 그리는 화면(Onboarding 등)으로
        // 전환될 때 이전 화면의 픽셀이 한 프레임 남아있다가 덮여서 "밀리는" 것처럼 보인다.
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}
