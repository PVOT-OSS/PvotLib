// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.list

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
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
import com.prauga.pvot.designsystem.components.PvotMenu
import com.prauga.pvot.designsystem.components.PvotMenuDefaults
import com.prauga.pvot.designsystem.components.PvotMenuEntry
import com.prauga.pvot.designsystem.components.PvotMenuItem
import com.prauga.pvot.designsystem.components.button.PvotIconButton
import com.prauga.pvot.designsystem.components.button.PvotIconButtonColors
import com.prauga.pvot.designsystem.components.button.PvotIconButtonDefaults
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlinx.coroutines.launch

/**
 * What swiping a [PvotSwipeRow] one way does. Make one with [PvotSwipeRowDefaults.action] or
 * [PvotSwipeRowDefaults.destructiveAction].
 *
 * @param label What the action does, such as "Call" or "Delete"; TalkBack offers it as an action on
 *   the row, since a swipe is not available to everyone
 * @param icon Icon shown in the gap the row leaves as it slides
 * @param onSwipe Called once the swipe passes the threshold and the finger lifts, or when the
 *   action is one of [PvotSwipeRow]'s end buttons, once its button is tapped
 * @param containerColor Fill behind the row while it slides
 * @param contentColor Tint of the icon
 * @param removesRow Whether the row slides off and stays off, for an action that removes it.
 *   Otherwise the row springs back once the action has run.
 * @param menu For one of [PvotSwipeRow]'s end buttons: a menu the button opens, titled [label],
 *   instead of running [onSwipe]; the row shuts once an item is picked
 */
@Stable
public class PvotSwipeAction(
    public val label: String,
    public val icon: Painter,
    public val onSwipe: () -> Unit,
    public val containerColor: Color,
    public val contentColor: Color,
    public val removesRow: Boolean = false,
    public val menu: List<PvotMenuEntry> = emptyList(),
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

    /** How far the row moves per pixel the finger drags. */
    internal var dragResistance: Float = 1f

    /** Runs drags and settles; a new drag cancels a settle in flight. */
    internal val draggableState: DraggableState = DraggableState { delta ->
        offset = (offset + delta * dragResistance).coerceIn(minOffset, maxOffset)
    }

    /** Whether the row has slid away by an action that [removes it][PvotSwipeAction.removesRow]. */
    public var isDismissed: Boolean by mutableStateOf(false)
        internal set

    /** Whether the row stands open on its end buttons. */
    public var isOpen: Boolean by mutableStateOf(false)
        internal set

    internal var closeRequested: Boolean by mutableStateOf(false)

    /** Slides the row back into place, for example after its removal was undone. */
    public suspend fun reset() {
        isDismissed = false
        close()
    }

    /** Slides a row that stands open on its end buttons shut. */
    public suspend fun close() {
        isOpen = false
        OpenSwipeRow.closed(this)
        settleTo(0f, 0f)
    }

    internal suspend fun settleTo(target: Float, velocity: Float) {
        // Sets the offset directly rather than dragging, so the drag resistance doesn't slow the
        // settle, and lands exactly on the target: a row left a fraction of a pixel off would keep
        // drawing its action's fill.
        draggableState.drag {
            animate(
                initialValue = offset,
                targetValue = target,
                initialVelocity = velocity,
                animationSpec = tween(PvotDurations.Medium, easing = PvotMotion.Settle),
            ) { value, _ ->
                offset = value
            }
            offset = target
        }
    }
}

internal object OpenSwipeRow {
    var state: PvotSwipeRowState? by mutableStateOf(null)
        private set

    fun opened(row: PvotSwipeRowState) {
        val previous = state
        state = row
        if (previous != null && previous !== row) previous.closeRequested = true
    }

