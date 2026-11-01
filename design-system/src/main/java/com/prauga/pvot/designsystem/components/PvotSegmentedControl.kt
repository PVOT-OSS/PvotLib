// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing
import kotlin.math.roundToInt

/**
 * Color configuration for [PvotSegmentedControl].
 *
 * @param trackColor Fill behind every segment
 * @param indicatorColor Fill of the pill under the selected segment
 * @param selectedContentColor Label color of the selected segment
 * @param contentColor Label color of the other segments
 */
@Immutable
public data class PvotSegmentedControlColors(
    val trackColor: Color,
    val indicatorColor: Color,
    val selectedContentColor: Color,
    val contentColor: Color
)

/** Defaults for [PvotSegmentedControl]. */
public object PvotSegmentedControlDefaults {

    /** Height of the control, the minimum touch target. */
    public val Height: Dp = 48.dp

    /** Shape of the track and the indicator. */
    public val Shape: Shape = PvotShapes.Pill

    /** Gap between the track's edge and the indicator. */
    public val IndicatorInset: Dp = PvotSpacing.XS

    /** Control colors derived from [colorScheme], each overridable. */
    public fun colors(
        colorScheme: ColorScheme,
        trackColor: Color = colorScheme.onSurface.copy(alpha = 0.07f),
        indicatorColor: Color = colorScheme.primary,
        selectedContentColor: Color = colorScheme.onPrimary,
        contentColor: Color = colorScheme.onSurfaceVariant
    ): PvotSegmentedControlColors = PvotSegmentedControlColors(
        trackColor = trackColor,
        indicatorColor = indicatorColor,
        selectedContentColor = selectedContentColor,
        contentColor = contentColor
    )

    /** Control colors from the theme's color scheme. */
    @Composable
    public fun themeColors(): PvotSegmentedControlColors = colors(MaterialTheme.colorScheme)
}

/**
 * A row of mutually exclusive choices, such as All and Missed, with a pill that slides to the
 * one selected.
 *
 * Segments share the width equally; keep labels short and to two to four segments. Selecting one
 * ticks the haptics. TalkBack reads the control as a group of radio buttons.
 *
 * @param options Label of each segment, in order
 * @param selectedIndex Index of the selected segment in [options]
 * @param onSelect Called with the index of the segment the user picks
 * @param modifier Modifier for the control
 * @param enabled Whether the control responds to taps; a disabled control is dimmed
 * @param colors The control's colors
 */
@Composable
public fun PvotSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: PvotSegmentedControlColors = PvotSegmentedControlDefaults.themeColors()
) {
    if (options.isEmpty()) return
    val haptics = LocalHapticFeedback.current
    val position by animateFloatAsState(
        targetValue = selectedIndex.coerceIn(0, options.lastIndex).toFloat(),
        animationSpec = tween(PvotDurations.Medium, easing = PvotMotion.Settle),
        label = "pvotSegmentIndicator"
    )
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(PvotSegmentedControlDefaults.Height)
            .alpha(if (enabled) 1f else 0.38f)
            .clip(PvotSegmentedControlDefaults.Shape)
            .background(colors.trackColor)
    ) {
        val segmentWidth = maxWidth / options.size
        Box(
            modifier = Modifier
                .width(segmentWidth)
                .fillMaxHeight()
                // Offset, not absoluteOffset: the pill follows the layout direction.
                .offset { IntOffset((segmentWidth.toPx() * position).roundToInt(), 0) }
                .padding(PvotSegmentedControlDefaults.IndicatorInset)
                .background(colors.indicatorColor, PvotSegmentedControlDefaults.Shape)
        )
        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight().selectableGroup()) {
            options.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                val interactionSource = remember { MutableInteractionSource() }
                val content by animateColorAsState(
                    targetValue = if (selected) colors.selectedContentColor else colors.contentColor,
                    animationSpec = tween(PvotDurations.Medium, easing = PvotMotion.Settle),
                    label = "pvotSegmentContent"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .pvotPressScale(interactionSource)
                        .selectable(
                            selected = selected,
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = enabled,
                            role = Role.RadioButton,
                            onClick = {
                                if (!selected) {
                                    haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                    onSelect(index)
                                }
                            }
                        )
                        .padding(horizontal = PvotSpacing.M),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = content,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
