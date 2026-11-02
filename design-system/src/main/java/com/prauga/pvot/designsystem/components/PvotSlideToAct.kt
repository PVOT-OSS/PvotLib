// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import android.animation.ValueAnimator
import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotSpacing
import com.prauga.pvot.designsystem.theme.PvotTheme
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/**
 * One end of a [PvotSlideToAct]: what sliding the puck that way does.
 *
 * @param label What the action does, offered to TalkBack as an action on the puck
 * @param onReached Called once when the puck is slid or flung far enough
 * @param distance How far the puck must travel
 * @param hint The line on this side of the puck, such as "Swipe up to answer", or null for none
 * @param color The puck's color once it is more than halfway there, or null to keep its color
 */
@Immutable
public data class PvotSlideTarget(
    val label: String,
    val onReached: () -> Unit,
    val distance: Dp,
    val hint: String? = null,
    val color: Color? = null,
)

/** Which way a released [PvotSlideToAct] puck went, from [PvotSlideToActDefaults.decide]. */
public enum class PvotSlideDecision {
    /** Far or fast enough up: the up action. */
    Up,

    /** Far or fast enough down: the down action. */
    Down,

    /** Neither: the puck springs back. */
    None,
}

/** Defaults for [PvotSlideToAct]. */
public object PvotSlideToActDefaults {

    /** Diameter of the puck. */
    public val PuckSize: Dp = 80.dp

    /** Size of the puck's icon. */
    public val IconSize: Dp = 32.dp

    /** How far up the puck travels for the up action, such as answering. */
    public val UpDistance: Dp = 120.dp

    /**
     * How far down the puck travels for the down action, such as declining: less than up, since the
     * puck sits near the bottom of the screen.
     */
    public val DownDistance: Dp = 56.dp

    /** How fast, per second, a released puck must move to count as a fling. */
    public val FlingVelocity: Dp = 1200.dp

    /** The share of a target's distance a fling must have covered to count. */
    public const val FlingFraction: Float = 1f / 3f

    /** How far past a target's distance the puck may be dragged, as a share of the distance. */
    public const val Overshoot: Float = 1.2f

    /**
     * Where a released puck goes, in pixels: [offset] is negative upward and [velocity] is per
     * second. Past a target's distance, or flung toward it after [FlingFraction] of it, it does
     * that target's action; otherwise none. A missing target is never chosen.
     *
     * @param offset How far the puck is from rest, negative upward
     * @param velocity How fast it was moving when released, negative upward
     * @param up The up target's distance, or null for no up target
     * @param down The down target's distance, or null for no down target
     * @param flingVelocity The speed that counts as a fling
     */
    public fun decide(
        offset: Float,
        velocity: Float,
        up: Float?,
        down: Float?,
        flingVelocity: Float,
    ): PvotSlideDecision =
        when {
            up != null && offset <= -up -> PvotSlideDecision.Up
            down != null && offset >= down -> PvotSlideDecision.Down
            up != null && velocity <= -flingVelocity && offset <= -up * FlingFraction ->
                PvotSlideDecision.Up
            down != null && velocity >= flingVelocity && offset >= down * FlingFraction ->
                PvotSlideDecision.Down
            else -> PvotSlideDecision.None
        }

    /**
     * Whether touch exploration, such as TalkBack, is on: then show plain buttons instead of a
     * slide, which is hard to do by touch exploration.
     */
    public fun isTouchExplorationEnabled(context: Context): Boolean =
        context.getSystemService(AccessibilityManager::class.java)?.isTouchExplorationEnabled ==
            true
}

/**
 * A puck slid up or down to act, such as up to answer a call and down to decline it.
 *
 * The puck follows the finger and does an action when slid past a target's distance or flung toward
 * it; let go early, it springs back. It ticks a haptic as it crosses a target's distance, takes on
 * the target's color past halfway, and reports how far it is through [onProgress]. While idle it
 * nudges up now and then to say it moves, unless animations are off. TalkBack gets each target as
 * an action on the puck, so nothing needs a slide; show plain buttons instead when
 * [PvotSlideToActDefaults.isTouchExplorationEnabled].
 *
 * @param icon The puck's icon
 * @param contentDescription What TalkBack reads for the puck
 * @param up What sliding up does, or null for nothing
 * @param down What sliding down does, or null for nothing
 * @param modifier Modifier for the control
 * @param onProgress How far the puck is: up to 1 toward [up], down to -1 toward [down], 0 at rest
 * @param enabled Whether the puck can be moved
 * @param nudge Whether the idle puck nudges up now and then
 * @param puckColor Color of the puck at rest
 * @param puckContentColor Color of the puck's icon
 * @param puckSize Diameter of the puck
 */
