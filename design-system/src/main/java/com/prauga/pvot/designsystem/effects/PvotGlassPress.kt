// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.effects

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isFinite
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max

/**
 * Swells this element while it is pressed, as a glass control does under a finger: it grows by
 * [grow] along its longer side, so a small round button and a wide pill swell alike, stretches a
 * little toward where the finger drags and follows it by up to [follow], and springs back past its
 * resting size when let go. A soft light shows where the finger touched.
 *
 * It watches the pointer itself, so it needs no interaction source. Put it before the clickable in
 * the chain, so the whole element scales; it stops swelling as soon as something else, such as a
 * scrolling list, takes the touch.
 *
 * @param cornerRadius The element's corner radius, so the light keeps to its shape;
 *   [PvotGlassDefaults.CapsuleRadius] for a capsule
 * @param enabled Whether the element responds to touch
 * @param grow How much the longer side grows while pressed
 * @param stretch How much the element stretches toward the direction of a drag
 * @param follow How far the element moves with a drag before it holds back
 * @param highlightColor The light at the point of touch, at its brightest
 */
@Composable
public fun Modifier.pvotGlassPress(
    cornerRadius: Dp = PvotGlassDefaults.CapsuleRadius,
    enabled: Boolean = true,
    grow: Dp = PvotGlassDefaults.PressGrow,
    stretch: Dp = PvotGlassDefaults.PressStretch,
    follow: Dp = PvotGlassDefaults.PressFollow,
    highlightColor: Color = PvotGlassDefaults.PressHighlightColor,
): Modifier {
    var pressed by remember { mutableStateOf(false) }
    var touch by remember { mutableStateOf(Offset.Unspecified) }
    var aimScaleX by remember { mutableFloatStateOf(1f) }
    var aimScaleY by remember { mutableFloatStateOf(1f) }
    var aimShiftX by remember { mutableFloatStateOf(0f) }
    var aimShiftY by remember { mutableFloatStateOf(0f) }
    val spec = if (pressed) PvotGlassDefaults.PressSpring else PvotGlassDefaults.ReleaseSpring
    val scaleX by animateFloatAsState(aimScaleX, spec, label = "pvotGlassPressScaleX")
    val scaleY by animateFloatAsState(aimScaleY, spec, label = "pvotGlassPressScaleY")
    val shiftX by animateFloatAsState(aimShiftX, spec, label = "pvotGlassPressShiftX")
    val shiftY by animateFloatAsState(aimShiftY, spec, label = "pvotGlassPressShiftY")
    val light by
        animateFloatAsState(
            targetValue = if (pressed) 1f else 0f,
            animationSpec = if (pressed) snap() else tween(PvotGlassDefaults.HighlightFadeMillis),
            label = "pvotGlassPressLight",
        )
    return this.pointerInput(enabled) {
            if (!enabled) return@pointerInput
            val growPx = grow.toPx()
            val stretchPx = stretch.toPx()
            val followPx = follow.toPx()
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val width = size.width.toFloat()
                val height = size.height.toFloat()
                if (width <= 0f || height <= 0f) return@awaitEachGesture
                val start = down.position
                val base = 1f + growPx / max(width, height)
                val aspect = width / height
                fun aim(at: Offset) {
                    val dx = (at.x - start.x) / aspect
                    val dy = at.y - start.y
                    val length = hypot(dx, dy)
                    if (length < 1e-3f) {
                        aimScaleX = base
                        aimScaleY = base
                        aimShiftX = 0f
                        aimShiftY = 0f
                        return
                    }
                    val nx = dx / length
                    val ny = dy / length
                    val rubber = 1f - 1f / (1f + length / height / (5f * aspect))
                    val pull = 1f + (stretchPx / height) * rubber * abs(abs(nx) - abs(ny))
                    val horizontal = abs(nx) > abs(ny)
                    aimScaleX = base * (if (horizontal) pull else 1f / pull)
                    aimScaleY = base * (if (horizontal) 1f / pull else pull)
                    aimShiftX = nx * followPx * rubber
                    aimShiftY = ny * followPx * rubber
                }
                touch = start
                pressed = true
                aim(start)
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Final)
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (change.changedToUpIgnoreConsumed()) break
                    if (change.isConsumed && change.positionChanged()) break
                    aim(change.position)
                }
                pressed = false
                aimScaleX = 1f
                aimScaleY = 1f
                aimShiftX = 0f
                aimShiftY = 0f
            }
        }
        .graphicsLayer {
            this.scaleX = scaleX
            this.scaleY = scaleY
            translationX = shiftX
            translationY = shiftY
        }
        .drawWithContent {
            drawContent()
            if (light <= 0f || !touch.isSpecified) return@drawWithContent
            val radius = if (cornerRadius.isFinite) cornerRadius.toPx() else size.minDimension / 2f
            drawRoundRect(
                brush =
                    Brush.radialGradient(
                        0f to highlightColor,
                        0.5f to highlightColor.copy(alpha = highlightColor.alpha * 0.7f),
                        1f to Color.Transparent,
                        center = touch,
                        radius = PvotGlassDefaults.HighlightRadius.toPx(),
                    ),
                cornerRadius = CornerRadius(radius),
                alpha = light,
            )
        }
}
