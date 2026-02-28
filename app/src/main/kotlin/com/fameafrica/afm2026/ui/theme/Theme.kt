package com.fameafrica.afm2026.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * FAME Africa™ Material 3 Theme
 * Dark theme by default - premium, modern sports feel
 */
@Composable
fun AFM2026Theme(
    darkTheme: Boolean = true, // Force dark by default - it's premium
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = FameColors.PitchGreen,
            onPrimary = FameColors.WarmIvory,
            primaryContainer = FameColors.SurfaceDark,
            onPrimaryContainer = FameColors.WarmIvory,

            secondary = FameColors.ChampionsGold,
            onSecondary = FameColors.StadiumBlack,
            secondaryContainer = FameColors.SurfaceMedium,
            onSecondaryContainer = FameColors.WarmIvory,

            tertiary = FameColors.AfroSunOrange,
            onTertiary = FameColors.StadiumBlack,
            tertiaryContainer = FameColors.SurfaceLight,
            onTertiaryContainer = FameColors.WarmIvory,

            background = FameColors.StadiumBlack,
            onBackground = FameColors.WarmIvory,

            surface = FameColors.SurfaceDark,
            onSurface = FameColors.WarmIvory,
            surfaceVariant = FameColors.SurfaceMedium,
            onSurfaceVariant = FameColors.MutedParchment,

            error = FameColors.KenteRed,
            onError = FameColors.WarmIvory,
            errorContainer = FameColors.KenteRed.copy(alpha = 0.2f),
            onErrorContainer = FameColors.KenteRed,

            outline = FameColors.BaobabBrown.copy(alpha = 0.3f),
            outlineVariant = FameColors.BaobabBrown.copy(alpha = 0.1f)
        )
    } else {
        // Light theme - optional, warm sand background
        lightColorScheme(
            primary = FameColors.PitchGreen,
            onPrimary = Color.White,
            primaryContainer = FameColors.PitchGreen.copy(alpha = 0.1f),
            onPrimaryContainer = FameColors.PitchGreen,

            secondary = FameColors.ChampionsGold,
            onSecondary = Color.Black,
            secondaryContainer = FameColors.ChampionsGold.copy(alpha = 0.1f),
            onSecondaryContainer = FameColors.BaobabBrown,

            tertiary = FameColors.AfroSunOrange,
            onTertiary = Color.Black,
            tertiaryContainer = FameColors.AfroSunOrange.copy(alpha = 0.1f),
            onTertiaryContainer = FameColors.AfroSunOrange,

            background = FameColors.LightSand,
            onBackground = FameColors.BaobabBrown,

            surface = FameColors.LightCard,
            onSurface = FameColors.BaobabBrown,
            surfaceVariant = FameColors.LightCard.copy(alpha = 0.7f),
            onSurfaceVariant = FameColors.BaobabBrown.copy(alpha = 0.7f),

            error = FameColors.KenteRed,
            onError = Color.White,
            errorContainer = FameColors.KenteRed.copy(alpha = 0.1f),
            onErrorContainer = FameColors.KenteRed,

            outline = FameColors.BaobabBrown.copy(alpha = 0.2f),
            outlineVariant = FameColors.BaobabBrown.copy(alpha = 0.1f)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FameTypography.toMaterial3Typography(),
        shapes = FameShapes,
        content = content
    )
}

// Theme wrapper for previews
@Composable
fun FameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
        typography = MaterialTheme.typography,
        content = content
    )
}
