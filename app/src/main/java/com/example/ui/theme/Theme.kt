package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val QuizDarkColorScheme = darkColorScheme(
    primary = QuizPrimary,
    onPrimary = TextPrimary,
    primaryContainer = QuizPrimaryDark,
    onPrimaryContainer = QuizPrimaryLight,
    secondary = QuizGold,
    onSecondary = QuizBgDark,
    secondaryContainer = QuizGoldDark,
    onSecondaryContainer = QuizGoldLight,
    tertiary = QuizCyan,
    onTertiary = QuizBgDark,
    background = QuizBgDark,
    onBackground = TextPrimary,
    surface = QuizSurface,
    onSurface = TextPrimary,
    surfaceVariant = QuizSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = QuizRuby,
    onError = TextPrimary
)

@Composable
fun QuizManiaTheme(
    darkTheme: Boolean = true, // Quiz Mania uses immersive dark arcade theme by default
    content: @Composable () -> Unit
) {
    val colorScheme = QuizDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = QuizBgDark.toArgb()
                window.navigationBarColor = QuizBgDarker.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
