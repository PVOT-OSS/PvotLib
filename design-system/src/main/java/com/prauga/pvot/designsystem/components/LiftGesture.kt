// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.positionChanged
import com.prauga.pvot.designsystem.motion.PvotMotion
import kotlin.math.abs

internal suspend fun PointerInputScope.detectLiftGesture(
    onLift: (Offset) -> Unit,
    onMove: (Offset) -> Unit,
    onRelease: (position: Offset, moved: Boolean) -> Unit,
    onCancel: () -> Unit,
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val start = down.position
        val slop = viewConfiguration.touchSlop
        var moved = false
        var claimed = false
        onLift(start)
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Main)
            val change = event.changes.firstOrNull { it.id == down.id }
            if (change == null) {
                onCancel()
                return@awaitEachGesture
            }
            if (change.changedToUpIgnoreConsumed()) {
                onRelease(change.position, moved)
                return@awaitEachGesture
            }
            if (change.isConsumed && change.positionChanged()) {
                onCancel()
                return@awaitEachGesture
            }
            val dx = change.position.x - start.x
            val dy = change.position.y - start.y
            if (!claimed) {
                val held = change.uptimeMillis - down.uptimeMillis >= PvotMotion.TouchHoldMillis
                when {
                    abs(dx) > slop && abs(dx) > abs(dy) -> claimed = true
                    held -> claimed = true
                    abs(dy) > slop -> {
                        onCancel()
                        return@awaitEachGesture
                    }
                }
            }
            if (claimed) {
                change.consume()
                if (abs(dx) > slop || abs(dy) > slop) moved = true
                onMove(change.position)
            }
        }
    }
}
