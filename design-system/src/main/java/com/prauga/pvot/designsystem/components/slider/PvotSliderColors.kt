// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.slider

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Color configuration for [PvotSlider] and [PvotRangeSlider].
 *
 * @param activeTrackColor Fill of the track between the start and the thumb (or between the thumbs)
 * @param inactiveTrackColor Fill of the rest of the track
 * @param thumbColor Fill of the thumb
 * @param tickColor Dots marking each step on the inactive part of the track
 * @param activeTickColor Dots marking each step on the active part of the track
 */
@Immutable
public data class PvotSliderColors(
    val activeTrackColor: Color,
    val inactiveTrackColor: Color,
    val thumbColor: Color,
    val tickColor: Color,
    val activeTickColor: Color
)

/** Defaults for [PvotSlider] and [PvotRangeSlider]. */
public object PvotSliderDefaults {

    /** Height of the track. */
    public val TrackHeight: Dp = 6.dp

    /** Diameter of the thumb at rest; it grows while pressed. */
    public val ThumbSize: Dp = 16.dp

    /** Height of the touch target, so a thin slider is still easy to grab. */
    public val TouchHeight: Dp = 48.dp

    /** Diameter of a step dot. */
    public val TickSize: Dp = 3.dp

    /** How much larger the thumb gets while pressed. */
    public const val PressedThumbScale: Float = 1.2f

    /** Slider colors derived from [colorScheme], each overridable. */
    public fun colors(
        colorScheme: ColorScheme,
        activeTrackColor: Color = colorScheme.primary,
        inactiveTrackColor: Color = colorScheme.onSurface.copy(alpha = 0.12f),
        thumbColor: Color = colorScheme.primary,
        tickColor: Color = colorScheme.onSurface.copy(alpha = 0.3f),
        activeTickColor: Color = colorScheme.onPrimary.copy(alpha = 0.7f)
    ): PvotSliderColors = PvotSliderColors(
        activeTrackColor = activeTrackColor,
        inactiveTrackColor = inactiveTrackColor,
        thumbColor = thumbColor,
        tickColor = tickColor,
        activeTickColor = activeTickColor
    )

    /** Slider colors from the theme's color scheme. */
    @Composable
    public fun themeColors(): PvotSliderColors = colors(MaterialTheme.colorScheme)
}
