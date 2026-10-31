// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.prauga.pvot.designsystem.R
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.theme.PvotSpacing
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/** The heights a [PvotPersistentSheet] rests at. */
public enum class PvotSheetDetent {
    /** Only the peek of the sheet shows, such as a search bar. */
    Collapsed,

    /** The sheet covers part of the screen. */
    Medium,

    /** The sheet fills the space it is given. */
    Expanded
}

/** Defaults for [PvotPersistentSheet]. */
public object PvotPersistentSheetDefaults {

    /** Share of the available height the sheet covers at [PvotSheetDetent.Medium]. */
    public const val MediumFraction: Float = 0.5f

    /** Corner shape of the sheet, rounded at the top only. */
    public val Shape: Shape = PvotBottomSheetDefaults.Shape

    /** Gap between a floating sheet and the edges of the space it moves in. */
    public val FloatingMargin: Dp = PvotSpacing.L

    /** Corner radius of a floating sheet once it is open; collapsed, it is a capsule. */
    public val FloatingCornerRadius: Dp = 32.dp

    /** Width of a floating sheet's drag handle, wider so the collapsed bar reads as grabbable. */
    public val FloatingHandleWidth: Dp = PvotBottomSheetDefaults.HandleWidth * 1.5f

    /** Width of the hairline around a floating sheet. */
    public val FloatingOutlineWidth: Dp = 1.dp

    /** Hairline around a floating sheet, so it holds its edge over busy content. */
    public val floatingOutlineColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f)

    /** Fill of the sheet. */
    public val containerColor: Color
        @Composable get() = PvotBottomSheetDefaults.containerColor
}

/**
 * State of a [PvotPersistentSheet]: which detent it rests at and where its top edge is.
 *
 * Create it with [rememberPvotPersistentSheetState].
 */
@Stable
public class PvotPersistentSheetState internal constructor(initialDetent: PvotSheetDetent) {

    internal val draggable: AnchoredDraggableState<PvotSheetDetent> = AnchoredDraggableState(initialDetent)

    /** The detent the sheet rests at, or is closest to while it moves. */
    public val currentDetent: PvotSheetDetent
        get() = draggable.currentValue

    /** The detent the sheet is heading for while it is dragged or animated. */
    public val targetDetent: PvotSheetDetent
        get() = draggable.targetValue

    /**
     * Distance in pixels from the top of the sheet's layout to its top edge, or `NaN` before the
     * sheet is first laid out. Use it to keep other content just above the sheet.
     */
    public val offset: Float
        get() = draggable.offset

    /** Slides the sheet to [detent]. */
    public suspend fun animateTo(detent: PvotSheetDetent) {
        draggable.animateTo(detent, spring())
    }

    /** Moves the sheet to [detent] at once. */
    public suspend fun snapTo(detent: PvotSheetDetent) {
        draggable.snapTo(detent)
    }

    internal companion object {
        val Saver: Saver<PvotPersistentSheetState, String> = Saver(
            save = { it.currentDetent.name },
            restore = { PvotPersistentSheetState(PvotSheetDetent.valueOf(it)) }
        )
    }
}

/**
 * Remembers a [PvotPersistentSheetState] across recomposition and configuration changes.
 *
 * @param initialDetent The detent the sheet starts at
 */
@Composable
public fun rememberPvotPersistentSheetState(
    initialDetent: PvotSheetDetent = PvotSheetDetent.Collapsed
): PvotPersistentSheetState = rememberSaveable(saver = PvotPersistentSheetState.Saver) {
    PvotPersistentSheetState(initialDetent)
}

/**
 * A bottom sheet that stays on screen over content that remains usable, like the search panel of
 * a map. It rests at three detents: [collapsed][PvotSheetDetent.Collapsed], showing only
 * [peekHeight]; [medium][PvotSheetDetent.Medium]; and [expanded][PvotSheetDetent.Expanded],
 * filling the space it is given. Drag it, or fling it, and it springs to the nearest detent.
 *
 * Nothing behind it is dimmed or blocked. It fills its parent, so place it last in a `Box` over
 * the content; pad the parent or pass `Modifier.statusBarsPadding()` to keep the expanded sheet
 * below the status bar. The content does not scroll inside the sheet.
 *
 * With [floating] the sheet is an outlined card of glass over [backdrop]: collapsed it is a capsule
 * inset from the edges and kept above the bottom [windowInsets], like a floating search bar; as it
 * opens it grows to the full width and down to the bottom edge.
 *
 * @param state The sheet's detent and position, from [rememberPvotPersistentSheetState]
 * @param peekHeight Height that shows when collapsed, above the bottom [windowInsets]
 * @param modifier Modifier for the area the sheet moves in
 * @param mediumFraction Share of the available height covered at the medium detent
 * @param windowInsets Insets the sheet keeps clear of at the bottom, the navigation bar by default
 * @param floating Whether the sheet floats as an inset card of glass rather than filling the width
 * @param backdrop The content behind a [floating] sheet, marked with
 * [pvotGlassBackdrop][com.prauga.pvot.designsystem.effects.pvotGlassBackdrop], or null for the
 * glass tint alone
 * @param containerColor Fill of the sheet when it is not [floating]
 * @param content The sheet's content, below the drag handle
 */
