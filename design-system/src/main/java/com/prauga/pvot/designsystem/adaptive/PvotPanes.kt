// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt

/**
 * Two panes side by side, such as a list and the item it opened. On a device folded like a book the
 * panes meet at the hinge, so neither is split by it; otherwise [first] takes [firstWeight] of the
 * width and [second] the rest, [gap] apart. Show it when [PvotWindowLayout.twoPane] is true.
 *
 * @param first The start pane
 * @param second The end pane
 * @param modifier Modifier for the panes together; give them a size
 * @param firstWeight Share of the width for [first] when there is no hinge
 * @param gap Space between the panes when there is no hinge
 */
@Composable
public fun PvotTwoPane(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    firstWeight: Float = PvotAdaptiveDefaults.FirstPaneWeight,
    gap: Dp = PvotAdaptiveDefaults.PaneGap,
) {
    val layout = rememberPvotWindowLayout()
    val hinge = layout.hinge?.takeIf { layout.book }
    var origin by remember { mutableStateOf(Offset.Zero) }
    Layout(
        contents = listOf(first, second),
        modifier = modifier.onGloballyPositioned { origin = it.positionInWindow() },
    ) { (firstMeasurables, secondMeasurables), constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val gapPx = gap.roundToPx()
        val (firstWidth, secondStart) =
            if (hinge != null) {
                val left = (hinge.bounds.left - origin.x).roundToInt().coerceIn(0, width)
                val right = (hinge.bounds.right - origin.x).roundToInt().coerceIn(left, width)
                left to right
            } else {
                val w = ((width - gapPx) * firstWeight).roundToInt()
                w to w + gapPx
            }
        val secondWidth = (width - secondStart).coerceAtLeast(0)
        val firstPlaceables =
            firstMeasurables.map {
                it.measure(Constraints(maxWidth = firstWidth, maxHeight = height))
            }
        val secondPlaceables =
            secondMeasurables.map {
                it.measure(Constraints(maxWidth = secondWidth, maxHeight = height))
            }
        layout(width, height) {
            firstPlaceables.forEach { it.place(0, 0) }
            secondPlaceables.forEach { it.place(secondStart, 0) }
        }
    }
}

/**
 * The tabletop posture of a half-folded device: [top] on the upright half above the hinge, for what
 * to look at, and [bottom] on the flat half below it, for the controls. Without a hinge the two
 * halves split the height evenly. Show it when [PvotWindowLayout.tabletop] is true.
 *
 * @param top What to look at, above the hinge
 * @param bottom The controls, below the hinge
 * @param modifier Modifier for the halves together; give them a size
 */
@Composable
public fun PvotTabletopSplit(
    top: @Composable () -> Unit,
    bottom: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hinge = rememberPvotWindowLayout().hinge
    var origin by remember { mutableStateOf(Offset.Zero) }
    Layout(
        contents = listOf(top, bottom),
        modifier = modifier.onGloballyPositioned { origin = it.positionInWindow() },
    ) { (topMeasurables, bottomMeasurables), constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val hingeTop =
            hinge?.let { (it.bounds.top - origin.y).roundToInt().coerceIn(0, height) } ?: height / 2
        val hingeBottom =
            hinge?.let { (it.bounds.bottom - origin.y).roundToInt().coerceIn(hingeTop, height) }
                ?: hingeTop
        val topPlaceables =
            topMeasurables.map { it.measure(Constraints(maxWidth = width, maxHeight = hingeTop)) }
        val bottomPlaceables =
            bottomMeasurables.map {
                it.measure(Constraints(maxWidth = width, maxHeight = height - hingeBottom))
            }
        layout(width, height) {
            topPlaceables.forEach { it.place(0, 0) }
            bottomPlaceables.forEach { it.place(0, hingeBottom) }
        }
    }
}

/**
 * A single column that stays readable on a wide window: [content] grows to [maxWidth] and is
 * centred, instead of stretching edge to edge on a tablet.
 *
 * @param modifier Modifier for the full-width container
 * @param maxWidth Widest the column grows
 * @param content The column
 */
@Composable
public fun PvotCenteredContent(
    modifier: Modifier = Modifier,
    maxWidth: Dp = PvotAdaptiveDefaults.ContentMaxWidth,
    content: @Composable () -> Unit,
) {
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Box(Modifier.widthIn(max = maxWidth).fillMaxWidth()) { content() }
    }
}
