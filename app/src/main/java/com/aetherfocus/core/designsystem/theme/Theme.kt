package com.aetherfocus.core.designsystem.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TerminalColorScheme = darkColorScheme(
    primary = PhosphorGreen,
    onPrimary = VoidBlack,
    primaryContainer = CrtBlackAlt,
    onPrimaryContainer = PhosphorGreen,
    secondary = CyberCyan,
    onSecondary = VoidBlack,
    secondaryContainer = CrtBlackAlt,
    onSecondaryContainer = CyberCyan,
    tertiary = TerminalAmber,
    onTertiary = VoidBlack,
    background = VoidBlack,
    surface = CrtBlack,
    surfaceVariant = CrtBlackAlt,
    onBackground = CrtWhite,
    onSurface = CrtWhite,
    onSurfaceVariant = TextMuted,
    error = CrtRed,
    onError = VoidBlack,
    outline = TerminalBorder
)

@Composable
fun AetherFocusTheme(
    content: @Composable () -> Unit
) {
    val view = LocalViewBar()
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = VoidBlack.toArgb()
                window.navigationBarColor = VoidBlack.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = TerminalColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
private fun LocalViewBar() = LocalView.current