@Composable
public fun PvotPersistentSheet(
    state: PvotPersistentSheetState,
    peekHeight: Dp,
    modifier: Modifier = Modifier,
    mediumFraction: Float = PvotPersistentSheetDefaults.MediumFraction,
    windowInsets: WindowInsets = WindowInsets.navigationBars,
    floating: Boolean = false,
    backdrop: PvotGlassBackdrop? = null,
    containerColor: Color = PvotPersistentSheetDefaults.containerColor,
    content: @Composable ColumnScope.() -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val margin = if (floating) PvotPersistentSheetDefaults.FloatingMargin else 0.dp
    val bottomGap = if (floating) windowInsets.getBottom(density) + with(density) { margin.toPx() } else 0f
    val peek = with(density) { peekHeight.toPx() } + if (floating) bottomGap else windowInsets.getBottom(density).toFloat()
    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(state.draggable)
    val interactionSource = remember { MutableInteractionSource() }
    val actionLabels = mapOf(
        PvotSheetDetent.Collapsed to stringResource(R.string.pvot_sheet_action_collapse),
        PvotSheetDetent.Medium to stringResource(R.string.pvot_sheet_action_medium),
        PvotSheetDetent.Expanded to stringResource(R.string.pvot_sheet_action_expand)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                val height = size.height.toFloat()
                val top = with(density) { margin.toPx() }
                state.draggable.updateAnchors(
                    DraggableAnchors {
                        PvotSheetDetent.Collapsed at (height - peek).coerceAtLeast(top)
                        PvotSheetDetent.Medium at height * (1f - mediumFraction)
                        PvotSheetDetent.Expanded at top
                    }
                )
            },
        contentAlignment = Alignment.TopCenter
    ) {
        val surface = if (floating) {
            val radius = lerp(peekHeight / 2, PvotPersistentSheetDefaults.FloatingCornerRadius, state.openProgress())
            Modifier
                .glassSurface(
                    backdrop = backdrop,
                    cornerRadius = radius,
                    style = PvotGlassDefaults.Style,
                    rim = true,
                    onClick = null,
                    enabled = true,
                    interactionSource = interactionSource
                )
                .border(
                    PvotPersistentSheetDefaults.FloatingOutlineWidth,
                    PvotPersistentSheetDefaults.floatingOutlineColor,
                    RoundedCornerShape(radius)
                )
        } else {
            Modifier
                .clip(PvotPersistentSheetDefaults.Shape)
                .background(containerColor)
        }
        Column(
            modifier = Modifier
                .widthIn(max = PvotBottomSheetDefaults.MaxWidth)
                .layout { measurable, constraints ->
                    val offset = state.offset
                    val detached = 1f - state.openProgress()
                    val side = (margin.toPx() * detached).roundToInt()
                    val top = if (offset.isNaN()) constraints.maxHeight else offset.roundToInt()
                    val height = (constraints.maxHeight - top - (bottomGap * detached).roundToInt()).coerceAtLeast(0)
                    val width = (constraints.maxWidth - 2 * side).coerceAtLeast(0)
                    val placeable = measurable.measure(Constraints.fixed(width, height))
                    layout(constraints.maxWidth, constraints.maxHeight) { placeable.place(side, top) }
                }
                .then(surface)
                .anchoredDraggable(
                    state = state.draggable,
                    orientation = Orientation.Vertical,
                    flingBehavior = flingBehavior
                )
                .semantics {
                    customActions = PvotSheetDetent.entries
                        .filter { it != state.currentDetent }
                        .map { detent ->
                            CustomAccessibilityAction(actionLabels.getValue(detent)) {
                                scope.launch { state.animateTo(detent) }
                                true
                            }
                        }
                }
                .then(if (floating) Modifier else Modifier.windowInsetsPadding(windowInsets))
                .padding(horizontal = PvotSpacing.L),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (floating) {
                PvotSheetHandle(verticalPadding = PvotSpacing.S, width = PvotPersistentSheetDefaults.FloatingHandleWidth)
            } else {
                PvotSheetHandle()
            }
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                Column(modifier = Modifier.fillMaxWidth(), content = content)
            }
        }
    }
}

private fun PvotPersistentSheetState.openProgress(): Float {
    val anchors = draggable.anchors
    val collapsed = anchors.positionOf(PvotSheetDetent.Collapsed)
    val medium = anchors.positionOf(PvotSheetDetent.Medium)
    if (collapsed.isNaN() || medium.isNaN() || collapsed <= medium || offset.isNaN()) return 0f
    return ((collapsed - offset) / (collapsed - medium)).coerceIn(0f, 1f)
}
