// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
 * With [floating] the sheet is a card of glass over [backdrop], inset from the edges and kept
 * above the bottom [windowInsets]: collapsed it is a capsule, like a floating search bar, and its
 * corners soften as it opens.
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
            Modifier.glassSurface(
                backdrop = backdrop,
                cornerRadius = floatingCornerRadius(state, peekHeight),
                style = PvotGlassDefaults.Style,
                rim = true,
                onClick = null,
                enabled = true,
                interactionSource = interactionSource
            )
        } else {
            Modifier
                .clip(PvotPersistentSheetDefaults.Shape)
                .background(containerColor)
        }
        Column(
            modifier = Modifier
                .widthIn(max = PvotBottomSheetDefaults.MaxWidth)
                .padding(horizontal = margin)
                .layout { measurable, constraints ->
                    val offset = state.offset
                    val top = if (offset.isNaN()) constraints.maxHeight else offset.roundToInt()
                    val height = (constraints.maxHeight - top - bottomGap.roundToInt()).coerceAtLeast(0)
                    val placeable = measurable.measure(
                        constraints.copy(minHeight = height, maxHeight = height)
                    )
                    layout(placeable.width, constraints.maxHeight) { placeable.place(0, top) }
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
            PvotSheetHandle(verticalPadding = if (floating) PvotSpacing.S else PvotSpacing.M)
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                Column(modifier = Modifier.fillMaxWidth(), content = content)
            }
        }
    }
}

@Composable
private fun floatingCornerRadius(state: PvotPersistentSheetState, peekHeight: Dp): Dp {
    val anchors = state.draggable.anchors
    val collapsed = anchors.positionOf(PvotSheetDetent.Collapsed)
    val medium = anchors.positionOf(PvotSheetDetent.Medium)
    val progress = if (collapsed.isNaN() || medium.isNaN() || collapsed <= medium || state.offset.isNaN()) {
        0f
    } else {
        ((collapsed - state.offset) / (collapsed - medium)).coerceIn(0f, 1f)
    }
    return lerp(peekHeight / 2, PvotPersistentSheetDefaults.FloatingCornerRadius, progress)
}
