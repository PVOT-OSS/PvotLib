// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.background

import android.animation.ValueAnimator
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.PvotGlassIntensity
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** How [PvotLiveBackground] moves. */
public enum class PvotBackgroundMotion {
    /** Every frame; for a demo, costly under glass because each frame re-records every glass. */
    Live,

    /** About fifteen frames a second: the drift still reads, at a quarter of the cost. */
    Slow,

    /** No motion: the background is drawn once, where its phase leaves it. */
    Still,
}

/** Defaults for [PvotLiveBackground], [PvotGradientBackground] and [PvotPhotoBackground]. */
public object PvotBackgroundDefaults {

    /** How long one full drift of the live background takes. */
    public const val LoopMillis: Int = 30_000

    /** The frame interval of [PvotBackgroundMotion.Slow]. */
    public const val SlowFrameMillis: Int = 66

    /** The frame interval of [PvotBackgroundMotion.Live]. */
    public const val LiveFrameMillis: Int = 16

    /** How many blobs of color drift across the live background. */
    public const val BlobCount: Int = 5

    /** How dark the scrim over a photo background is, so text and controls over it stay readable. */
    public const val PhotoScrimAlpha: Float = 0.45f

    /** How much a photo background is blurred, at the size it is shown. */
    public val PhotoBlurRadius: Dp = 24.dp

    /** By how much a photo is shrunk before it is blurred, which makes the blur cheap. */
    public const val PhotoDownscale: Int = 8

    /**
     * The colors of the live background's blobs, from [colorScheme]'s container roles with the
     * primary color among them, so the drift reads as the app's own hue over its background.
     */
    @Composable
    public fun colors(colorScheme: ColorScheme = MaterialTheme.colorScheme): List<Color> = listOf(
        colorScheme.primaryContainer,
        colorScheme.tertiaryContainer,
        colorScheme.primary.copy(alpha = 0.7f),
        colorScheme.secondaryContainer,
        colorScheme.primaryContainer.copy(alpha = 0.8f),
    )

    /**
     * How the live background should move here: [PvotBackgroundMotion.Still] when the user has
     * turned animations off or the device asks for less glass, [PvotBackgroundMotion.Slow]
     * otherwise.
     */
    @Composable
    public fun motion(): PvotBackgroundMotion =
        if (!ValueAnimator.areAnimatorsEnabled() || PvotGlassIntensity.isReduced()) {
            PvotBackgroundMotion.Still
        } else {
            PvotBackgroundMotion.Slow
        }

    /**
     * The phase of the drift at [nowNanos], or null when the frame is too soon after
     * [lastFrameNanos] for [frameMillis].
     */
    internal fun stepPhase(
        nowNanos: Long,
        startNanos: Long,
        lastFrameNanos: Long,
        frameMillis: Int,
        loopMillis: Int = LoopMillis,
    ): Float? {
        if (nowNanos - lastFrameNanos < frameMillis * 1_000_000L) return null
        val loopNanos = loopMillis * 1_000_000L
        return ((nowNanos - startNanos) % loopNanos).toFloat() / loopNanos * (2f * PI.toFloat())
    }
}

private val BlobSpeeds = floatArrayOf(3f, -2f, 4f, -3f, 2f)

/**
 * The app's background with a slow drift of soft color across it, for glass to refract.
 *
 * It is the base of a screen: put it inside the glass backdrop, under the content, as
 * [PvotScreenScaffold][com.prauga.pvot.designsystem.components.PvotScreenScaffold] does. Each
 * frame of the drift re-records every glass over it, so it moves slowly by default and not at
 * all when animations are off.
 *
 * @param modifier Modifier for the background; give it the size to fill
 * @param colors The blobs' colors
 * @param base The solid color under the blobs, the screen's background
 * @param motion How the drift moves
 * @param seed Where the drift starts, 0 to 2π, so two backgrounds need not look the same
 */
