// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.legacy

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// The palette PvotLib shipped before the new UI, kept so "Enable New UI" can switch back to it.
private val LegacyPrimaryLight: Color = Color(0xFF6B66FF)
private val LegacyOnPrimaryLight: Color = Color(0xFFFFFFFF)
private val LegacyPrimaryContainerLight: Color = Color(0xFFE5DEFF)
private val LegacyOnPrimaryContainerLight: Color = Color(0xFF1F0061)
private val LegacyPrimaryDark: Color = Color(0xFFCBBEFF)
private val LegacyOnPrimaryDark: Color = Color(0xFF352388)
private val LegacyPrimaryContainerDark: Color = Color(0xFF4D3D9E)
private val LegacyOnPrimaryContainerDark: Color = Color(0xFFE5DEFF)
private val LegacySecondaryLight: Color = Color(0xFF605B71)
private val LegacyOnSecondaryLight: Color = Color(0xFFFFFFFF)
private val LegacySecondaryContainerLight: Color = Color(0xFFE6DFF9)
private val LegacyOnSecondaryContainerLight: Color = Color(0xFF1C192B)
private val LegacySecondaryDark: Color = Color(0xFFCAC3DC)
private val LegacyOnSecondaryDark: Color = Color(0xFF322E41)
private val LegacySecondaryContainerDark: Color = Color(0xFF494458)
private val LegacyOnSecondaryContainerDark: Color = Color(0xFFE6DFF9)
private val LegacyTertiaryLight: Color = Color(0xFF7D5260)
private val LegacyOnTertiaryLight: Color = Color(0xFFFFFFFF)
private val LegacyTertiaryContainerLight: Color = Color(0xFFFFD9E2)
private val LegacyOnTertiaryContainerLight: Color = Color(0xFF31101D)
private val LegacyTertiaryDark: Color = Color(0xFFEFB8C8)
private val LegacyOnTertiaryDark: Color = Color(0xFF4A2532)
private val LegacyTertiaryContainerDark: Color = Color(0xFF633B48)
private val LegacyOnTertiaryContainerDark: Color = Color(0xFFFFD9E2)
private val LegacyErrorLight: Color = Color(0xFFBA1A1A)
private val LegacyOnErrorLight: Color = Color(0xFFFFFFFF)
private val LegacyErrorContainerLight: Color = Color(0xFFFFDAD6)
private val LegacyOnErrorContainerLight: Color = Color(0xFF410002)
private val LegacyErrorDark: Color = Color(0xFFFFB4AB)
private val LegacyOnErrorDark: Color = Color(0xFF690005)
private val LegacyErrorContainerDark: Color = Color(0xFF93000A)
private val LegacyOnErrorContainerDark: Color = Color(0xFFFFDAD6)
private val LegacyBackgroundLight: Color = Color(0xFFFFFFFF)
private val LegacyOnBackgroundLight: Color = Color(0xFF1C1B1F)
private val LegacySurfaceLight: Color = Color(0xFFFFFFFF)
private val LegacyOnSurfaceLight: Color = Color(0xFF1C1B1F)
private val LegacySurfaceVariantLight: Color = Color(0xFFE7E0EC)
private val LegacyOnSurfaceVariantLight: Color = Color(0xFF49454E)
private val LegacyBackgroundDark: Color = Color(0xFF000000)
private val LegacyOnBackgroundDark: Color = Color(0xFFE6E1E6)
private val LegacySurfaceDark: Color = Color(0xFF000000)
private val LegacyOnSurfaceDark: Color = Color(0xFFE6E1E6)
private val LegacySurfaceVariantDark: Color = Color(0xFF49454E)
private val LegacyOnSurfaceVariantDark: Color = Color(0xFFCAC4CF)
private val LegacyOutlineLight: Color = Color(0xFF7A757F)
private val LegacyOutlineVariantLight: Color = Color(0xFFCAC4CF)
private val LegacyOutlineDark: Color = Color(0xFF948F99)
private val LegacyOutlineVariantDark: Color = Color(0xFF49454E)
private val LegacyInverseSurfaceLight: Color = Color(0xFF313033)
private val LegacyInverseOnSurfaceLight: Color = Color(0xFFF4EFF4)
private val LegacyInversePrimaryLight: Color = Color(0xFFCBBEFF)
private val LegacyInverseSurfaceDark: Color = Color(0xFFE6E1E6)
private val LegacyInverseOnSurfaceDark: Color = Color(0xFF313033)
private val LegacyInversePrimaryDark: Color = Color(0xFF6750A4)
private val LegacyScrim: Color = Color(0xFF000000)

