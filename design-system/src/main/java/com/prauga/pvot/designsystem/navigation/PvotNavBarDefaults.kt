// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.navigation

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.theme.PvotTheme

/** Defaults for [PvotNavBar] and its items. */
object PvotNavBarDefaults {

    /** Padding either side of an expanded item's content. */
    val ItemHorizontalPadding = 16.dp

    /** Gap between an item's icon and its label. */
    val IconTextGap = 8.dp

    val GradientStart = Color(0xFF9B84FF)
    val GradientEnd = Color(0xFF6B66FF)
    val CollapsedChipColor = Color(0xFF4D4D4F)
    val ContainerColor = Color(0x1AFFFFFF)
    val IconSelectedColor = Color.White
    val IconUnselectedColor = Color(0xFFECECEC)
    val RippleColor = Color.White.copy(alpha = 0.25f)

    val Gradient: Brush = Brush.horizontalGradient(listOf(GradientStart, GradientEnd))

    fun colors(
        gradient: Brush = Gradient,
        collapsedChipColor: Color = CollapsedChipColor,
        containerColor: Color = ContainerColor,
        iconSelectedColor: Color = IconSelectedColor,
        iconUnselectedColor: Color = IconUnselectedColor,
        rippleColor: Color = RippleColor
    ): PvotNavBarColors = PvotNavBarColors(
        gradient = gradient,
        collapsedChipColor = collapsedChipColor,
        containerColor = containerColor,
        iconSelectedColor = iconSelectedColor,
        iconUnselectedColor = iconUnselectedColor,
        rippleColor = rippleColor
    )

    /** Nav bar colors derived from [colorScheme]. */
    fun colors(colorScheme: ColorScheme): PvotNavBarColors = PvotNavBarColors(
        gradient = Brush.horizontalGradient(listOf(colorScheme.primary, colorScheme.tertiary)),
        collapsedChipColor = colorScheme.surfaceVariant,
        containerColor = colorScheme.surface.copy(alpha = 0.1f),
        iconSelectedColor = colorScheme.onPrimary,
        iconUnselectedColor = colorScheme.onSurface.copy(alpha = 0.7f),
        rippleColor = colorScheme.onPrimary.copy(alpha = 0.25f)
    )

    /**
     * Nav bar colors derived from the theme's color scheme while dynamic color is in effect,
     * and the Pvot brand gradient otherwise.
     */
    @Composable
    fun themeColors(): PvotNavBarColors =
        if (PvotTheme.isDynamicColor) colors(MaterialTheme.colorScheme) else colors()
}
