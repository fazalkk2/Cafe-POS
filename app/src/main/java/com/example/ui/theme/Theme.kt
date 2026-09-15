package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DeepTealPrimaryDark,
    onPrimary = DeepTealDark,
    primaryContainer = DeepTealDark,
    onPrimaryContainer = DeepTealContainer,
    secondary = WarmGoldAccentDark,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = WarmGoldContainer,
    background = BackgroundDark,
    onBackground = Color(0xFFF1F5F9),
    surface = SurfaceDark,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = DeepTealPrimary,
    onPrimary = Color.White,
    primaryContainer = DeepTealContainer,
    onPrimaryContainer = OnDeepTealContainer,
    secondary = WarmGoldAccent,
    onSecondary = Color.White,
    secondaryContainer = WarmGoldContainer,
    onSecondaryContainer = OnWarmGoldContainer,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight,
    error = StatusDangerRed,
    onError = Color.White,
    errorContainer = StatusDangerContainer,
    onErrorContainer = Color(0xFF991B1B)
  )

@Composable
fun DineFlowTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  DineFlowTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

