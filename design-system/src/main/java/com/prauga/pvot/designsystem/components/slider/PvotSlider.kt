// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.slider

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.pvotGlass
import com.prauga.pvot.designsystem.effects.pvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.pvotGlassRim
import com.prauga.pvot.designsystem.effects.rememberPvotGlassBackdrop
import com.prauga.pvot.designsystem.motion.PvotMotion
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A minimal slider: a slim rounded track, one round thumb and nothing else.
 *
 * Touch anywhere on the track to jump there, or drag. Under the finger the thumb lifts into a lens
 * that swells the track beneath it, and settles back when let go. With [steps] the value snaps to
 * evenly spaced stops, marked by dots, and the device ticks as each is crossed. A horizontal drag
 * claims the gesture, so a slider inside a scrolling list never fights the list's own vertical
 * scroll.
 *
 * @param value The current value, within [valueRange]
 * @param onValueChange Called as the user moves the thumb, with the new value
 * @param modifier Modifier for the slider; it fills the width and is
 *   [PvotSliderDefaults.TouchHeight] tall
 * @param enabled Whether the slider responds to touch
 * @param valueRange The range [value] moves within
 * @param steps Number of stops strictly between the ends, or 0 for a continuous slider
 * @param onValueChangeFinished Called when the user lets go
 * @param colors Color configuration
 */
@Composable
public fun PvotSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: PvotSliderColors = PvotSliderDefaults.themeColors(),
) {
    require(steps >= 0) { "steps must not be negative" }
    val currentOnChange by rememberUpdatedState(onValueChange)
    val currentOnFinished by rememberUpdatedState(onValueChangeFinished)
    val haptics = LocalHapticFeedback.current
    var pressed by remember { mutableStateOf(false) }

    val fraction = valueRange.toFraction(value, steps)
    val lift by
        animateFloatAsState(
            targetValue = if (pressed) 1f else 0f,
            animationSpec = PvotMotion.liftSpring(),
            label = "pvotSliderLift",
        )
    val backdrop = rememberPvotGlassBackdrop()

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxWidth()
                .height(PvotSliderDefaults.TouchHeight)
                .semantics {
                    progressBarRangeInfo =
                        ProgressBarRangeInfo(
                            current = value.coerceIn(valueRange),
                            range = valueRange,
                            steps = steps,
                        )
                    setProgress { target ->
                        currentOnChange(
                            valueRange.fromFraction(valueRange.toFraction(target, steps))
                        )
                        true
                    }
                    if (!enabled) disabled()
                }
                .pointerInput(enabled, valueRange, steps) {
                    if (!enabled) return@pointerInput
                    val thumbPx = PvotSliderDefaults.ThumbSize.toPx()
                    var last = Float.NaN
                    fun emit(x: Float) {
                        val usable = (size.width - thumbPx).coerceAtLeast(1f)
                        val snapped =
                            valueRange.snap(((x - thumbPx / 2f) / usable).coerceIn(0f, 1f), steps)
                        if (snapped != last) {
                            if (!last.isNaN() && steps > 0) {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            last = snapped
                            currentOnChange(valueRange.fromFraction(snapped))
                        }
                    }
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        pressed = true
                        last = valueRange.toFraction(value, steps)
                        val slop =
                            awaitHorizontalTouchSlopOrCancellation(down.id) { change, _ ->
                                change.consume()
                            }
                        if (slop != null) {
                            emit(slop.position.x)
                            horizontalDrag(slop.id) { change ->
                                change.consume()
                                emit(change.position.x)
                            }
                        } else {
                            // No horizontal drag began: a tap if the finger lifted unclaimed.
                            val lift = currentEvent.changes.firstOrNull { it.id == down.id }
                            if (lift != null && lift.changedToUp() && !lift.isConsumed)
                                emit(lift.position.x)
                        }
                        pressed = false
                        currentOnFinished?.invoke()
                    }
                }
    ) {
        val alpha = if (enabled) 1f else DisabledAlpha
        Track(colors, alpha, steps, backdrop) { thumbPx, usable ->
            val thumbX = thumbPx / 2f + fraction * usable
            drawTrack(colors.activeTrackColor.scaled(alpha), 0f, thumbX, size.height / 2f)
            drawTicks(steps, thumbPx, usable) { x ->
                if (x <= thumbX) colors.activeTickColor.scaled(alpha)
                else colors.tickColor.scaled(alpha)
            }
        }
        Thumb(
            constraints.maxWidth.toFloat(),
            fraction,
            lift,
            colors.thumbColor.scaled(alpha),
            backdrop,
        )
    }
}

