// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.pvotGlass
import com.prauga.pvot.designsystem.effects.pvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.pvotGlassRim
import com.prauga.pvot.designsystem.effects.rememberPvotGlassBackdrop
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotShapes
import kotlin.math.roundToInt

/**
 * Color configuration for [PvotSwitch].
 *
 * @param checkedTrackColor Fill of the track while the switch is on
 * @param uncheckedTrackColor Fill of the track while the switch is off
 * @param thumbColor Fill of the thumb
 */
@Immutable
public data class PvotSwitchColors(
    val checkedTrackColor: Color,
    val uncheckedTrackColor: Color,
    val thumbColor: Color,
)

/** Defaults for [PvotSwitch]. */
public object PvotSwitchDefaults {

    /** Size of the track. */
    public val TrackSize: DpSize = DpSize(52.dp, 32.dp)

    /** Diameter of the thumb at rest. */
    public val ThumbSize: Dp = 24.dp

    /** Gap between the track's edge and the thumb. */
    public val ThumbInset: Dp = 4.dp

    /** How much larger the thumb is once lifted into a lens under the finger. */
    public const val LiftedThumbScale: Float = 1.25f

    /** How much of the thumb's fill fades out once lifted, so the track shows through the lens. */
    public const val LiftedFaceFade: Float = 0.85f

    /** How far past either end the thumb can be dragged, as a share of the pull past it. */
    public const val Overshoot: Float = 1f / 22f

    /** Opacity of a disabled switch. */
    public const val DisabledAlpha: Float = 0.38f

    /** The thumb's fill: white, as a thumb is on any track. */
    public val ThumbColor: Color = Color.White

    /** Switch colors derived from [colorScheme], each overridable. */
    public fun colors(
        colorScheme: ColorScheme,
        checkedTrackColor: Color = colorScheme.primary,
        uncheckedTrackColor: Color = colorScheme.onSurface.copy(alpha = 0.18f),
        thumbColor: Color = ThumbColor,
    ): PvotSwitchColors =
        PvotSwitchColors(
            checkedTrackColor = checkedTrackColor,
            uncheckedTrackColor = uncheckedTrackColor,
            thumbColor = thumbColor,
        )

    /** Switch colors from the theme's color scheme. */
    @Composable public fun themeColors(): PvotSwitchColors = colors(MaterialTheme.colorScheme)
}

/**
 * A switch: a pill track with a round thumb that slides to the on side.
 *
 * Tap it to toggle. Held, the thumb lifts into a lens over the track and follows the finger;
 * letting go leaves it on whichever side it is nearer. A quick swipe up or down still scrolls the
 * list the switch is in. TalkBack reads it as a switch.
 *
 * @param checked Whether the switch is on
 * @param onCheckedChange Called with the new value when the user toggles it, or null for a switch
 *   that only shows a state, such as the one at the end of a `PvotSwitchRow`, which toggles as a
 *   whole
 * @param modifier Modifier for the switch
 * @param enabled Whether the switch responds to touch; a disabled switch is dimmed
 * @param colors The switch's colors
 */
