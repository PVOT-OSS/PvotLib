// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker.internal

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.components.picker.PvotPickerColors

/**
 * Visual appearance configuration for wheel picker.
 *
 * @param itemHeight Height of each item in the wheel
 * @param visibleItemsCount Number of items visible at once
 * @param wheelWidth Width of the wheel
 * @param colors Color configuration for the picker
 */
internal data class WheelAppearance(
    val itemHeight: Dp = 40.dp,
    val visibleItemsCount: Int = 5,
    val wheelWidth: Dp = 110.dp,
    val colors: PvotPickerColors = PvotPickerColors(
        textColor = Color.White,
        textSecondaryColor = Color.White.copy(alpha = 0.7f),
        selectionBackgroundColor = Color.White.copy(alpha = 0.08f)
    )
)