/**
 * A [PvotSlider] with two thumbs, for choosing a span rather than a single value.
 *
 * Touching the track moves whichever thumb is nearer; dragging carries that thumb. The thumbs can
 * meet but never cross.
 *
 * @param value The selected span, within [valueRange]
 * @param onValueChange Called as the user moves a thumb, with the new span
 * @param modifier Modifier for the slider; it fills the width and is
 *   [PvotSliderDefaults.TouchHeight] tall
 * @param enabled Whether the slider responds to touch
 * @param valueRange The range the span moves within
 * @param steps Number of stops strictly between the ends, or 0 for a continuous slider
 * @param onValueChangeFinished Called when the user lets go
 * @param colors Color configuration
 */
@Composable
public fun PvotRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: PvotSliderColors = PvotSliderDefaults.themeColors(),
) {
    require(steps >= 0) { "steps must not be negative" }
    val currentOnChange by rememberUpdatedState(onValueChange)
    val currentOnFinished by rememberUpdatedState(onValueChangeFinished)
    val currentValue by rememberUpdatedState(value)
    val haptics = LocalHapticFeedback.current
    var pressedThumb by remember { mutableStateOf(-1) }

    val startFraction = valueRange.toFraction(value.start, steps)
    val endFraction = valueRange.toFraction(value.endInclusive, steps)
    val startLift by
        animateFloatAsState(
            targetValue = if (pressedThumb == 0) 1f else 0f,
            animationSpec = PvotMotion.liftSpring(),
            label = "pvotRangeStartLift",
        )
    val endLift by
        animateFloatAsState(
            targetValue = if (pressedThumb == 1) 1f else 0f,
            animationSpec = PvotMotion.liftSpring(),
            label = "pvotRangeEndLift",
        )
    val backdrop = rememberPvotGlassBackdrop()

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxWidth()
                .height(PvotSliderDefaults.TouchHeight)
                .semantics {
                    progressBarRangeInfo =
                        ProgressBarRangeInfo(
                            current = value.endInclusive.coerceIn(valueRange),
                            range = valueRange,
                            steps = steps,
                        )
                    if (!enabled) disabled()
                }
                .pointerInput(enabled, valueRange, steps) {
                    if (!enabled) return@pointerInput
                    val thumbPx = PvotSliderDefaults.ThumbSize.toPx()
                    fun fractionAt(x: Float): Float {
                        val usable = (size.width - thumbPx).coerceAtLeast(1f)
                        return valueRange.snap(
                            ((x - thumbPx / 2f) / usable).coerceIn(0f, 1f),
                            steps,
                        )
                    }
                    fun nearestThumb(f: Float): Int {
                        val s = valueRange.toFraction(currentValue.start, steps)
                        val e = valueRange.toFraction(currentValue.endInclusive, steps)
                        return when {
                            f <= s -> 0
                            f >= e -> 1
                            abs(f - s) <= abs(f - e) -> 0
                            else -> 1
                        }
                    }
                    fun move(thumb: Int, f: Float) {
                        val s = valueRange.toFraction(currentValue.start, steps)
                        val e = valueRange.toFraction(currentValue.endInclusive, steps)
                        val next = if (thumb == 0) minOf(f, e) to e else s to maxOf(f, s)
                        if (next.first != s || next.second != e) {
                            if (steps > 0)
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            currentOnChange(
                                valueRange.fromFraction(next.first)..valueRange.fromFraction(
                                        next.second
                                    )
                            )
                        }
                    }
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        var thumb = nearestThumb(fractionAt(down.position.x))
                        pressedThumb = thumb
                        val slop =
                            awaitHorizontalTouchSlopOrCancellation(down.id) { change, _ ->
                                change.consume()
                            }
                        if (slop != null) {
                            thumb = nearestThumb(fractionAt(down.position.x))
                            pressedThumb = thumb
                            move(thumb, fractionAt(slop.position.x))
                            horizontalDrag(slop.id) { change ->
                                change.consume()
                                move(thumb, fractionAt(change.position.x))
                            }
                        } else {
                            val lift = currentEvent.changes.firstOrNull { it.id == down.id }
                            if (lift != null && lift.changedToUp() && !lift.isConsumed) {
                                move(thumb, fractionAt(lift.position.x))
                            }
                        }
                        pressedThumb = -1
                        currentOnFinished?.invoke()
                    }
                }
    ) {
        val alpha = if (enabled) 1f else DisabledAlpha
        Track(colors, alpha, steps, backdrop) { thumbPx, usable ->
            val startX = thumbPx / 2f + startFraction * usable
            val endX = thumbPx / 2f + endFraction * usable
            drawTrack(colors.activeTrackColor.scaled(alpha), startX, endX, size.height / 2f)
            drawTicks(steps, thumbPx, usable) { x ->
                if (x in startX..endX) colors.activeTickColor.scaled(alpha)
                else colors.tickColor.scaled(alpha)
            }
        }
        val width = constraints.maxWidth.toFloat()
        Thumb(width, startFraction, startLift, colors.thumbColor.scaled(alpha), backdrop)
        Thumb(width, endFraction, endLift, colors.thumbColor.scaled(alpha), backdrop)
    }
}

