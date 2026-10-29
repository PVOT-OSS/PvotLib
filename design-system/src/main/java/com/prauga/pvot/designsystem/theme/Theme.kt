// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

/** The Pvot palette arranged for a dark background. */
public val PvotDarkColorScheme: ColorScheme = darkColorScheme(
    primary = PvotPrimaryDark,
    onPrimary = PvotOnPrimaryDark,
    primaryContainer = PvotPrimaryContainerDark,
    onPrimaryContainer = PvotOnPrimaryContainerDark,
    secondary = PvotSecondaryDark,
    onSecondary = PvotOnSecondaryDark,
    secondaryContainer = PvotSecondaryContainerDark,
    onSecondaryContainer = PvotOnSecondaryContainerDark,
    tertiary = PvotTertiaryDark,
    onTertiary = PvotOnTertiaryDark,
    tertiaryContainer = PvotTertiaryContainerDark,
    onTertiaryContainer = PvotOnTertiaryContainerDark,
    error = PvotErrorDark,
    onError = PvotOnErrorDark,
    errorContainer = PvotErrorContainerDark,
    onErrorContainer = PvotOnErrorContainerDark,
    background = PvotBackgroundDark,
    onBackground = PvotOnBackgroundDark,
    surface = PvotSurfaceDark,
    onSurface = PvotOnSurfaceDark,
    surfaceVariant = PvotSurfaceVariantDark,
    onSurfaceVariant = PvotOnSurfaceVariantDark,
    outline = PvotOutlineDark,
    outlineVariant = PvotOutlineVariantDark,
    inverseSurface = PvotInverseSurfaceDark,
    inverseOnSurface = PvotInverseOnSurfaceDark,
    inversePrimary = PvotInversePrimaryDark,
    scrim = PvotScrim
)

/** The Pvot palette arranged for a light background. */
public val PvotLightColorScheme: ColorScheme = lightColorScheme(
    primary = PvotPrimaryLight,
    onPrimary = PvotOnPrimaryLight,
    primaryContainer = PvotPrimaryContainerLight,
    onPrimaryContainer = PvotOnPrimaryContainerLight,
    secondary = PvotSecondaryLight,
    onSecondary = PvotOnSecondaryLight,
    secondaryContainer = PvotSecondaryContainerLight,
    onSecondaryContainer = PvotOnSecondaryContainerLight,
    tertiary = PvotTertiaryLight,
    onTertiary = PvotOnTertiaryLight,
    tertiaryContainer = PvotTertiaryContainerLight,
    onTertiaryContainer = PvotOnTertiaryContainerLight,
    error = PvotErrorLight,
    onError = PvotOnErrorLight,
    errorContainer = PvotErrorContainerLight,
    onErrorContainer = PvotOnErrorContainerLight,
    background = PvotBackgroundLight,
    onBackground = PvotOnBackgroundLight,
    surface = PvotSurfaceLight,
    onSurface = PvotOnSurfaceLight,
    surfaceVariant = PvotSurfaceVariantLight,
    onSurfaceVariant = PvotOnSurfaceVariantLight,
    outline = PvotOutlineLight,
    outlineVariant = PvotOutlineVariantLight,
    inverseSurface = PvotInverseSurfaceLight,
    inverseOnSurface = PvotInverseOnSurfaceLight,
    inversePrimary = PvotInversePrimaryLight,
    scrim = PvotScrim
)

/**
 * Accessor for the Pvot design system values in scope, alongside [MaterialTheme].
 *
 * Component parameters default to these, so read them here when you need a value
 * outside a component, or to derive one.
 */
public object PvotTheme {
    /** Whether the platform's dynamic colors are in effect. */
    public val isDynamicColor: Boolean
        @Composable get() = LocalPvotDynamicColor.current
}

internal val LocalPvotDynamicColor = staticCompositionLocalOf { false }

/** Defaults for [PvotAppTheme]. */
public object PvotThemeDefaults {

    /** Whether the platform can supply a dynamic color scheme. */
    public val isDynamicColorSupported: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    /** Whether [dynamicColor] will actually take effect on this device. */
    public fun usingDynamicColor(dynamicColor: Boolean): Boolean =
        dynamicColor && isDynamicColorSupported

    /**
     * The wallpaper-derived scheme when [dynamicColor] is on and the platform
     * supports it, and the Pvot palette otherwise.
     *
     * @param darkTheme Whether to resolve the dark arrangement
     * @param dynamicColor Whether to prefer the platform's dynamic colors
     */
    @Composable
    public fun colorScheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        dynamicColor: Boolean = false
    ): ColorScheme = when {
        usingDynamicColor(dynamicColor) -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> PvotDarkColorScheme
        else -> PvotLightColorScheme
    }
}

/**
 * Applies the Pvot design system to [content].
 *
 * Wraps [MaterialTheme] and additionally provides whether dynamic color is in effect,
 * which Pvot components read for their defaults. Every value is overridable, so an
 * app can adopt the whole system or take it a piece at a time:
 *
 * ```
 * PvotAppTheme(dynamicColor = true) {
 *     Scaffold(bottomBar = { PvotNavBar(selectedTab, onTabClick, tabs) }) { ... }
 * }
 * ```
 *
 * @param darkTheme Whether to use the dark arrangement of the palette
 * @param dynamicColor Whether to prefer the platform's wallpaper-derived colors
 * @param colorScheme The Material color scheme to apply
 * @param typography The Material type scale to apply
 */
@Composable
public fun PvotAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    colorScheme: ColorScheme = PvotThemeDefaults.colorScheme(darkTheme, dynamicColor),
    typography: Typography = PvotTypography,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalPvotDynamicColor provides PvotThemeDefaults.usingDynamicColor(dynamicColor)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}
