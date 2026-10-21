package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DujaEmeraldDark,
    onPrimary = DujaEmeraldOnDark,
    primaryContainer = DujaEmeraldDarkContainer,
    onPrimaryContainer = DujaEmeraldOnDarkContainer,
    secondary = DujaGoldDark,
    onSecondary = DujaGoldOnDark,
    secondaryContainer = DujaGoldDarkContainer,
    onSecondaryContainer = DujaGoldOnDarkContainer,
    background = DujaBackgroundDark,
    onBackground = DujaOnBackgroundDark,
    surface = DujaSurfaceDark,
    onSurface = DujaOnSurfaceDark,
    surfaceVariant = DujaSurfaceVariantDark,
    onSurfaceVariant = DujaOnSurfaceVariantDark,
    outline = DujaOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = DujaEmeraldPrimary,
    onPrimary = DujaEmeraldOnPrimary,
    primaryContainer = DujaEmeraldPrimaryContainer,
    onPrimaryContainer = DujaEmeraldOnPrimaryContainer,
    secondary = DujaGoldSecondary,
    onSecondary = DujaGoldOnSecondary,
    secondaryContainer = DujaGoldSecondaryContainer,
    onSecondaryContainer = DujaGoldOnSecondaryContainer,
    tertiary = DujaTealTertiary,
    onTertiary = DujaTealOnTertiary,
    tertiaryContainer = DujaTealTertiaryContainer,
    onTertiaryContainer = DujaTealOnTertiaryContainer,
    background = DujaBackgroundLight,
    onBackground = DujaOnBackgroundLight,
    surface = DujaSurfaceLight,
    onSurface = DujaOnSurfaceLight,
    surfaceVariant = DujaSurfaceVariantLight,
    onSurfaceVariant = DujaOnSurfaceVariantLight,
    outline = DujaOutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep Islamic theme identity active
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
