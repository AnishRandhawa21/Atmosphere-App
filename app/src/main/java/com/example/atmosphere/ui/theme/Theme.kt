package com.example.atmosphere.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Primary,
    primaryContainer = PrimaryContainer,

    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,

    tertiaryContainer = TertiaryContainer,

    background = Surface,
    surface = Surface,

    surfaceVariant = SurfaceContainer,
    outline = OutlineVariant,

    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant
)

@Composable
fun AtmosphereTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}