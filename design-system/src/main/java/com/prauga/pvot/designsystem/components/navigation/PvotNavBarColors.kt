// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Color configuration for [PvotNavBar].
 *
 * @param gradient Fill behind a selected item
 * @param collapsedChipColor Fill behind an unselected item
 * @param containerColor Fill behind the bar itself
 * @param iconSelectedColor Icon and label color of a selected item
 * @param iconUnselectedColor Icon color of an unselected item
 * @param rippleColor Ripple drawn over an item while it is pressed
 */
@Immutable
data class PvotNavBarColors(
    val gradient: Brush,
    val collapsedChipColor: Color,
    val containerColor: Color,
    val iconSelectedColor: Color,
    val iconUnselectedColor: Color,
    val rippleColor: Color
)

/** The [PvotNavBarColors] in scope. Provided by PvotAppTheme, read via PvotTheme.navBarColors. */
val LocalPvotNavBarColors = staticCompositionLocalOf { PvotNavBarDefaults.colors() }
