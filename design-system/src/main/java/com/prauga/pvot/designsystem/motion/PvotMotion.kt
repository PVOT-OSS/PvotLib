// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.motion

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Shared motion defaults. Every value is overridable at the call site. */
public object PvotMotion {

    /** Expo-out: arrives fast, eases to rest. */
    public val Settle: Easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

    /** Length of a single item's reveal. */
    public const val RevealDurationMillis: Int = PvotDurations.Long

    /** Delay added per item in a staggered reveal. */
    public const val StaggerStepMillis: Int = 20

    /** Past this many items the delay stops growing, so long lists never wait. */
    public const val MaxStaggeredItems: Int = 3

    /** How far an item rises while it reveals. */
    public val RevealRise: Dp = 12.dp

    /** Scale of an element while it is pressed. */
    public const val PressedScale: Float = 0.97f

    /** Slightly underdamped, so a press settles with some life. */
    public val PressSpring: SpringSpec<Float> = pressSpring()

    /** [PressSpring] for animations of other types. */
    public fun <T> pressSpring(): SpringSpec<T> =
        spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow)

    /** Springy overshoot for playful state changes, such as a nav item growing into its pill. */
    public fun <T> bounceSpring(): SpringSpec<T> =
        spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium)

    /**
     * How long a touch rests on a control before the control owns it rather than the list it is in:
     * a quick swipe that starts on the control still scrolls, a press held this long, or a drag
     * already under way along it, keeps the touch.
     */
    public const val TouchHoldMillis: Long = 150

    /** A selection sliding to the item picked: firm, and settles without a bounce. */
    public fun <T> slideSpring(): SpringSpec<T> = spring(dampingRatio = 0.92f, stiffness = 1024f)

    /** A selection following the finger: stiffer than [slideSpring], so it keeps up. */
    public fun <T> followSpring(): SpringSpec<T> = spring(dampingRatio = 0.94f, stiffness = 2500f)

    /** A thumb or selection lifting into a lens while held, and settling back when let go. */
    public fun <T> liftSpring(): SpringSpec<T> = spring(dampingRatio = 0.92f, stiffness = 676f)
}
