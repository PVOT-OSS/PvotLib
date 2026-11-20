// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.progress

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics

/** Defaults for [PvotProgressRing]. */
public object PvotProgressRingDefaults {

    /** Thickness of the ring as a share of its diameter. */
    public const val StrokeFraction: Float = 0.07f

    /** Opacity of the track behind the arc, a faint copy of the ring's color. */
    public const val TrackAlpha: Float = 0.24f

    /** The theme's `primary`. */
    @Composable public fun color(): Color = MaterialTheme.colorScheme.primary
}

/**
 * A round progress ring with content in its middle, such as a stopwatch's time or a timer's
 * remaining minutes. The arc starts at the top and runs clockwise; changes of [color] animate, so a
 * state such as running or paused can recolor it.
 *
 * [progress] is read only when drawing, so a value that changes every frame costs no recomposition.
 *
 * @param progress How far round, 0 to 1
 * @param modifier Modifier for the ring; it is kept square
 * @param color The arc's color; the track is a faint copy of it
 * @param strokeFraction Thickness of the ring as a share of its diameter
 * @param content What sits in the middle of the ring
 */
@Composable
public fun PvotProgressRing(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = PvotProgressRingDefaults.color(),
    strokeFraction: Float = PvotProgressRingDefaults.StrokeFraction,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val animated by animateColorAsState(color, label = "pvotProgressRing")
    Box(
        modifier.aspectRatio(1f).semantics {
            progressBarRangeInfo = ProgressBarRangeInfo(progress().coerceIn(0f, 1f), 0f..1f)
        },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = size.minDimension * strokeFraction
            val inset = Offset(stroke / 2, stroke / 2)
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val style = Stroke(width = stroke)
            drawArc(
                color = animated.copy(alpha = PvotProgressRingDefaults.TrackAlpha),
                startAngle = 0f,
                sweepAngle = FullCircle,
                useCenter = false,
                topLeft = inset,
                size = arcSize,
                style = style,
            )
            drawArc(
                color = animated,
                startAngle = StartAngle,
                sweepAngle = progress().coerceIn(0f, 1f) * FullCircle,
                useCenter = false,
                topLeft = inset,
                size = arcSize,
                style = style,
            )
        }
        content()
    }
}

private const val FullCircle = 360f
private const val StartAngle = -90f
