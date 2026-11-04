// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.camera

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.components.pvotGlassSurface
import com.prauga.pvot.designsystem.effects.LocalPvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassStyle
import com.prauga.pvot.designsystem.motion.PvotDurations

/** What a [PvotShutterButton] does when tapped, which sets its look. */
public enum class PvotShutterState {
    /** Takes a photo: a white disc. */
    Photo,

    /** Starts recording: a red disc. */
    Video,

    /** Stops recording: a small red square. */
    Recording,
}

/**
 * Color configuration for [PvotShutterButton].
 *
 * @param photoColor The disc of [PvotShutterState.Photo]
 * @param recordColor The disc of [PvotShutterState.Video] and the square of
 *   [PvotShutterState.Recording]
 * @param lockColor The target a held shutter slides to, to keep recording
 */
@Immutable
public data class PvotShutterButtonColors(
    val photoColor: Color,
    val recordColor: Color,
    val lockColor: Color,
)

/** Defaults for [PvotShutterButton]. */
public object PvotShutterButtonDefaults {
    /** The shutter's diameter. */
    public val Size: Dp = 78.dp

    /** The disc's diameter when ready. */
    public val CoreSize: Dp = 64.dp

    /** The square's side while recording. */
    public val RecordingCoreSize: Dp = 30.dp

    /** How long the shutter is held before [PvotShutterButton]'s `onHold` runs. */
    public const val HoldTimeoutMillis: Long = 450L

    /** How far a held shutter slides right to lock. */
    public val LockDistance: Dp = 90.dp

    /** How far the shutter slides left, before the hold, to call `onSlideStart`. */
    public val SlideDistance: Dp = 40.dp

    /** The camera's shutter colors: white, and the recording red. */
    public fun colors(
        photoColor: Color = Color.White,
        recordColor: Color = Color(0xFFFF3B30),
        lockColor: Color = Color.White.copy(alpha = 0.9f),
    ): PvotShutterButtonColors = PvotShutterButtonColors(photoColor, recordColor, lockColor)
}

/**
 * A camera shutter: a glass ring around a disc that turns into a stop square while recording.
 *
 * A tap calls [onClick]. With [onHold], holding the shutter for
 * [PvotShutterButtonDefaults.HoldTimeoutMillis] calls it instead, the release then calls
 * [onHoldRelease], and sliding right to the lock target calls [onHoldLock] so that letting go keeps
 * going. With [onSlideStart], sliding left before the hold calls it, and the release
 * [onSlideRelease], for a burst.
 *
 * @param state What a tap does, which sets the look
 * @param onClick Called when the shutter is tapped
 * @param contentDescription What the shutter does, for TalkBack
 * @param modifier Modifier for the shutter
 * @param backdrop The content behind the ring, or null for the tint alone
 * @param enabled Whether the shutter responds
 * @param holding Whether a hold is in progress, which shows the lock target
 * @param onHold Called when the shutter has been held, or null for taps only
 * @param onHoldLock Called when a held shutter slides to the lock target
 * @param onHoldRelease Called when a held shutter is let go without locking
 * @param onSlideStart Called when the shutter slides left before the hold, or null for no slide
 * @param onSlideRelease Called when a slid shutter is let go
 * @param colors The disc, square and lock target colors
 * @param style How the ring's glass looks
 */
