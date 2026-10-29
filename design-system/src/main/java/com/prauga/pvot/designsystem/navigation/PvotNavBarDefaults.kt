// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.navigation

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** Defaults for [PvotNavBar] and its items. */
public object PvotNavBarDefaults {

    /** Padding either side of an expanded item's content. */
    public val ItemHorizontalPadding: Dp = PvotSpacing.L

    /** Gap between an item's icon and its label. */
    public val IconTextGap: Dp = PvotSpacing.S

    /** Start of the brand gradient behind a selected item. */
    public val GradientStart: Color = Color(0xFF7A6BFF)
    /** End of the brand gradient behind a selected item. */
    public val GradientEnd: Color = Color(0xFF5A4BFF)
    /** Fill behind an unselected item; none, so only the selected item is marked. */
    public val CollapsedChipColor: Color = Color.Transparent
    /** Fill behind the bar itself. */
    public val ContainerColor: Color = Color(0xFF231F36)
    /** Icon and label color of a selected item. */
    public val IconSelectedColor: Color = Color.White
    /** Icon color of an unselected item. */
    public val IconUnselectedColor: Color = Color(0xFFB9B3D6)
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

    /** Nav bar colors derived from [colorScheme]: a solid primary pill on a tinted surface bar. */
    public fun colors(colorScheme: ColorScheme): PvotNavBarColors = PvotNavBarColors(
        gradient = SolidColor(colorScheme.primary),
        collapsedChipColor = Color.Transparent,
        containerColor = colorScheme.surfaceVariant,
        iconSelectedColor = colorScheme.onPrimary,
        iconUnselectedColor = colorScheme.onSurfaceVariant,
        rippleColor = colorScheme.primary.copy(alpha = 0.16f)
    )

    /** Nav bar colors derived from the theme's color scheme, so they follow light, dark and dynamic color. */
    @Composable
    public fun themeColors(): PvotNavBarColors = colors(MaterialTheme.colorScheme)

    /**
     * Nav bar colors for a bar drawn as glass: the [themeColors] selected item over a clear bar,
     * with unselected icons in the theme's content color and no chip behind them.
     */
    @Composable
    public fun glassColors(): PvotNavBarColors {
        val onSurface = MaterialTheme.colorScheme.onSurface
        return themeColors().copy(
            collapsedChipColor = Color.Transparent,
            containerColor = Color.Transparent,
            iconUnselectedColor = onSurface.copy(alpha = 0.8f),
            rippleColor = onSurface.copy(alpha = 0.12f)
        )
    }
}
