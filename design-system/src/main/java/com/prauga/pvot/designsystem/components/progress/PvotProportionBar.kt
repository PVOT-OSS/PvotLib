// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.theme.PvotShapes

/**
 * The colors of a [PvotProportionBar].
 *
 * @param segmentColors The color of each segment in order; segments past the last reuse it
 * @param trackColor The rest of the bar
 */
@Immutable
public data class PvotProportionBarColors(
    val segmentColors: List<Color>,
    val trackColor: Color
)

/** Defaults for [PvotProportionBar]. */
public object PvotProportionBarDefaults {

    /** Thickness of the bar. */
    public val Height: Dp = 8.dp

    /** Diameter of a [LegendDot]. */
    public val LegendDotSize: Dp = 10.dp

    /** How far each step after the first blends toward the track, in order. */
    public val TonalSteps: List<Float> = listOf(0f, 0.25f, 0.45f, 0.6f)

    /**
     * Steps of one hue, [base] first and each next one blended further toward [track], so the
     * parts of one bar read as one family; use these rather than a different hue per part.
     */
    @Composable
    public fun tonalColors(
        base: Color = MaterialTheme.colorScheme.primary,
        track: Color = MaterialTheme.colorScheme.surfaceContainerHigh
    ): PvotProportionBarColors = PvotProportionBarColors(
        segmentColors = tonalSteps(base, track),
        trackColor = track
    )

    /** The [TonalSteps] of [base] toward [track], with any transparency settled over the track. */
    public fun tonalSteps(base: Color, track: Color): List<Color> =
        TonalSteps.map { step -> lerp(base, track, step).compositeOver(track) }

    /** A dot in a segment's color, for a legend under the bar. */
    @Composable
    public fun LegendDot(color: Color, modifier: Modifier = Modifier) {
        Box(
            modifier
                .size(LegendDotSize)
                .clip(CircleShape)
                .background(color)
        )
    }
}

/**
 * A bar split into parts, such as incoming and outgoing calls, each a share of the whole.
 *
 * [segments] are fractions of the bar's length, laid end to end from the start; together they may
 * be less than 1, leaving track. By default the parts are tonal steps of one hue
 * ([PvotProportionBarDefaults.tonalColors]); name them in a legend with
 * [PvotProportionBarDefaults.LegendDot] in the same colors. The bar is decorative: say the numbers
 * in text next to it.
 *
 * @param segments Each part's share of the bar, 0 to 1
 * @param modifier Modifier for the bar; give it a width
 * @param height Thickness of the bar
 * @param colors The bar's colors
 */
@Composable
public fun PvotProportionBar(
    segments: List<Float>,
    modifier: Modifier = Modifier,
    height: Dp = PvotProportionBarDefaults.Height,
    colors: PvotProportionBarColors = PvotProportionBarDefaults.tonalColors()
) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Canvas(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(PvotShapes.Pill)
    ) {
        drawRect(colors.trackColor)
        var start = 0f
        segments.forEachIndexed { index, fraction ->
            val width = size.width * fraction.coerceIn(0f, 1f)
            if (width <= 0f) return@forEachIndexed
            val color = colors.segmentColors.getOrElse(index) { colors.segmentColors.last() }
            val x = if (rtl) size.width - start - width else start
            drawRect(color, topLeft = Offset(x, 0f), size = Size(width, size.height))
            start += width
        }
    }
}
