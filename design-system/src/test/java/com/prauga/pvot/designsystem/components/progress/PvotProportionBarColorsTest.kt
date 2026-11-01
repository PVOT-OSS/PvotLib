// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.progress

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.prauga.pvot.designsystem.theme.PvotPrimaryDark
import com.prauga.pvot.designsystem.theme.PvotPrimaryLight
import com.prauga.pvot.designsystem.theme.PvotSecondaryDark
import com.prauga.pvot.designsystem.theme.PvotSecondaryLight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The steps of one hue a proportion bar's parts are drawn in. */
class PvotProportionBarColorsTest {

    private val darkTrack = Color(0xFF2B2930)
    private val lightTrack = Color(0xFFECE6F0)

    @Test
    fun theFirstStepIsTheBaseColor() {
        val steps = PvotProportionBarDefaults.tonalSteps(PvotSecondaryDark, darkTrack)
        assertEquals(PvotSecondaryDark, steps.first())
    }

    @Test
    fun eachStepIsCloserToTheTrackThanTheOneBefore() {
        for ((base, track) in pairs()) {
            val distances = PvotProportionBarDefaults.tonalSteps(base, track).map { contrast(it, track) }
            assertTrue("steps of $base don't fade toward the track: $distances", distances.zipWithNext().all { (a, b) -> a > b })
        }
    }

    @Test
    fun theFirstStepStandsOutFromTheTrack() {
        for ((base, track) in pairs()) {
            val first = PvotProportionBarDefaults.tonalSteps(base, track).first()
            assertTrue("$base on $track is only ${contrast(first, track)}:1", contrast(first, track) >= MinFirstContrast)
        }
    }

    @Test
    fun theLastStepStillStandsOutFromTheTrack() {
        for ((base, track) in pairs()) {
            val last = PvotProportionBarDefaults.tonalSteps(base, track).last()
            assertTrue("last step of $base on $track is only ${contrast(last, track)}:1", contrast(last, track) >= MinLastContrast)
        }
    }

    @Test
    fun stepsAreOpaque() {
        val steps = PvotProportionBarDefaults.tonalSteps(PvotPrimaryDark.copy(alpha = 0.5f), darkTrack)
        assertTrue(steps.all { it.alpha == 1f })
    }

    private fun pairs() = listOf(
        PvotPrimaryDark to darkTrack,
        PvotSecondaryDark to darkTrack,
        PvotPrimaryLight to lightTrack,
        PvotSecondaryLight to lightTrack,
    )

    private fun contrast(a: Color, b: Color): Float {
        val (hi, lo) = listOf(a.luminance(), b.luminance()).sortedDescending()
        return (hi + 0.05f) / (lo + 0.05f)
    }

    private companion object {
        const val MinFirstContrast = 3f
        const val MinLastContrast = 1.5f
    }
}
