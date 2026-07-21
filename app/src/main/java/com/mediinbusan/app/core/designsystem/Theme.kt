package com.mediinbusan.app.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
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
    darkTheme: Boolean = isSystemInDarkTheme(),
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
