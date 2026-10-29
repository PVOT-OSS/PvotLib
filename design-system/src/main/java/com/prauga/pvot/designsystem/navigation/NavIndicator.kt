// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.navigation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Rect

/** One item as the bar sees it: where it sits in the bar's item row and what tapping it does. */
@Stable
internal class NavItemEntry {
    var bounds by mutableStateOf(Rect.Zero)
    var selected by mutableStateOf(false)
    var onClick: () -> Unit = {}
}

/**
 * What the bar and its items share: the items' positions, which one the finger is over while
 * dragging, and so where the indicator slides to.
 */
@Stable
internal class NavBarState {
    val entries = mutableStateListOf<NavItemEntry>()

    /** How compact the bar is, from 0 (full) to 1 (compact), animated by the bar. */
    var compactFraction by mutableStateOf(0f)

    /** The item under the finger during a drag, or null when not dragging. */
    var hovered by mutableStateOf<NavItemEntry?>(null)

    /** The item the indicator sits behind: the hovered one, else the selected one. */
    val active: NavItemEntry?
        get() = hovered ?: entries.firstOrNull { it.selected }

    /** The item whose bounds hold [x], or the nearest one when [x] is past either end. */
    fun entryAt(x: Float): NavItemEntry? = entries
        .filter { it.bounds != Rect.Zero }
        .minByOrNull { entry ->
            val b = entry.bounds
            if (x in b.left..b.right) 0f else minOf(kotlin.math.abs(x - b.left), kotlin.math.abs(x - b.right))
        }
}

internal val LocalNavBarState = staticCompositionLocalOf<NavBarState?> { null }
