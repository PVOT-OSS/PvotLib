// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.list

import androidx.compose.runtime.MonotonicFrameClock
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Test

/** How a swipe row's position follows drags and settles. The row is 1000 wide both ways. */
class PvotSwipeRowStateTest {

    private val state =
        PvotSwipeRowState().apply {
            minOffset = -1000f
            maxOffset = 1000f
        }

    /** Runs frames 16 ms apart as fast as they are asked for. */
    private val clock =
        object : MonotonicFrameClock {
            private var nanos = 0L

            override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R {
                nanos += 16_000_000L
                return onFrame(nanos)
            }
        }

    @Test
    fun aDragStopsAtTheRowsEdge() {
        state.draggableState.dispatchRawDelta(5000f)
        assertEquals(1000f, state.offset, 0f)
    }

    @Test
    fun aSettleLandsExactlyOnItsTarget() = runBlocking {
        state.draggableState.dispatchRawDelta(123.4567f)
        withContext(clock) { state.settleTo(0f, velocity = -800f) }
        assertEquals(0f, state.offset, 0f)
    }

    @Test
    fun aResetPutsADismissedRowBack() = runBlocking {
        state.draggableState.dispatchRawDelta(-1000f)
        withContext(clock) { state.reset() }
        assertEquals(0f, state.offset, 0f)
        assertEquals(false, state.isDismissed)
    }
}
