// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.camera

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prauga.pvot.designsystem.components.pvotGlassSurface
import com.prauga.pvot.designsystem.effects.LocalPvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassStyle
import com.prauga.pvot.designsystem.motion.PvotDurations
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln

/**
 * Color configuration for [PvotZoomSelector] and [PvotZoomDial].
 *
 * @param selectedContentColor Label of the selected lens, and the dial's value and index
 * @param contentColor Labels of the other lenses, and the dial's ticks and marks
 */
@Immutable
public data class PvotZoomSelectorColors(val selectedContentColor: Color, val contentColor: Color)

/** Defaults for [PvotZoomSelector] and [PvotZoomDial]. */
public object PvotZoomSelectorDefaults {
    /** The diameter of a lens that is not selected. */
    public val ItemSize: Dp = 30.dp

    /** The diameter of the selected lens. */
    public val SelectedItemSize: Dp = 34.dp

    /** Space between the lenses. */
    public val Spacing: Dp = 4.dp

    /** How long a lens is held before the zoom is adjusted by dragging. */
    public const val HoldTimeoutMillis: Long = 450L

    /** How far a drag goes to double, or halve, the zoom. */
    public val DoublingDistance: Dp = 80.dp

    /** The dial's width. */
    public val DialWidth: Dp = 330.dp

    /** The dial's height. */
    public val DialHeight: Dp = 131.dp

    /** The glass of the selected lens: darker, so it reads as picked. */
    public val SelectedStyle: PvotGlassStyle =
        PvotCameraGlassDefaults.Style.copy(fillColor = Color(0xA6222222))

    /** The glass of the other lenses: barely tinted. */
    public val Style: PvotGlassStyle =
        PvotCameraGlassDefaults.Style.copy(fillColor = Color(0x38222222))

    /** Colors from [MaterialTheme]: the primary color for the selected lens. */
    @Composable
    public fun colors(
        selectedContentColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = MaterialTheme.colorScheme.onSurface,
    ): PvotZoomSelectorColors = PvotZoomSelectorColors(selectedContentColor, contentColor)
}

/**
 * A row of the camera's lenses or zoom stops, such as .5, 1x, 2 and 5, the selected one larger and
 * labelled with the zoom as it is now.
 *
 * Tapping a lens calls [onSelect]. With [onAdjustStart], holding a lens for
 * [PvotZoomSelectorDefaults.HoldTimeoutMillis], or dragging across the row, adjusts the zoom
 * instead: [onAdjust] receives how much to scale it by as the finger moves, right to zoom in, and
 * [onAdjustEnd] the release. Show a [PvotZoomDial] while adjusting.
 *
 * @param labels Label of each lens, in order
 * @param selectedIndex Index of the selected lens in [labels]
 * @param selectedLabel Label shown on the selected lens, such as the exact zoom
 * @param onSelect Called with the index of the lens tapped, also when it is already selected
 * @param modifier Modifier for the row
 * @param backdrop The content behind the lenses, or null for the tint alone
 * @param enabled Whether the row responds
 * @param onAdjustStart Called when a hold or drag starts adjusting, or null for taps only
 * @param onAdjust Called with the factor to scale the zoom by as the finger moves
 * @param onAdjustEnd Called when the finger lifts after adjusting
 * @param colors The label colors
 */