internal val LegacyDarkColorScheme: ColorScheme = darkColorScheme(
    primary = LegacyPrimaryDark,
    onPrimary = LegacyOnPrimaryDark,
    primaryContainer = LegacyPrimaryContainerDark,
    onPrimaryContainer = LegacyOnPrimaryContainerDark,
    secondary = LegacySecondaryDark,
    onSecondary = LegacyOnSecondaryDark,
    secondaryContainer = LegacySecondaryContainerDark,
    onSecondaryContainer = LegacyOnSecondaryContainerDark,
    tertiary = LegacyTertiaryDark,
    onTertiary = LegacyOnTertiaryDark,
    tertiaryContainer = LegacyTertiaryContainerDark,
    onTertiaryContainer = LegacyOnTertiaryContainerDark,
    error = LegacyErrorDark,
    onError = LegacyOnErrorDark,
    errorContainer = LegacyErrorContainerDark,
    onErrorContainer = LegacyOnErrorContainerDark,
    background = LegacyBackgroundDark,
    onBackground = LegacyOnBackgroundDark,
    surface = LegacySurfaceDark,
    onSurface = LegacyOnSurfaceDark,
    surfaceVariant = LegacySurfaceVariantDark,
    onSurfaceVariant = LegacyOnSurfaceVariantDark,
    outline = LegacyOutlineDark,
    outlineVariant = LegacyOutlineVariantDark,
    inverseSurface = LegacyInverseSurfaceDark,
    inverseOnSurface = LegacyInverseOnSurfaceDark,
    inversePrimary = LegacyInversePrimaryDark,
    scrim = LegacyScrim
)

internal val LegacyLightColorScheme: ColorScheme = lightColorScheme(
    primary = LegacyPrimaryLight,
    onPrimary = LegacyOnPrimaryLight,
    primaryContainer = LegacyPrimaryContainerLight,
    onPrimaryContainer = LegacyOnPrimaryContainerLight,
    secondary = LegacySecondaryLight,
    onSecondary = LegacyOnSecondaryLight,
    secondaryContainer = LegacySecondaryContainerLight,
    onSecondaryContainer = LegacyOnSecondaryContainerLight,
    tertiary = LegacyTertiaryLight,
    onTertiary = LegacyOnTertiaryLight,
    tertiaryContainer = LegacyTertiaryContainerLight,
    onTertiaryContainer = LegacyOnTertiaryContainerLight,
    error = LegacyErrorLight,
    onError = LegacyOnErrorLight,
    errorContainer = LegacyErrorContainerLight,
    onErrorContainer = LegacyOnErrorContainerLight,
    background = LegacyBackgroundLight,
    onBackground = LegacyOnBackgroundLight,
    surface = LegacySurfaceLight,
    onSurface = LegacyOnSurfaceLight,
    surfaceVariant = LegacySurfaceVariantLight,
    onSurfaceVariant = LegacyOnSurfaceVariantLight,
    outline = LegacyOutlineLight,
    outlineVariant = LegacyOutlineVariantLight,
    inverseSurface = LegacyInverseSurfaceLight,
    inverseOnSurface = LegacyInverseOnSurfaceLight,
    inversePrimary = LegacyInversePrimaryLight,
    scrim = LegacyScrim
)
