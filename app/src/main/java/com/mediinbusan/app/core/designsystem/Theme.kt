package com.mediinbusan.app.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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
        shapes = MediInBusanShapes,
        content = content
    )
}
