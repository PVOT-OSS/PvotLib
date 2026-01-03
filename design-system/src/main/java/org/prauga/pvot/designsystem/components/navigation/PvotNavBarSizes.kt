// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.pvot.designsystem.components.navigation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Size configuration for [PvotNavBar].
 *
 * For expanded item width, use either:
 * - [expandedWidth] for fixed width (bypasses measurement)
 * - [minExpandedWidth]/[maxExpandedWidth] for intrinsic sizing with constraints
 */
@Immutable
data class PvotNavBarSizes(
    val barHeight: Dp = 72.dp,
    val barWidth: Dp? = null,
    val fillWidth: Boolean = false,
    val collapsedItemSize: Dp = 56.dp,
    val expandedWidth: Dp = Dp.Unspecified,
    val minExpandedWidth: Dp = Dp.Unspecified,
    val maxExpandedWidth: Dp = 200.dp,
    val cornerRadius: Dp = 100.dp,
    val itemCornerRadius: Dp = 28.dp,
    val collapsedIconSize: Dp = 22.dp,
    val expandedIconSize: Dp = 22.dp,
    val labelFontSize: TextUnit = 16.sp,
    val horizontalPadding: Dp = 22.dp,
    val contentPaddingHorizontal: Dp = 9.dp,
    val itemSpacing: Dp = 4.dp
)

val LocalPvotNavBarSizes = staticCompositionLocalOf { PvotNavBarSizes() }