@Composable
private fun Track(
    colors: PvotSliderColors,
    alpha: Float,
    steps: Int,
    backdrop: PvotGlassBackdrop,
    active: DrawScope.(thumbPx: Float, usable: Float) -> Unit,
) {
    Box(
        modifier =
            Modifier.fillMaxSize().pvotGlassBackdrop(backdrop).drawWithCache {
                val thumbPx = PvotSliderDefaults.ThumbSize.toPx()
                onDrawBehind {
                    val usable = size.width - thumbPx
                    drawTrack(
                        colors.inactiveTrackColor.scaled(alpha),
                        0f,
                        size.width,
                        size.height / 2f,
                    )
                    active(thumbPx, usable)
                }
            }
    )
}

@Composable
private fun BoxScope.Thumb(
    maxWidthPx: Float,
    fraction: Float,
    lift: Float,
    color: Color,
    backdrop: PvotGlassBackdrop,
) {
    val thumb = PvotSliderDefaults.ThumbSize
    val diameter = thumb * (1f + (PvotSliderDefaults.LiftedThumbScale - 1f) * lift)
    val face = 1f - PvotSliderDefaults.LiftedFaceFade * lift
    Box(
        modifier =
            Modifier.align(Alignment.CenterStart)
                .offset {
                    val thumbPx = thumb.toPx()
                    val centerX = thumbPx / 2f + fraction * (maxWidthPx - thumbPx)
                    IntOffset((centerX - diameter.toPx() / 2f).roundToInt(), 0)
                }
                .size(diameter)
                .then(
                    if (lift > 0f) {
                        Modifier.pvotGlass(
                            backdrop,
                            PvotGlassDefaults.CapsuleRadius,
                            PvotGlassDefaults.lensStyle(lift),
                        )
                    } else {
                        Modifier
                    }
                )
                .drawBehind {
                    val radius = size.minDimension / 2f
                    drawCircle(
                        Color.Black.copy(alpha = 0.18f * face),
                        radius + 1.5.dp.toPx(),
                        center + Offset(0f, 0.5.dp.toPx()),
                    )
                    drawCircle(color.copy(alpha = color.alpha * face), radius)
                }
                .then(if (lift > 0f) Modifier.pvotGlassRim(alpha = lift) else Modifier)
    )
}

private const val DisabledAlpha = 0.38f

private fun Color.scaled(alpha: Float): Color = copy(alpha = this.alpha * alpha)

private fun ClosedFloatingPointRange<Float>.toFraction(value: Float, steps: Int): Float {
    val span = endInclusive - start
    val raw = if (span == 0f) 0f else ((value - start) / span).coerceIn(0f, 1f)
    return snap(raw, steps)
}

private fun ClosedFloatingPointRange<Float>.fromFraction(fraction: Float): Float =
    start + (endInclusive - start) * fraction

private fun ClosedFloatingPointRange<Float>.snap(fraction: Float, steps: Int): Float =
    if (steps > 0) (fraction * (steps + 1)).roundToInt() / (steps + 1).toFloat() else fraction

private fun DrawScope.drawTrack(color: Color, fromX: Float, toX: Float, centerY: Float) {
    if (toX <= fromX) return
    val height = PvotSliderDefaults.TrackHeight.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(fromX, centerY - height / 2f),
        size = Size(toX - fromX, height),
        cornerRadius = CornerRadius(height / 2f),
    )
}

private fun DrawScope.drawTicks(
    steps: Int,
    thumbPx: Float,
    usable: Float,
    colorAt: (x: Float) -> Color,
) {
    if (steps <= 0) return
    val radius = PvotSliderDefaults.TickSize.toPx() / 2f
    val centerY = size.height / 2f
    for (i in 1..steps) {
        val x = thumbPx / 2f + usable * i / (steps + 1)
        drawCircle(colorAt(x), radius, Offset(x, centerY))
    }
}
