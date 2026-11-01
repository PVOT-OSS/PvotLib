// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import com.prauga.pvot.designsystem.components.PvotSlideToActDefaults.decide
import org.junit.Assert.assertEquals
import org.junit.Test

/** Where a released slide-to-act puck goes. Up is negative; distances are 120 up, 56 down. */
class PvotSlideToActDecideTest {

    private fun go(offset: Float, velocity: Float = 0f, up: Float? = 120f, down: Float? = 56f) =
        decide(offset, velocity, up, down, flingVelocity = 1200f)

    @Test
    fun pastADistanceActs() {
        assertEquals(PvotSlideDecision.Up, go(-120f))
        assertEquals(PvotSlideDecision.Down, go(56f))
    }

    @Test
    fun shortOfADistanceSpringsBack() {
        assertEquals(PvotSlideDecision.None, go(-119f))
        assertEquals(PvotSlideDecision.None, go(55f))
        assertEquals(PvotSlideDecision.None, go(0f))
    }

    @Test
    fun aFlingActsAfterAThirdOfTheWay() {
        assertEquals(PvotSlideDecision.Up, go(-40f, velocity = -1200f))
        assertEquals(PvotSlideDecision.Down, go(19f, velocity = 1200f))
    }

    @Test
    fun aFlingTooEarlyOrTooSlowSpringsBack() {
        assertEquals(PvotSlideDecision.None, go(-39f, velocity = -5000f))
        assertEquals(PvotSlideDecision.None, go(-100f, velocity = -1199f))
    }

    @Test
    fun aFlingTheOtherWayDoesNotAct() {
        assertEquals(PvotSlideDecision.None, go(-60f, velocity = 3000f))
    }

    @Test
    fun aMissingTargetIsNeverChosen() {
        assertEquals(PvotSlideDecision.None, go(-500f, velocity = -5000f, up = null))
        assertEquals(PvotSlideDecision.None, go(500f, velocity = 5000f, down = null))
    }
}
