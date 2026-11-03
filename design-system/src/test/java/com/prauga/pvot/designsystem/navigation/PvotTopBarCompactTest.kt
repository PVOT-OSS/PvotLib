// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** When the top bar folds and unfolds as the list under it scrolls. The threshold is 8 px. */
class PvotTopBarCompactTest {

    private val threshold = 8f

    @Test
    fun anExpandedBarStaysExpandedUpToTheThreshold() {
        assertFalse(pvotTopBarCompact(false, 0, 0, threshold))
        assertFalse(pvotTopBarCompact(false, 0, 8, threshold))
    }

    @Test
    fun anExpandedBarFoldsPastTheThresholdOrOnceTheFirstItemIsGone() {
        assertTrue(pvotTopBarCompact(false, 0, 9, threshold))
        assertTrue(pvotTopBarCompact(false, 1, 0, threshold))
    }

    @Test
    fun aCompactBarStaysCompactUntilTheListIsBackAtTheTop() {
        assertTrue(pvotTopBarCompact(true, 0, 9, threshold))
        assertTrue(pvotTopBarCompact(true, 0, 3, threshold))
        assertTrue(pvotTopBarCompact(true, 0, 1, threshold))
        assertFalse(pvotTopBarCompact(true, 0, 0, threshold))
    }

    @Test
    fun scrollingToAndFroAroundTheThresholdNeverFlapsTheBar() {
        var compact = false
        val offsets = listOf(0, 5, 9, 12, 7, 9, 6, 10, 3, 0, 4, 0)
        val seen = mutableListOf<Boolean>()
        for (offset in offsets) {
            compact = pvotTopBarCompact(compact, 0, offset, threshold)
            seen += compact
        }
        // Folds at 9, holds through every dip, unfolds only at 0, and stays expanded under 8.
        assertEquals(
            listOf(false, false, true, true, true, true, true, true, true, false, false, false),
            seen,
        )
    }

    @Test
    fun theLargeTitleFoldsFromItsFullHeightToNothing() {
        assertEquals(100, pvotTopBarTitleHeight(100, 0f))
        assertEquals(50, pvotTopBarTitleHeight(100, 0.5f))
        assertEquals(0, pvotTopBarTitleHeight(100, 1f))
    }
}
