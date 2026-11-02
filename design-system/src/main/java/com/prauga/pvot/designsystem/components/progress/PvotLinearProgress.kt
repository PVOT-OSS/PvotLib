// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * The colors of a [PvotLinearProgress].
 *
 * @param indicatorColor The part done
 * @param trackColor The part to go
 */
@Immutable
public data class PvotLinearProgressColors(val indicatorColor: Color, val trackColor: Color)

/** Defaults for [PvotLinearProgress]. */
public object PvotLinearProgressDefaults {

    /** Thickness of the bar. */
    public val Height: Dp = 4.dp

    /** Height of the touch area of a bar that seeks, round the thin bar. */
    public val SeekTouchHeight: Dp = 24.dp

    /** Colors from the theme: `primary` done, a faint `onSurface` to go. */
    @Composable
    public fun colors(): PvotLinearProgressColors =
        PvotLinearProgressColors(
            indicatorColor = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = TrackAlpha),
        )

    /** Opacity of the track color. */
    public const val TrackAlpha: Float = 0.16f
}

/**
 * A thin rounded bar that shows how far along something is, such as a voicemail playing.
 *
 * [progress] is read only when drawing, so a value that changes every frame costs no recomposition.
 * With [onSeek] the bar takes taps and drags along it and reports where, 0 to 1; its touch area is
 * taller than the bar, and TalkBack can set it too.
 *
 * @param progress How far along, 0 to 1
 * @param modifier Modifier for the bar; give it a width
 * @param height Thickness of the bar
 * @param colors The bar's colors
 * @param onSeek Called with a position from 0 to 1 when the user taps or drags along the bar, or
 *   null for a bar that only shows progress
 */
@Composable
public fun PvotLinearProgress(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    height: Dp = PvotLinearProgressDefaults.Height,
    colors: PvotLinearProgressColors = PvotLinearProgressDefaults.colors(),
    onSeek: ((Float) -> Unit)? = null,
) {
    val seek by rememberUpdatedState(onSeek)
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val input =
        if (onSeek != null) {
            Modifier.heightIn(min = PvotLinearProgressDefaults.SeekTouchHeight)
                .pointerInput(rtl) {
                    fun at(x: Float): Float =
                        (x / size.width).coerceIn(0f, 1f).let { if (rtl) 1f - it else it }
                    detectTapGestures { seek?.invoke(at(it.x)) }
                }
                .pointerInput(rtl) {
                    fun at(x: Float): Float =
                        (x / size.width).coerceIn(0f, 1f).let { if (rtl) 1f - it else it }
                    detectHorizontalDragGestures { change, _ ->
                        change.consume()
                        seek?.invoke(at(change.position.x))
                    }
                }
        } else {
            Modifier
        }
    Box(
        modifier =
            modifier.then(input).semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(progress().coerceIn(0f, 1f), 0f..1f)
                if (onSeek != null) {
                    setProgress { value ->
                        seek?.invoke(value.coerceIn(0f, 1f))
                        true
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxWidth().height(height)) {
            val radius = CornerRadius(size.height / 2f)
            drawRoundRect(colors.trackColor, cornerRadius = radius)
            val done = size.width * progress().coerceIn(0f, 1f)
            if (done > 0f) {
                drawRoundRect(
                    color = colors.indicatorColor,
                    topLeft = Offset(if (rtl) size.width - done else 0f, 0f),
                    size = Size(done, size.height),
                    cornerRadius = radius,
                )
            }
        }
    }
}
