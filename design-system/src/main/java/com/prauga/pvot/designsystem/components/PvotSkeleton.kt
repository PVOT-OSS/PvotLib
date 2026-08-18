// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp

/** Defaults for [PvotSkeleton]. */
object PvotSkeletonDefaults {

    val Shape: Shape = RoundedCornerShape(8.dp)

    /** One full sweep of the highlight across the placeholder. */
    const val DurationMillis = 1100

    /** Fraction of the placeholder's width covered by the moving highlight. */
    const val BandFraction = 0.6f

    val baseColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

    val highlightColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f)
}

/**
 * A shimmering placeholder standing in for content that has not arrived.
 *
 * Lay these out in the shape of the real content so the screen does not jump when
 * the data lands, then reveal the content with pvotReveal.
 *
 * The placeholder has no intrinsic size, so [modifier] must give it one:
 *
 * ```
 * PvotSkeleton(Modifier.fillMaxWidth().height(20.dp))
 * ```
 *
 * It is hidden from accessibility services, which should hear the loading state
 * from the screen rather than from each placeholder.
 *
 * @param modifier Modifier for the placeholder, which must set its size
 * @param shape The placeholder's shape
 * @param baseColor Fill behind the highlight
 * @param highlightColor Color of the band sweeping across the placeholder
 */
@Composable
fun PvotSkeleton(
    modifier: Modifier,
    shape: Shape = PvotSkeletonDefaults.Shape,
    baseColor: Color = PvotSkeletonDefaults.baseColor,
    highlightColor: Color = PvotSkeletonDefaults.highlightColor
) {
    val transition = rememberInfiniteTransition(label = "pvotSkeleton")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(PvotSkeletonDefaults.DurationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Box(
        modifier = modifier
            .clearAndSetSemantics { }
            .clip(shape)
            .drawBehind {
                drawRect(baseColor)

                val bandWidth = size.width * PvotSkeletonDefaults.BandFraction
                val startX = -bandWidth + progress.value * (size.width + bandWidth * 2f)
                drawRect(
                    brush = Brush.horizontalGradient(
                        0f to Color.Transparent,
                        0.5f to highlightColor,
                        1f to Color.Transparent,
                        startX = startX,
                        endX = startX + bandWidth
                    )
                )
            }
    )
}
