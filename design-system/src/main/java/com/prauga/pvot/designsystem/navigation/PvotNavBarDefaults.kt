// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.navigation

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.prauga.pvot.designsystem.theme.PvotSpacing
import com.prauga.pvot.designsystem.theme.PvotTheme

/** Defaults for [PvotNavBar] and its items. */
public object PvotNavBarDefaults {

    /** Padding either side of an expanded item's content. */
    public val ItemHorizontalPadding: Dp = PvotSpacing.L

    /** Gap between an item's icon and its label. */
    public val IconTextGap: Dp = PvotSpacing.S

    /** Start of the brand gradient behind a selected item. */
    public val GradientStart: Color = Color(0xFF9B84FF)
    /** End of the brand gradient behind a selected item. */
    public val GradientEnd: Color = Color(0xFF6B66FF)
    /** Fill behind an unselected item. */
    public val CollapsedChipColor: Color = Color(0xFF4D4D4F)
    /** Fill behind the bar itself. */
    public val ContainerColor: Color = Color(0x1AFFFFFF)
    /** Icon and label color of a selected item. */
    public val IconSelectedColor: Color = Color.White
    /** Icon color of an unselected item. */
    public val IconUnselectedColor: Color = Color(0xFFECECEC)
    /** Ripple drawn over an item while it is pressed. */
    public val RippleColor: Color = Color.White.copy(alpha = 0.25f)

    /** The brand gradient behind a selected item. */
    public val Gradient: Brush = Brush.horizontalGradient(listOf(GradientStart, GradientEnd))

    /** Nav bar colors on the brand palette, each overridable. */
    public fun colors(
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
    public fun colors(colorScheme: ColorScheme): PvotNavBarColors = PvotNavBarColors(
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
    public fun themeColors(): PvotNavBarColors =
        if (PvotTheme.isDynamicColor) colors(MaterialTheme.colorScheme) else colors()
}
