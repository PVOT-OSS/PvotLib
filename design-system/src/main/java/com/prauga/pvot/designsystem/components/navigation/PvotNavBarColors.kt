// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Color configuration for [PvotNavBar]. */
@Immutable
data class PvotNavBarColors(
    val selectedChipColor: Color,
    val collapsedChipColor: Color,
    val containerColor: Color,
    val iconSelectedColor: Color,
    val iconUnselectedColor: Color
)

// Dark Theme Palette
val NavBarSelectedChipDark = Color(0xFF8A79FF)
val NavBarCollapsedChipDark = Color(0xFF434247)
val NavBarContainerDark = Color(0xFF2B2931)
val NavBarIconSelectedDark = Color.White
val NavBarIconUnselectedDark = Color(0xFFECECEC)

// Light Theme Palette
val NavBarSelectedChipLight = Color(0xFF8A79FF)
val NavBarCollapsedChipLight = Color(0xFFE0E0E0)
val NavBarContainerLight = Color(0xFFF3F3F7)
val NavBarIconSelectedLight = Color.White
val NavBarIconUnselectedLight = Color(0xFF424242)

val LocalPvotNavBarColors = staticCompositionLocalOf {
    PvotNavBarColors(
        selectedChipColor = NavBarSelectedChipDark,
        collapsedChipColor = NavBarCollapsedChipDark,
        containerColor = NavBarContainerDark,
        iconSelectedColor = NavBarIconSelectedDark,
        iconUnselectedColor = NavBarIconUnselectedDark
    )
}
