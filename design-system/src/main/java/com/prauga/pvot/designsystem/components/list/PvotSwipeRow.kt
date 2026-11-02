// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.list

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

/**
 * What swiping a [PvotSwipeRow] one way does. Make one with [PvotSwipeRowDefaults.action] or
 * [PvotSwipeRowDefaults.destructiveAction].
 *
 * @param label What the action does, such as "Call" or "Delete"; TalkBack offers it as an action on
 *   the row, since a swipe is not available to everyone
 * @param icon Icon shown in the gap the row leaves as it slides
 * @param onSwipe Called once the swipe passes the threshold and the finger lifts
 * @param containerColor Fill behind the row while it slides
 * @param contentColor Tint of the icon
 * @param removesRow Whether the row slides off and stays off, for an action that removes it.
 *   Otherwise the row springs back once the action has run.
 */
@Stable
public class PvotSwipeAction(
    public val label: String,
    public val icon: Painter,
    public val onSwipe: () -> Unit,
    public val containerColor: Color,
    public val contentColor: Color,
    public val removesRow: Boolean = false,
)

/**
 * The position of a [PvotSwipeRow]. Hoist it only to put back a row that was swiped away but is
 * still in the list; a row that leaves the list and comes back with Undo starts settled anyway.
 */
@Stable
public class PvotSwipeRowState {

    /** How far the row has slid, positive toward the end. */
    internal var offset: Float by mutableFloatStateOf(0f)

    internal var minOffset: Float = 0f
    internal var maxOffset: Float = 0f

    /** Runs drags and settles; a new drag cancels a settle in flight. */
    internal val draggableState: DraggableState = DraggableState { delta ->
        offset = (offset + delta).coerceIn(minOffset, maxOffset)
    }

    /** Whether the row has slid away by an action that [removes it][PvotSwipeAction.removesRow]. */
    public var isDismissed: Boolean by mutableStateOf(false)
        internal set

    /** Slides the row back into place, for example after its removal was undone. */
    public suspend fun reset() {
        isDismissed = false
        settleTo(0f, 0f)
    }

    internal suspend fun settleTo(target: Float, velocity: Float) {
        draggableState.drag {
            var previous = offset
            animate(
                initialValue = offset,
                targetValue = target,
                initialVelocity = velocity,
                animationSpec = tween(PvotDurations.Medium, easing = PvotMotion.Settle),
            ) { value, _ ->
                dragBy(value - previous)
                previous = value
            }
        }
    }
}

/** Remembers a [PvotSwipeRowState]. */
@Composable
public fun rememberPvotSwipeRowState(): PvotSwipeRowState = remember { PvotSwipeRowState() }

/** Defaults for [PvotSwipeRow]. */
public object PvotSwipeRowDefaults {

    /** Corner shape of the row and of the fill it reveals. */
    public val Shape: Shape = PvotShapes.Card

    /** How far across the row, as a fraction of its width, a swipe must go to count. */
    public const val ThresholdFraction: Float = 0.35f

    /** A fling at least this fast, per second, counts from half the threshold. */
    public val FlingVelocity: Dp = 600.dp

    /** Scale of the action icon once the swipe is past the threshold. */
    public const val ArmedIconScale: Float = 1.2f

    /** Fill of the row itself, opaque so the action behind it only shows in the gap. */
    public val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.background

    /**
     * A swipe action in the brand color, such as Call or Message. The row springs back after it.
     *
     * @param label What the action does
     * @param icon Icon shown behind the row
     * @param onSwipe Called when the swipe completes
     * @param containerColor Fill behind the row
     * @param contentColor Tint of the icon
     * @param removesRow Whether the row slides off and stays off
     */
    @Composable
    public fun action(
        label: String,
        icon: Painter,
        onSwipe: () -> Unit,
        containerColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = MaterialTheme.colorScheme.onPrimary,
        removesRow: Boolean = false,
    ): PvotSwipeAction =
        PvotSwipeAction(label, icon, onSwipe, containerColor, contentColor, removesRow)

    /**
     * A swipe action in the error color that removes the row, such as Delete. Offer Undo after it
     * rather than asking first.
     *
     * @param label What the action does
     * @param icon Icon shown behind the row
     * @param onSwipe Called when the swipe completes; remove the item here
     * @param containerColor Fill behind the row
     * @param contentColor Tint of the icon
     * @param removesRow Whether the row slides off and stays off
     */
    @Composable
    public fun destructiveAction(
        label: String,
        icon: Painter,
        onSwipe: () -> Unit,
        containerColor: Color = MaterialTheme.colorScheme.error,
        contentColor: Color = MaterialTheme.colorScheme.onError,
        removesRow: Boolean = true,
    ): PvotSwipeAction =
        PvotSwipeAction(label, icon, onSwipe, containerColor, contentColor, removesRow)
}

