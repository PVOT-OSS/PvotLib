// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.prauga.pvot.designsystem.components.navigation.LocalPvotNavBarColors
import com.prauga.pvot.designsystem.components.navigation.LocalPvotNavBarSizes
import com.prauga.pvot.designsystem.components.navigation.NavBarCollapsedChipDark
import com.prauga.pvot.designsystem.components.navigation.NavBarCollapsedChipLight
import com.prauga.pvot.designsystem.components.navigation.NavBarContainerDark
import com.prauga.pvot.designsystem.components.navigation.NavBarContainerLight
import com.prauga.pvot.designsystem.components.navigation.NavBarIconSelectedDark
import com.prauga.pvot.designsystem.components.navigation.NavBarIconSelectedLight
import com.prauga.pvot.designsystem.components.navigation.NavBarIconUnselectedDark
import com.prauga.pvot.designsystem.components.navigation.NavBarIconUnselectedLight
import com.prauga.pvot.designsystem.components.navigation.NavBarSelectedChipDark
import com.prauga.pvot.designsystem.components.navigation.NavBarSelectedChipLight
import com.prauga.pvot.designsystem.components.navigation.PvotNavBarColors
import com.prauga.pvot.designsystem.components.navigation.PvotNavBarSizes
import com.prauga.pvot.designsystem.components.picker.LocalPvotPickerColors
import com.prauga.pvot.designsystem.components.picker.PvotPickerColors

private val DarkColorScheme = darkColorScheme(
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

private val LightColorScheme = lightColorScheme(
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

@Composable
fun PvotAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    navBarColors: PvotNavBarColors? = null,
    navBarSizes: PvotNavBarSizes = PvotNavBarSizes(),
    pickerColors: PvotPickerColors? = null,
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

    val resolvedNavBarColors = when {
        navBarColors != null -> navBarColors
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            PvotNavBarColors(
                selectedChipColor = colorScheme.primary,
                collapsedChipColor = colorScheme.surfaceVariant,
                containerColor = colorScheme.surface,
                iconSelectedColor = colorScheme.onPrimary,
                iconUnselectedColor = colorScheme.onSurfaceVariant
            )
        }
        darkTheme -> {
            PvotNavBarColors(
                selectedChipColor = NavBarSelectedChipDark,
                collapsedChipColor = NavBarCollapsedChipDark,
                containerColor = NavBarContainerDark,
                iconSelectedColor = NavBarIconSelectedDark,
                iconUnselectedColor = NavBarIconUnselectedDark
            )
        }
        else -> {
            PvotNavBarColors(
                selectedChipColor = NavBarSelectedChipLight,
                collapsedChipColor = NavBarCollapsedChipLight,
                containerColor = NavBarContainerLight,
                iconSelectedColor = NavBarIconSelectedLight,
                iconUnselectedColor = NavBarIconUnselectedLight
            )
        }
    }

    val resolvedPickerColors = pickerColors ?: PvotPickerColors(
        textColor = colorScheme.onBackground,
        textSecondaryColor = colorScheme.onBackground.copy(alpha = 0.7f),
        selectionBackgroundColor = colorScheme.onBackground.copy(alpha = 0.1f)
    )

    CompositionLocalProvider(
        LocalPvotNavBarColors provides resolvedNavBarColors,
        LocalPvotNavBarSizes provides navBarSizes,
        LocalPvotPickerColors provides resolvedPickerColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PvotTypography,
            content = content
        )
    }
}