@Composable
public fun PvotSlideToAct(
    icon: Painter,
    contentDescription: String,
    up: PvotSlideTarget?,
    down: PvotSlideTarget?,
    modifier: Modifier = Modifier,
    onProgress: (Float) -> Unit = {},
    enabled: Boolean = true,
    nudge: Boolean = true,
    puckColor: Color = PvotTheme.extendedColors.success,
    puckContentColor: Color = PvotTheme.extendedColors.onSuccess,
    puckSize: Dp = PvotSlideToActDefaults.PuckSize,
) {
    val density = LocalDensity.current
    val haptics = LocalHapticFeedback.current
    val upDistance = up?.let { with(density) { it.distance.toPx() } }
    val downDistance = down?.let { with(density) { it.distance.toPx() } }
    val flingVelocity = with(density) { PvotSlideToActDefaults.FlingVelocity.toPx() }
    val offset = remember { Animatable(0f) }
    // The drag's own total: the animatable only catches up when its snap runs, which can be after
    // the next delta arrives.
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val currentUp by rememberUpdatedState(up)
    val currentDown by rememberUpdatedState(down)
    val progress by rememberUpdatedState(onProgress)
    val animate = nudge && ValueAnimator.areAnimatorsEnabled()
    val nudgeOffset =
        if (animate) {
            val value by
                rememberInfiniteTransition(label = "slideNudge")
                    .animateFloat(
                        initialValue = 0f,
                        targetValue = 0f,
                        animationSpec =
                            infiniteRepeatable(
                                keyframes {
                                    durationMillis = NudgeCycleMillis
                                    0f at 0
                                    0f at NudgeStartMillis
                                    -NudgeRise at NudgeStartMillis + 200
                                    0f at NudgeStartMillis + 500
                                },
                                RepeatMode.Restart,
                            ),
                        label = "nudge",
                    )
            value
        } else {
            0f
        }

    fun finish(target: PvotSlideTarget?) {
        if (target == null || done) return
        done = true
        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
        target.onReached()
    }

    val pastHalfway =
        when {
            downDistance != null && offset.value > downDistance / 2 -> down.color
            upDistance != null && offset.value < -upDistance / 2 -> up.color
            else -> null
        }
    val fill by
        animateColorAsState(
            pastHalfway ?: puckColor,
            tween(PvotDurations.Short),
            label = "puckColor",
        )
    val restDistance = upDistance ?: downDistance ?: 1f
    val hintAlpha = 1f - (abs(offset.value) / restDistance).coerceIn(0f, 1f)
    val actions =
        listOfNotNull(
            up?.let { target ->
                CustomAccessibilityAction(target.label) {
                    finish(currentUp)
                    true
                }
            },
            down?.let { target ->
                CustomAccessibilityAction(target.label) {
                    finish(currentDown)
                    true
                }
            },
        )

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        up?.hint?.let { Hint(it, MaterialTheme.colorScheme.onSurface, hintAlpha) }
        Box(
            modifier =
                Modifier.padding(vertical = PvotSpacing.L)
                    .offset {
                        val rest = if (dragging || done) 0f else nudgeOffset.dp.toPx()
                        IntOffset(0, (offset.value + rest).roundToInt())
                    }
                    .size(puckSize)
                    .background(fill, CircleShape)
                    .draggable(
                        state =
                            rememberDraggableState { delta ->
                                if (done) return@rememberDraggableState
                                val min = -(upDistance ?: 0f) * PvotSlideToActDefaults.Overshoot
                                val max = (downDistance ?: 0f) * PvotSlideToActDefaults.Overshoot
                                val before = dragOffset
                                val value = (dragOffset + delta).coerceIn(min, max)
                                dragOffset = value
                                scope.launch { offset.snapTo(value) }
                                val crossedUp =
                                    upDistance != null &&
                                        before > -upDistance &&
                                        value <= -upDistance
                                val crossedDown =
                                    downDistance != null &&
                                        before < downDistance &&
                                        value >= downDistance
                                if (crossedUp || crossedDown) {
                                    haptics.performHapticFeedback(
                                        HapticFeedbackType.GestureThresholdActivate
                                    )
                                }
                                progress(
                                    when {
                                        value < 0 && upDistance != null ->
                                            (-value / upDistance).coerceAtMost(1f)
                                        value > 0 && downDistance != null ->
                                            (-value / downDistance).coerceAtLeast(-1f)
                                        else -> 0f
                                    }
                                )
                            },
                        orientation = Orientation.Vertical,
                        enabled = enabled && !done,
                        onDragStarted = {
                            dragging = true
                            dragOffset = offset.value
                        },
                        onDragStopped = { velocity ->
                            dragging = false
                            if (done) return@draggable
                            when (
                                PvotSlideToActDefaults.decide(
                                    dragOffset,
                                    velocity,
                                    upDistance,
                                    downDistance,
                                    flingVelocity,
                                )
                            ) {
                                PvotSlideDecision.Up -> finish(currentUp)
                                PvotSlideDecision.Down -> finish(currentDown)
                                PvotSlideDecision.None -> {
                                    progress(0f)
                                    dragOffset = 0f
                                    offset.animateTo(0f, PvotMotion.bounceSpring())
                                }
                            }
                        },
                    )
                    .semantics {
                        role = Role.Button
                        this.contentDescription = contentDescription
                        customActions = actions
                    },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = puckContentColor,
                modifier = Modifier.size(PvotSlideToActDefaults.IconSize),
            )
        }
        down?.hint?.let { Hint(it, MaterialTheme.colorScheme.onSurfaceVariant, hintAlpha) }
    }
}

@Composable
private fun Hint(text: String, color: Color, alpha: Float) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier.alpha(alpha),
    )
}

/** One nudge cycle: still, then a short rise and fall at its end. */
private const val NudgeCycleMillis = 2400
private const val NudgeStartMillis = 1600

/** How far the idle puck rises, in dp. */
private const val NudgeRise = 14f
