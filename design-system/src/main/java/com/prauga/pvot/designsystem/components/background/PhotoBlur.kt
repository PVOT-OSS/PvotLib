// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.background

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Blurs [source] for [PvotPhotoBackground]: shrinks it by [downscale], then blurs it with three box
 * passes, which together read as a Gaussian blur. The work runs off the main thread, once; the
 * small result is drawn scaled up, so the blur is free from then on.
 *
 * @param source The photo
 * @param density The density the background is shown at, to turn [blurRadius] into pixels
 * @param downscale By how much the photo is shrunk first
 * @param blurRadius The blur, in dp at the size the background is shown
 * @return The blurred photo, [downscale] times smaller than [source]
 */
public suspend fun preparePvotPhotoBackground(
    source: Bitmap,
    density: Density,
    downscale: Int = PvotBackgroundDefaults.PhotoDownscale,
    blurRadius: Dp = PvotBackgroundDefaults.PhotoBlurRadius,
): ImageBitmap =
    withContext(Dispatchers.Default) {
        val width = max(1, source.width / downscale)
        val height = max(1, source.height / downscale)
        val small = Bitmap.createScaledBitmap(source, width, height, true)
        val pixels = IntArray(width * height)
        small.getPixels(pixels, 0, width, 0, 0, width, height)
        val radius = (with(density) { blurRadius.toPx() } / downscale).roundToInt()
        if (radius > 0) {
            repeat(BlurPasses) {
                boxBlurHorizontal(pixels, width, height, radius)
                boxBlurVertical(pixels, width, height, radius)
            }
        }
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out.asImageBitmap()
    }

private const val BlurPasses = 3

internal fun boxBlurHorizontal(pixels: IntArray, width: Int, height: Int, radius: Int) {
    val row = IntArray(width)
    val span = radius * 2 + 1
    for (y in 0 until height) {
        val offset = y * width
        var a = 0
        var r = 0
        var g = 0
        var b = 0
        for (x in -radius..radius) {
            val p = pixels[offset + x.coerceIn(0, width - 1)]
            a += p ushr 24
            r += p shr 16 and 0xFF
            g += p shr 8 and 0xFF
            b += p and 0xFF
        }
        for (x in 0 until width) {
            row[x] = (a / span shl 24) or (r / span shl 16) or (g / span shl 8) or (b / span)
            val leaving = pixels[offset + (x - radius).coerceIn(0, width - 1)]
            val entering = pixels[offset + (x + radius + 1).coerceIn(0, width - 1)]
            a += (entering ushr 24) - (leaving ushr 24)
            r += (entering shr 16 and 0xFF) - (leaving shr 16 and 0xFF)
            g += (entering shr 8 and 0xFF) - (leaving shr 8 and 0xFF)
            b += (entering and 0xFF) - (leaving and 0xFF)
        }
        System.arraycopy(row, 0, pixels, offset, width)
    }
}

internal fun boxBlurVertical(pixels: IntArray, width: Int, height: Int, radius: Int) {
    val column = IntArray(height)
    val span = radius * 2 + 1
    for (x in 0 until width) {
        var a = 0
        var r = 0
        var g = 0
        var b = 0
        for (y in -radius..radius) {
            val p = pixels[y.coerceIn(0, height - 1) * width + x]
            a += p ushr 24
            r += p shr 16 and 0xFF
            g += p shr 8 and 0xFF
            b += p and 0xFF
        }
        for (y in 0 until height) {
            column[y] = (a / span shl 24) or (r / span shl 16) or (g / span shl 8) or (b / span)
            val leaving = pixels[(y - radius).coerceIn(0, height - 1) * width + x]
            val entering = pixels[(y + radius + 1).coerceIn(0, height - 1) * width + x]
            a += (entering ushr 24) - (leaving ushr 24)
            r += (entering shr 16 and 0xFF) - (leaving shr 16 and 0xFF)
            g += (entering shr 8 and 0xFF) - (leaving shr 8 and 0xFF)
            b += (entering and 0xFF) - (leaving and 0xFF)
        }
        for (y in 0 until height) pixels[y * width + x] = column[y]
    }
}
