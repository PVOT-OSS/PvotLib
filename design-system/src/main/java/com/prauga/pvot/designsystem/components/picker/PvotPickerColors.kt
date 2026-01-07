// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Color configuration for [WheelPicker], [TimePicker], and [PvotTimerPicker]. */
@Immutable
data class PvotPickerColors(
    val textColor: Color,
    val textSecondaryColor: Color,
    val selectionBackgroundColor: Color
)

val LocalPvotPickerColors = staticCompositionLocalOf {
    PvotPickerColors(
        textColor = PickerTextColor,
        textSecondaryColor = PickerTextSecondaryColor,
        selectionBackgroundColor = PickerSelectionBackground
    )
}

// Default picker colors
val PickerTextColor = Color.White
val PickerTextSecondaryColor = Color.White.copy(alpha = 0.7f)
val PickerSelectionBackground = Color.White.copy(alpha = 0.08f)