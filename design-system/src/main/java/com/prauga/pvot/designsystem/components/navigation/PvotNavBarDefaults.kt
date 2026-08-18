// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
}