/**
 * A row that slides sideways to reveal an action: [startAction] when swiped from the start toward
 * the end, [endAction] the other way.
 *
 * Past [PvotSwipeRowDefaults.ThresholdFraction] of its width the icon swells and the haptics tick;
 * lifting the finger there runs the action. An action that
 * [removes the row][PvotSwipeAction.removesRow] slides it off, then calls
 * [PvotSwipeAction.onSwipe]: remove the item there and offer Undo, for example with a snackbar. Key
 * the row by the item, so a row brought back by Undo is a new row and starts settled. Any other
 * action springs the row back. Each action is also offered to TalkBack as an action on the row.
 *
 * @param modifier Modifier for the row
 * @param startAction What a swipe toward the end does, or null for none
 * @param endAction What a swipe toward the start does, or null for none
 * @param state The row's position, hoist it to [reset][PvotSwipeRowState.reset] a dismissed row
 * @param enabled Whether the row can be swiped
 * @param shape Shape of the row and the fill behind it
 * @param containerColor Fill of the row, opaque so the action only shows in the gap
 * @param content The row, typically a [PvotListRow]
 */
@Composable
public fun PvotSwipeRow(
    modifier: Modifier = Modifier,
    startAction: PvotSwipeAction? = null,
    endAction: PvotSwipeAction? = null,
    state: PvotSwipeRowState = rememberPvotSwipeRowState(),
    enabled: Boolean = true,
    shape: Shape = PvotSwipeRowDefaults.Shape,
    containerColor: Color = PvotSwipeRowDefaults.containerColor,
    content: @Composable () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val flingVelocity = with(LocalDensity.current) { PvotSwipeRowDefaults.FlingVelocity.toPx() }
    val start by rememberUpdatedState(startAction)
    val end by rememberUpdatedState(endAction)
    var width by remember { mutableIntStateOf(0) }
    val threshold = width * PvotSwipeRowDefaults.ThresholdFraction

    state.maxOffset = if (startAction != null) width.toFloat() else 0f
    state.minOffset = if (endAction != null) -width.toFloat() else 0f

    val direction by remember(state) { derivedStateOf { sign(state.offset) } }
    val armed by
        remember(state, threshold) {
            derivedStateOf { threshold > 0f && abs(state.offset) >= threshold }
        }
    val revealed =
        when {
            direction > 0f -> startAction
            direction < 0f -> endAction
            else -> null
        }
    val iconScale by
        animateFloatAsState(
            targetValue = if (armed) PvotSwipeRowDefaults.ArmedIconScale else 1f,
            animationSpec = PvotMotion.PressSpring,
            label = "pvotSwipeIcon",
        )
    LaunchedEffect(armed) {
        if (armed && !state.isDismissed) {
            haptics.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
        }
    }

    val a11yActions =
        listOfNotNull(startAction, endAction).map { action ->
            CustomAccessibilityAction(action.label) {
                action.onSwipe()
                true
            }
        }

    Box(
        modifier =
            modifier
                .onSizeChanged { width = it.width }
                .clip(shape)
                .semantics { if (enabled) customActions = a11yActions }
                .draggable(
                    state = state.draggableState,
                    orientation = Orientation.Horizontal,
                    enabled =
                        enabled && !state.isDismissed && (startAction != null || endAction != null),
                    // Offsets run toward the end, so in a right-to-left layout a drag left is
                    // positive.
                    reverseDirection = rtl,
                    onDragStopped = { velocity ->
                        val offset = state.offset
                        val action = if (offset > 0f) start else if (offset < 0f) end else null
                        val fling = abs(velocity) >= flingVelocity && sign(velocity) == sign(offset)
                        val passed =
                            abs(offset) >= threshold || (fling && abs(offset) >= threshold / 2)
                        when {
                            action == null || !passed -> state.settleTo(0f, velocity)
                            action.removesRow -> {
                                state.settleTo(sign(offset) * width, velocity)
                                state.isDismissed = true
                                action.onSwipe()
                            }
                            else -> {
                                action.onSwipe()
                                state.settleTo(0f, velocity)
                            }
                        }
                    },
                )
    ) {
        if (revealed != null) {
            Box(
                modifier = Modifier.matchParentSize().background(revealed.containerColor),
                contentAlignment =
                    if (direction > 0f) Alignment.CenterStart else Alignment.CenterEnd,
            ) {
                Icon(
                    painter = revealed.icon,
                    contentDescription = null,
                    tint = revealed.contentColor,
                    modifier =
                        Modifier.padding(horizontal = PvotSpacing.L * 1.5f).graphicsLayer {
                            val progress =
                                if (threshold > 0f) {
                                    (abs(state.offset) / threshold).coerceAtMost(1f)
                                } else {
                                    0f
                                }
                            alpha = progress
                            scaleX = iconScale * (0.6f + 0.4f * progress)
                            scaleY = scaleX
                        },
                )
            }
        }
        // Filled square: the row's own clip rounds the corners. A rounded fill here would leave
        // the action's color showing at the corners still inside the clip while the row slides.
        Box(
            modifier =
                Modifier.offset { IntOffset(state.offset.roundToInt(), 0) }
                    .background(containerColor)
        ) {
            content()
        }
    }
}