@Composable
public fun PvotLiveBackground(
    modifier: Modifier = Modifier,
    colors: List<Color> = PvotBackgroundDefaults.colors(),
    base: Color = MaterialTheme.colorScheme.background,
    motion: PvotBackgroundMotion = PvotBackgroundDefaults.motion(),
    seed: Float = 0f,
) {
    var phase by remember { mutableFloatStateOf(seed) }
    LaunchedEffect(motion, seed) {
        if (motion == PvotBackgroundMotion.Still) {
            phase = seed
            return@LaunchedEffect
        }
        val frameMillis = when (motion) {
            PvotBackgroundMotion.Live -> PvotBackgroundDefaults.LiveFrameMillis
            else -> PvotBackgroundDefaults.SlowFrameMillis
        }
        val start = withFrameNanos { it }
        var last = 0L
        while (true) {
            withFrameNanos { now ->
                PvotBackgroundDefaults.stepPhase(now, start, last, frameMillis)?.let {
                    last = now
                    phase = seed + it
                }
            }
        }
    }
    Canvas(modifier) {
        drawRect(base)
        colors.forEachIndexed { index, color ->
            val speed = BlobSpeeds[index % BlobSpeeds.size]
            val angle = phase * speed + index * 1.3f
            val center = Offset(
                size.width * (0.5f + 0.4f * cos(angle)),
                size.height * (0.5f + 0.4f * sin(angle * 2f + index))
            )
            val radius = size.width * (0.45f + 0.08f * sin(phase * 2f + index))
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(color, color.copy(alpha = color.alpha * 0.6f), color.copy(alpha = 0f)),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
        }
    }
}

/**
 * The app's background with a still gradient across it, where the drift of [PvotLiveBackground]
 * is not wanted: a screen that must not move, or one with no scrolling content under its glass.
 *
 * @param modifier Modifier for the background; give it the size to fill
 * @param colors The gradient's stops, over [base]
 * @param base The solid color under the gradient
 * @param angle The gradient's direction in degrees, 0 left to right, 90 top to bottom
 */
@Composable
public fun PvotGradientBackground(
    modifier: Modifier = Modifier,
    colors: List<Color> = PvotBackgroundDefaults.colors().take(2),
    base: Color = MaterialTheme.colorScheme.background,
    angle: Float = 135f,
) {
    Canvas(modifier) {
        drawRect(base)
        val radians = angle / 180f * PI.toFloat()
        val direction = Offset(cos(radians), sin(radians))
        val half = Offset(size.width / 2f, size.height / 2f)
        val reach = (size.width * kotlin.math.abs(direction.x) + size.height * kotlin.math.abs(direction.y)) / 2f
        val start = half - direction * reach
        val end = half + direction * reach
        drawRect(Brush.linearGradient(colors + Color.Transparent, start = start, end = end))
    }
}

/**
 * A photo, blurred and dimmed, as the background of a screen about that photo, such as a call
 * with a contact's picture.
 *
 * Blur the photo once with [preparePvotPhotoBackground] off the main thread and pass the result;
 * this draws it as it is, so the backdrop never blurs again. With no [image] it shows [fallback].
 *
 * @param image The blurred photo, or null while it loads or when there is none
 * @param modifier Modifier for the background; give it the size to fill
 * @param scrim The dim over the photo, so what sits on it stays readable
 * @param fallback What to show without a photo
 */
@Composable
public fun PvotPhotoBackground(
    image: ImageBitmap?,
    modifier: Modifier = Modifier,
    scrim: Color = Color.Black.copy(alpha = PvotBackgroundDefaults.PhotoScrimAlpha),
    fallback: @Composable () -> Unit = { PvotGradientBackground(Modifier.fillMaxSize()) },
) {
    Box(modifier) {
        if (image == null) {
            fallback()
        } else {
            Image(
                bitmap = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(Modifier.fillMaxSize().background(scrim))
        }
    }
}
