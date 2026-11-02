// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.background

import kotlin.math.PI
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The live background's drift steps at its frame rate, and the photo blur spreads color. */
class PvotBackgroundsTest {

    private val ms = 1_000_000L

    @Test
    fun aFrameTooSoonAfterTheLastIsSkipped() {
        assertNull(
            PvotBackgroundDefaults.stepPhase(
                nowNanos = 30 * ms,
                startNanos = 0,
                lastFrameNanos = 0,
                frameMillis = 66,
            )
        )
    }

    @Test
    fun aFrameAtTheIntervalAdvancesThePhase() {
        val phase =
            PvotBackgroundDefaults.stepPhase(
                nowNanos = 66 * ms,
                startNanos = 0,
                lastFrameNanos = 0,
                frameMillis = 66,
            )
        assertNotNull(phase)
        assertTrue(phase!! > 0f)
    }

    @Test
    fun thePhaseWrapsAfterOneLoop() {
        val loop = PvotBackgroundDefaults.LoopMillis * ms
        val quarter = PvotBackgroundDefaults.stepPhase(loop / 4, 0, 0, 16)!!
        val again = PvotBackgroundDefaults.stepPhase(loop + loop / 4, 0, 0, 16)!!
        assertEquals(PI.toFloat() / 2f, quarter, 0.001f)
        assertEquals(quarter, again, 0.001f)
    }

    @Test
    fun theBoxBlurSpreadsASinglePixel() {
        val width = 5
        val height = 5
        val pixels = IntArray(width * height)
        pixels[2 * width + 2] = 0xFFFFFFFF.toInt()
        boxBlurHorizontal(pixels, width, height, 1)
        boxBlurVertical(pixels, width, height, 1)
        // The centre pixel's white has been shared across its 3x3 neighbourhood.
        val centre = pixels[2 * width + 2] and 0xFF
        val corner = pixels[1 * width + 1] and 0xFF
        val far = pixels[0]
        assertTrue(centre in 1..254)
        assertEquals(centre, corner)
        assertEquals(0, far)
    }
}
