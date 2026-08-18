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

object PvotTheme {
    val navBarColors: PvotNavBarColors
        @Composable get() = LocalPvotNavBarColors.current

    val navBarSizes: PvotNavBarSizes
        @Composable get() = LocalPvotNavBarSizes.current

    val pickerColors: PvotPickerColors
        @Composable get() = LocalPvotPickerColors.current
}

object PvotThemeDefaults {

    @Composable
    fun colorScheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        dynamicColor: Boolean = false
    ): ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> PvotDarkColorScheme
        else -> PvotLightColorScheme
    }

    fun navBarColors(colorScheme: ColorScheme) = PvotNavBarColors(
        gradient = Brush.horizontalGradient(listOf(colorScheme.primary, colorScheme.tertiary)),
        collapsedChipColor = colorScheme.surfaceVariant,
        containerColor = colorScheme.surface.copy(alpha = 0.1f),
        iconSelectedColor = colorScheme.onPrimary,
        iconUnselectedColor = colorScheme.onSurface.copy(alpha = 0.7f)
    )

    fun pickerColors(colorScheme: ColorScheme) = PvotPickerColors(
        textColor = colorScheme.onBackground,
        textSecondaryColor = colorScheme.onBackground.copy(alpha = 0.7f),
        selectionBackgroundColor = colorScheme.onBackground.copy(alpha = 0.1f)
    )
}

@Composable
fun PvotAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    colorScheme: ColorScheme = PvotThemeDefaults.colorScheme(darkTheme, dynamicColor),
    typography: Typography = PvotTypography,
    navBarColors: PvotNavBarColors = PvotTheme.navBarColors,
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
