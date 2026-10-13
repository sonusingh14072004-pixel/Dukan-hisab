package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreenPrimaryDark,
    onPrimary = EmeraldGreenOnPrimaryDark,
    primaryContainer = EmeraldGreenContainerDark,
    onPrimaryContainer = EmeraldGreenOnContainerDark,
    secondary = AmberGoldSecondaryDark,
    onSecondary = AmberGoldOnSecondaryDark,
    secondaryContainer = AmberGoldContainerDark,
    onSecondaryContainer = AmberGoldOnContainerDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreenPrimary,
    onPrimary = EmeraldGreenOnPrimary,
    primaryContainer = EmeraldGreenContainer,
    onPrimaryContainer = EmeraldGreenOnContainer,
    secondary = AmberGoldSecondary,
    onSecondary = AmberGoldOnSecondary,
    secondaryContainer = AmberGoldContainer,
    onSecondaryContainer = AmberGoldOnContainer,
    tertiary = TrustBlueTertiary,
    onTertiary = TrustBlueOnTertiary,
    tertiaryContainer = TrustBlueContainer,
    onTertiaryContainer = TrustBlueOnContainer,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep merchant theme prominent by default
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
