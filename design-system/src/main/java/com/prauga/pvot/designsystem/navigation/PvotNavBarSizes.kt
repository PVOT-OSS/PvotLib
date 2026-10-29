// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.navigation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prauga.pvot.designsystem.theme.PvotSpacing

/**
 * Size configuration for [PvotNavBar].
 *
 * For expanded item width, use either:
 * - [expandedWidth] for fixed width (bypasses measurement)
 * - [minExpandedWidth]/[maxExpandedWidth] for intrinsic sizing with constraints
 *
 * @param barHeight Height of the bar itself
 * @param barWidth Fixed bar width, or null to fall back to [fillWidth]
 * @param fillWidth Whether the bar spans its parent, ignored when [barWidth] is set
 * @param collapsedItemSize Diameter of an unselected item, and the height of every item
 * @param expandedWidth Fixed width for a selected item, or [Dp.Unspecified] to measure it
 * @param minExpandedWidth Floor for a measured item width, or [Dp.Unspecified] for none
 * @param maxExpandedWidth Ceiling for a measured item width, or [Dp.Unspecified] for none
 * @param barShadowElevation Elevation of the bar's drop shadow
 * @param cornerRadius Corner radius of the bar
 * @param itemCornerRadius Corner radius of a selected item
 * @param collapsedIconSize Icon size while unselected
 * @param expandedIconSize Icon size while selected
 * @param labelFontSize Font size of a selected item's label
 * @param horizontalPadding Padding either side of the bar
 * @param contentPaddingHorizontal Padding either side of the item row, inside the bar
 * @param itemSpacing Gap between adjacent items
 */
@Immutable
public data class PvotNavBarSizes(
    val barHeight: Dp = 72.dp,
    val barWidth: Dp? = null,
    val fillWidth: Boolean = false,
    val collapsedItemSize: Dp = 56.dp,
    val expandedWidth: Dp = Dp.Unspecified,
    val minExpandedWidth: Dp = Dp.Unspecified,
    val maxExpandedWidth: Dp = 200.dp,
    val barShadowElevation: Dp = 18.dp,
    val cornerRadius: Dp = 100.dp,
    val itemCornerRadius: Dp = 28.dp,
    val collapsedIconSize: Dp = 22.dp,
    val expandedIconSize: Dp = 22.dp,
    val labelFontSize: TextUnit = 16.sp,
    val horizontalPadding: Dp = 22.dp,
    val contentPaddingHorizontal: Dp = 9.dp,
    val itemSpacing: Dp = PvotSpacing.XS
)

/** The [PvotNavBarSizes] in scope, read by PvotNavBar and PvotScreen for their defaults. */
public val LocalPvotNavBarSizes: ProvidableCompositionLocal<PvotNavBarSizes> = staticCompositionLocalOf { PvotNavBarSizes() }
