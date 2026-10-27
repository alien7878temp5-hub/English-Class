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

private val DarkColorScheme = darkColorScheme(
    primary = RoyalGold,
    onPrimary = BritishNavyDark,
    primaryContainer = BritishNavyLight,
    onPrimaryContainer = RoyalGoldLight,
    secondary = RoyalGoldLight,
    onSecondary = BritishNavyDark,
    secondaryContainer = BritishNavy,
    onSecondaryContainer = BritishTextLight,
    tertiary = BritishCrimsonLight,
    onTertiary = BritishTextLight,
    background = BritishNavyDark,
    onBackground = BritishTextLight,
    surface = BritishSurfaceDark,
    onSurface = BritishTextLight,
    surfaceVariant = BritishSurfaceCardDark,
    onSurfaceVariant = BritishTextLightMuted
)

private val LightColorScheme = lightColorScheme(
    primary = OxfordBlue,
    onPrimary = BritishParchment,
    primaryContainer = RoyalGoldLight,
    onPrimaryContainer = BritishNavyDark,
    secondary = RoyalGold,
    onSecondary = BritishNavyDark,
    secondaryContainer = BritishParchment,
    onSecondaryContainer = BritishNavyDark,
    tertiary = BritishCrimson,
    onTertiary = BritishParchment,
    background = BritishParchment,
    onBackground = BritishTextDark,
    surface = BritishParchmentCard,
    onSurface = BritishTextDark,
    surfaceVariant = Color(0xFFEBE6DC),
    onSurfaceVariant = BritishTextMuted
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep intentional British styling by default
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
