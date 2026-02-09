// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Visual appearance configuration for navigation bar.
 *
 * @param sizes Size configuration for the navigation bar
 * @param colors Color configuration for the navigation bar
 */
data class NavBarAppearance(
    val sizes: PvotNavBarSizes = PvotNavBarSizes(),
    val colors: PvotNavBarColors = PvotNavBarColors(
        gradient = Brush.horizontalGradient(listOf(NavBarGradientStart, NavBarGradientEnd)),
        collapsedChipColor = NavBarCollapsedChip,
        containerColor = NavBarContainer,
        iconSelectedColor = NavBarIconSelected,
        iconUnselectedColor = NavBarIconUnselected
    )
)
