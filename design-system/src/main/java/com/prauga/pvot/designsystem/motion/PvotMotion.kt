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

    public const val RevealDurationMillis: Int = PvotDurations.Long

    public const val StaggerStepMillis: Int = 20

    /** Past this many items the delay stops growing, so long lists never wait. */
    public const val MaxStaggeredItems: Int = 3

    public val RevealRise: Dp = 12.dp

    public const val PressedScale: Float = 0.97f

    /** Slightly underdamped, so a press settles with some life. */
    public val PressSpring: SpringSpec<Float> = pressSpring()

    /** [PressSpring] for animations of other types. */
    public fun <T> pressSpring(): SpringSpec<T> = spring(
        dampingRatio = 0.55f,
        stiffness = Spring.StiffnessMediumLow,
    )
}