@Composable
public fun PvotSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: PvotSwitchColors = PvotSwitchDefaults.themeColors(),
) {
    val haptics = LocalHapticFeedback.current
    val currentChecked by rememberUpdatedState(checked)
    val currentOnChange by rememberUpdatedState(onCheckedChange)
    var lifted by remember { mutableStateOf(false) }
    var dragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableFloatStateOf(0f) }
    val on = if (dragging) dragPosition > 0.5f else checked
    val position by
        animateFloatAsState(
            targetValue = if (dragging) dragPosition else if (checked) 1f else 0f,
            animationSpec = if (dragging) PvotMotion.followSpring() else PvotMotion.slideSpring(),
            label = "pvotSwitchPosition",
        )
    val lift by
        animateFloatAsState(
            targetValue = if (lifted) 1f else 0f,
            animationSpec = PvotMotion.liftSpring(),
            label = "pvotSwitchLift",
        )
    val track by
        animateColorAsState(
            targetValue = if (on) colors.checkedTrackColor else colors.uncheckedTrackColor,
            animationSpec = tween(PvotDurations.Short),
            label = "pvotSwitchTrack",
        )
    val backdrop = rememberPvotGlassBackdrop()
    val interactive = onCheckedChange != null
    Box(
        modifier =
            modifier
                .size(PvotSwitchDefaults.TrackSize)
                .alpha(if (enabled) 1f else PvotSwitchDefaults.DisabledAlpha)
                .then(
                    if (interactive) {
                        Modifier.semantics {
                                role = Role.Switch
                                toggleableState = ToggleableState(checked)
                                if (enabled) {
                                    onClick {
                                        currentOnChange?.invoke(!currentChecked)
                                        true
                                    }
                                } else {
                                    disabled()
                                }
                            }
                            .pointerInput(enabled) {
                                if (!enabled) return@pointerInput
                                val travel =
                                    (PvotSwitchDefaults.TrackSize.width -
                                            PvotSwitchDefaults.ThumbInset * 2 -
                                            PvotSwitchDefaults.ThumbSize)
                                        .toPx()
                                var from = 0f
                                var startX = 0f
                                var wasOn = false
                                detectLiftGesture(
                                    onLift = { at ->
                                        from = if (currentChecked) 1f else 0f
                                        startX = at.x
                                        wasOn = currentChecked
                                        dragPosition = from
                                        lifted = true
                                    },
                                    onMove = { at ->
                                        dragging = true
                                        val raw = from + (at.x - startX) / travel
                                        dragPosition =
                                            when {
                                                raw < 0f -> raw * PvotSwitchDefaults.Overshoot
                                                raw > 1f ->
                                                    1f + (raw - 1f) * PvotSwitchDefaults.Overshoot
                                                else -> raw
                                            }
                                        val nowOn = dragPosition > 0.5f
                                        if (nowOn != wasOn) {
                                            wasOn = nowOn
                                            haptics.performHapticFeedback(
                                                HapticFeedbackType.ToggleOn
                                            )
                                        }
                                    },
                                    onRelease = { _, moved ->
                                        val next =
                                            if (moved) dragPosition > 0.5f else !currentChecked
                                        lifted = false
                                        dragging = false
                                        if (next != currentChecked) {
                                            if (!moved) {
                                                haptics.performHapticFeedback(
                                                    if (next) HapticFeedbackType.ToggleOn
                                                    else HapticFeedbackType.ToggleOff
                                                )
                                            }
                                            currentOnChange?.invoke(next)
                                        }
                                    },
                                    onCancel = {
                                        lifted = false
                                        dragging = false
                                    },
                                )
                            }
                    } else {
                        Modifier
                    }
                ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier =
                Modifier.matchParentSize()
                    .pvotGlassBackdrop(backdrop)
                    .background(track, PvotShapes.Pill)
        )
        val thumb = PvotSwitchDefaults.ThumbSize
        val inset = PvotSwitchDefaults.ThumbInset
        val diameter = thumb * (1f + (PvotSwitchDefaults.LiftedThumbScale - 1f) * lift)
        val face = 1f - PvotSwitchDefaults.LiftedFaceFade * lift
        val travel = PvotSwitchDefaults.TrackSize.width - inset * 2 - thumb
        Box(
            modifier =
                Modifier.offset {
                        IntOffset(
                            (inset.toPx() +
                                    travel.toPx() * position +
                                    (thumb - diameter).toPx() / 2f)
                                .roundToInt(),
                            0,
                        )
                    }
                    .size(diameter)
                    .then(
                        if (lift > 0f) {
                            Modifier.pvotGlass(
                                backdrop,
                                PvotGlassDefaults.CapsuleRadius,
                                PvotGlassDefaults.lensStyle(lift),
                            )
                        } else {
                            Modifier
                        }
                    )
                    .drawBehind {
                        val radius = size.minDimension / 2f
                        drawCircle(
                            Color.Black.copy(alpha = 0.2f * face),
                            radius + 1.dp.toPx(),
                            center + Offset(0f, 1.dp.toPx()),
                        )
                        drawCircle(
                            colors.thumbColor.copy(alpha = colors.thumbColor.alpha * face),
                            radius,
                        )
                    }
                    .then(if (lift > 0f) Modifier.pvotGlassRim(alpha = lift) else Modifier)
        )
    }
}
