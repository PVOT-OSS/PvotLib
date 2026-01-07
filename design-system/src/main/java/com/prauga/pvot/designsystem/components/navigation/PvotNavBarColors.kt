// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Color configuration for [PvotNavBar]. */
@Immutable
data class PvotNavBarColors(
    val gradient: Brush,
    val collapsedChipColor: Color,
    val containerColor: Color,
    val iconSelectedColor: Color,
    val iconUnselectedColor: Color
)

val LocalPvotNavBarColors = staticCompositionLocalOf {
    PvotNavBarColors(
        gradient = Brush.horizontalGradient(listOf(NavBarGradientStart, NavBarGradientEnd)),
        collapsedChipColor = NavBarCollapsedChip,
        containerColor = NavBarContainer,
        iconSelectedColor = NavBarIconSelected,
        iconUnselectedColor = NavBarIconUnselected
    )
}

// Navigation bar colors
val NavBarGradientStart = Color(0xFF9B84FF)
val NavBarGradientEnd = Color(0xFF6B66FF)
val NavBarCollapsedChip = Color(0xFF4D4D4F)
val NavBarContainer = Color(0x1AFFFFFF)
val NavBarIconSelected = Color.White
val NavBarIconUnselected = Color(0xFFECECEC)
