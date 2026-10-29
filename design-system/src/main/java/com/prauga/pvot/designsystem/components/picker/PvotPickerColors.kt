// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Color configuration for [PvotClockPicker] and [PvotDurationPicker].
 *
 * @param textColor Color of a wheel's value
 * @param textSecondaryColor Color of a wheel's suffix
 * @param selectionBackgroundColor Fill behind the centred selection row
 */
@Immutable
public data class PvotPickerColors(
    val textColor: Color,
    val textSecondaryColor: Color,
    val selectionBackgroundColor: Color
)

/** Defaults for [PvotClockPicker] and [PvotDurationPicker]. */
public object PvotPickerDefaults {

    public val TextColor: Color = Color.White
    public val TextSecondaryColor: Color = Color.White.copy(alpha = 0.7f)
    public val SelectionBackgroundColor: Color = Color.White.copy(alpha = 0.08f)

    public fun colors(
        textColor: Color = TextColor,
        textSecondaryColor: Color = TextSecondaryColor,
        selectionBackgroundColor: Color = SelectionBackgroundColor
    ): PvotPickerColors = PvotPickerColors(
        textColor = textColor,
        textSecondaryColor = textSecondaryColor,
        selectionBackgroundColor = selectionBackgroundColor
    )

    /** Picker colors derived from [colorScheme]. */
    public fun colors(colorScheme: ColorScheme): PvotPickerColors = PvotPickerColors(
        textColor = colorScheme.onBackground,
        textSecondaryColor = colorScheme.onBackground.copy(alpha = 0.7f),
        selectionBackgroundColor = colorScheme.onBackground.copy(alpha = 0.1f)
    )

    /** Picker colors derived from the theme's color scheme. */
    @Composable
    public fun themeColors(): PvotPickerColors = colors(MaterialTheme.colorScheme)
}
