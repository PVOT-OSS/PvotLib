// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.adaptive

import android.graphics.Rect
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/** How wide the window is, in the breakpoints of [PvotAdaptiveDefaults]. */
public enum class PvotWindowWidth {
    /** A phone held upright: one column. */
    Compact,

    /** A small tablet, an unfolded phone or a phone on its side: room for two panes. */
    Medium,

    /** A tablet or a desktop window: two panes with room to spare. */
    Expanded,
}

/** How tall the window is, in the breakpoints of [PvotAdaptiveDefaults]. */
public enum class PvotWindowHeight {
    /** A phone on its side: little room above and below the content. */
    Compact,

    /** Most phones upright and tablets on their side. */
    Medium,

    /** A tablet upright or a tall desktop window. */
    Expanded,
}

/**
 * The fold of a foldable device, as the window sees it.
 *
 * @param bounds The hinge in window coordinates, in pixels
 * @param horizontal Whether the hinge runs side to side, so the window folds into a top and a
 *   bottom half
 * @param halfOpened Whether the device is partly folded, like a laptop or a book
 * @param separating Whether the hinge splits the window into two separate areas
 */
@Immutable
public class PvotHinge(
    public val bounds: Rect,
    public val horizontal: Boolean,
    public val halfOpened: Boolean,
    public val separating: Boolean,
)

/**
 * The window's size class and fold, for choosing between a single column, two panes and a layout
 * split at the hinge.
 *
 * @param width How wide the window is
 * @param hinge The fold, or null on a device without one or while it lies flat in no posture
 * @param height How tall the window is; when [PvotWindowHeight.Compact], keep bars slim and size
 *   content to the height as well as the width
 */
@Immutable
public data class PvotWindowLayout(
    val width: PvotWindowWidth,
    val hinge: PvotHinge?,
    val height: PvotWindowHeight = PvotWindowHeight.Medium,
) {

    /** Half folded with the hinge across: the top half stands up, the bottom half lies flat. */
    public val tabletop: Boolean
        get() = hinge?.let { it.horizontal && it.halfOpened } == true

    /** Folded like a book, or split by a hinge down the middle. */
    public val book: Boolean
        get() = hinge?.let { !it.horizontal && (it.halfOpened || it.separating) } == true

    /** Whether to show two panes side by side: a medium or wider window, or a book fold. */
    public val twoPane: Boolean
        get() = width != PvotWindowWidth.Compact || book
}

/** Breakpoints and sizes for adaptive layouts. */
public object PvotAdaptiveDefaults {

    /** Narrowest window that counts as [PvotWindowWidth.Medium]. */
    public val MediumWidth: Dp = 600.dp

    /** Narrowest window that counts as [PvotWindowWidth.Expanded]. */
    public val ExpandedWidth: Dp = 840.dp

    /** Shortest window that counts as [PvotWindowHeight.Medium]. */
    public val MediumHeight: Dp = 480.dp

    /** Shortest window that counts as [PvotWindowHeight.Expanded]. */
    public val ExpandedHeight: Dp = 900.dp

    /** Widest a single column of content grows on a wide window. */
    public val ContentMaxWidth: Dp = 640.dp

    /** Gap between two panes. */
    public val PaneGap: Dp = 24.dp

    /** Share of the width the first pane takes when there is no hinge to split at. */
    public const val FirstPaneWeight: Float = 0.45f
}

/**
 * The current window's [PvotWindowLayout]. The size follows the window as it resizes; the hinge
 * comes from Jetpack WindowManager and is null outside an activity.
 */
@Composable
public fun rememberPvotWindowLayout(): PvotWindowLayout {
    val activity = LocalActivity.current
    val size = LocalWindowInfo.current.containerSize
    val widthDp = with(LocalDensity.current) { size.width.toDp() }
    val heightDp = with(LocalDensity.current) { size.height.toDp() }
    val hinges =
        remember(activity) {
            if (activity == null) {
                flowOf(null)
            } else {
                WindowInfoTracker.getOrCreate(activity).windowLayoutInfo(activity).map { info ->
                    info.displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()?.let {
                        PvotHinge(
                            bounds = it.bounds,
                            horizontal = it.orientation == FoldingFeature.Orientation.HORIZONTAL,
                            halfOpened = it.state == FoldingFeature.State.HALF_OPENED,
                            separating = it.isSeparating,
                        )
                    }
                }
            }
        }
    val hinge by hinges.collectAsState(initial = null)
    val width =
        when {
            widthDp >= PvotAdaptiveDefaults.ExpandedWidth -> PvotWindowWidth.Expanded
            widthDp >= PvotAdaptiveDefaults.MediumWidth -> PvotWindowWidth.Medium
            else -> PvotWindowWidth.Compact
        }
    val height =
        when {
            heightDp >= PvotAdaptiveDefaults.ExpandedHeight -> PvotWindowHeight.Expanded
            heightDp >= PvotAdaptiveDefaults.MediumHeight -> PvotWindowHeight.Medium
            else -> PvotWindowHeight.Compact
        }
    return PvotWindowLayout(width, hinge, height)
}