@Composable
public fun PvotZoomSelector(
    labels: List<String>,
    selectedIndex: Int,
    selectedLabel: String,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: PvotGlassBackdrop? = LocalPvotGlassBackdrop.current,
    enabled: Boolean = true,
    onAdjustStart: (() -> Unit)? = null,
    onAdjust: (Float) -> Unit = {},
    onAdjustEnd: () -> Unit = {},
    colors: PvotZoomSelectorColors = PvotZoomSelectorDefaults.colors(),
) {
    val bounds = remember { mutableMapOf<Int, ClosedFloatingPointRange<Float>>() }
    val haptics = LocalHapticFeedback.current

    val currentEnabled by rememberUpdatedState(enabled)
    val currentOnSelect by rememberUpdatedState(onSelect)
    val currentOnAdjustStart by rememberUpdatedState(onAdjustStart)
    val currentOnAdjust by rememberUpdatedState(onAdjust)
    val currentOnAdjustEnd by rememberUpdatedState(onAdjustEnd)

    Row(
        modifier =
            modifier
                .pointerInput(Unit) {
                    val slop = viewConfiguration.touchSlop
                    val padding = 12.dp.toPx()
                    val doubling = PvotZoomSelectorDefaults.DoublingDistance.toPx()
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        if (!currentEnabled) return@awaitEachGesture

                        val tapX =
                            withTimeoutOrNull(PvotZoomSelectorDefaults.HoldTimeoutMillis) {
                                var releasedAt: Float? = null
                                while (true) {
                                    val change = awaitPointerEvent().changes.first()
                                    if (!change.pressed) {
                                        releasedAt = change.position.x
                                        break
                                    }
                                    if (abs(change.position.x - down.position.x) > slop) break
                                }
                                releasedAt
                            }

                        if (tapX != null) {
                            bounds.entries
                                .firstOrNull { tapX - padding in it.value }
                                ?.let { currentOnSelect(it.key) }
                            return@awaitEachGesture
                        }

                        val adjustStart = currentOnAdjustStart ?: return@awaitEachGesture
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        adjustStart()
                        var lastX = down.position.x
                        while (true) {
                            val change = awaitPointerEvent().changes.first()
                            if (!change.pressed) break
                            val dx = change.position.x - lastX
                            lastX = change.position.x
                            if (dx != 0f) currentOnAdjust(exp(dx / doubling * ln(2f)))
                            change.consume()
                        }
                        currentOnAdjustEnd()
                    }
                }
                .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(PvotZoomSelectorDefaults.Spacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        labels.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            val size by
                animateDpAsState(
                    if (selected) PvotZoomSelectorDefaults.SelectedItemSize
                    else PvotZoomSelectorDefaults.ItemSize,
                    tween(PvotDurations.Medium),
                    label = "lens",
                )

            Box(
                modifier =
                    Modifier.size(size)
                        .onGloballyPositioned {
                            val x = it.positionInParent().x
                            bounds[index] = (x - 2f)..(x + it.size.width + 2f)
                        }
                        .pvotGlassSurface(
                            backdrop = backdrop,
                            style =
                                if (selected) PvotZoomSelectorDefaults.SelectedStyle
                                else PvotZoomSelectorDefaults.Style,
                            rim = false,
                            role = null,
                        )
                        .semantics {
                            role = Role.Button
                            this.selected = selected
                            onClick {
                                onSelect(index)
                                true
                            }
                        },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (selected) selectedLabel else label,
                    color = if (selected) colors.selectedContentColor else colors.contentColor,
                    fontSize = if (selected) 14.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
    }
}

/**
 * A glass dial for fine zoom, shown while a [PvotZoomSelector] is adjusting: the zoom as text over
 * a ruler from [range]'s start to its end on a log scale, with [marks] labelled under it.
 *
 * @param value The zoom now, within [range]
 * @param range The lowest to the highest zoom
 * @param label The zoom as text, such as 2.5×
 * @param modifier Modifier for the dial
 * @param backdrop The content behind the dial, or null for the tint alone
 * @param marks Zooms to label under the ruler; those outside [range] are left out
 * @param markLabel Text of each mark
 * @param colors The value, index, tick and mark colors
 * @param style How the dial's glass looks
 */
@Composable
public fun PvotZoomDial(
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    label: String,
    modifier: Modifier = Modifier,
    backdrop: PvotGlassBackdrop? = LocalPvotGlassBackdrop.current,
    marks: List<Float> = listOf(0.5f, 1f, 2f, 3f, 5f, 10f, 15f, 25f, 50f, 100f),
    markLabel: (Float) -> String = { it.toString() },
    colors: PvotZoomSelectorColors = PvotZoomSelectorDefaults.colors(),
    style: PvotGlassStyle = PvotCameraGlassDefaults.PanelStyle,
) {
    Box(
        modifier =
            modifier
                .width(PvotZoomSelectorDefaults.DialWidth)
                .height(PvotZoomSelectorDefaults.DialHeight)
                .pvotGlassSurface(
                    backdrop = backdrop,
                    cornerRadius = 48.dp,
                    style = style,
                    role = null,
                ),
        contentAlignment = Alignment.TopCenter,
    ) {
        Text(
            text = label,
            color = colors.selectedContentColor,
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 15.dp),
        )

        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth().padding(top = 64.dp, start = 19.dp, end = 19.dp)
        ) {
            val minLog = ln(range.start)
            val span = (ln(range.endInclusive) - minLog).takeIf { it > 0f } ?: 1f
            val fraction = { zoom: Float -> ((ln(zoom) - minLog) / span).coerceIn(0f, 1f) }

            Canvas(modifier = Modifier.fillMaxWidth().height(37.dp)) {
                val ticks = 38
                for (i in 0..ticks) {
                    val x = size.width * i / ticks
                    val major = i % 5 == 0
                    drawLine(
                        color = colors.contentColor.copy(alpha = if (major) 0.9f else 0.5f),
                        start = Offset(x, 0f),
                        end = Offset(x, if (major) 23.dp.toPx() else 11.dp.toPx()),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
                val indexX = size.width * fraction(value.coerceIn(range))
                drawLine(
                    color = colors.selectedContentColor,
                    start = Offset(indexX, 0f),
                    end = Offset(indexX, size.height),
                    strokeWidth = 2.dp.toPx(),
                )
            }

            val width = maxWidth
            marks
                .filter { it in range }
                .forEach { mark ->
                    Text(
                        text = markLabel(mark),
                        color = colors.contentColor,
                        fontSize = 10.sp,
                        modifier = Modifier.offset(x = width * fraction(mark) - 4.dp, y = 35.dp),
                    )
                }
        }
    }
}
