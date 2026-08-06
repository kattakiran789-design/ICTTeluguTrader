// Theme.kt
package com.icttelugu.trader.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = DarkBackground,
    surface = DarkSurface,
    primary = BullishGreen,
    secondary = AccentYellow,
    error = BearishRed,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
ComposableTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
