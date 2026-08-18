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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import com.prauga.pvot.designsystem.components.navigation.LocalPvotNavBarColors
import com.prauga.pvot.designsystem.components.navigation.LocalPvotNavBarSizes
import com.prauga.pvot.designsystem.components.navigation.PvotNavBarColors
import com.prauga.pvot.designsystem.components.navigation.PvotNavBarSizes
import com.prauga.pvot.designsystem.components.picker.LocalPvotPickerColors
import com.prauga.pvot.designsystem.components.picker.PvotPickerColors

/** The Pvot palette arranged for a dark background. */
val PvotDarkColorScheme = darkColorScheme(
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
val PvotLightColorScheme = lightColorScheme(
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
object PvotTheme {
    val navBarColors: PvotNavBarColors
        @Composable get() = LocalPvotNavBarColors.current

    val navBarSizes: PvotNavBarSizes
        @Composable get() = LocalPvotNavBarSizes.current

    val pickerColors: PvotPickerColors
        @Composable get() = LocalPvotPickerColors.current
}

/** Defaults for [PvotAppTheme]. */
object PvotThemeDefaults {

    /** Whether the platform can supply a dynamic color scheme. */
    val isDynamicColorSupported: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    /** Whether [dynamicColor] will actually take effect on this device. */
    fun usingDynamicColor(dynamicColor: Boolean): Boolean =
        dynamicColor && isDynamicColorSupported

    /**
     * The wallpaper-derived scheme when [dynamicColor] is on and the platform
     * supports it, and the Pvot palette otherwise.
     *
     * @param darkTheme Whether to resolve the dark arrangement
     * @param dynamicColor Whether to prefer the platform's dynamic colors
     */
    @Composable
    fun colorScheme(
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

    /**
     * Nav bar colors derived from [colorScheme] while dynamic color is in effect, and the
     * Pvot brand gradient otherwise.
     */
    @Composable
    fun navBarColors(colorScheme: ColorScheme, dynamicColor: Boolean): PvotNavBarColors =
        if (usingDynamicColor(dynamicColor)) navBarColors(colorScheme) else PvotTheme.navBarColors

    /** Nav bar colors derived from [colorScheme]. */
    fun navBarColors(colorScheme: ColorScheme) = PvotNavBarColors(
        gradient = Brush.horizontalGradient(listOf(colorScheme.primary, colorScheme.tertiary)),
        collapsedChipColor = colorScheme.surfaceVariant,
        containerColor = colorScheme.surface.copy(alpha = 0.1f),
        iconSelectedColor = colorScheme.onPrimary,
        iconUnselectedColor = colorScheme.onSurface.copy(alpha = 0.7f),
        rippleColor = colorScheme.onPrimary.copy(alpha = 0.25f)
    )

    /** Picker colors derived from [colorScheme]. */
    fun pickerColors(colorScheme: ColorScheme) = PvotPickerColors(
        textColor = colorScheme.onBackground,
        textSecondaryColor = colorScheme.onBackground.copy(alpha = 0.7f),
        selectionBackgroundColor = colorScheme.onBackground.copy(alpha = 0.1f)
    )
}

/**
 * Applies the Pvot design system to [content].
 *
 * Wraps [MaterialTheme] and additionally provides the nav bar and picker
 * configuration that Pvot components read for their defaults. Every value is
 * overridable, so an app can adopt the whole system or take it a piece at a time:
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
 * @param navBarColors Colors that PvotNavBar reads for its defaults
 * @param navBarSizes Sizes that PvotNavBar and PvotScreen read for their defaults
 * @param pickerColors Colors that the pickers read for their defaults
 */
@Composable
fun PvotAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    colorScheme: ColorScheme = PvotThemeDefaults.colorScheme(darkTheme, dynamicColor),
    typography: Typography = PvotTypography,
    navBarColors: PvotNavBarColors = PvotThemeDefaults.navBarColors(colorScheme, dynamicColor),
    navBarSizes: PvotNavBarSizes = PvotNavBarSizes(),
    pickerColors: PvotPickerColors = PvotThemeDefaults.pickerColors(colorScheme),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalPvotNavBarColors provides navBarColors,
        LocalPvotNavBarSizes provides navBarSizes,
        LocalPvotPickerColors provides pickerColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}