@Composable
public fun PvotShutterButton(
    state: PvotShutterState,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    backdrop: PvotGlassBackdrop? = LocalPvotGlassBackdrop.current,
    enabled: Boolean = true,
    holding: Boolean = false,
    onHold: (() -> Unit)? = null,
    onHoldLock: () -> Unit = {},
    onHoldRelease: () -> Unit = {},
    onSlideStart: (() -> Unit)? = null,
    onSlideRelease: () -> Unit = {},
    colors: PvotShutterButtonColors = PvotShutterButtonDefaults.colors(),
    style: PvotGlassStyle = PvotCameraGlassDefaults.Style,
) {
    var pressed by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val scale by
        animateFloatAsState(if (pressed) 0.92f else 1f, tween(PvotDurations.Short), label = "scale")

    val recording = state == PvotShutterState.Recording || holding
    val coreColor by
        animateColorAsState(
            if (state == PvotShutterState.Photo && !holding) colors.photoColor
            else colors.recordColor,
            tween(PvotDurations.Medium),
            label = "core",
        )
    val coreSize by
        animateDpAsState(
            if (recording) PvotShutterButtonDefaults.RecordingCoreSize
            else PvotShutterButtonDefaults.CoreSize,
            tween(PvotDurations.Medium),
            label = "coreSize",
        )
    val coreRadius by
        animateDpAsState(
            if (recording) 8.dp else PvotShutterButtonDefaults.CoreSize / 2,
            tween(PvotDurations.Medium),
            label = "coreRadius",
        )

    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current
    val lockDistance = with(density) { PvotShutterButtonDefaults.LockDistance.toPx() }
    val slideDistance = with(density) { PvotShutterButtonDefaults.SlideDistance.toPx() }

    val currentEnabled by rememberUpdatedState(enabled)
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnHold by rememberUpdatedState(onHold)
    val currentOnHoldLock by rememberUpdatedState(onHoldLock)
    val currentOnHoldRelease by rememberUpdatedState(onHoldRelease)
    val currentOnSlideStart by rememberUpdatedState(onSlideStart)
    val currentOnSlideRelease by rememberUpdatedState(onSlideRelease)

    Box(
        modifier = modifier.size(PvotShutterButtonDefaults.Size),
        contentAlignment = Alignment.Center,
    ) {
        if (holding) {
            Box(
                modifier =
                    Modifier.offset(x = PvotShutterButtonDefaults.LockDistance)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(colors.lockColor)
            )
        }

        Box(
            modifier =
                Modifier.size(PvotShutterButtonDefaults.Size)
                    .scale(scale)
                    .pvotGlassSurface(backdrop = backdrop, style = style, role = null)
                    .semantics {
                        role = Role.Button
                        this.contentDescription = contentDescription
                        onClick {
                            currentOnClick()
                            true
                        }
                    }
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            if (!currentEnabled) {
                                return@awaitEachGesture
                            }
                            pressed = true

                            val hold = currentOnHold
                            val slide = currentOnSlideStart
                            val gesture =
                                if (hold == null && slide == null) {
                                    ShutterGesture.Press
                                } else {
                                    withTimeoutOrNull(PvotShutterButtonDefaults.HoldTimeoutMillis) {
                                        while (true) {
                                            val change = awaitPointerEvent().changes.first()
                                            if (!change.pressed) {
                                                return@withTimeoutOrNull ShutterGesture.Tap
                                            }
                                            if (
                                                slide != null &&
                                                    change.position.x - down.position.x <
                                                        -slideDistance
                                            ) {
                                                return@withTimeoutOrNull ShutterGesture.Slide
                                            }
                                        }
                                    }
                                        ?: if (hold != null) ShutterGesture.Hold
                                        else ShutterGesture.Press
                                }

                            when (gesture) {
                                ShutterGesture.Tap -> currentOnClick()

                                ShutterGesture.Press -> {
                                    var inside = true
                                    var released = false
                                    while (!released) {
                                        val change = awaitPointerEvent().changes.first()
                                        if (
                                            change.position.x !in 0f..size.width.toFloat() ||
                                                change.position.y !in 0f..size.height.toFloat()
                                        ) {
                                            inside = false
                                        }
                                        released = !change.pressed
                                    }
                                    if (inside) currentOnClick()
                                }

                                ShutterGesture.Slide -> {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    currentOnSlideStart?.invoke()
                                    while (awaitPointerEvent().changes.first().pressed) {
                                        continue
                                    }
                                    currentOnSlideRelease()
                                }

                                ShutterGesture.Hold -> {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    currentOnHold?.invoke()
                                    var locked = false
                                    while (true) {
                                        val change = awaitPointerEvent().changes.first()
                                        if (!change.pressed) break
                                        dragOffset =
                                            (change.position.x - down.position.x).coerceIn(
                                                0f,
                                                lockDistance,
                                            )
                                        if (!locked && dragOffset >= lockDistance * 0.9f) {
                                            locked = true
                                            haptics.performHapticFeedback(
                                                HapticFeedbackType.TextHandleMove
                                            )
                                            currentOnHoldLock()
                                        }
                                    }
                                    if (!locked) currentOnHoldRelease()
                                }
                            }

                            pressed = false
                            dragOffset = 0f
                        }
                    }
        )

        Box(
            modifier =
                Modifier.offset { IntOffset(dragOffset.toInt(), 0) }
                    .scale(scale)
                    .size(coreSize)
                    .clip(RoundedCornerShape(coreRadius))
                    .background(coreColor.copy(alpha = if (enabled) 1f else 0.5f))
        )
    }
}

private enum class ShutterGesture {
    Tap,
    Press,
    Slide,
    Hold,
}
