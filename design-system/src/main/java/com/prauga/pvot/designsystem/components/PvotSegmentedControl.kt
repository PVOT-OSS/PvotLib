// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.pvotGlass
import com.prauga.pvot.designsystem.effects.pvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.pvotGlassRim
import com.prauga.pvot.designsystem.effects.rememberPvotGlassBackdrop
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
    val contentColor: Color,
)

/** Defaults for [PvotSegmentedControl]. */
public object PvotSegmentedControlDefaults {

    /** Height of the control, the minimum touch target. */
    public val Height: Dp = 48.dp

    /** Shape of the track and the indicator. */
    public val Shape: Shape = PvotShapes.Pill

    /** Gap between the track's edge and the indicator. */
    public val IndicatorInset: Dp = PvotSpacing.XS

    /** How much wider and taller than the pill the lens is once lifted. */
    public val LensGrow: DpSize = DpSize(20.dp, 12.dp)

    /** Control colors derived from [colorScheme], each overridable. */
    public fun colors(
        colorScheme: ColorScheme,
        trackColor: Color = colorScheme.onSurface.copy(alpha = 0.07f),
        indicatorColor: Color = colorScheme.primary,
        selectedContentColor: Color = colorScheme.onPrimary,
        contentColor: Color = colorScheme.onSurfaceVariant,
    ): PvotSegmentedControlColors =
        PvotSegmentedControlColors(
            trackColor = trackColor,
            indicatorColor = indicatorColor,
            selectedContentColor = selectedContentColor,
            contentColor = contentColor,
        )

    /** Control colors from the theme's color scheme. */
    @Composable
    public fun themeColors(): PvotSegmentedControlColors = colors(MaterialTheme.colorScheme)
}

/**
 * A row of mutually exclusive choices, such as All and Missed, with a pill that slides to the one
 * selected.
 *
 * Tap a segment to pick it. Held, the pill lifts into a lens over the labels and follows the finger
 * along the track; letting go picks the segment it lands on. A quick swipe up or down still scrolls
 * the list the control is in. Segments share the width equally; keep labels short and to two to
 * four segments. Picking one ticks the haptics. TalkBack reads the control as a group of radio
 * buttons.
 *
 * @param options Label of each segment, in order
 * @param selectedIndex Index of the selected segment in [options]
 * @param onSelect Called with the index of the segment the user picks
 * @param modifier Modifier for the control
 * @param enabled Whether the control responds to touch; a disabled control is dimmed
 * @param colors The control's colors
 */
@Composable
public fun PvotSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: PvotSegmentedControlColors = PvotSegmentedControlDefaults.themeColors(),
) {
    if (options.isEmpty()) return
    val haptics = LocalHapticFeedback.current
    val count = options.size
    val selected = selectedIndex.coerceIn(0, options.lastIndex)
    val currentSelected by rememberUpdatedState(selected)
    val currentOnSelect by rememberUpdatedState(onSelect)
    var lifted by remember { mutableStateOf(false) }
    var dragging by remember { mutableStateOf(false) }
    var fingerIndex by remember { mutableFloatStateOf(selected.toFloat()) }
    var hovered by remember { mutableIntStateOf(selected) }
    val activeIndex = if (dragging) hovered else selected
    val position by
        animateFloatAsState(
            targetValue = if (dragging) fingerIndex else selected.toFloat(),
            animationSpec = if (dragging) PvotMotion.followSpring() else PvotMotion.slideSpring(),
            label = "pvotSegmentIndicator",
        )
    val lift by
        animateFloatAsState(
            targetValue = if (lifted) 1f else 0f,
            animationSpec = PvotMotion.liftSpring(),
            label = "pvotSegmentLift",
        )
    val backdrop = rememberPvotGlassBackdrop()
    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxWidth()
                .height(PvotSegmentedControlDefaults.Height)
                .alpha(if (enabled) 1f else 0.38f)
                .background(colors.trackColor, PvotSegmentedControlDefaults.Shape)
                .pointerInput(enabled, count) {
                    if (!enabled) return@pointerInput
                    val segment = size.width.toFloat() / count
                    fun indexAt(x: Float): Int = (x / segment).toInt().coerceIn(0, count - 1)
                    var anchor = 0f
                    var startX = 0f
                    detectLiftGesture(
                        onLift = { at ->
                            anchor = indexAt(at.x).toFloat()
                            startX = at.x
                            fingerIndex = anchor
                            hovered = anchor.roundToInt()
                            lifted = true
                            dragging = true
                        },
                        onMove = { at ->
                            fingerIndex =
                                (anchor + (at.x - startX) / segment).coerceIn(0f, count - 1f)
                            val under = fingerIndex.roundToInt()
                            if (under != hovered) {
                                hovered = under
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        },
                        onRelease = { at, moved ->
                            val pick = if (moved) fingerIndex.roundToInt() else indexAt(at.x)
                            lifted = false
                            dragging = false
                            if (pick != currentSelected) {
                                haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                currentOnSelect(pick)
                            }
                        },
                        onCancel = {
                            lifted = false
                            dragging = false
                        },
                    )
                }
    ) {
        val segmentWidth = maxWidth / count
        val inset = PvotSegmentedControlDefaults.IndicatorInset
        Box(modifier = Modifier.fillMaxSize().pvotGlassBackdrop(backdrop)) {
            Box(
                modifier =
                    Modifier.width(segmentWidth)
                        .fillMaxHeight()
                        // Offset, not absoluteOffset: the pill follows the layout direction.
                        .offset { IntOffset((segmentWidth.toPx() * position).roundToInt(), 0) }
                        .padding(inset)
                        .background(colors.indicatorColor, PvotSegmentedControlDefaults.Shape)
            )
            Row(modifier = Modifier.fillMaxSize().selectableGroup()) {
                options.forEachIndexed { index, label ->
                    val active = index == activeIndex
                    val content by
                        animateColorAsState(
                            targetValue =
                                if (active) colors.selectedContentColor else colors.contentColor,
                            animationSpec = tween(PvotDurations.Medium, easing = PvotMotion.Settle),
                            label = "pvotSegmentContent",
                        )
                    Box(
                        modifier =
                            Modifier.weight(1f)
                                .fillMaxHeight()
                                .semantics {
                                    this.selected = index == selected
                                    role = Role.RadioButton
                                    if (enabled) {
                                        onClick {
                                            if (index != currentSelected) currentOnSelect(index)
                                            true
                                        }
                                    } else {
                                        disabled()
                                    }
                                }
                                .padding(horizontal = PvotSpacing.M),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = content,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
        if (lift > 0f) {
            val grow = PvotSegmentedControlDefaults.LensGrow
            val lensWidth = segmentWidth - inset * 2 + grow.width * lift
            val lensHeight = PvotSegmentedControlDefaults.Height - inset * 2 + grow.height * lift
            Box(
                modifier =
                    Modifier.offset {
                            IntOffset(
                                (segmentWidth.toPx() * position + inset.toPx() -
                                        (grow.width * lift).toPx() / 2f)
                                    .roundToInt(),
                                (inset.toPx() - (grow.height * lift).toPx() / 2f).roundToInt(),
                            )
                        }
                        .size(lensWidth, lensHeight)
                        .graphicsLayer { alpha = lift }
                        .pvotGlass(
                            backdrop,
                            PvotGlassDefaults.CapsuleRadius,
                            PvotGlassDefaults.lensStyle(lift, PvotGlassDefaults.LabelLensStyle),
                        )
                        .pvotGlassRim(alpha = lift)
            )
        }
    }
}