    fun closed(row: PvotSwipeRowState) {
        if (state === row) state = null
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

    /** How far the row moves per pixel the finger drags; 1 follows the finger. */
    public const val DragResistance: Float = 1f

    /** A fling at least this fast, per second, counts from half the threshold. */
    public val FlingVelocity: Dp = 600.dp

    /** Scale of the action icon once the swipe is past the threshold. */
    public const val ArmedIconScale: Float = 1.2f

    /** Diameter of an end button, the round button a swipe toward the start uncovers. */
    public val ButtonSize: Dp = PvotIconButtonDefaults.Size

    /** Space between end buttons, between them and the row, and after the last one. */
    public val ButtonSpacing: Dp = PvotSpacing.S

    /**
     * How far across the row, as a fraction of its width, a swipe past the end buttons must go to
     * run the last one.
     */
    public const val FullSwipeFraction: Float = 0.6f

    /** Fill of the row itself, opaque so the action behind it only shows in the gap. */
    public val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.background

    /** Fill of a row standing open on its end buttons, a card beside them. */
    public val openContainerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceVariant

    /**
     * A swipe action in the brand color, such as Call or Message. The row springs back after it.
     *
     * @param label What the action does
     * @param icon Icon shown behind the row
     * @param onSwipe Called when the swipe completes
     * @param containerColor Fill behind the row
     * @param contentColor Tint of the icon
     * @param removesRow Whether the row slides off and stays off
     * @param menu For an end button: the menu it opens instead of running [onSwipe]
     */
    @Composable
    public fun action(
        label: String,
        icon: Painter,
        onSwipe: () -> Unit,
        containerColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = MaterialTheme.colorScheme.onPrimary,
        removesRow: Boolean = false,
        menu: List<PvotMenuEntry> = emptyList(),
    ): PvotSwipeAction =
        PvotSwipeAction(label, icon, onSwipe, containerColor, contentColor, removesRow, menu)

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
 * With [endButtons] a swipe toward the start uncovers round buttons instead, such as History and
 * Delete, and past half of them the row stands open on them as a card in [openContainerColor]. A
 * button with a [menu][PvotSwipeAction.menu] opens it, as a [PvotMenu] titled with its label.
 * Tapping a button runs it and shuts the row, or slides it off if it
 * [removes the row][PvotSwipeAction.removesRow]. Swiping on past
 * [PvotSwipeRowDefaults.FullSwipeFraction] of the width runs the last button. One row stands open
 * at a time: tapping any swipe row, scrolling, or opening another shuts it.
 *
 * @param modifier Modifier for the row
 * @param startAction What a swipe toward the end does, or null for none
 * @param endAction What a swipe toward the start does, or null for none
 * @param endButtons Round buttons a swipe toward the start uncovers, in order toward the end; when
 *   not empty they take the place of [endAction]
 * @param state The row's position, hoist it to [reset][PvotSwipeRowState.reset] a dismissed row
 * @param enabled Whether the row can be swiped
 * @param shape Shape of the row and the fill behind it
 * @param containerColor Fill of the row, opaque so the action only shows in the gap
 * @param openContainerColor Fill of the row while it stands open on its [endButtons]
 * @param thresholdFraction How far across the row, as a fraction of its width, a swipe must go to
 *   count
 * @param dragResistance How far the row moves per pixel the finger drags, from 0 to 1; lower makes
 *   the swipe feel heavier and harder to set off by accident
 * @param content The row, typically a [PvotListRow]
 */
@Composable
public fun PvotSwipeRow(
    modifier: Modifier = Modifier,
    startAction: PvotSwipeAction? = null,
    endAction: PvotSwipeAction? = null,
    endButtons: List<PvotSwipeAction> = emptyList(),
    state: PvotSwipeRowState = rememberPvotSwipeRowState(),
    enabled: Boolean = true,
    shape: Shape = PvotSwipeRowDefaults.Shape,
    containerColor: Color = PvotSwipeRowDefaults.containerColor,
    openContainerColor: Color = PvotSwipeRowDefaults.openContainerColor,
    thresholdFraction: Float = PvotSwipeRowDefaults.ThresholdFraction,
    dragResistance: Float = PvotSwipeRowDefaults.DragResistance,
    content: @Composable () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val density = LocalDensity.current
    val flingVelocity = with(density) { PvotSwipeRowDefaults.FlingVelocity.toPx() }
    val scope = rememberCoroutineScope()
    val start by rememberUpdatedState(startAction)
    val end by rememberUpdatedState(endAction)
    val buttons by rememberUpdatedState(endButtons)
    val reveals = endButtons.isNotEmpty()
    var width by remember { mutableIntStateOf(0) }
    val threshold = width * thresholdFraction
    val openWidth =
        with(density) {
            (PvotSwipeRowDefaults.ButtonSize * endButtons.size +
                    PvotSwipeRowDefaults.ButtonSpacing * (endButtons.size + 1))
                .toPx()
        }
    val fullSwipe =
        maxOf(
            width * PvotSwipeRowDefaults.FullSwipeFraction,
            openWidth + with(density) { PvotSwipeRowDefaults.ButtonSize.toPx() },
        )

    state.dragResistance = dragResistance
    state.maxOffset = if (startAction != null) width.toFloat() else 0f
    state.minOffset = if (endAction != null || reveals) -width.toFloat() else 0f

    val direction by
        remember(state) {
            derivedStateOf { if (abs(state.offset) < 0.5f) 0f else sign(state.offset) }
        }
    val armed by
        remember(state, threshold, fullSwipe, reveals) {
            derivedStateOf {
                if (reveals && state.offset < 0f) {
                    width > 0 && -state.offset >= fullSwipe
                } else {
                    threshold > 0f && abs(state.offset) >= threshold
                }
            }
        }
    val revealed =
        when {
            direction > 0f -> startAction
            direction < 0f && !reveals -> endAction
            else -> null
        }
    val openProgress: () -> Float = {
        if (reveals && openWidth > 0f) (-state.offset / openWidth).coerceIn(0f, 1f) else 0f
    }

    suspend fun shut(velocity: Float) {
        state.isOpen = false
        OpenSwipeRow.closed(state)
        state.settleTo(0f, velocity)
    }

    suspend fun open(velocity: Float) {
        state.settleTo(-openWidth, velocity)
        state.isOpen = true
        OpenSwipeRow.opened(state)
    }

    suspend fun runButton(action: PvotSwipeAction, velocity: Float) {
        if (action.removesRow) {
            state.isOpen = false
            OpenSwipeRow.closed(state)
            state.settleTo(-width.toFloat(), velocity)
            state.isDismissed = true
            action.onSwipe()
        } else {
            action.onSwipe()
            shut(velocity)
        }
    }

    LaunchedEffect(state) {
        snapshotFlow { state.closeRequested }
            .collect { requested ->
                if (requested) {
                    state.closeRequested = false
                    if (state.isOpen) shut(0f)
                }
            }
    }
    DisposableEffect(state) { onDispose { OpenSwipeRow.closed(state) } }
    val top = remember { mutableFloatStateOf(Float.NaN) }
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
        (listOfNotNull(startAction, endAction.takeUnless { reveals }) + endButtons).flatMap { action
            ->
            if (action.menu.isEmpty()) {
                listOf(
                    CustomAccessibilityAction(action.label) {
                        action.onSwipe()
                        true
                    }
                )
            } else {
                action.menu.filterIsInstance<PvotMenuItem>().map { item ->
                    CustomAccessibilityAction(item.label) {
                        item.onClick()
                        true
                    }
                }
            }
        }

    Box(
        modifier =
            modifier
                .onSizeChanged { width = it.width }
                .onGloballyPositioned { coordinates ->
                    val y = coordinates.positionInRoot().y
                    if (state.isOpen && !top.floatValue.isNaN() && abs(y - top.floatValue) > 1f) {
                        state.closeRequested = true
                    }
                    top.floatValue = y
                }
                .clip(shape)
                .semantics { if (enabled) customActions = a11yActions }
                .draggable(
                    state = state.draggableState,
                    orientation = Orientation.Horizontal,
                    enabled =
                        enabled &&
                            !state.isDismissed &&
                            (startAction != null || endAction != null || reveals),
                    // Offsets run toward the end, so in a right-to-left layout a drag left is
                    // positive.
                    reverseDirection = rtl,
                    onDragStopped = { velocity ->
                        val offset = state.offset
                        if (reveals && offset < 0f) {
                            val last = buttons.lastOrNull()
                            val fling = abs(velocity) >= flingVelocity
                            when {
                                last != null && last.menu.isEmpty() && -offset >= fullSwipe ->
                                    runButton(last, velocity)
                                fling && velocity > 0f -> shut(velocity)
                                (fling && velocity < 0f) || -offset >= openWidth / 2 ->
                                    open(velocity)
                                else -> shut(velocity)
                            }
                            return@draggable
                        }
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
            // Fills only the gap the row leaves, never under the row: a fill under it would bleed
            // through the anti-aliased edge where the clip rounds the row's corners.
            val fillsFromLeft = (direction > 0f) != rtl
            Box(
                modifier =
                    Modifier.matchParentSize().drawBehind {
                        val gap =
                            (abs(state.offset).roundToInt() + 1).toFloat().coerceAtMost(size.width)
                        drawRect(
                            color = revealed.containerColor,
                            topLeft = Offset(if (fillsFromLeft) 0f else size.width - gap, 0f),
                            size = Size(gap, size.height),
                        )
                    },
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
        if (reveals && direction < 0f) {
            Row(
                modifier =
                    Modifier.matchParentSize().padding(end = PvotSwipeRowDefaults.ButtonSpacing),
                horizontalArrangement =
                    Arrangement.spacedBy(PvotSwipeRowDefaults.ButtonSpacing, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                endButtons.forEachIndexed { index, action ->
                    val isLast = index == endButtons.lastIndex
                    val reveal =
                        Modifier.graphicsLayer {
                            val progress = openProgress()
                            alpha = if (armed && !isLast) 0f else progress
                            scaleX = (0.6f + 0.4f * progress) * if (isLast) iconScale else 1f
                            scaleY = scaleX
                        }
                    if (action.menu.isNotEmpty()) {
                        Box(modifier = reveal) {
                            PvotMenu(
                                entries =
                                    action.menu.map { entry ->
                                        if (entry is PvotMenuItem) {
                                            entry.copy(
                                                onClick = {
                                                    entry.onClick()
                                                    scope.launch { shut(0f) }
                                                }
                                            )
                                        } else {
                                            entry
                                        }
                                    },
                                contentDescription = action.label,
                                icon = action.icon,
                                title = action.label,
                                backdrop = null,
                                colors =
                                    PvotMenuDefaults.themeColors()
                                        .copy(
                                            triggerColor = action.containerColor,
                                            triggerContentColor = action.contentColor,
                                        ),
                            )
                        }
                        return@forEachIndexed
                    }
                    PvotIconButton(
                        icon = action.icon,
                        contentDescription = action.label,
                        onClick = { scope.launch { runButton(action, 0f) } },
                        size = PvotSwipeRowDefaults.ButtonSize,
                        colors =
                            PvotIconButtonColors(
                                containerColor = action.containerColor,
                                contentColor = action.contentColor,
                                checkedContainerColor = action.containerColor,
                                checkedContentColor = action.contentColor,
                            ),
                        modifier = reveal,
                    )
                }
            }
        }
        // Filled square: the row's own clip rounds the corners. A rounded fill here would leave
        // the action's color showing at the corners still inside the clip while the row slides.
        Box(
            modifier =
                Modifier.offset { IntOffset(state.offset.roundToInt(), 0) }
                    .drawBehind {
                        val progress = openProgress()
                        if (progress > 0f) {
                            drawOutline(
                                outline = shape.createOutline(size, layoutDirection, this),
                                color = lerp(containerColor, openContainerColor, progress),
                            )
                        } else {
                            drawRect(containerColor)
                        }
                    }
        ) {
            content()
            if (OpenSwipeRow.state != null) {
                Box(
                    modifier =
                        Modifier.matchParentSize().pointerInput(Unit) {
                            detectTapGestures { OpenSwipeRow.state?.closeRequested = true }
                        }
                )
            }
        }
    }
}
