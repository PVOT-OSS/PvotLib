// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.textfield

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.theme.PvotShapes

/**
 * Color configuration for [PvotTextField].
 *
 * @param containerColor Fill of the field
 * @param focusedBorderColor Ring drawn around the field while it has focus
 * @param errorColor Ring, supporting text and label of a field in error
 * @param textColor Color of the typed text
 * @param labelColor Color of the label at rest
 * @param focusedLabelColor Color of the label while it is floated and the field has focus
 * @param placeholderColor Color of the placeholder
 * @param supportingTextColor Color of the supporting text and counter
 * @param cursorColor Color of the cursor
 * @param iconColor Color of the leading and trailing icons
 */
@Immutable
public data class PvotTextFieldColors(
    val containerColor: Color,
    val focusedBorderColor: Color,
    val errorColor: Color,
    val textColor: Color,
    val labelColor: Color,
    val focusedLabelColor: Color,
    val placeholderColor: Color,
    val supportingTextColor: Color,
    val cursorColor: Color,
    val iconColor: Color
)

/** Defaults for [PvotTextField]. */
public object PvotTextFieldDefaults {

    /** Corner shape of the field. */
    public val Shape: Shape = PvotShapes.Card

    /** Width of the ring around a focused field or a field in error. */
    public val BorderWidth: Dp = 2.dp

    /** Height of a single-line field with no label floating. */
    public val MinHeight: Dp = 56.dp

    /** Text field colors derived from [colorScheme], each overridable. */
    public fun colors(
        colorScheme: ColorScheme,
        containerColor: Color = colorScheme.onSurface.copy(alpha = 0.07f),
        focusedBorderColor: Color = colorScheme.primary,
        errorColor: Color = colorScheme.error,
        textColor: Color = colorScheme.onSurface,
        labelColor: Color = colorScheme.onSurfaceVariant,
        focusedLabelColor: Color = colorScheme.primary,
        placeholderColor: Color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        supportingTextColor: Color = colorScheme.onSurfaceVariant,
        cursorColor: Color = colorScheme.primary,
        iconColor: Color = colorScheme.onSurfaceVariant
    ): PvotTextFieldColors = PvotTextFieldColors(
        containerColor = containerColor,
        focusedBorderColor = focusedBorderColor,
        errorColor = errorColor,
        textColor = textColor,
        labelColor = labelColor,
        focusedLabelColor = focusedLabelColor,
        placeholderColor = placeholderColor,
        supportingTextColor = supportingTextColor,
        cursorColor = cursorColor,
        iconColor = iconColor
    )

    /** Text field colors from the theme's color scheme. */
    @Composable
    public fun themeColors(): PvotTextFieldColors = colors(MaterialTheme.colorScheme)
}
