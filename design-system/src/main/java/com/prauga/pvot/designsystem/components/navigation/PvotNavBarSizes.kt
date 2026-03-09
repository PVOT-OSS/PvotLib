// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Size configuration for [PvotNavBar].
 */
@Immutable
data class PvotNavBarSizes(
    val barHeight: Dp = 64.dp,
    val barWidth: Dp? = null,
    val fillWidth: Boolean = false,
    val collapsedItemSize: Dp = 48.dp,
    val expandedWidth: Dp = Dp.Unspecified,
    val minExpandedWidth: Dp = Dp.Unspecified,
    val maxExpandedWidth: Dp = 200.dp,
    val cornerRadius: Dp = 32.dp,
    val itemCornerRadius: Dp = 24.dp,
    val collapsedIconSize: Dp = 20.dp,
    val expandedIconSize: Dp = 20.dp,
    val labelFontSize: TextUnit = 14.sp,
    val horizontalPadding: Dp = 16.dp,
    val contentPaddingHorizontal: Dp = 8.dp,
    val itemSpacing: Dp = 8.dp
)

val LocalPvotNavBarSizes = staticCompositionLocalOf { PvotNavBarSizes() }
