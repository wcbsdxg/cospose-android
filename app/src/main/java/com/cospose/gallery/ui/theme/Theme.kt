package com.cospose.gallery.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.cospose.gallery.data.SettingsRepository
import com.cospose.gallery.data.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    onPrimary = Color.White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue700,
    secondary = Purple500,
    onSecondary = Color.White,
    secondaryContainer = Purple100,
    background = Color.White,
    onBackground = Gray900,
    surface = Color.White,
    onSurface = Gray900,
    surfaceVariant = Gray50,
    onSurfaceVariant = Gray500,
    outline = Gray200,
    outlineVariant = Gray100,
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue400,
    onPrimary = Blue700,
    primaryContainer = Blue700,
    onPrimaryContainer = Blue100,
    secondary = Purple500,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A3A6B),
    background = Gray900,
    onBackground = Gray50,
    surface = Gray800,
    onSurface = Gray50,
    surfaceVariant = Gray700,
    onSurfaceVariant = Gray400,
    outline = Gray600,
    outlineVariant = Gray700,
)

@Composable
fun CosPoseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CosPoseTypography,
        content = content
    )
}

@Composable
fun CosPoseThemeWithSettings(
    settingsRepository: SettingsRepository,
    content: @Composable () -> Unit
) {
    val themeMode by settingsRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val dynamicColor by settingsRepository.dynamicColor.collectAsState(initial = true)

    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    CosPoseTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content = content
    )
}
