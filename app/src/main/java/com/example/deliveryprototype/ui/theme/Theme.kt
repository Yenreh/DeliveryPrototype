package com.example.deliveryprototype.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun DeliveryPrototypeTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = Primary,
        secondary = GraySurface,
        tertiary = Accent,
        background = GrayBackground,
        surface = GraySurface,
        onPrimary = White,
        onSecondary = BlackText,
        onTertiary = White,
        onBackground = BlackText,
        onSurface = BlackText
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}