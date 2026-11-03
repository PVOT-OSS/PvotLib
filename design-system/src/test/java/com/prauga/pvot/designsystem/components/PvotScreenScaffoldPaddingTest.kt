// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import com.prauga.pvot.designsystem.navigation.pvotTopBarTitleHeight
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * How the scaffold pads its content under the top bar. The bar is 64 px of row over a 24 px status
 * bar with a 60 px large title: 148 px expanded, 88 px compact.
 */
class PvotScreenScaffoldPaddingTest {

    private val statusBar = 24
    private val row = 64
    private val title = 60

    /**
     * The bar as the scaffold measures it, and what the title reports folded away, at [progress].
     */
    private fun paddingAt(progress: Float): Int {
        val laidOut = pvotTopBarTitleHeight(title, progress)
        val measuredBar = statusBar + row + laidOut
        return pvotScaffoldTopPadding(
            measuredBar,
            foldedHeight = title - laidOut,
            statusBar = statusBar,
        )
    }

    @Test
    fun thePaddingIsTheExpandedBarWhileExpanded() {
        assertEquals(148, paddingAt(0f))
    }

    @Test
    fun thePaddingStaysTheExpandedBarThroughTheFold() {
        for (step in 0..10) {
            assertEquals("at ${step / 10f}", 148, paddingAt(step / 10f))
        }
    }

    @Test
    fun thePaddingStaysTheExpandedBarOnceCompact() {
        assertEquals(148, paddingAt(1f))
    }

    @Test
    fun aBarThatDoesNotFoldPadsByItsOwnHeight() {
        assertEquals(
            64,
            pvotScaffoldTopPadding(topBarHeight = 64, foldedHeight = 0, statusBar = 24),
        )
    }

    @Test
    fun aScreenWithNoBarStillClearsTheStatusBar() {
        assertEquals(24, pvotScaffoldTopPadding(topBarHeight = 0, foldedHeight = 0, statusBar = 24))
    }
}
